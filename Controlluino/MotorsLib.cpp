
#include "MotorsLib.h"

Motor motor1;
Motor motor2;
Motor motor3;
Motor motor4;
Motor motor5;
Motor motor6;

Motor* motors[] = {&motor1, &motor2, &motor3, &motor4, &motor5, &motor6};

int motor_pins[] = {MOTOR1_PIN, MOTOR2_PIN, MOTOR3_PIN,
    MOTOR4_PIN, MOTOR5_PIN, MOTOR6_PIN};                // Motor pins
float c[] = {969.85, 969.85, 969.85,
    1621.8, 1562.9, 1356.7};                            // Map coefficients
float m[] = {0.75615, 0.75615, 0.75615,                 // Wd->PWM width
    -564.3, -554.54, -564.28};                          // Offset/Gradient
int types[] = {MOTOR, MOTOR, MOTOR,
    SERVO, SERVO, SERVO};                               // Motor type (SERVO/MOTOR)
int min[] = {0, 0, 0, 850, 790, 790};                   // Min us PWM width
int max[] = {1877, 1877, 1877, 2178, 2171, 2178};       // Max us PWM width

void setMotorGradient(float* gradient) {
    for (int i=0; i<6; i++) *(m+i) = *(gradient+i);
}

void setMotorOffset(float* offset) {
    for (int i=0; i<6; i++) *(c+i) = *(offset+i);
}

void setMotorMin(int* usmin) {
    for (int i=0; i<6; i++) *(min+i) = *(usmin+i);
}

void setMotorMax(int* usmax) {
    for (int i=0; i<6; i++) *(max+i) = *(usmax+i);
}

void setMotorTypes(byte btypes) {
    for (int i=0; i<6; i++) *(types+5-i) = ((btypes>>i)&0x01) ? SERVO : MOTOR;
}

void initMotors() {
    
    for(int i=0; i<6; i++) {
        pinMode(*(motor_pins+i),OUTPUT);                // Configure output pins
        (*(motors+i))->type = *(types+i);               // Set type of motor
        (*(motors+i))->motor.attach(*(motor_pins+i));   // Attach motor to pin
        (*(motors+i))->offset = *(c+i);                 //
        (*(motors+i))->gradient = *(m+i);               // Specify motor
        (*(motors+i))->us_min = *(min+i);               // parameters
        (*(motors+i))->us_max = *(max+i);               //
    }
    
#ifdef DEBUG
    for(int i=0;i<6;i++) {
        DSerial.print("ACTUATOR ");
        DSerial.print(i);
        DSerial.print(": ");
        if(((*(motors+i))->type)==MOTOR) {
            DSerial.print("Motor");
        } else {
            DSerial.print("Servo");
        }
        DSerial.print(" c=");
        DSerial.print(c[i]);
        DSerial.print(" m=");
        DSerial.print(m[i]);
        DSerial.print(" min_us=");
        DSerial.print(min[i]);
        DSerial.print(" max_us=");
        DSerial.println(max[i]);
    }
#endif
    //    BLINK_DELAY(10, 100);
    // Calibrate/test motors
    // If DC motor calibrate by sending 1000us else send offset
    for(int i=0; i<6; i++)
        (*(motors+i))->motor.writeMicroseconds((*(motors+i))->type == MOTOR ?
                                               1000 : (*(motors+i))->offset);
    BLINK_DELAY(8,250);
}

void setMotorSpeeds(float* speeds) {
    for(int i=0;i<6;i++) {
        
        // Calculate PWM pulse width
        int pwm_width = *(speeds+i)*(*(motors+i))->gradient+(*(motors+i))->offset;
        
        // Check if limits are exceeded and
        // saturate at us_min/us_max if required
        if(pwm_width>(*(motors+i))->us_max) {
            (*(motors+i))->motor.writeMicroseconds((*(motors+i))->us_max);
        } else if(pwm_width<(*(motors+i))->us_min) {
            (*(motors+i))->motor.writeMicroseconds((*(motors+i))->us_min);
        } else {
            (*(motors+i))->motor.writeMicroseconds(pwm_width);
        }
    }
}


