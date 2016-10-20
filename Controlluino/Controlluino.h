
#include "Arduino.h"

/***********************************************************************************/
/*                                Pin definitions                                  */
/***********************************************************************************/

#define VOLT_SENS_PIN A0    // Battery voltage analog input

#define STATUS_PIN  A7      // Status LED
#define POWER_PIN   9       // Power electronics enable
#define SENSOR_PIN  8       // Sensor power

#define SRX_PIN A6          // Sensor RX LED
#define STX_PIN A5          // Sensor TX LED
#define XRX_PIN A4          // XBee RX LED
#define XTX_PIN A3          // XBee TX LED

/***********************************************************************************/
/*                                Macro Definitions                                */
/***********************************************************************************/

// Turn on LEDs
#define SRX_HIGH digitalWrite(SRX_PIN, HIGH)
#define STX_HIGH digitalWrite(STX_PIN, HIGH)
#define XRX_HIGH digitalWrite(XRX_PIN, HIGH)
#define XTX_HIGH digitalWrite(XTX_PIN, HIGH)
#define STATUS_HIGH digitalWrite(STATUS_PIN, HIGH)

// Turn off LEDs
#define SRX_LOW digitalWrite(SRX_PIN, LOW)
#define STX_LOW digitalWrite(STX_PIN, LOW)
#define XRX_LOW digitalWrite(XRX_PIN, LOW)
#define XTX_LOW digitalWrite(XTX_PIN, LOW)
#define STATUS_LOW digitalWrite(STATUS_PIN, LOW)

// Blink status LED t times with a delay of d millis
#define BLINK_DELAY(t,d) \
for (int i=0; i<t; i++)  { i%2 == 0 ? STATUS_LOW : STATUS_HIGH; delay(d); }

/***********************************************************************************/
/*                               Serial Port definitions                           */
/***********************************************************************************/

#define XSerial Serial  // XBee port
#define SSerial Serial3 // Sensor port
#define DSerial Serial  // Debug port

/***********************************************************************************/
/*                                 Scenarios & Outputs                             */
/***********************************************************************************/

#define ATTITUDE_CONTROL 0
#define POSITION_CONTROL 1
#define FORCE_CONTROL 2

#define QUATERNION_OUTPUT 0x00
#define EULER_OUTPUT 0x01
#define GYRO_OUTPUT 0x02
#define TORQUES_OUTPUT 0x03
#define FORCES_OUTPUT 0x04
#define SERVOS_OUTPUT 0x05
#define MOTORS_OUTPUT 0x06
#define VELOCITY_OUTPUT 0x07
#define POSITION_OUTPUT 0x08
#define ACCELERATION_OUTPUT 0x09
#define NO_OUTPUT 0x0A

// #define DEBUG
