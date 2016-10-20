
#include "XsensLib.h"
#include "Controller.h"

/***********************************************************************************/
/*                                                                                 */
/*                               ATITUDE CONTROL                                   */
/*                          | GYRO | QUAT | STATUS? |                              */
/*                                                                                 */
/* Transmission Time Calculation:                                                  */
/* 4 BYTES/FLOAT    9 BITS/BYTE (including stop bit)   10us DELAY BETWEEN TX       */
/* ( 1 + 1 + 1 + 1 + 1 + 4*( 4  +  3 )+  1  )*(9/115200 + 10us)=2.996ms            */
/*  PRE MID BID LEN CS      QUAT GYRO  STATUS                                      */
/*                                                                                 */
/***********************************************************************************/

byte SET_OUTPUT_SETTINGS_ATT[] = {0xFA,0xFF,SetOutputSettings,0x04,0x00,0x30,0x0C,0x50};
byte SET_OUTPUT_MODE_ATT[] =     {0xFA,0xFF,SetOutputMode,0x02,0x00,0x06};

/***********************************************************************************/
/*                                                                                 */
/*                               POSITION CONTROL                                  */
/*                  | ACC? | GYRO | QUAT | POS | VEL | STATUS? |                   */
/*                                                                                 */
/* Transmission Time Calculation:                                                  */
/* 4 BYTES/FLOAT    9 BITS/BYTE (including stop bit)   10us DELAY BETWEEN TX       */
/* ( 1 + 1 + 1 + 1 + 1 + 4*( 4  +  3 + 3 + 3 + 3 )+  1  )*(9/115200 + 10us)=6.17ms */
/*  PRE MID BID LEN CS     QUAT  GYRO POS VEL ACC  STATUS                          */
/*                                                                                 */
/***********************************************************************************/

byte SET_OUTPUT_SETTINGS_POS[] = {0xFA,0xFF,SetOutputSettings,0x04,0x00,0x30,0x0C,0x40};
byte SET_OUTPUT_MODE_POS[] =     {0xFA,0xFF,SetOutputMode,0x02,0x08,0x36};


// Common initialization messages
byte WAKE_UP_ACK[] =        {0xFA,0xFF,WakeUpAck,0x00};
byte RESET[] =              {0xFA,0xFF,Reset,0x00};
byte GO_TO_MEAS[] =         {0xFA,0xFF,GoToMeasurement,0x00};
byte GO_TO_CONF[] =         {0xFA,0xFF,GoToConfig,0x00};
byte SET_OUTPUT_SKIP[] =    {0xFA,0xFF,SetOutputSkipFactor,0x02,0xFF,0xFF};
byte REQ_DATA[] =           {0xFA,0xFF,ReqData,0x00};

// Incoming message storage
byte* DATA = NULL;
byte BID = 0;
byte MID = 0;
byte LEN = 0;
byte CS  = 0;
byte XSENS_STATUS = 0;

boolean GPSFix = false;
int controlScenario = 0;

/*
 // Convert unsigned 32-bit integer to single precision float (IEE754)
 float conv32toF(uint32_t valX) {
 short exponent = (((valX & ~SIGN_MASK) & EXP_MASK) >> 23)-127;
 float fraction = 1;
 for(byte i=23;i>0;i--) \
 fraction = fraction + (((valX & FRAC_MASK) >> (i-1)) & 1)*pow(2,i-24);
 
 float valF = pow(-1,(valX & SIGN_MASK) >> 31)*fraction*pow(2,exponent);
 return valF;
 }
 
 // Combine 4 bytes into an unsigned 32-bit integer
 uint32_t byte4toUint32(byte *in) {
 uint32_t val = 0;
 for(byte i=0;i<4;i++) val = (val<<8) + *(in+i);
 return val;
 }
 */

// Reverse byte endianness
byte convBEtoLE (byte in) {
    byte incp = in;
    for (int i=0; i<8; i++) incp = (incp&0x01) ? (incp>>1) + 0x80 : (incp>>1);
    return incp;
}

// Send message to Xsens
void sendMsg(byte *msg, char len) {
    STX_HIGH;
    int cs = -*msg; // Initialise checksum to -PREAMPLE
    for (int i=0; i<len; i++) {
        cs += *(msg+i); // Add byte to checksum
        SSerial.write(convBEtoLE(*(msg+i))); // Send byte to sensor
        delayMicroseconds(XSEND_SEND_DELAY); // Don't send bytes back-to-back
    }
    SSerial.write(convBEtoLE((byte)(256-(cs%256))));
    STX_LOW;
}

byte preample = 0;

// Read one message from the sensor
boolean readSensorData() {
    preample = 0;
    // Wait for preample (indicates incoming message)
    while (SSerial.available()<1) ;
    
    SRX_HIGH;
    preample = convBEtoLE((byte)SSerial.read());
    
    if(preample==0xFA) {
        while (SSerial.available()<3);
        
        BID = convBEtoLE((byte)SSerial.read());  // Read bus ID/address
        MID = convBEtoLE((byte)SSerial.read());  // Read message ID
        LEN = convBEtoLE((byte)SSerial.read());  // Read message length
        DATA = (byte*) realloc (DATA, LEN * sizeof(byte)); // Reallocate DATA memory
        while (SSerial.available()<LEN);
        
        // The data is stored backwards to facilitate conversion to floats
        for (int i=0; i<LEN; i++)
            *(DATA+LEN-i-1) = convBEtoLE((byte)SSerial.read());
        
        while (SSerial.available()<1);
        
        CS = convBEtoLE(((byte)SSerial.read()));
        
        int cs = BID+MID+LEN+CS;
        for (int i=0; i<LEN; i++)
            cs += *(DATA+i);
        
        if((cs&0xFF)==0) {
            SRX_LOW;
            return true;
        } else {
            BID = 0;
            MID = 0;
            LEN = 0;
            free(DATA);
            CS = 0;
            SRX_LOW;
            return false;
        }
    } else {
        SRX_LOW;
        return false;
    }
    
}

