#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <unistd.h>


MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    sc = new SerialComms();

    int numOfPorts = sc->discoverPorts();
    for(int i=0;i<numOfPorts;i++)
        ui->cmbPorts->addItem(QString::fromStdString(sc->getPortName(i)));

    ui->cmbPorts->setCurrentIndex(sc->getArduinoPortIndex());

    QString str[7] = {"SETUP","POWER_OFF","STOP","START","SENSOR","REFERENCE","MOTOR_SETUP"};
    for(auto s : str)
        ui->cmbMsgSelect->addItem(s);
    loadConfigurationFile("/Users/mihalistsiakkas/Dropbox/Hardware Projects/Arduino Project/controlluino/CIpp/test_config.cvc");
}

MainWindow::~MainWindow()
{

    if(configFileLoaded) {
        delete [] motors;
    }
    delete sc;
    delete ui;
}

void MainWindow::on_btnSend_clicked()
{
    MESSAGE_TYPE reqMsg = (MESSAGE_TYPE)ui->cmbMsgSelect->currentIndex();
    if(reqMsg == MOTOR_SETUP) {
        if(!configFileLoaded) {
            cout << "CONFIGURATION FILE NOT LOADED" << endl;
            return;
        } else {
            sendMotorSetupMsg();
        }
    } else if(reqMsg == REFERENCE) {
        cout << "NOT YET IMPLEMENTED" << endl;
        return;
    } else {

        char *msg;
        msg = new char[10];

        int len = constructMsg((MESSAGE_TYPE)ui->cmbMsgSelect->currentIndex(),msg);
        if(len>0) {
            cout << "Sending: ";
            for(int i=0;i<len;i++)
                cout << hex << (int)(*(msg+i)&0xFF) << " ";
            cout << endl;

            sc->sendMsg(msg,len);
            char buff[10];
            sc->readMsg(buff,2);
            stringstream ss;
            for(int i=0; i<2; ++i)
                ss << hex << (int)(*(buff+i)&0xFF) << " ";
            string str =  ss.str();
            ui->plainTextEdit->appendPlainText("Received: " + QString::fromStdString(str));
        } else {
            cout << "Unable to construct message!" << endl;
        }
    }
}

void MainWindow::on_btnPortOC_clicked()
{
    if(!sc->portReady()) {
        cout << "Attempting to open " << ui->cmbPorts->currentText().toStdString() << endl;
        sc->openPort(ui->cmbPorts->currentIndex());
        if(sc->portReady()) {
            ui->btnPortOC->setText("Close port");
            ui->btnSend->setEnabled(true);
            ui->cmbMsgSelect->setEnabled(true);
        }
    } else {
        sc->closePort();
        ui->btnPortOC->setText("Open port");
        ui->btnSend->setEnabled(false);
        ui->cmbMsgSelect->setEnabled(false);
    }
}

void MainWindow::on_btnClear_clicked()
{
    ui->plainTextEdit->clear();
}

void MainWindow::on_cmbPorts_currentIndexChanged(const QString &arg1)
{
    ui->plainTextEdit->appendPlainText("Selected port: " + arg1);
}


void MainWindow::on_btnLoadConfig_clicked()
{
    QStringList filters;
    filters << "Vehicle configuration files (*.cvc)"
            << "Any files (*)";
    QFileDialog fd(this);
    fd.setNameFilters(filters);
    if(fd.exec()) {
        string filename = fd.selectedFiles().first().toStdString();
        loadConfigurationFile(filename);
    }
}

bool MainWindow::loadConfigurationFile(const string &file)
{
    ifstream inputFile;
    inputFile.open(file,ios::in);


    if(inputFile.is_open()) {
        string header;
        getline(inputFile,header);
        if(header == "** VEHICLE CONFIGURATION FILE **") {
            motors = new Motor[numOfMotors];

            cout << "LOADING VEHICLE CONFIGURATION FILE" << endl;

            string line;
            int i = 0;
            while(getline(inputFile,line) && i < numOfMotors) {
                int cp = 0;
                int del[4] = {0,0,0,0};
                for(int j=0;j<4;j++) {
                    del[j] = line.find(',',cp);
                    cp = del[j]+1;
                }

                bool type;
                float offset;
                float gradient;
                int pwmMin;
                int pwmMax;

                try {
                    type = !(line.substr(0,del[0]) == "0");
                    offset =  stof(line.substr(del[0]+1,del[1]-del[0]));
                    gradient =  stof(line.substr(del[1]+1,del[2]-del[1]));
                    pwmMin = stoi(line.substr(del[2]+1,del[3]-del[2]));
                    pwmMax = stoi(line.substr(del[3]+1));
                } catch(...) {
                    cout << "Unable to parse configuration file." << endl;
                    return false;
                }
                Motor m;
                m.type = type;
                m.offset = offset;
                m.gradient = gradient;
                m.pwmMin = pwmMin;
                m.pwmMax = pwmMax;

                *(motors+i) = m;

                cout << dec << i << ": " << type << " " << offset << " " << gradient << " " << pwmMin << " " << pwmMax << endl;
                i++;
            }
            configFileLoaded = true;
            cout << "DONE" << endl;
        } else {
            cout << "Not a valid vehicle configuration file!" << endl;
            return false;
        }

        inputFile.close();
    } else {
        cout << "Cannot open file!" << endl;
        return false;
    }
    return true;

}

