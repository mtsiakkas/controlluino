# controlluino

An arduino based project for autonomous systems control. 
Developed for the Arduino DUE.
Vehicle structure agnostic.

Parts:
- Ground station software (Java)
* Simple version - vehicle init/reference update/data collection
* Complete version - ditto + real time plotting
- Arduino sketch (C/C++)
   a) Communications protocol (via XBee)
   b) XSens (IMU) library
   c) Controller class
   d) Actuator control lib (upto 6 servos+BLDC motors)
