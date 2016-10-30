#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <iostream>
#include <string>
#include <sstream>
#include <fstream>
#include "serialcomms.h"
#include <QFileDialog>

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

    struct Motor {
        bool type;
        float offset;
        float gradient;
        unsigned int pwmMin;
        unsigned int pwmMax;
    };

    struct Message {
        char* pointer;
        unsigned int length;
    };

    enum MESSAGE_TYPE {
        SETUP = 0,
        POWER_OFF = 1,
        STOP = 2,
        START = 3,
        SENSOR = 4,
        REFERENCE = 5,
        MOTOR_SETUP = 6
    };


private:
    SerialComms* sc;
    bool configFileLoaded = false;
    int numOfMotors = 6;
    Motor *motors;

    int constructMsg(MESSAGE_TYPE type,char* msg);
    bool sendMotorSetupMsg(void);
    template<class T> Message msgFromArray(T* msgIn, unsigned int dataLength, char* frontMatter, unsigned int fmLength, bool cs = true);
    void printHexMessage(char* msg, unsigned int length);
    void printHexMessage(Message msg);

private slots:

    void on_btnSend_clicked();

    void on_btnPortOC_clicked();

    void on_btnClear_clicked();

    void on_cmbPorts_currentIndexChanged(const QString &arg1);

    void on_btnLoadConfig_clicked();

    bool loadConfigurationFile(const string& file);

private:
    Ui::MainWindow *ui;
};

#endif // MAINWINDOW_H