bool MainWindow::sendMotorSetupMsg(void) {

    bool motorTypes[numOfMotors];
    float motorOffsets[numOfMotors];
    float motorGradients[numOfMotors];
    int motorPwmMin[numOfMotors];
    int motorPwmMax[numOfMotors];
    for(int i=0;i<numOfMotors;i++) {
        motorTypes[i] = motors[i].type;
        motorOffsets[i] = motors[i].offset;
        motorGradients[i] = motors[i].gradient;
        motorPwmMin[i] = motors[i].pwmMin;
        motorPwmMax[i] = motors[i].pwmMax;
    }

    {
        char frontMatter[] = {0x33,0x00};
        Message msg =  msgFromArray(motorTypes, numOfMotors, frontMatter, 2);
        cout << "MOTOR MSG (TYPES): ";
        printHexMessage(msg);

        sc->sendMsg(msg.pointer,msg.length);

        char buff[2];
        sc->readMsg(buff,2);
        if(!(buff[0]==0x3F && buff[0]==0x3F)) {
            cout << "FAILED TO SET MOTOR PARAMS." << endl;
            return false;
        }
    }
    {
        cout << "MOTOR MSG (OFFSETS): ";
        char frontMatter[] = {0x33,0x01};
        Message msg =  msgFromArray(motorOffsets, numOfMotors, frontMatter, 2);
        printHexMessage(msg);

        sc->sendMsg(msg.pointer,msg.length);

        char buff[2];
        sc->readMsg(buff,2);
        if(!(buff[0]==0x3F && buff[0]==0x3F)) {
            cout << "FAILED TO SET MOTOR PARAMS." << endl;
            return false;
        }
    }
    {
        cout << "MOTOR MSG (GRADIENTS): ";
        char frontMatter[] = {0x33,0x02};
        Message msg =  msgFromArray(motorGradients, numOfMotors, frontMatter, 2);
        printHexMessage(msg);

        sc->sendMsg(msg.pointer,msg.length);

        char buff[2];
        sc->readMsg(buff,2);
        if(!(buff[0]==0x3F && buff[0]==0x3F)) {
            cout << "FAILED TO SET MOTOR PARAMS." << endl;
            return false;
        }
    }
    {
        cout << "MOTOR MSG (PWMMIN): ";
        char frontMatter[] = {0x33,0x03};
        Message msg =  msgFromArray(motorPwmMin, numOfMotors, frontMatter, 2);
        printHexMessage(msg);

        sc->sendMsg(msg.pointer,msg.length);

        char buff[2];
        sc->readMsg(buff,2);
        if(!(buff[0]==0x3F && buff[0]==0x3F)) {
            cout << "FAILED TO SET MOTOR PARAMS." << endl;
            return false;
        }
    }
    {
        cout << "MOTOR MSG (PWMMAX): ";
        char frontMatter[] = {0x33,0x04};
        Message msg =  msgFromArray(motorPwmMax, numOfMotors, frontMatter, 2);
        printHexMessage(msg);

        sc->sendMsg(msg.pointer,msg.length);

        char buff[2];
        sc->readMsg(buff,2);
        if(!(buff[0]==0x3F && buff[0]==0x3F)) {
            cout << "FAILED TO SET MOTOR PARAMS." << endl;
            return false;
        }
    }
    return true;
}

// Print hex values of char array
void MainWindow::printHexMessage(char* msg,unsigned int length) {
    for(unsigned int i=0;i<length;i++)
        cout << hex << (int)(*(msg+i)&0xFF) << " ";
    cout << endl;
}

void MainWindow::printHexMessage(Message msg) {
    for(unsigned int i=0;i<msg.length;i++)
        cout << hex << (int)(*(msg.pointer+i)&0xFF) << " ";
    cout << endl;
}

template<class T> MainWindow::Message MainWindow::msgFromArray(T* msgIn, unsigned int dataLength, char* frontMatter, unsigned int fmLength, bool cs) {
    char*  ptrTmp = (char*)msgIn;                                       // Pointer type casting to read data from memory as chars
    unsigned int msgLength = dataLength*sizeof(T)/sizeof(char);         // Number of chars in data
    unsigned int totalMsgLength = fmLength + msgLength +(cs ? 1 : 0);
    char* msgTmp;
    msgTmp = new char[totalMsgLength];
    long sum = 0;

    // Add front matter to message
    for(unsigned int i=0;i<fmLength;i++) {
        *(msgTmp+i) = *(frontMatter+i);
        sum += *(frontMatter+i);
    }

    // Add data to message
    for(unsigned int i=0;i<msgLength;i++) {
        sum += *(ptrTmp+i);
        *(msgTmp+i+fmLength) = *(ptrTmp+i);
    }

    // Calculate and add checksum
    if(cs) *(msgTmp+msgLength+fmLength) = 0x100-sum%0x100;

    Message msg;
    msg.pointer = msgTmp;
    msg.length = totalMsgLength;
    return msg;
}

int MainWindow::constructMsg(MESSAGE_TYPE type,char* msg) {

    /*
     * TODO: RUNTIME MESSAGES FOR REF AND SETUP
     */

    // EXCEPT FOR MOTOR SETUP

    int msgLength = 0;
    char* msgTmp;
    switch(type) {
    case SETUP: {
        msgLength = 7;
        msgTmp = "\x22\x01\x00\x14\x00\x02\xE9";
        break;
    }
    case POWER_OFF: {
        msgLength = 2;
        msgTmp = "\xFE\xFE";
        break;
    }
    case START: {
        msgLength = 2;
        msgTmp = "\xEE\xEE";
        break;
    }
    case STOP: {
        msgLength = 2;
        msgTmp = "\xFF\xFF";
    }
    case SENSOR: {
        msgLength = 2;
        msgTmp = "\xDD\xDD";
        break;
    }
    case REFERENCE: {
        msgLength = 2;
        msgTmp = "\xCC";
        // TODO
        return 0;
        break;
    }
    default:
        break;
    }
    memcpy(msg,msgTmp,msgLength);
    return msgLength;
}
