# controlluino

- An arduino based project for autonomous systems control. 
- Developed for the Arduino DUE.
- Vehicle structure agnostic.

## Software Components:
1. Ground station software (Java)
  1. Simple version - vehicle init/reference update/data collection
  2. Complete version - ditto + real time plotting
2. Arduino sketch (C/C++)
  1. Communications protocol (via XBee)
  2. XSens (IMU) library
  3. Controller class
  4. Actuator control lib (upto 6 servos+BLDC motors)

## Depends:
1. RXTX Java library for serial communications (http://rxtx.qbang.org/)
2. JFreeChart for real-time data plotting (http://www.jfree.org/jfreechart/)
