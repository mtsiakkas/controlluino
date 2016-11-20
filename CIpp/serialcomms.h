#ifndef SERIALCOMMS_H
#define SERIALCOMMS_H

#include <iostream>
#include <QSerialPort>
#include <QSerialPortInfo>
#include <string>



using namespace std;

class SerialComms {
public:
    SerialComms();
    ~SerialComms();

    struct Message {
        char* pointer;
        unsigned int length;
    };

    bool sendMsg(Message msg);
    int readMsg(Message msg);
    int discoverPorts();
    int getArduinoPortIndex() {return arduinoPortIndex>-1 ? arduinoPortIndex : -1;}

    string getPortName(int);
    bool openPort(int);
    bool closePort(void);
    bool portReady(void);


private:
    QSerialPort *port = nullptr;
    QList<QSerialPortInfo> infos;
    int numOfPorts;
    int arduinoPortIndex = -1;

};

#endif // SERIALCOMMS_H
