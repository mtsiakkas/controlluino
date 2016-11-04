
#include "Arduino.h"

/***********************************************************************************/
/*                                Pin definitions                                  */
/***********************************************************************************/

#define VOLT_SENS_PIN A0    // Battery voltage analog input

#define STATUS_PIN  13      // Status LED

/***********************************************************************************/
/*                                Macro Definitions                                */
/***********************************************************************************/

// Turn on LEDs
#define STATUS_HIGH digitalWrite(STATUS_PIN, HIGH)

// Turn off LEDs
#define STATUS_LOW digitalWrite(STATUS_PIN, LOW)

/***********************************************************************************/
/*                               Serial Port definitions                           */
/***********************************************************************************/

#define XSerial Serial  // XBee port

/***********************************************************************************/
/*                                 Scenarios & Outputs                             */
/***********************************************************************************/

#define ATTITUDE_CONTROL 0
#define POSITION_CONTROL 1 // DO NOT USE. WILL BE STUCK IN FOR LOOP WAITING FOR GPS
#define FORCE_CONTROL 2

#define QUATERNION_OUTPUT 0x00
#define EULER_OUTPUT 0x01
#define GYRO_OUTPUT 0x02
#define ACTUATOR_OUTPUT 0x06
#define VELOCITY_OUTPUT 0x07
#define POSITION_OUTPUT 0x08
#define ACCELERATION_OUTPUT 0x09
#define NO_OUTPUT 0x0A

// #define DEBUG
