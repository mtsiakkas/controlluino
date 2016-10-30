#ifndef SERIALCOMMS_H
#define SERIALCOMMS_H

#include <iostream>
#include <QSerialPort>
#include <QSerialPortInfo>
#include <string>
//#include "mainwindow.h"


using namespace std;
class SerialComms {
public:
    SerialComms();
    ~SerialComms();

    bool sendMsg(char *, int len2);
    int readMsg(char *, int len2);
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
    int arduinoPortIndex;
};
#endif // SERIALCOMMS_H
