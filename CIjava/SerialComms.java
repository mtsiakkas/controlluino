package controlluinointerface;

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
                } catch (Exception ee) {
                    System.out.println("Port " + cpid.getName() + " is not available");
                }
            }
        }

        String[] portNames = ports.toArray(new String[ports.size()]);
        return portNames;
    }

    public static boolean connect(String portName, int baudrate) {
        try {
            sessionLog.addSessionLog("Attempting to connect to " + portName + " at " + baudrate + "BAUD...\n");
            cpid = CommPortIdentifier.getPortIdentifier(portName);
            if (!cpid.isCurrentlyOwned()) {
                commPort = cpid.open("ControlluinoInterface", 500);
                if (commPort instanceof SerialPort) {
                    serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                    in = serialPort.getInputStream();
                    out = serialPort.getOutputStream();


                    (new Thread(new SerialReader(in), "SerialReader")).start();
                    sessionLog.addSessionLog("Connected...\n");
                } else {
                    sessionLog.addSessionLog("Unable to connect...\n");
                    return false;
                }
            }
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException | IOException ex) {
            sessionLog.addSessionLog("Unable to connect...\n");
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
        sessionLog.addSessionLog("Disconnected from " + serialPort.getName() + "...\n");
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
                                while (this.in.available() < 1);
                                int len = this.in.read();
                                byte[] data = new byte[len];
                                while (this.in.available() < len);
                                for (int i = 0; i < len; i++) {
                                    data[i] = (byte) this.in.read();
                                }

                                while (this.in.available() < 2);
                                int vbat = this.in.read();
                                int cs = this.in.read();
                                
                                ControlluinoInterface.newMessageReceived(data, vbat, cs);
                                reading = false;
                            } else if (c == 0xFF) {
                                reading = true;
                                while (this.in.available() < 2);
                                int c1 = this.in.read();
                                int c2 = this.in.read();
                                if (c1 == 0xFF && c2 == 0xFF) {
                                    sessionLog.addSessionLog("Received ready message...\n");
                                    mainGUI.receivedReadyMsg();
                                }
                                reading = false;
                            } else if(c == 0xEF) {
                                reading = true;
                                while (this.in.available() < 2);
                                int c1 = this.in.read();
                                int c2 = this.in.read();
                                if (c1 == 0xEF && c2 == 0xEF) {
                                    mainGUI.sensorInitialised();
                                }
                                reading = false;
                            } else if(c == 0xDF) {
                                reading = true;
                                while (this.in.available() < 2);
                                int c1 = this.in.read();
                                int c2 = this.in.read();
                                if ((c1 == 0xDF) && (c2 == 0xDF)) {
                                    mainGUI.motorsInitialised();
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
