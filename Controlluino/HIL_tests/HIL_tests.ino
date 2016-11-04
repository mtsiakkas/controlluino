/***********************************************************************************/
/*                                                                                 */
/*       _____            _             _ _       _               _____ _____      */
/*      / ____|          | |           | | |     (_)             |_   _|_   _|     */
/*     | |     ___  _ __ | |_ _ __ ___ | | |_   _ _ _ __   ___     | |   | |       */
/*     | |    / _ \| '_ \| __| '__/ _ \| | | | | | | '_ \ / _ \    | |   | |       */
/*     | |___| (_) | | | | |_| | | (_) | | | |_| | | | | | (_) |  _| |_ _| |_      */
/*      \_____\___/|_| |_|\__|_|  \___/|_|_|\__,_|_|_| |_|\___/  |_____|_____|     */
/*                                                                                 */
/*                                                                                 */
/***********************************************************************************/
/*
    This is a HIL test file for testing ground station software.
*/


#include "HIL_tests.h"
#include "XsensLib.h"     // Library for communicating with Xsens MTi-G
#include "CommsLib.h"     // Library for communicating with host

/***********************************************************************************/

byte batteryVoltage = 0;
boolean RUN = false;
boolean SENSOR = false;

float zf = 0;

float euler[3] = {0, 0, 0};
float quat[4] = {1, 0, 0, 0};   // Attitude Quaternion
float gyro[3] = {0, 0, 0};      // Angular Rates

float pos[3] = {0, 0, 0};       // Position
float vel[3] = {0, 0, 0};       // Velocity
float acc[3] = {0, 0, 0};       // Acceleration

float initPos[3] = {0, 0, 0};   // Initial position

float ref[6]  = {0, 0, 0, 0, 0, 0}; // Reference point
float u[6] = {0, 0, 0, 0, 0, 0}; // Servo/Motor Angles/Speeds

float tfb[6] = {0, 0, 0, 0, 0, 0}; // Torque/Force vector

float* output = NULL;

int samplingTime = 50000;       // Sampling time in us

int scenario = ATTITUDE_CONTROL;
int output1 = ACTUATOR_OUTPUT;
int output2 = QUATERNION_OUTPUT;
int outputLength = 6;

void configureOutput();
void assembleOutput();
void quat2euler(float* q, float* e);
void reset();
void powerOff();

/***********************************************************************************/

void setup() {

  pinMode(STATUS_PIN, OUTPUT);
  STATUS_LOW;

  pinMode(VOLT_SENS_PIN, INPUT);
  //    digitalWrite(VOLT_SENS_PIN, HIGH); // Enable internal pullup
  analogReadResolution(12);

  // Initialize serial communication to PC/XBee
  // on pins TX2/RX2
  XSerial.begin(XBEE_BAUD_RATE);
  xSendInfoMsg(X_READY_MSG);
  STATUS_HIGH;

}

/***********************************************************************************/
/*                                                                                 */
/*                                     MAIN LOOP                                   */
/*                                                                                 */
/***********************************************************************************/
/*                                                                                 */
/* 1. Check for new XBee message and act accordingly                               */
/* 2. Update measurements from sensor                                              */
/* 3. Send data to host PC                                                         */
/* 4. Compute control action                                                       */
/* 5. Set new motor/servo speeds/angles                                            */
/*                                                                                 */
/***********************************************************************************/

int di = 0;
int txInterval = 0;

