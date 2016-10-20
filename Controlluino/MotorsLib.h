
#include "Controlluino.h"
#include "Servo.h"

// Output pins
#define MOTOR1_PIN 2
#define MOTOR2_PIN 3
#define MOTOR3_PIN 4
#define MOTOR4_PIN 5
#define MOTOR5_PIN 6
#define MOTOR6_PIN 7

#define MOTOR 0
#define SERVO 1

typedef struct {
    Servo motor;
    int type;
    float offset;
    float gradient;
    int us_min;
    int us_max;
} Motor;

void setMotorParams(float* gradient, float* offset, int* usmin, int* usmax, byte btypes);
void initMotors();
void setMotorSpeeds(float* speeds);
void setMotorGradient(float* gradient);
void setMotorOffset(float* offset);
void setMotorMin(int* usmin);
void setMotorMax(int* usmax);
void setMotorTypes(byte btypes);

