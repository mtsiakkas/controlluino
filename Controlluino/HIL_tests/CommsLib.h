
#include "HIL_tests.h"

/***********************************************************************************/
/*                                                                                 */
/* Transmission Time Calculation:                                                  */
/* 4 BYTES/FLOAT                                                                   */
/* 9 BITS/BYTE (including stop bit)                                                */
/*                                                                                 */
/* (  2  +  2 + 1 + 1 +  4*7 )*(9/115200+10us)=0.002996s=2.996ms                   */
/*  START STOP  CS  VB  7 DATA                                                     */
/*                                                                                 */
/***********************************************************************************/

#define XBEE_BAUD_RATE 115200
#define XBEE_SEND_DELAY delay(10)

#define X_READY_MSG 0
#define X_INIT_SENSOR 1
#define X_INIT_MOTORS 2
#define X_SETUP_MSG 3
#define X_MOTOR_PARAMS 4
#define X_POWER_OFF 5

void xSendInfoMsg(int msg);
void sendReadyMsg();
void sendSensorInitMsg();
void sendMotorsInitMsg();
void sendDataToHost(float* d, int s,  byte vbat);
void readReference(float* ref);