void loop() {
  // Check for new XBee message
  if (XSerial.available()) {

    byte XBEEr = (byte)XSerial.read();
    switch (XBEEr) {
      case 0xFF: { // Stop operation but don't power off
          byte c = (byte)XSerial.read();
          if (c == 0xFF) RUN = false;
          break;
        }
      case 0xEE: { // Power up the sensor
          byte c = (byte)XSerial.read();
          if (c == 0xEE) {
            if (scenario != FORCE_CONTROL) {
              // Initialize serial communication to Xsens on pins TX1/RX1
              // SSerial.begin(XSENS_BAUD_RATE);

              if (!initSensor(scenario)) {
                powerOff();
                break;
              } else SENSOR = true;

              if (scenario == POSITION_CONTROL) {
                // Wait for GPS fix
                do {
                  updateMeasurements(quat, gyro, pos, vel, acc);
                } while (!getGPSFix());

                // Set initial position
                for (int i = 0; i < 3; i++) *(initPos + i) = *(pos + i);
              }
              xSendInfoMsg(X_INIT_SENSOR);
            }
          }
          break;
        }
      case 0xDD: { // If the sensor is powered, run control loop
          byte c = (byte)XSerial.read();
          if (c == 0xDD) {
            if (scenario != FORCE_CONTROL) {
              if (SENSOR) RUN = true;
            } else {
              RUN = true;
            }
          }
          break;
        }
      case 0xCC: // Incoming message with new reference values
        readReference(ref);
        break;
      case 0xBB: { // Initialize motors/servos
          byte c = (byte)XSerial.read();
          if (c == 0xBB) {
            // initMotors();
            xSendInfoMsg(X_INIT_MOTORS);
          }
          break;
        }
      case 0xAA: {// Reset all variables to 0 and turn off motors
          byte c = (byte)XSerial.read();
          if (c == 0xAA) reset();
          STATUS_HIGH;
          delay(10);
          STATUS_LOW;
          break;
        }
      case 0x33: { // Motor config
          byte param = (byte)XSerial.read();
          switch (param) {
            case 0: {
                byte types = (byte)XSerial.read();
                byte cs = (byte)XSerial.read();
                if (((param + types + cs) & 0xFF) == 0)  {
                  // setMotorTypes(types);
                  // SETTING MOTOR TYPES
                }
                xSendInfoMsg(X_MOTOR_PARAMS);
                break;
              }
            case 1: {
                byte tmp[] = {
                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                };
                float c[] = {0, 0, 0, 0, 0, 0};
                while (XSerial.available() < 24);
                for (int i = 23; i >= 0; i--) *(tmp + i) = (byte)XSerial.read();
                byte cs = (byte)XSerial.read();
                float *tmpF = (float*)&tmp;
                for (int i = 0; i < 6; i++) *(c + 5 - i) = *(tmpF + i);
                // setMotorOffset(c);
                // SETTING MOTOR OFFSET
                xSendInfoMsg(X_MOTOR_PARAMS);
                break;
              }
            case 2: {
                byte tmp[] = {
                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                };
                float m[] = {0, 0, 0, 0, 0, 0};
                while (XSerial.available() < 24);
                for (int i = 23; i >= 0; i--) *(tmp + i) = (byte)XSerial.read();
                byte cs = (byte)XSerial.read();
                float *tmpF = (float*)&tmp;
                for (int i = 0; i < 6; i++) *(m + 5 - i) = *(tmpF + i);
                //setMotorGradient(m);
                // SETTING MOTORO GRADIENTS
                xSendInfoMsg(X_MOTOR_PARAMS);
                break;
              }
            case 3: {
                byte tmp[] = {
                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                };
                int min[] = {0, 0, 0, 0, 0, 0};
                while (XSerial.available() < 24);
                for (int i = 23; i >= 0; i--) *(tmp + i) = (byte)XSerial.read();
                byte cs = (byte)XSerial.read();
                int *tmpF = (int*)&tmp;
                for (int i = 0; i < 6; i++) *(min + 5 - i) = *(tmpF + i);
                // setMotorMin(min);
                // SETTING MOTOR MIN
                xSendInfoMsg(X_MOTOR_PARAMS);
                break;
              }
            case 4: {
                byte tmp[] = {
                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                };
                int max[] = {0, 0, 0, 0, 0, 0};
                while (XSerial.available() < 24);
                for (int i = 23; i >= 0; i--) *(tmp + i) = (byte)XSerial.read();
                byte cs = (byte)XSerial.read();
                int *tmpF = (int*)&tmp;
                for (int i = 0; i < 6; i++) *(max + 5 - i) = *(tmpF + i);
                // setMotorMax(max);
                // SETTING MOTOR MAX
                xSendInfoMsg(X_MOTOR_PARAMS);
                break;
              }
            default:
              break;
          }
          break;
        }
      case 0x22: { // Setup

          while (XSerial.available() < 6);
          byte txi = (byte)XSerial.read();    // Transmission interval
          byte sc = (byte)XSerial.read();     // Scenario
          byte sH = (byte)XSerial.read();     // Sampling time
          byte out1 = (byte)XSerial.read();   // First output
          byte out2 = (byte)XSerial.read();   // Second output
          byte cs = (byte)XSerial.read();     // Checksum
          int sum = txi + sc + sH + out1 + out2 + cs;
          if ((sum & 0xFF) == 0 && !RUN) {
            txInterval = txi;
            di = di > txInterval ? 0 : di;
            samplingTime = sH * 1000;
            scenario = sc;
            output1 = out1;
            output2 = out2;
            configureOutput();
            xSendInfoMsg(X_SETUP_MSG);
          } else {
            XSerial.write(txi);
            delayMicroseconds(10);
            XSerial.write(sc);
            delayMicroseconds(10);
            XSerial.write(sH);
            delayMicroseconds(10);
            XSerial.write(out1);
            delayMicroseconds(10);
            XSerial.write(out2);
            delayMicroseconds(10);
            XSerial.write(cs);
            delayMicroseconds(10);
            XSerial.write(sum);
          }
          break;
        }
      case 0xFE: { // Power off
          byte c = (byte)XSerial.read();
          if (c == 0xFE) powerOff();

          break;
        }
      default:
        // Unrecognised XBee message
        // Do nothing
        break;
    }
  }

  if (RUN) {

    // Compute control
    // Controller(u, quat, gyro, pos, vel, acc, ref, tfb);

    // Update motor speeds
    // setMotorSpeeds(u);

    // Read battery voltage and scale to byte
    batteryVoltage = (byte)(map(analogRead(VOLT_SENS_PIN), 0, 4095, 0, 255));
    if (scenario == FORCE_CONTROL) {
      if (di == txInterval) {
        assembleOutput();
        sendDataToHost(output, outputLength, batteryVoltage);
        di = 0;
      } else {
        di++;
      }
    } else {
      switch (updateMeasurements(quat, gyro, pos, vel, acc)) {
        case 0:
          if (di == txInterval) {
            assembleOutput();
            sendDataToHost(output, outputLength, batteryVoltage);
            di = 0;
          } else {
            di++;
          }
          break;
        case 1: // Sensor returned error
          powerOff();
          break;
        case 2: // Unable to read from sensor
          powerOff();
          break;
        default:
          break;
      }
    }
  }

  // Timing!
  while (micros() % samplingTime < samplingTime - 10);
}

