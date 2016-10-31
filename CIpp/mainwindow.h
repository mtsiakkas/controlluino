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

    struct Message {
        char* pointer;
        unsigned int length;
    };

private:
    SerialComms* sc;
    bool configFileLoaded = false;
    int numOfMotors = 6;
    Motor *motors;

    bool sendSetupMsg(void);
    bool sendMotorSetupMsg(void);
    template<class T> Message msgFromArray(T* msgIn, unsigned int dataLength, char* frontMatter, unsigned int fmLength, bool cs = true);
    void printHexMessage(char* msg, unsigned int length);
    void printHexMessage(Message msg);
    void listenForComms(void);
    bool loadConfigurationFile(const string& file);
    string selectedPort;
    int spIndex;
    RefInputDiag* rid;
    float* posRef;
    float* attRef;

    char messageHeaders[4][2] = {{(char)0xFE,(char)0xFE},  // POWER OFF
                                 {(char)0xEE,(char)0xEE},  // STOP
                                 {(char)0xFF,(char)0xFF},  // START
                                 {(char)0xDD,(char)0xDD}}; // SENSOR


    bool run = false;

private slots:

    void on_portSelectionAction_triggered();

    void on_actionSend_triggered();

    void on_btnPortOC_clicked();

    void on_btnClear_clicked();

    void refDialogReturn(bool validRef);


    void on_actionLoad_triggered();

private:
    Ui::MainWindow *ui;
};

#endif // MAINWINDOW_H
