package controlluinosimple;

import gnu.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SerialComms {

    static CommPort commPort;
    static CommPortIdentifier cpid;
    static SerialPort serialPort;
    static InputStream in;
    static OutputStream out;

    public static String[] getAvailableCommPorts() {
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> ports = new ArrayList<>();
        while (portEnum.hasMoreElements()) {
            cpid = portEnum.nextElement();
            if (cpid.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                try {
                    CommPort pn = cpid.open("COMTEST", 50);
                    pn.close();
                    
                    ports.add(cpid.getName());
                    mainGui.appendToLog("Testing Port " + cpid.getName() + "\n");
                } catch (Exception ee) {
                    mainGui.appendToLog("Port " + cpid.getName() + " is not available\n");
                }
            }
        }
        if (ports.isEmpty()) {
            ports.add("N/A");
        }

        String[] portNames = ports.toArray(new String[ports.size()]);
        return portNames;
    }

    public static boolean connect(String portName, int baudrate) {
        try {
            mainGui.appendToLog("Attempting to connect to port " + portName + " at " + baudrate + "BAUD\n");
            cpid = CommPortIdentifier.getPortIdentifier(portName);
            if (!cpid.isCurrentlyOwned()) {
                commPort = cpid.open("ControlluinoInterface", 500);
                if (commPort instanceof SerialPort) {
                    serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                    in = serialPort.getInputStream();
                    out = serialPort.getOutputStream();


                    (new Thread(new SerialReader(in), "SerialReader")).start();
                    mainGui.appendToLog("Connected\n");
                } else {
                    mainGui.appendToLog("Not a serial port\n");
                    return false;
                }
            }
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException | IOException ex) {
            mainGui.appendToLog("Unable to connect\n");
            return false;
        }
        return true;
    }

    static int getBytesInBuffer() {
        try {
            return in.available();
        } catch (IOException ex) {
            return 0;
        }
    }

    public static boolean disconnect() {
        if (serialPort != null) {
            new Thread() {
                @Override
                public void run() {

                    try {
                        in.close();
                        out.close();
                        serialPort.close();
                    } catch (IOException ex) {
                    }
                }
            }.start();
        }
        return true;
    }

    public static class SerialReader implements Runnable {

        InputStream in;

        public SerialReader(InputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            boolean reading = false;
            while (true) {
                try {
                    if (this.in.available() > 0) {
                        int c = this.in.read();
                        if (!reading) {
                            if (c == 0xFE) {
                                reading = true;
                                while (this.in.available() < 2);
                                int vbat = this.in.read();
                                int cs = this.in.read();
                                
                                if(((vbat+cs)&0xFF)==0) {
                                    mainGui.newMessage(vbat);
                                }
                                reading = false;
                            } else if (c == 0xFF) {
                                reading = true;
                                while (this.in.available() < 1);
                                int c1 = this.in.read();
                                if (c1 == 0xFF) {
                                    mainGui.appendToLog("Arduino ready\n");
                                    mainGui.receivedReadyMsg();
                                }
                                reading = false;
                            } else if (c == 0xEF) {
                                reading = true;
                                while (this.in.available() < 1);
                                int c1 = this.in.read();
                                if (c1 == 0xEF) {
                                    mainGui.appendToLog("Sensor Initialized\n");
                                    mainGui.sensorInitialized();
                                }
                                reading = false;
                            } else if (c == 0xDF) {
                                reading = true;
                                while (this.in.available() < 1);
                                int c1 = this.in.read();
                                if (c1 == 0xDF) {
                                    mainGui.appendToLog("Motors initialized\n");
                                    mainGui.motorsInitialized();
                                }
                                reading = false;
                            } else if (c == 0x2F) {
                                reading = true;
                                while (this.in.available() < 1);
                                int c1 = this.in.read();
                                if (c1 == 0x2F) {
                                    mainGui.appendToLog("Arduino setup complete\n");
                                    mainGui.arduinoSetup();
                                }
                                reading = false;
                            } else if (c == 0x3F) {
                                reading = true;
                                while (this.in.available() < 1);
                                int c1 = this.in.read();
                                if (c1 == 0x3F) {
                                    mainGui.motorParametersUpdated();
                                }
                                reading = false;
                            } else if (c == 0xCF) {
                                reading = true;
                                while (this.in.available() < 1);
                                int c1 = this.in.read();
                                if (c1 == 0xCF) {
                                    mainGui.appendToLog("Power off\n");
                                }
                                reading = false;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }
    }

    public static void send(byte[] buffer) {
        try {
            out.write(buffer);
        } catch (Exception ex) {
            Logger.getLogger(SerialComms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
