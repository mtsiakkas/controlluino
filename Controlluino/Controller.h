
#include "Math.h"

#define SAMPLING_TIME_US 20000
#define SAMPLING_TIME_MIN 19990

/*
 Define one of the following to set scenario
    ATTITUDE_CONTROL
    POSITION_CONTROL
    FORCE_CONTROL
 */


void Controller(float* u, float* quat, float* gyro, float* pos, float* vel, float* acc, float* ref, float* tfb);

void calcQuatError(float* qe, float* qd, float* quat);
void euler2quat(float* euler, float* quat);
void calcAW(float* u, float* tf);
void shiftArray(float* a, int s, float* n);
void prefilter(float *r, float* Pr);