#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <unistd.h>


MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);
//    ui->menuPorts->removeAction(ui->menuPorts->actions().constFirst());
    ui->menuPorts->clear();


    // Instantiate SerialComms
    sc = new SerialComms();

    // Look for available serial ports
    int numOfPorts = sc->discoverPorts();

    // Add ports to menu and select arduino port
    for(int i=0;i<numOfPorts;i++) {
        QAction* act = ui->menuPorts->addAction(QString::fromStdString(sc->getPortName(i)));
        act->setCheckable(true);
        connect(act,SIGNAL(triggered()),this,SLOT(on_actionPLACEHOLDER_triggered()));
    }

    spIndex = sc->getArduinoPortIndex();
    // Check if valid value returned
    spIndex = spIndex > -1 && spIndex < numOfPorts ? spIndex : 0;
    cout << "ARDUINO PORT INDEX " << spIndex << endl;
    selectedPort = ui->menuPorts->actions().at(spIndex)->text().toStdString();
    ui->menuPorts->actions().at(spIndex)->setChecked(true);

    ui->menuSend->setEnabled(false);

    // Allocate heap memory
    posRef = new float[3];
    attRef = new float[3];
    motors = new Motor[numOfMotors]; // TODO: Allow variability based on vehicle config file


}

MainWindow::~MainWindow()
{
    delete [] posRef;
    delete [] attRef;
    delete [] motors;
    delete sc;
    delete ui;
}

// TODO : HANDLE INCOMING MESSAGE
bool MainWindow::listenForComms(SerialComms::Message msg) {
    char buff[msg.length];
    sc->readMsg({buff,msg.length});

    for(unsigned int i=0;i<msg.length;i++) {
        if(!(*(msg.pointer+i)==*(buff+i))) {
            printHexMessage(msg);
            printHexMessage(buff,msg.length);
            return false;
        }
    }
    return true;
}

void MainWindow::on_btnPortOC_clicked()
{
    if(!sc->portReady()) {
        cout << "Attempting to open " << selectedPort << endl;
        sc->openPort(spIndex);
        if(sc->portReady()) {
            ui->btnPortOC->setText("Close port");
            ui->menuSend->setEnabled(true);
            for(QAction* a : ui->menuSend->actions())
                a->setEnabled(true);

            if(listenForComms({incomingMessageHeaders[MESSAGE_IN::READY],2})) {
                cout << "RECEIVED HELLO FROM ARDUINO." << endl;
                ui->plainTextEdit->appendPlainText("CONNECTED TO ARDUINO!");
            }
        }
    } else {
        sc->closePort();
        ui->btnPortOC->setText("Open port");
        ui->menuSend->setEnabled(false);
        for(QAction* a : ui->menuSend->actions())
            a->setEnabled(false);
    }
}

void MainWindow::on_btnClear_clicked()
{
    ui->plainTextEdit->clear();
}

// Print hex values of char array
void MainWindow::printHexMessage(char* msg,unsigned int length) {
    for(unsigned int i=0;i<length;i++)
        cout << hex << (int)(*(msg+i)&0xFF) << " ";
    cout << endl;
}

void MainWindow::printHexMessage(SerialComms::Message msg) {
    for(unsigned int i=0;i<msg.length;i++)
        cout << hex << (int)(*(msg.pointer+i)&0xFF) << " ";
    cout << endl;
}

// Function template to generate message from data array
template<class T> SerialComms::Message MainWindow::msgFromArray(T* msgIn, unsigned int dataLength, char* frontMatter, unsigned int fmLength, bool cs) {
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

    SerialComms::Message msg = {msgTmp, totalMsgLength};
    return msg;
}

