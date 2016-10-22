/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlluinosimple;

import java.awt.Component;

/**
 *
 * @author mihalis
 */
public class ControlluinoSimple {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        mainGui gui = new mainGui();
        gui.setVisible(true);
        gui.setLocationRelativeTo(null);
        mainGui.setAvailablePorts(SerialComms.getAvailableCommPorts());
        
    }
}