/***********************************************************************************/
/*                                                                                 */
/*                               MAIN LOOP END                                     */
/*                                                                                 */
/***********************************************************************************/

void powerOff() {
  STATUS_HIGH;
  delay(50);
  STATUS_LOW;
  delay(50);
  STATUS_HIGH;
  delay(50);
  STATUS_LOW;
  delay(50);
  STATUS_HIGH;
  delay(50);
  STATUS_LOW;
  delay(50);
  STATUS_HIGH;
  delay(50);
  STATUS_LOW;
  delay(50);
  STATUS_HIGH;
  delay(50);
  STATUS_LOW;
  delay(50);

  reset();
  xSendInfoMsg(X_POWER_OFF);
}

void reset() {
  for (int i = 0; i < 6; i++) {
    *(u + i) = 0;
    *(ref + i) = 0;
  }
  for (int i = 0; i < 3; i++) {
    *(pos + i) = 0;
    *(vel + i) = 0;
    *(acc + i) = 0;
    *(gyro + i) = 0;
  }

  for (int i = 0; i < 4; i++) *(quat + i) = (i == 0);
}


void quat2euler(float* q, float* e) {
  *(e + 0) = atan2(2 * (*(quat + 0)**(quat + 1) + * (quat + 2)**(quat + 3)), 1 - 2 * (*(quat + 1)**(quat + 1) + * (quat + 2)**(quat + 2)));
  *(e + 1) = asin(2 * (*(quat + 0)**(quat + 2) - * (quat + 3)**(quat + 1)));
  *(e + 2) = atan2(2 * (*(quat + 0)**(quat + 3) + * (quat + 1)**(quat + 2)), 1 - 2 * (*(quat + 2)**(quat + 2) + * (quat + 3)**(quat + 3)));
}