void MainWindow::on_actionLoad_triggered()
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

            cout << "LOADING VEHICLE CONFIGURATION FILE" << endl;
            ui->plainTextEdit->appendPlainText("LOADING VEHICLE CONFIGURATION FILE");
            string line;
            int i = 0;
            while(getline(inputFile,line) && i < numOfMotors) {
                int cp = 0;
                int delimiters[4] = {0,0,0,0};
                for(int j=0;j<4;j++) {
                    delimiters[j] = line.find(',',cp);
                    cp = delimiters[j]+1;
                }

                bool type;
                float offset;
                float gradient;
                unsigned int pwmMin;
                unsigned int pwmMax;

                try {
                    type = !(line.substr(0,delimiters[0]) == "0");
                    offset =  stof(line.substr(delimiters[0]+1,delimiters[1]-delimiters[0]));
                    gradient =  stof(line.substr(delimiters[1]+1,delimiters[2]-delimiters[1]));
                    pwmMin = stoi(line.substr(delimiters[2]+1,delimiters[3]-delimiters[2]));
                    pwmMax = stoi(line.substr(delimiters[3]+1));
                } catch(...) {
                    ui->plainTextEdit->appendPlainText("Unable to parse configuration file.");
                    cout << "Unable to parse configuration file." << endl;
                    return false;
                }
                Motor m = {type, offset, gradient, pwmMin, pwmMax};
                *(motors+i) = m;

                stringstream ss;
                ss << dec << i << ": " << type << " " << offset << " " << gradient << " " << pwmMin << " " << pwmMax;
                cout << ss.str() << endl;
                ui->plainTextEdit->appendPlainText(QString::fromStdString(ss.str()));
                i++;
            }
            configFileLoaded = true;
             ui->plainTextEdit->appendPlainText("DONE.");
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

void MainWindow::on_actionSETUP_triggered()
{

}



void MainWindow::on_actionMOTOR_SETUP_triggered()
{
    if(!configFileLoaded) {
        cout << "CONFIGURATION FILE NOT LOADED" << endl;
        ui->plainTextEdit->appendPlainText("CONFIGURATION FILE NOT LOADED.");
        return;
    } else {
        sendMotorSetupMsg();
    }
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


    SerialComms::Message ackMsg = {incomingMessageHeaders[4],2};

    {

        cout << "MOTOR MSG (TYPES): ";
        ui->plainTextEdit->appendPlainText("SENDING MOTOR PARAMS/TYPES...");
        char frontMatter[] = {0x33,0x00};
        SerialComms::Message msg =  msgFromArray(motorTypes, numOfMotors, frontMatter, 2);
        printHexMessage(msg);

        sc->sendMsg(msg);

        if(!listenForComms(ackMsg)) {
            cout << "FAILED TO SET MOTOR PARAMS." << endl;
            ui->plainTextEdit->appendPlainText("FAILED TO SET MOTOR PARAMS.");
            return false;
        }
    }
    {
        cout << "MOTOR MSG (OFFSETS): ";
        ui->plainTextEdit->appendPlainText("SENDING MOTOR PARAMS/OFFSETS...");
        char frontMatter[] = {0x33,0x01};
        SerialComms::Message msg =  msgFromArray(motorOffsets, numOfMotors, frontMatter, 2);
        printHexMessage(msg);

        sc->sendMsg(msg);
        if(!listenForComms(ackMsg)) {
            cout << "FAILED TO SET MOTOR PARAMS." << endl;
            ui->plainTextEdit->appendPlainText("FAILED TO SET MOTOR PARAMS.");
            return false;
        }
    }
    {
        cout << "MOTOR MSG (GRADIENTS): ";
        ui->plainTextEdit->appendPlainText("SENDING MOTOR PARAMS/GRADIENTS...");
        char frontMatter[] = {0x33,0x02};
        SerialComms::Message msg =  msgFromArray(motorGradients, numOfMotors, frontMatter, 2);
        printHexMessage(msg);

        sc->sendMsg(msg);
        if(!listenForComms(ackMsg)) {
            cout << "FAILED TO SET MOTOR PARAMS." << endl;
            ui->plainTextEdit->appendPlainText("FAILED TO SET MOTOR PARAMS.");
            return false;
        }
    }
    {
        cout << "MOTOR MSG (PWMMIN): ";
        ui->plainTextEdit->appendPlainText("SENDING MOTOR PARAMS/PWMMIN...");
        char frontMatter[] = {0x33,0x03};
        SerialComms::Message msg =  msgFromArray(motorPwmMin, numOfMotors, frontMatter, 2);
        printHexMessage(msg);

        sc->sendMsg(msg);
        if(!listenForComms(ackMsg)) {
            cout << "FAILED TO SET MOTOR PARAMS." << endl;
            ui->plainTextEdit->appendPlainText("FAILED TO SET MOTOR PARAMS.");
            return false;
        }
    }
    {
        cout << "MOTOR MSG (PWMMAX): ";
        ui->plainTextEdit->appendPlainText("SENDING MOTOR PARAMS/PWMMAX...");
        char frontMatter[] = {0x33,0x04};
        SerialComms::Message msg =  msgFromArray(motorPwmMax, numOfMotors, frontMatter, 2);
        printHexMessage(msg);

        sc->sendMsg(msg);
        if(!listenForComms(ackMsg)) {
            cout << "FAILED TO SET MOTOR PARAMS." << endl;
            ui->plainTextEdit->appendPlainText("FAILED TO SET MOTOR PARAMS.");
            return false;
        }
    }
    return true;
}

