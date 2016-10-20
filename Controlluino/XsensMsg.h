//
// Definitions for Xsens MTi-G Low-level messages
// Reference: Xsens Document MT0101P.L
//

// WAKEUP AND STATE MESSAGES
#define WakeUp                      0x3E
#define WakeUpAck                   0x3F
#define GoToConfig                  0x30
#define GoToConfigAck               0x31
#define GoToMeasurement             0x10
#define GoToMeasurementAck          0x11
#define Reset                       0x40
#define ResetAck                    0x41
#define RunSelftest                 0x24
#define SelftestAck                 0x25

// INFORMATIONAL MESSAGES
#define ReqDID                      0x00
#define DeviceID                    0x01
#define InitMT                      0x02
#define InitMTResults               0x03
#define ReqProductCode              0x1C
#define ProductCode                 0x1D
#define ReqFWRev                    0x12
#define FirmwareRev                 0x13
#define ReqDataLength               0x0A
#define DataLength                  0x0B
#define Error                       0x42
#define ReqGPSStatus                0xA6
#define GPSStatus                   0xA7

// DEVICE-SPECIFIC MESSAGES
#define ReqBaudrate                 0x18
#define ReqBaudrateAck              0x19
#define SetBaudrate                 0x18
#define SetBaudrateAck              0x19
#define ReqErrorMode                0xDA
#define ReqErrorModeAck             0xDB
#define SetErrorMode                0xDA
#define SetErrorModeAck             0xDB
#define ReqLocationID               0x84
#define ReqLocationIDAck            0x85
#define SetLocationID               0x84
#define SetLocationIDAck            0x85
#define RestoreFactoryDef           0x0E
#define RestoreFactoryDefAck        0x0F
#define ReqTransmitDelay            0xDC
#define ReqTransmitDelayAck         0xDD
#define SetTransmitDelay            0xDC
#define SetTransmitDelayAck         0xDD
#define StoreXkfState               0x8A

// SYNCHRONISATION MESSAGES
#define ReqSyncInSettings           0xD6
#define ReqSyncInSettingsAck        0xD7
#define SetSyncInSettings           0xD6
#define SetSyncInSettingsAck        0xD7
#define ReqSyncOutSettings          0xD8
#define ReqSyncOutSettingsAck       0xD9
#define SetSyncOutSettings          0xD8
#define SetSyncOutSettingsAck       0xD9

// CONFIGURATION MESSAGES
#define ReqConfiguration            0x0C
#define Configuration               0x0D
#define ReqPeriod                   0x04
#define ReqPeriodAck                0x05
#define SetPeriod                   0x04
#define SetPeriodAck                0x05
#define ReqOutputSkipFactor         0xD4
#define ReqOutputSkipFactorAck      0xD5
#define SetOutputSkipFactor         0xD4
#define SetOutputSkipFactorAck      0xD5
#define ReqObjectAlignment          0xE0
#define ReqObjectAlignmentAck       0xE1
#define SetObjectAlignment          0xE0
#define SetObjectAlignmentAck       0xE1
#define ReqOutputMode               0xD0
#define ReqOutputModeAck        	0xD1
#define SetOutputMode           	0xD0
#define SetOutputModeAck        	0xD1
#define ReqOutputSettings       	0xD2
#define ReqOutputSettingsAck    	0xD3
#define SetOutputSettings       	0xD2
#define SetOutputSettingsAck    	0xD3

// DATA-RELATED MESSAGES
#define ReqData                 	0x34
#define MTData                  	0x32

// XKF Filter Messages
#define ReqHeading              	0x82
#define ReqHeadingAck           	0x83
#define SetHeading              	0x82
#define SetHeadingAck           	0x83
#define ResetOrientation        	0xA4
#define ResetOrientationAck     	0xA5
#define ReqUTCTime              	0x60
#define UTCTime                 	0x61
#define ReqAvailableScenarios   	0x62
#define AvailableScenarios      	0x63
#define ReqCurrentScenario      	0x64
#define ReqCurrentScenarioAck   	0x65
#define SetCurrentScenario      	0x64
#define SetCurrentScenarioAck   	0x65
#define ReqGravityMagnitude     	0x66
#define ReqGravityMagnitudeAck  	0x67
#define SetGravityMagnitude     	0x66
#define SetGravityMagnitudeAck  	0x67
#define ReqleverArmGPS          	0x68
#define ReqleverArmGPSAck       	0x69
#define SetleverArmGPS              0x68
#define SetleverArmGPSAck           0x69
#define ReqMagneticDeclination      0x6A
#define ReqMagneticDeclinationAck   0x6B
#define SetMagneticDeclination      0x6A


