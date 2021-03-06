
#include "CommsLib.h"

byte XBEE_RESPONSES[6][2] = {{0xFF,0xFF},
    {0xEF,0xEF},
    {0xDF,0xDF},
    {0x2F,0x2F},
    {0x3F,0x3F},
    {0xCF,0xCF}};

void xSendInfoMsg(int msg) {
    XSerial.write(XBEE_RESPONSES[msg][0]);
    XBEE_SEND_DELAY;
    XSerial.write(XBEE_RESPONSES[msg][1]);
}

void sendDataToHost(float* d, int s,  byte vbat) {
    long checksum = vbat;
    
    XSerial.write(0xFE);
    XBEE_SEND_DELAY;
    
    if(s!=0) {
        checksum += (byte)s*sizeof(float);
        XSerial.write((byte)(s*sizeof(float)));
        XBEE_SEND_DELAY;
    }
    
    for(int i=0;i<s*sizeof(float);i++) {
        XSerial.write(*((byte*)d+i));
        checksum += *((byte*)d+i);
        XBEE_SEND_DELAY;
    }
    
    XSerial.write(vbat);
    XBEE_SEND_DELAY;
    
    XSerial.write((byte)(256-(checksum%256)));
}

void readReference(float* ref) {
    byte inData[] = {
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    while(XSerial.available()<24);
    long cs = 0;
    for(int i=23;i>=0;i--) {
        *(inData+i) = (byte)XSerial.read();
        cs += *(inData+i);
    }
    cs += (byte)XSerial.read();
    
    if((cs&0xFF)==0) {
        float* dPtr = (float*)&inData;
        for(int i=0;i<6;i++) *(ref+5-i) = *(dPtr+i);
    }
}