void MainWindow::on_actionREFERENCE_triggered()
{
    rid = new RefInputDiag(posRef,attRef);
    connect(rid,SIGNAL(refDialogReturn(bool)),this,SLOT(refDialogReturn(bool)));
    rid->show();
}

void MainWindow::refDialogReturn(bool validRef)
{
    if(validRef) {
        float* tmp = new float[6];

        cout << "Pos ref: ";
        for(int i=0;i<3;i++) {
            tmp[i] = *(posRef+i);
            cout << *(posRef+i) << " ";
        }
        cout << endl;
        cout << "Att ref: ";
        for(int i=0;i<3;i++) {
            tmp[i+3] = *(attRef+i);
            cout << *(attRef+i) << " ";
        }
        cout << endl;
        char frontMatter = 0xCC;
        SerialComms::Message msg = msgFromArray(tmp, 6, &frontMatter, 1);
        sc->sendMsg(msg);

    } else {
        cout << "No valid ref" << endl;
    }
}

void MainWindow::on_actionSENSOR_INIT_triggered()
{
    sc->sendMsg({outgoingMessageHeaders[MESSAGE_OUT::SENSOR_INIT],2});
    if(listenForComms({incomingMessageHeaders[MESSAGE_IN::SENSOR_INIT],2}))
        cout << "SENSOR INIT ACK" << endl;
}

void MainWindow::on_actionSTART_triggered()
{
    sc->sendMsg({outgoingMessageHeaders[MESSAGE_OUT::START],2});
}

void MainWindow::on_actionSTOP_triggered()
{
    sc->sendMsg({outgoingMessageHeaders[MESSAGE_OUT::STOP],2});
}

void MainWindow::on_actionPOWER_OFF_triggered()
{
    sc->sendMsg({outgoingMessageHeaders[MESSAGE_OUT::POWER_OFF],2});
    if(listenForComms({incomingMessageHeaders[MESSAGE_IN::POWER_OFF],2}))
        cout << "ARDUINO POWERING OFF" << endl;
}


void MainWindow::on_actionPLACEHOLDER_triggered()
{
    // Which action emitted signal?
    QAction* a = qobject_cast<QAction*>(sender());
    spIndex = ui->menuPorts->actions().indexOf(a);

    cout << "Selected port: " << spIndex << " " << selectedPort << endl;

    selectedPort = a->text().toStdString();

    for(QAction* act : ui->menuPorts->actions()) {
        act->setChecked(false);
    }
    a->setChecked(true);
}
