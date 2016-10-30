
#include "HIL_tests.h"

boolean initSensor(int sc);
int updateMeasurements(float* quat, float* gyro, float* pos, float* vel, float* acc);
void handleError(byte ErrorCode);
boolean getGPSFix();