boolean initSensor(int sc) {
    controlScenario = sc;
    digitalWrite(SENSOR_PIN,HIGH);
    
    do {
        readSensorData();
    } while (MID!=WakeUp);
    sendMsg(WAKE_UP_ACK, sizeof(WAKE_UP_ACK));
    BLINK_DELAY(2, 50);
    
    do {
        if(controlScenario==ATTITUDE_CONTROL) {
            sendMsg(SET_OUTPUT_MODE_ATT, sizeof(SET_OUTPUT_MODE_ATT));
        } else if(controlScenario==POSITION_CONTROL) {
            sendMsg(SET_OUTPUT_MODE_POS, sizeof(SET_OUTPUT_MODE_POS));
        }
        readSensorData();
    } while (MID!=SetOutputModeAck);
    BLINK_DELAY(2, 50);
    
    do {
        sendMsg(SET_OUTPUT_SKIP, sizeof(SET_OUTPUT_SKIP));
        readSensorData();
    } while (MID!=SetOutputSkipFactorAck);
    BLINK_DELAY(2, 50);
    
    do {
        if(controlScenario==ATTITUDE_CONTROL) {
            sendMsg(SET_OUTPUT_SETTINGS_ATT, sizeof(SET_OUTPUT_SETTINGS_ATT));
        } else if(controlScenario==POSITION_CONTROL) {
            sendMsg(SET_OUTPUT_SETTINGS_POS, sizeof(SET_OUTPUT_SETTINGS_POS));
        }
        readSensorData();
    } while (MID!=SetOutputSettingsAck);
    BLINK_DELAY(2, 50);
    
    do {
        sendMsg(GO_TO_MEAS, sizeof(GO_TO_MEAS));
        readSensorData();
    } while (MID!=GoToMeasurementAck);
    BLINK_DELAY(2, 50);
    return true;
    
}

// If OutputSkipFactor is set to 0xFFFF use this function to request data
void requestData() {
    sendMsg(REQ_DATA, sizeof(REQ_DATA));
}

int updateMeasurements(float* quat, float* gyro, float* pos, float* vel, float* acc) {
    // Return values:
    // 0 - Everything OK
    // 1 - Sensor returned error message
    // 2 - Unable to communicate with the sensor
    
    requestData();
    if(readSensorData()) {
        switch (MID) {
            case MTData:
            {
                
                if (controlScenario==ATTITUDE_CONTROL) {
                    // Pointers to the beginning of each set of data
                    float *quatDataPtr = (float*)DATA;
                    float *gyroDataPtr = quatDataPtr+4;
                    
                    // Convert data to floats
                    for (int i=0; i<3; i++) *(gyro+2-i) = *(gyroDataPtr+i);
                    for (int i=0; i<4; i++) *(quat+3-i) = *(quatDataPtr+i);
                                        
                } else if(controlScenario==POSITION_CONTROL) {
                    // Pointers to the beginning of each set of data
                    XSENS_STATUS = *(DATA+0);
                    
                    GPSFix = (XSENS_STATUS >> 2) & 0x01;
                    
                    float *velDataPtr = (float*)(DATA+1);   // Velocity
                    float *posDataPtr = velDataPtr+3;       // Position
                    float *quatDataPtr = posDataPtr+3;      // Quaternion
                    float *gyroDataPtr = quatDataPtr+4;     // Gyroscope
                    float *accDataPtr = gyroDataPtr+3;      // Acceleration
                    
                    // Convert data to floats
                    for (int i=3; i>0; i--) {
                        *(vel+i) = *(velDataPtr+i-1);
                        *(pos+i) = *(posDataPtr+i-1);
                        *(gyro+i) = *(gyroDataPtr+i-1);
                        *(acc+i) = *(accDataPtr+i-1);
                    }
                    for (int i=4; i>0; i--) *(quat+i) = *(quatDataPtr+i-1);
                }
                return 0;
                break;
            } // END MTDATA CASE
            case Error:
                handleError(MID);
                return 1;
                break;
        } // END SWITCH(MID)
    } else {
        return 2;
    }
}

boolean getGPSFix() {
    return GPSFix;
}

void Geo2ENU(float* geo) {
    // TODO: Convert from Geodetic to Local tangent plane ENU coordinates
}

// ErrorCode definitions taken from MTi-G manual
void handleError(byte ErrorCode) {
    switch (ErrorCode) {
        case 0x03:
            // Period sent is not within valid range
            break;
        case 0x04:
            // Message sent is invalid
            break;
        case 0x1E:
            //Timer overflow, this can be caused to high output\
            frequency or sending too much data to MT during measurement
            break;
        case 0x20:
            // Baud rate sent is not within valid range
            break;
        case 0x21:
            // Parameter sent is invalid or not within range
            break;
        default:
            // Unknown error code
            break;
    }
}



