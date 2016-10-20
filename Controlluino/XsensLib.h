
#include "XsensMsg.h"
#include "Controlluino.h"

/*
 // Bit masks for converting uint32 to float
 #define SIGN_MASK  0x80000000 // Bit mask for sign bit
 #define EXP_MASK   0x7F800000 // Bit mask for exponent bits
 #define FRAC_MASK  0x007FFFFF // Bit mask for fraction bits
 */

#define XSENS_BAUD_RATE 115200
#define XSEND_SEND_DELAY 10

#define MSG_DATA &DATA, &BID, &MID, &LEN, &CS

//float conv32toF(uint32_t valX);
//uint32_t byte4toUint32(byte *in);
byte convBEtoLE(byte in);

void sendMsg(byte *msg, char len);
void requestData();
boolean readSensorData();
boolean initSensor(int sc);

int updateMeasurements(float* quat, float* gyro, float* pos, float* vel, float* acc);

boolean getGPSFix();

void Geo2ENU(float* geo);

void handleError(byte ErrorCode);