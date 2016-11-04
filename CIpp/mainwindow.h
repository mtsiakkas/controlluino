#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <iostream>
#include <string>
#include <sstream>
#include <fstream>
#include "serialcomms.h"
#include "refinputdiag.h"
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

private:
    SerialComms* sc;
    bool configFileLoaded = false;
    int numOfMotors = 6;
    Motor *motors;

    bool sendMotorSetupMsg(void);
    template<class T> SerialComms::Message msgFromArray(T* msgIn, unsigned int dataLength, char* frontMatter, unsigned int fmLength, bool cs = true);
    void printHexMessage(char* msg, unsigned int length);
    void printHexMessage(SerialComms::Message msg);
    bool listenForComms(SerialComms::Message msg);
    bool loadConfigurationFile(const string& file);
    string selectedPort;
    int spIndex;
    RefInputDiag* rid;
    float* posRef;
    float* attRef;

    struct MESSAGE_OUT {
        enum {
            POWER_OFF,
            SENSOR_INIT,
            STOP,
            START,
            MOTORS_INIT,
            RESET
        };
    };

    struct MESSAGE_IN {
        enum {
            READY,
            SENSOR_INIT,
            SETUP,
            MOTORS_INIT,
            MOTOR_PARAMS,
            POWER_OFF
        };
    };

    char outgoingMessageHeaders[6][2] = {{(char)0xFE,(char)0xFE},  // POWER OFF
                                         {(char)0xEE,(char)0xEE},  // SENSOR
                                         {(char)0xFF,(char)0xFF},  // STOP
                                         {(char)0xDD,(char)0xDD},  // START/RUN
                                         {(char)0xBB,(char)0xBB},  // MOTORS_INIT
                                         {(char)0xAA,(char)0xAA},};// RESET

    char incomingMessageHeaders[6][2] = {{(char)0xFF,(char)0xFF},  // READY
                                         {(char)0xEF,(char)0xEF},  // SENSOR
                                         {(char)0x2F,(char)0x2F},  // SETUP
                                         {(char)0xDF,(char)0xDF},  // MOTORS INIT
                                         {(char)0x3F,(char)0x3F},  // MOTOR PARAMS
                                         {(char)0xCF,(char)0xCF}}; // POWER OFF


    bool run = false;

private slots:

    void on_portSelectionAction_triggered();
    void on_btnPortOC_clicked();
    void on_btnClear_clicked();
    void refDialogReturn(bool validRef);
    void on_actionLoad_triggered();
    void on_actionSETUP_triggered();
    void on_actionMOTOR_SETUP_triggered();
    void on_actionREFERENCE_triggered();
    void on_actionSENSOR_INIT_triggered();
    void on_actionSTART_triggered();
    void on_actionSTOP_triggered();
    void on_actionPOWER_OFF_triggered();

private:
    Ui::MainWindow *ui;
};

#endif // MAINWINDOW_H
