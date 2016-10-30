#include "serialcomms.h"


SerialComms::SerialComms() {
}

SerialComms::~SerialComms() {
    if (port!=nullptr) {
        closePort();
        delete port;
    }
}

int SerialComms::discoverPorts() {
    infos = QSerialPortInfo::availablePorts();
    cout << "Found " << infos.length() << " serial ports." << endl;
    numOfPorts = infos.length();
    cout << "Populating serial port list..." << endl;
    int i = 0;
    for(QSerialPortInfo &p : infos) {
        int VID = p.hasVendorIdentifier() ? p.vendorIdentifier() : 0000;
        int PID = p.hasProductIdentifier() ? p.productIdentifier() : 0000;
        cout << i++ << ": " << p.portName().toStdString() << "\t" << hex << VID << ":" << PID;
        if(VID == 0x2341) {
            if(PID == 0x003d) {
                cout << " (Arduino DUE)";
                arduinoPortIndex = i-1;
            } else {
                cout << " (Arduino)";
            }
        }
        cout << endl;
    }
    return numOfPorts;
}

bool SerialComms::openPort(int sp) {
    cout << "Opening  port " << infos[sp].portName().toStdString() << endl;
    if(port == nullptr) {
        port = new QSerialPort();
        port->setPort(infos[sp]);
        cout << "Opening port: " << port->portName().toStdString() << endl;
        if(port->open(QIODevice::ReadWrite)) {
            port->setBaudRate(QSerialPort::Baud115200);
            port->setParity(QSerialPort::NoParity);
            port->setDataBits(QSerialPort::Data8);
            port->setStopBits(QSerialPort::OneStop);
            port->setFlowControl(QSerialPort::NoFlowControl);
            cout << "Port opened successfully" << endl;
            return true;
        } else {
            cout << "Error: Cannot open port " << port->portName().toStdString() << endl;
            port = nullptr;
            return false;
        }
    } else {
        return port->isOpen();
    }
}

bool SerialComms::closePort() {
    if(portReady()) {
        cout << "Closing port" << endl;
        port->close();
        port = nullptr;
    } else {
        cout << "Port already closed" << endl;
    }
    return true;
}

bool SerialComms::sendMsg(char* msg, int len) {
    if(portReady()) {
        port->clear();
        int w = port->write(msg,len);

        port->flush();
        return w>-1? true : false;
    } else {
        return false;
    }
}

int SerialComms::readMsg(char *buff, int len) {
    if(portReady()) {
        int wCount = 0;
        int bytesRead = 0;

        QByteArray resp;
        while (bytesRead<len) {
            port->waitForReadyRead(10);
            if(port->bytesAvailable()) bytesRead += port->bytesAvailable();
            resp.append(port->readAll());
            if(wCount>100 && bytesRead<len) {
                return -1;
            }
            wCount++;
        }
        port->clear();

        cout << "Received: ";
        for(int i=0;i<resp.length();i++) {
            *(buff + i) = resp.at(i);
            cout << hex << (int)(resp.at(i)&0xFF) << " ";
        }
        cout << endl;
        return bytesRead;
    } else {
        return -2;
    }
}

bool SerialComms::portReady() {
    if(port != nullptr) {
        if(port->isOpen()) {
            return true;
        } else {
            return false;
        }
    } else {
        return false;
    }
}

string SerialComms::getPortName(int index) {
    return infos[index].portName().toStdString();
}