// FIX ALLOC FOR ACTUATOR OUTPUT
void configureOutput() {
  if ((output1 == QUATERNION_OUTPUT) && (output2 == QUATERNION_OUTPUT)) {
    output = (float*) realloc (output, 8 * sizeof(float));
    outputLength = 8;
  } else if (((output1 == NO_OUTPUT) || (output2 == NO_OUTPUT)) && !((output1 == NO_OUTPUT) && (output2 == NO_OUTPUT))) {
    if ((output1 == QUATERNION_OUTPUT) || (output2 == QUATERNION_OUTPUT)) {
      output = (float*) realloc (output, 4 * sizeof(float));
      outputLength = 4;
    } else {
      output = (float*) realloc (output, 3 * sizeof(float));
      outputLength = 3;
    }
  } else if ((output1 == QUATERNION_OUTPUT) || (output2 == QUATERNION_OUTPUT)) {
    output = (float*) realloc (output, 7 * sizeof(float));
    outputLength = 7;
  } else if ((output1 == NO_OUTPUT) && (output2 == NO_OUTPUT)) {
    output = (float*) realloc (output, 0 * sizeof(float));
    outputLength = 0;
  } else {
    output = (float*) realloc (output, 6 * sizeof(float));
    outputLength = 6;
  }
}

void assembleOutput() {
  int offset = 3;
  switch (output1) {
    case QUATERNION_OUTPUT:
      for (int i = 0; i < 4; i++) *(output + i) = *(quat + i);
      offset = 4;
      break;
    case EULER_OUTPUT:
      quat2euler(quat, euler);
      for (int i = 0; i < 3; i++) *(output + i) = *(euler + i);
      break;
    case GYRO_OUTPUT:
      for (int i = 0; i < 3; i++) *(output + i) = *(gyro + i);
      break;
    case ACTUATOR_OUTPUT:
      for (int i = 0; i < 6; i++) *(output + i) = *(u + i);
      break;
    case VELOCITY_OUTPUT:
      for (int i = 0; i < 3; i++) *(output + i) = *(vel + i);
      break;
    case POSITION_OUTPUT:
      for (int i = 0; i < 3; i++) *(output + i) = *(pos + i);
      break;
    case ACCELERATION_OUTPUT:
      for (int i = 0; i < 3; i++) *(output + i) = *(acc + i);
      break;
    default:
      offset = 0;
      break;
  }

  switch (output2) {
    case QUATERNION_OUTPUT:
      for (int i = 0; i < 4; i++) *(output + offset + i) = *(quat + i);
      break;
    case EULER_OUTPUT:
      quat2euler(quat, euler);
      for (int i = 0; i < 3; i++) *(output + offset + i) = *(euler + i);
      break;
    case GYRO_OUTPUT:
      for (int i = 0; i < 3; i++) *(output + offset + i) = *(gyro + i);
      break;
    case ACTUATOR_OUTPUT:
      for (int i = 0; i < 6; i++) *(output + offset + i) = *(u + i);
      break;
    case VELOCITY_OUTPUT:
      for (int i = 0; i < 3; i++) *(output + offset + i) = *(vel + i);
      break;
    case POSITION_OUTPUT:
      for (int i = 0; i < 3; i++) *(output + offset + i) = *(pos + i);
      break;
    case ACCELERATION_OUTPUT:
      for (int i = 0; i < 3; i++) *(output + offset + i) = *(acc + i);
      break;
    default:
      break;
  }
}


