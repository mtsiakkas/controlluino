
#include "XsensLib.h"

boolean getGPSFix() {
	return true;
}

boolean initSensor(int sc) {  
    //INITIALISE SENSOR - TRUE=>SUCCESS
    return true;
    
}

int updateMeasurements(float* quat, float* gyro, float* pos, float* vel, float* acc) {
    // Return values:
    // 0 - Everything OK
    // 1 - Sensor returned error message
    // 2 - Unable to communicate with the sensor
    
    // TODO: SIMULATE VEHICLE
   return 0;
}




