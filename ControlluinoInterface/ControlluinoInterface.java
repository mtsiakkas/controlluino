package controlluinointerface;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControlluinoInterface {

    final static int expectedMessageLength = 34;
    static int missed = 0;
    static int totalMessagesReceived = 0;
    static boolean broken = false;
    static ArrayList<Integer> incomingDataStorage;
    static BufferedWriter logger = null;
    static int logSkipFactor = 0;
    static int logCounter = 0;
    static boolean hudOpen = false;
    static boolean mainOpen = false;
    static boolean sessionLogOpen = false;
    static mainGUI gui;
    static hudInfo hud;
    static sessionLog log;
    static int screenW = 0;
    static int screenH = 0;
    static int screenOX = 0;
    static int screenOY = 0;

    public static void main(String[] args) {

        incomingDataStorage = new ArrayList<>();
        gui = new mainGUI();
        gui.setVisible(true);
        mainOpen = true;
        hud = new hudInfo();
        hud.setVisible(true);
        hudOpen = true;
        log = new sessionLog();
        log.setVisible(true);
        sessionLogOpen = true;
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();  
        Rectangle gebounds = ge.getMaximumWindowBounds();
        
        screenW = gebounds.width;
        screenH = gebounds.height;
        
        screenOX = gebounds.x;
        screenOY = gebounds.y;
        
        int hudW = hud.getWidth();
        int logH = log.getHeight();

        gui.setSize(screenW - hudW, screenH - 300);
        gui.setLocation(0, 0);
                      
        log.setLocation(screenOX, screenOY + gui.getHeight());
        log.setSize(gui.getWidth(), 300);
        hud.setLocation(screenOX + gui.getWidth(), screenOY);
        gui.toFront();
    }

    public static int[] getMessageStats() {
        return new int[]{totalMessagesReceived, SerialComms.getBytesInBuffer(), missed};
    }

    public static void newMessageReceived(byte[] data, int vbat, int csin) {
        totalMessagesReceived++;
        hudInfo.setMessagesInfo(totalMessagesReceived, SerialComms.getBytesInBuffer(), missed);
        long cs = data.length + vbat + csin;
        for (byte i : data) {
            cs += i;
        }

        if ((cs & 0xFF) == 0) {

            mainGUI.appendText(0xFE);
            mainGUI.appendText(data.length);
            for (int i : data) {
                mainGUI.appendText(i);
            }
            mainGUI.appendText(vbat);
            mainGUI.appendText(csin);

            float[] graph_tmp = new float[data.length / 4];

            for (int i = 0; i < data.length / 4; i++) {
                byte[] ca = new byte[4];
                for (int j = 0; j < 4; j++) {
                    ca[j] = (byte) (data[(4 * i) + j] & 0xFF);
                }
                graph_tmp[i] = convBAtoF(ca);
            }

            logData(graph_tmp);
            mainGUI.addPointToChart(graph_tmp);
            hudInfo.setBatteryLevel(vbat);

        } else {
            missed++;
        }
    }

    static void logData(float[] data) {
        if (logger != null) {
            if (logCounter == logSkipFactor) {
                String fout = "";
                for (float f : data) {
                    fout += f + ";";
                }
                try {
                    logger.write(fout);
                    logger.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(ControlluinoInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
                logCounter = -1;
            }
            logCounter++;
        }
    }

    static float convBAtoF(byte[] in) {
        int valX = 0;
        for (int i = 3; i > 0; i--) {
            valX |= (0xFF & in[i]) << 8 * i;
        }
        return Float.intBitsToFloat(valX);
    }

    static byte[] convFtoBA(float in) {
        int valF = Float.floatToRawIntBits(in);
        byte[] out = new byte[4];
        for (int i = 0; i < 4; i++) {
            out[i] = (byte) ((valF >> (i * 8)) & 0xFF);
        }
        return out;
    }

}
