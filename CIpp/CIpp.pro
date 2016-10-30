#-------------------------------------------------
#
# Project created by QtCreator 2016-10-22T07:30:32
#
#-------------------------------------------------

QT       += core gui serialport

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = ControlluinoInterfacepp
TEMPLATE = app


SOURCES += main.cpp\
        mainwindow.cpp \
    serialcomms.cpp

HEADERS  += mainwindow.h \
    serialcomms.h

FORMS    += mainwindow.ui

DISTFILES += \
    test_config.cvc
