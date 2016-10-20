/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlluinosimple;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JSpinner;

/**
 *
 * @author mihalis
 */
public class mainGui extends javax.swing.JFrame {

    static boolean connected = false;
    static volatile boolean motorsInit = false;
    static volatile boolean sensorInit = false;
    static volatile boolean ardSetup = false;
    static volatile boolean motorParams = false;
    static int totalMsg = 0;
    static long startTime;
    Thread runtimeStats;
    static long globalStartTime = 0;

    public mainGui() {
        globalStartTime = System.currentTimeMillis();
        initComponents();
        mnuTrirotor.setSelected(true);
        mnuTrirotorActionPerformed(null);
        runtimeStats = (new Thread() {
            public void run() {
                try {
                    while (true) {
                        long currentTimeMillis = System.currentTimeMillis() - startTime;
                        mainGui.lblRuntime.setText(String.format("%6.2fs", (double) (currentTimeMillis) / 1000));
                        mainGui.lblSamplingTime.setText(String.format("%6.3fs", (((double) (currentTimeMillis) / 1000)) / (totalMsg * ((int) spnArdTransmitRate.getValue() + 1))));
                        sleep(10);
                    }
                } catch (Exception ex) {
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.ButtonGroup buttonGroup1 = new javax.swing.ButtonGroup();
        javax.swing.JTabbedPane jTabbedPane1 = new javax.swing.JTabbedPane();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        spnRef1 = new javax.swing.JSpinner();
        spnRef2 = new javax.swing.JSpinner();
        spnRef3 = new javax.swing.JSpinner();
        spnRef4 = new javax.swing.JSpinner();
        spnRef5 = new javax.swing.JSpinner();
        spnRef6 = new javax.swing.JSpinner();
        btnUpdate = new javax.swing.JButton();
        btnConnect = new javax.swing.JButton();
        btnInitMotors = new javax.swing.JButton();
        btnInitSensor = new javax.swing.JButton();
        btnRun = new javax.swing.JButton();
        cmbScenario = new javax.swing.JComboBox();
        btnStop = new javax.swing.JButton();
        lblRef1 = new javax.swing.JLabel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        txtInfo = new javax.swing.JTextArea();
        lblRef2 = new javax.swing.JLabel();
        lblRef3 = new javax.swing.JLabel();
        lblRef4 = new javax.swing.JLabel();
        lblRef5 = new javax.swing.JLabel();
        lblRef6 = new javax.swing.JLabel();
        btnSetup = new javax.swing.JButton();
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        cmbAvailablePorts = new javax.swing.JComboBox();
        cmbBaudRate = new javax.swing.JComboBox();
        spnSamplingTime = new javax.swing.JSpinner();
        spnUpdateRate = new javax.swing.JSpinner();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel13 = new javax.swing.JLabel();
        spnArdTransmitRate = new javax.swing.JSpinner();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        cmbType3 = new javax.swing.JComboBox();
        spnOffset3 = new javax.swing.JSpinner();
        spnGradient3 = new javax.swing.JSpinner();
        spnUsMin3 = new javax.swing.JSpinner();
        spnUsMax3 = new javax.swing.JSpinner();
        cmbType4 = new javax.swing.JComboBox();
        spnOffset4 = new javax.swing.JSpinner();
        spnGradient4 = new javax.swing.JSpinner();
        spnUsMin4 = new javax.swing.JSpinner();
        spnUsMax4 = new javax.swing.JSpinner();
        cmbType5 = new javax.swing.JComboBox();
        spnOffset5 = new javax.swing.JSpinner();
        spnGradient5 = new javax.swing.JSpinner();
        spnUsMin5 = new javax.swing.JSpinner();
        spnUsMax5 = new javax.swing.JSpinner();
        cmbType6 = new javax.swing.JComboBox();
        spnOffset6 = new javax.swing.JSpinner();
        spnGradient6 = new javax.swing.JSpinner();
        spnUsMin6 = new javax.swing.JSpinner();
        spnUsMax6 = new javax.swing.JSpinner();
        cmbType1 = new javax.swing.JComboBox();
        cmbType2 = new javax.swing.JComboBox();
        spnOffset2 = new javax.swing.JSpinner();
        spnOffset1 = new javax.swing.JSpinner();
        spnGradient1 = new javax.swing.JSpinner();
        spnGradient2 = new javax.swing.JSpinner();
        spnUsMin2 = new javax.swing.JSpinner();
        spnUsMin1 = new javax.swing.JSpinner();
        spnUsMax1 = new javax.swing.JSpinner();
        spnUsMax2 = new javax.swing.JSpinner();
        javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
        lblRuntime = new javax.swing.JLabel();
        javax.swing.JLabel jLabel12 = new javax.swing.JLabel();
        lblMsgCount = new javax.swing.JLabel();
        javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
        lblSamplingTime = new javax.swing.JLabel();
        pgrBattery = new javax.swing.JProgressBar();
        javax.swing.JLabel jLabel14 = new javax.swing.JLabel();
        javax.swing.JMenuBar jMenuBar1 = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenu1 = new javax.swing.JMenu();
        mnuTrirotor = new javax.swing.JRadioButtonMenuItem();
        mnuQuadrotor = new javax.swing.JRadioButtonMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        spnRef1.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        spnRef2.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        spnRef3.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        spnRef4.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        spnRef5.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        spnRef6.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        btnUpdate.setText("Update");
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnConnect.setText("Connect");
        btnConnect.setEnabled(false);
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        btnInitMotors.setText("Init. Motors");
        btnInitMotors.setEnabled(false);
        btnInitMotors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInitMotorsActionPerformed(evt);
            }
        });

        btnInitSensor.setText("Init. Sensor");
        btnInitSensor.setEnabled(false);
        btnInitSensor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInitSensorActionPerformed(evt);
            }
        });

        btnRun.setText("Run");
        btnRun.setEnabled(false);
        btnRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunActionPerformed(evt);
            }
        });

        cmbScenario.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Attitude Control", "Position Control", "Force Control" }));

        btnStop.setText("Stop");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        lblRef1.setText("a");

        txtInfo.setEditable(false);
        txtInfo.setColumns(20);
        txtInfo.setFont(new java.awt.Font("Menlo", 0, 10)); // NOI18N
        txtInfo.setLineWrap(true);
        txtInfo.setRows(5);
        txtInfo.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtInfo);

        lblRef2.setText("a");

        lblRef3.setText("a");

        lblRef4.setText("a");

        lblRef5.setText("a");

        lblRef6.setText("a");

        btnSetup.setText("Setup");
        btnSetup.setEnabled(false);
        btnSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRef1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRef2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRef3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRef4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRef5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRef6, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spnRef6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnRef1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnRef2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnRef3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnRef4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnRef5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbScenario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConnect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnInitMotors, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnInitSensor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRun, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnStop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSetup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnUpdate, spnRef1, spnRef2, spnRef3, spnRef4, spnRef5, spnRef6});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnConnect, btnInitMotors, btnInitSensor, btnRun, btnSetup, btnStop, cmbScenario});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(spnRef1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblRef1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(spnRef2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblRef2)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cmbScenario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnConnect)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnRef3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnInitMotors)
                            .addComponent(lblRef3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnRef4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnInitSensor)
                            .addComponent(lblRef4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnRef5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblRef5)
                            .addComponent(btnSetup))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnRef6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblRef6)
                            .addComponent(btnRun))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnUpdate)
                            .addComponent(btnStop))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Control Panel", jPanel1);

        cmbBaudRate.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "9600", "57600", "115200" }));
        cmbBaudRate.setSelectedIndex(2);

        spnSamplingTime.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(20), Integer.valueOf(0), null, Integer.valueOf(1)));

        spnUpdateRate.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(10), Integer.valueOf(-1), null, Integer.valueOf(1)));

        jLabel6.setText("Serial Port:");

        jLabel7.setText("BUAD Rate:");

        jLabel8.setText("Sampling Time (ms):");

        jLabel9.setText("Update Reference Interval:");

        jLabel13.setText("Arduino Transmit Interval:");

        spnArdTransmitRate.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel13))
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbBaudRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnSamplingTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUpdateRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbAvailablePorts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnArdTransmitRate, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(639, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmbAvailablePorts, cmbBaudRate, spnArdTransmitRate, spnSamplingTime, spnUpdateRate});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbAvailablePorts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbBaudRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnSamplingTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnUpdateRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(spnArdTransmitRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(135, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Settings", jPanel3);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Type");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Offset");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Gradient");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("μs Min");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("μs Max");

        cmbType3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Motor", "Servo" }));

        spnOffset3.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), null, Float.valueOf(1.0f)));

        spnGradient3.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        spnUsMin3.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        spnUsMax3.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        cmbType4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Motor", "Servo" }));

        spnOffset4.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), null, Float.valueOf(1.0f)));

        spnGradient4.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        spnUsMin4.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        spnUsMax4.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        cmbType5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Motor", "Servo" }));

        spnOffset5.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), null, Float.valueOf(1.0f)));

        spnGradient5.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        spnUsMin5.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        spnUsMax5.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        cmbType6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Motor", "Servo" }));

        spnOffset6.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), null, Float.valueOf(1.0f)));

        spnGradient6.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        spnUsMin6.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        spnUsMax6.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        cmbType1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Motor", "Servo" }));

        cmbType2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Motor", "Servo" }));

        spnOffset2.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), null, Float.valueOf(1.0f)));

        spnOffset1.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), null, Float.valueOf(0.001f)));

        spnGradient1.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        spnGradient2.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        spnUsMin2.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        spnUsMin1.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        spnUsMax1.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        spnUsMax2.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbType1, 0, 170, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(spnOffset1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spnGradient1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spnUsMin1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spnUsMax1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cmbType2, 0, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnOffset2, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnGradient2, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnUsMin2, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnUsMax2, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cmbType6, 0, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnOffset6, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnGradient6, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnUsMin6, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnUsMax6, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cmbType5, 0, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnOffset5, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnGradient5, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnUsMin5, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnUsMax5, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cmbType4, 0, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnOffset4, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnGradient4, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnUsMin4, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnUsMax4, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cmbType3, 0, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnOffset3, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnGradient3, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnUsMin3, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnUsMax3, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbType1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnOffset1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnGradient1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUsMin1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUsMax1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbType2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnOffset2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUsMin2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUsMax2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbType3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnOffset3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUsMin3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUsMax3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbType4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnOffset4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUsMin4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUsMax4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbType5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnOffset5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUsMin5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUsMax5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbType6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnOffset6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnGradient6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUsMin6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnUsMax6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(90, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Structure", jPanel2);

        jLabel10.setText("Runtime:");

        lblRuntime.setText("0.0s");

        jLabel12.setText("Msg Count:");

        lblMsgCount.setText("0");

        jLabel11.setText("Sampling Time:");

        lblSamplingTime.setText("0");

        pgrBattery.setMaximum(255);

        jLabel14.setText("Battery Voltage:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRuntime)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMsgCount)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSamplingTime)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pgrBattery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel10)
                    .addComponent(lblRuntime)
                    .addComponent(jLabel12)
                    .addComponent(lblMsgCount)
                    .addComponent(jLabel11)
                    .addComponent(lblSamplingTime)
                    .addComponent(pgrBattery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addContainerGap())
        );

        jMenu1.setText("Presets");

        buttonGroup1.add(mnuTrirotor);
        mnuTrirotor.setSelected(true);
        mnuTrirotor.setText("Trirotor");
        mnuTrirotor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrirotorActionPerformed(evt);
            }
        });
        jMenu1.add(mnuTrirotor);

        buttonGroup1.add(mnuQuadrotor);
        mnuQuadrotor.setText("Quadrotor");
        mnuQuadrotor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuQuadrotorActionPerformed(evt);
            }
        });
        jMenu1.add(mnuQuadrotor);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateReference();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        if (!connected) {
            connected = SerialComms.connect((String) cmbAvailablePorts.getSelectedItem(), Integer.parseInt((String) cmbBaudRate.getSelectedItem()));
        }
    }//GEN-LAST:event_btnConnectActionPerformed

    private void btnInitMotorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInitMotorsActionPerformed

        if (connected) {
            new Thread() {
                public void run() {
                    byte types[] = getActuatorParameters(0);
                    byte c[] = getActuatorParameters(1);
                    byte m[] = getActuatorParameters(2);
                    byte min[] = getActuatorParameters(3);
                    byte max[] = getActuatorParameters(4);

                    SerialComms.send(types);
                    appendToLog("Updating motor\t...types\n");
                    while (!motorParams);
                    motorParams = false;

                    SerialComms.send(c);
                    appendToLog("\t\t\t...offsets\n");
                    while (!motorParams);
                    motorParams = false;

                    SerialComms.send(m);
                    appendToLog("\t\t\t...gradients\n");
                    while (!motorParams);
                    motorParams = false;

                    SerialComms.send(min);
                    appendToLog("\t\t\t...minimum pulse width\n");
                    while (!motorParams);
                    motorParams = false;

                    SerialComms.send(max);
                    appendToLog("\t\t\t...maximum pulse width\n");
                    while (!motorParams);
                    appendToLog("Motor parameters updated\n");

                    byte[] msg = {(byte) 0xBB,(byte) 0xBB};
                    SerialComms.send(msg);
                    appendToLog("Initializing motors\n");
                }
            }.start();
        }
    }//GEN-LAST:event_btnInitMotorsActionPerformed

    private void btnInitSensorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInitSensorActionPerformed
        byte[] msg = {(byte) 0xEE};
        if (connected) {
            SerialComms.send(msg);
            appendToLog("Initializing sensor\n");
        }
    }//GEN-LAST:event_btnInitSensorActionPerformed

    private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed

        if (connected && motorsInit && (cmbScenario.getSelectedItem().equals("Force Control") || sensorInit) && ardSetup) {
            byte[] msg = {(byte) 0xDD,(byte) 0xDD};

            SerialComms.send(msg);
            appendToLog("Run\n");
        }
        startTime = System.currentTimeMillis();
        runtimeStats.start();
    }//GEN-LAST:event_btnRunActionPerformed

    private void btnSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetupActionPerformed
        if (connected) {
            byte[] msg = new byte[7];
            msg[0] = (byte) 0x22;
            msg[1] = (byte) ((int) spnArdTransmitRate.getValue());
            msg[2] = (byte) cmbScenario.getSelectedIndex();
            msg[3] = (byte) ((int) spnSamplingTime.getValue());
            msg[4] = (byte) 0x0A;
            msg[5] = (byte) 0x0A;
            long cs = -0x22;
            for (byte b : msg) {
                cs += b;
            }
            msg[6] = (byte) (256 - (cs % 256));

            SerialComms.send(msg);
            appendToLog("Setup:\n");
            appendToLog("Arduino Transmit Interval: " + ((int) spnArdTransmitRate.getValue()) + "\n");
            appendToLog("Reference Update Interval: " + ((int) spnUpdateRate.getValue()) + "\n");
            appendToLog("Sampling Time (ms): " + ((int) spnSamplingTime.getValue()) + "\n");
            appendToLog("Scenario: " + (String) cmbScenario.getSelectedItem() + "(" + cmbScenario.getSelectedIndex() + ")\n");

        }
    }//GEN-LAST:event_btnSetupActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed

        if (connected) {
            byte[] msg = {(byte) 0xFF,(byte) 0xFF, (byte) 0xFE,(byte) 0xFE};
            SerialComms.send(msg);
            appendToLog("Stop\n");
            runtimeStats.interrupt();
        }
    }//GEN-LAST:event_btnStopActionPerformed

    private void mnuTrirotorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrirotorActionPerformed
        float c[] = {969.85f, 969.85f, 969.85f, 1621.8f, 1562.9f, 1356.7f};
        float m[] = {0.75615f, 0.75615f, 0.75615f, -564.3f, -554.54f, -564.28f};
        int types[] = {0, 0, 0, 1, 1, 1};
        int min[] = {0, 0, 0, 850, 790, 790};
        int max[] = {1877, 1877, 1877, 2178, 2171, 2178};

        JComboBox[] typesGui = {cmbType1, cmbType2, cmbType3, cmbType4, cmbType5, cmbType6};
        JSpinner[] mGui = {spnGradient1, spnGradient2, spnGradient3, spnGradient4, spnGradient5, spnGradient6};
        JSpinner[] cGui = {spnOffset1, spnOffset2, spnOffset3, spnOffset4, spnOffset5, spnOffset6};
        JSpinner[] minGui = {spnUsMin1, spnUsMin2, spnUsMin3, spnUsMin4, spnUsMin5, spnUsMin6};
        JSpinner[] maxGui = {spnUsMax1, spnUsMax2, spnUsMax3, spnUsMax4, spnUsMax5, spnUsMax6};

        for (int i = 0; i < 6; i++) {
            mGui[i].setValue(m[i]);
            cGui[i].setValue(c[i]);
            minGui[i].setValue(min[i]);
            maxGui[i].setValue(max[i]);
            typesGui[i].setSelectedIndex(types[i]);
        }

    }//GEN-LAST:event_mnuTrirotorActionPerformed

    private void mnuQuadrotorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuQuadrotorActionPerformed
        float c[] = {969.85f, 969.85f, 969.85f, 969.85f, 0, 0};
        float m[] = {0.75615f, 0.75615f, 0.75615f, 0.75615f, 0, 0};
        int types[] = {0, 0, 0, 0, 0, 0};
        int min[] = {0, 0, 0, 0, 0, 0};
        int max[] = {1877, 1877, 1877, 1877, 0, 0};

        JComboBox[] typesGui = {cmbType1, cmbType2, cmbType3, cmbType4, cmbType5, cmbType6};
        JSpinner[] mGui = {spnGradient1, spnGradient2, spnGradient3, spnGradient4, spnGradient5, spnGradient6};
        JSpinner[] cGui = {spnOffset1, spnOffset2, spnOffset3, spnOffset4, spnOffset5, spnOffset6};
        JSpinner[] minGui = {spnUsMin1, spnUsMin2, spnUsMin3, spnUsMin4, spnUsMin5, spnUsMin6};
        JSpinner[] maxGui = {spnUsMax1, spnUsMax2, spnUsMax3, spnUsMax4, spnUsMax5, spnUsMax6};

        for (int i = 0; i < 6; i++) {
            mGui[i].setValue(m[i]);
            cGui[i].setValue(c[i]);
            minGui[i].setValue(min[i]);
            maxGui[i].setValue(max[i]);
            typesGui[i].setSelectedIndex(types[i]);
        }
    }//GEN-LAST:event_mnuQuadrotorActionPerformed

    public static void appendToLog(String s) {

        txtInfo.append(System.currentTimeMillis() - globalStartTime + ": " + s);
        txtInfo.setCaretPosition(txtInfo.getText().length());
    }

    public static void setAvailablePorts(String[] ports) {
        for (String s : ports) {
            cmbAvailablePorts.addItem(s);
        }
        if (ports.length > 0) {
            btnConnect.setEnabled(true);
        }
        cmbAvailablePorts.setSelectedIndex(cmbAvailablePorts.getItemCount() - 1);
        appendToLog("Selected port " + (String) cmbAvailablePorts.getSelectedItem() + " at " + Integer.parseInt((String) cmbBaudRate.getSelectedItem()) + "\n");
    }

    public static void receivedReadyMsg() {
        btnRun.setEnabled(connected);
        btnInitMotors.setEnabled(connected);
        btnInitSensor.setEnabled(connected);
        btnStop.setEnabled(connected);
        btnUpdate.setEnabled(connected);
        btnSetup.setEnabled(connected);
    }

    public static void sensorInitialized() {
        sensorInit = true;
    }

    public static void motorsInitialized() {
        motorsInit = true;
    }

    public static void arduinoSetup() {
        ardSetup = true;
    }

    static void motorParametersUpdated() {
        motorParams = true;
    }

    public static void newMessage(int vbat) {
        totalMsg++;
        lblMsgCount.setText("" + totalMsg);
        pgrBattery.setValue(vbat);
        if ((((int) spnUpdateRate.getValue()) != -1) && (totalMsg % (int) spnUpdateRate.getValue() == 0)) {
            updateReference();
        }
    }

    private static void updateReference() {
        byte[] msg = new byte[26];
        long cs = 0;
        float ref[] = {
            (Float) spnRef1.getValue(),
            (Float) spnRef2.getValue(),
            (Float) spnRef3.getValue(),
            (Float) spnRef4.getValue(),
            (Float) spnRef5.getValue(),
            (Float) spnRef6.getValue()};


        int i = 1;
        msg[0] = (byte) 0xCC;
        cs = 0;
        for (int j = 0; j < 6; j++) {
            for (byte c : convFtoBA(ref[j])) {
                msg[i++] = c;
                cs += c;
            }
        }

        msg[25] = (byte) ((256 - (cs % 256)));
        String logMsg = "";
        for (byte b : msg) {
            String tmp = Integer.toHexString(((int) b) & 0xFF).toUpperCase();
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            logMsg += "0x" + tmp + " ";
        }
        logMsg += "\n";

        if (connected) {
            SerialComms.send(msg);
//            appendToLog(logMsg);
        } else {
        }
    }

    private static byte[] getActuatorParameters(int param) {

        ArrayList<Byte> tempOut = new ArrayList<Byte>();
        JComboBox[] typesSource = {cmbType1, cmbType2, cmbType3, cmbType4, cmbType5, cmbType6};
        JSpinner[] mSource = {spnGradient1, spnGradient2, spnGradient3, spnGradient4, spnGradient5, spnGradient6};
        JSpinner[] cSource = {spnOffset1, spnOffset2, spnOffset3, spnOffset4, spnOffset5, spnOffset6};
        JSpinner[] minSource = {spnUsMin1, spnUsMin2, spnUsMin3, spnUsMin4, spnUsMin5, spnUsMin6};
        JSpinner[] maxSource = {spnUsMax1, spnUsMax2, spnUsMax3, spnUsMax4, spnUsMax5, spnUsMax6};

        tempOut.add((byte) 0x33);
        tempOut.add((byte) param);

        int types = 0;
        long cs = 0;
        switch (param) {
            case 0:
                for (JComboBox cm : typesSource) {
                    types = (types << 1) + cm.getSelectedIndex();
                }
                tempOut.add((byte) types);
                cs += types;
                break;
            case 1:
                for (JSpinner spn : cSource) {
                    byte[] tmp = convFtoBA((float) spn.getValue());
                    for (byte b : tmp) {
                        tempOut.add(b);
                        cs += b;
                    }
                }
                break;
            case 2:
                for (JSpinner spn : mSource) {
                    byte[] tmp = convFtoBA((float) spn.getValue());
                    for (byte b : tmp) {
                        tempOut.add(b);
                        cs += b;
                    }
                }
                break;
            case 3:
                for (JSpinner spn : minSource) {
                    byte[] tmp = convItoBA((int) spn.getValue());
                    for (byte b : tmp) {
                        tempOut.add(b);
                        cs += b;
                    }
                }
                break;
            case 4:
                for (JSpinner spn : maxSource) {
                    byte[] tmp = convItoBA((int) spn.getValue());
                    for (byte b : tmp) {
                        tempOut.add(b);
                        cs += b;
                    }
                }
                break;
        }

        tempOut.add(
                (byte) (256 - (cs % 256)));
        byte[] out = new byte[tempOut.size()];
        for (int i = 0;
                i < tempOut.size();
                i++) {
            out[i] = tempOut.get(i);
        }
        return out;
    }

    private static byte[] convItoBA(int in) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(in).array();
        return bytes;
    }

    private static byte[] convFtoBA(float in) {
//        int valF = Float.floatToRawIntBits(in);
//        byte[] out = new byte[4];
//        for (int i = 0; i < 4; i++) {
//            out[i] = (byte) ((valF >> (i * 8)) & 0xFF);
//        }
        byte[] bytes = ByteBuffer.allocate(4).putFloat(in).array();
        return bytes;
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mainGui().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton btnConnect;
    private static javax.swing.JButton btnInitMotors;
    private static javax.swing.JButton btnInitSensor;
    private static javax.swing.JButton btnRun;
    private static javax.swing.JButton btnSetup;
    private static javax.swing.JButton btnStop;
    private static javax.swing.JButton btnUpdate;
    private static javax.swing.JComboBox cmbAvailablePorts;
    private static javax.swing.JComboBox cmbBaudRate;
    private static javax.swing.JComboBox cmbScenario;
    private static javax.swing.JComboBox cmbType1;
    private static javax.swing.JComboBox cmbType2;
    private static javax.swing.JComboBox cmbType3;
    private static javax.swing.JComboBox cmbType4;
    private static javax.swing.JComboBox cmbType5;
    private static javax.swing.JComboBox cmbType6;
    private static javax.swing.JLabel lblMsgCount;
    private static javax.swing.JLabel lblRef1;
    private static javax.swing.JLabel lblRef2;
    private static javax.swing.JLabel lblRef3;
    private static javax.swing.JLabel lblRef4;
    private static javax.swing.JLabel lblRef5;
    private static javax.swing.JLabel lblRef6;
    private static javax.swing.JLabel lblRuntime;
    private static javax.swing.JLabel lblSamplingTime;
    private static javax.swing.JRadioButtonMenuItem mnuQuadrotor;
    private static javax.swing.JRadioButtonMenuItem mnuTrirotor;
    private static javax.swing.JProgressBar pgrBattery;
    private static javax.swing.JSpinner spnArdTransmitRate;
    private static javax.swing.JSpinner spnGradient1;
    private static javax.swing.JSpinner spnGradient2;
    private static javax.swing.JSpinner spnGradient3;
    private static javax.swing.JSpinner spnGradient4;
    private static javax.swing.JSpinner spnGradient5;
    private static javax.swing.JSpinner spnGradient6;
    private static javax.swing.JSpinner spnOffset1;
    private static javax.swing.JSpinner spnOffset2;
    private static javax.swing.JSpinner spnOffset3;
    private static javax.swing.JSpinner spnOffset4;
    private static javax.swing.JSpinner spnOffset5;
    private static javax.swing.JSpinner spnOffset6;
    private static javax.swing.JSpinner spnRef1;
    private static javax.swing.JSpinner spnRef2;
    private static javax.swing.JSpinner spnRef3;
    private static javax.swing.JSpinner spnRef4;
    private static javax.swing.JSpinner spnRef5;
    private static javax.swing.JSpinner spnRef6;
    private static javax.swing.JSpinner spnSamplingTime;
    private static javax.swing.JSpinner spnUpdateRate;
    private static javax.swing.JSpinner spnUsMax1;
    private static javax.swing.JSpinner spnUsMax2;
    private static javax.swing.JSpinner spnUsMax3;
    private static javax.swing.JSpinner spnUsMax4;
    private static javax.swing.JSpinner spnUsMax5;
    private static javax.swing.JSpinner spnUsMax6;
    private static javax.swing.JSpinner spnUsMin1;
    private static javax.swing.JSpinner spnUsMin2;
    private static javax.swing.JSpinner spnUsMin3;
    private static javax.swing.JSpinner spnUsMin4;
    private static javax.swing.JSpinner spnUsMin5;
    private static javax.swing.JSpinner spnUsMin6;
    private static javax.swing.JTextArea txtInfo;
    // End of variables declaration//GEN-END:variables
}
