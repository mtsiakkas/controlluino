package controlluinointerface;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class mainGUI extends javax.swing.JFrame {

    String[] portNames;
    static int t = 0;
    static XYSeries[] seriesW;
    static XYSeries[] seriesQ;
    static XYSeriesCollection datasetW;
    static XYSeriesCollection datasetQ;
    static ChartPanel chartPanelW;
    static ChartPanel chartPanelQ;
    static int plotRange = 0;
    static int plotRangeMin = 0;
    static NumberAxis domainW;
    static NumberAxis domainQ;
    static boolean connected = false;
    static boolean prevMovingRange = false;
    volatile static boolean sensorInit = false;
    volatile static boolean motorsInit = false;
    static int txSkip = 0;
    static int rxSkip = 0;
    static int expectedData = 6;
    static int wLength = 3;
    static int qLength = 4;

    public mainGUI() {

        initComponents();
        portNames = SerialComms.getAvailableCommPorts();

        cmbPortSelect.removeAllItems();

        for (String s : portNames) {
            if (!s.toLowerCase().contains("bluetooth")) {
                cmbPortSelect.addItem(s);
            }
        }
        if (cmbPortSelect.getItemCount() == 0) {
            cmbPortSelect.addItem("N/A");
        }
        cmbPortSelect.setSelectedIndex(cmbPortSelect.getItemCount() - 1);
        cmbScenario.setSelectedIndex(0);

        DateFormat dateFormat = new SimpleDateFormat("ddMMyy-HHmmss");
        Date date = new Date();
        txtLogFilePath.setText("log-" + dateFormat.format(date) + ".dat");
        spnTxSkip.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mainGUI.txSkip = (int) spnTxSkip.getValue();
                hudInfo.setTxSkipInterval(txSkip);
            }
        });

        mainGUI.txSkip = (int) spnTxSkip.getValue();
        spnRxSkip.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mainGUI.rxSkip = (int) spnRxSkip.getValue();
                hudInfo.setRxSkipInterval(rxSkip);
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                try {
                    mainGUI.chartPanelW.setSize(pnlChartW.getSize());
                    mainGUI.chartPanelQ.setSize(pnlChartQ.getSize());
                } catch (Exception ee) {
                }
            }
        });
    }

    public static void receivedReadyMsg() {
        btnRun.setEnabled(true);
    }

    public static void sensorInitialised() {
        sensorInit = true;
        sessionLog.addSessionLog("Sensor initialised...\n");
    }

    public static void motorsInitialised() {
        motorsInit = true;
        sessionLog.addSessionLog("Motors initialised...\n");
    }

    private void createPlots(String[][] plotTitles, String[] graphTitles) {
        datasetW = new XYSeriesCollection();

        seriesW = new XYSeries[wLength];
        for (int i = 0; i < wLength; i++) {
            seriesW[i] = new XYSeries(plotTitles[0][i]);
            datasetW.addSeries(seriesW[i]);
        }
        datasetQ = new XYSeriesCollection();

        seriesQ = new XYSeries[qLength];
        for (int i = 0; i < qLength; i++) {
            seriesQ[i] = new XYSeries(plotTitles[1][i]);
            datasetQ.addSeries(seriesQ[i]);
        }

        JFreeChart chartW = ChartFactory.createXYLineChart(
                "", "", graphTitles[0],
                datasetW,
                PlotOrientation.VERTICAL,
                true, false, false);
        XYPlot plotW = (XYPlot) chartW.getPlot();

        JFreeChart chartQ = ChartFactory.createXYLineChart(
                "", "", graphTitles[1],
                datasetQ,
                PlotOrientation.VERTICAL,
                true, false, false);
        XYPlot plotQ = (XYPlot) chartQ.getPlot();

        chartPanelW = new ChartPanel(chartW);
        chartPanelW.setSize(pnlChartW.getSize());
        chartW.setBackgroundPaint(pnlChartW.getBackground());
        pnlChartW.add(chartPanelW);
        chartPanelW.setVisible(true);
        domainW = (NumberAxis) plotW.getDomainAxis();
        domainW.setRange(0, 1);


        chartPanelQ = new ChartPanel(chartQ);
        chartPanelQ.setSize(pnlChartQ.getSize());
        chartQ.setBackgroundPaint(pnlChartQ.getBackground());
        pnlChartQ.add(chartPanelQ);
        chartPanelQ.setVisible(true);
        domainQ = (NumberAxis) plotQ.getDomainAxis();
        domainQ.setRange(0, 1);

        plotQ.setBackgroundPaint(Color.BLACK);
        plotW.setBackgroundPaint(Color.BLACK);

    }

    public static void addPointToChart(float[] yn) {
        try {
            if (t > plotRange && chkMovingRange.isSelected()) {
                domainW.setRange(t - plotRange, t);
                domainQ.setRange(t - plotRange, t);

                plotRangeMin = t - plotRange;
                for (int i = 0; i < qLength; i++) {
                    seriesQ[i].setMaximumItemCount(plotRange);
                }
                for (int i = 0; i < wLength; i++) {
                    seriesW[i].setMaximumItemCount(plotRange);
                }
                prevMovingRange = true;
            } else if (t > plotRangeMin) {
                if (prevMovingRange) {
                    for (int i = 0; i < qLength; i++) {
                        seriesQ[i].setMaximumItemCount(2147483647);
                    }
                    for (int i = 0; i < wLength; i++) {
                        seriesW[i].setMaximumItemCount(2147483647);
                    }
                    prevMovingRange = false;
                }
                domainQ.setRange(plotRangeMin, t);
                domainW.setRange(plotRangeMin, t);
            }
            for (int i = wLength; i < wLength + qLength; i++) {
                seriesQ[i - wLength].add(t, yn[i]);
            }
            for (int i = 0; i < wLength; i++) {
                seriesW[i].add(t, yn[i]);
            }

            chartPanelW.revalidate();
            chartPanelQ.revalidate();

            t++;
        } catch (Exception ee) {
            System.out.println("Caught plotting exception at t=" + t);
            ee.printStackTrace();
        }
        if ((txSkip != 0) && ((t % txSkip) == 0)) {
            updateReference();
        }
    }

    public static int getSelectedTab() {
        return tpnTabPane.getSelectedIndex();
    }

    public static void appendText(int in) {
        if (in <= 255) {
            String s = in + " ";
            if (rbHEX.isSelected()) {
                s = Integer.toHexString(in).toUpperCase() + " ";
                if (s.length() == 2) {
                    s = "0" + s;
                }
            } else if (rbASCIIchar.isSelected()) {
                s = (char) in + " ";
            }
            txtRawData.append(s);
            if (chkAutoscroll.isSelected()) {
                txtRawData.setCaretPosition(txtRawData.getText().length());
            }
        }
    }

    public static void appendText(String in) {
        txtRawData.append(in);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectRawDataBase = new javax.swing.ButtonGroup();
        btnStart = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        tpnTabPane = new javax.swing.JTabbedPane();
        pnlControlPanel = new javax.swing.JPanel();
        javax.swing.JPanel jPanel6 = new javax.swing.JPanel();
        javax.swing.JButton btnUpdateReference = new javax.swing.JButton();
        lblRef11 = new javax.swing.JLabel();
        lblRef12 = new javax.swing.JLabel();
        lblRef13 = new javax.swing.JLabel();
        lblRef21 = new javax.swing.JLabel();
        lblRef22 = new javax.swing.JLabel();
        lblRef23 = new javax.swing.JLabel();
        cmbScenario = new javax.swing.JComboBox();
        spnRef11 = new javax.swing.JSpinner();
        spnRef12 = new javax.swing.JSpinner();
        spnRef13 = new javax.swing.JSpinner();
        spnRef21 = new javax.swing.JSpinner();
        spnRef22 = new javax.swing.JSpinner();
        spnRef23 = new javax.swing.JSpinner();
        pnlChartW = new javax.swing.JPanel();
        pnlChartQ = new javax.swing.JPanel();
        spnPlotRange = new javax.swing.JSpinner();
        chkMovingRange = new javax.swing.JCheckBox();
        lblExtraInfo = new javax.swing.JTextArea();
        javax.swing.JPanel pnlRawData = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        txtRawData = new javax.swing.JTextArea();
        rbASCIInum = new javax.swing.JRadioButton();
        rbHEX = new javax.swing.JRadioButton();
        javax.swing.JButton btnClearRawData = new javax.swing.JButton();
        chkAutoscroll = new javax.swing.JCheckBox();
        rbASCIIchar = new javax.swing.JRadioButton();
        javax.swing.JPanel pnlSettings = new javax.swing.JPanel();
        javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
        cmbBaudRate = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        cmbPortSelect = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        spnTxSkip = new javax.swing.JSpinner();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        spnRxSkip = new javax.swing.JSpinner();
        javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
        chkLogData = new javax.swing.JCheckBox();
        txtLogFilePath = new javax.swing.JTextField();
        btnLogFileBrowse = new javax.swing.JButton();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        spnSkipFactor = new javax.swing.JSpinner();
        javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel14 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        spnSamplingTime = new javax.swing.JSpinner();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        javax.swing.JTextArea jTextArea1 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        cmbOutput1 = new javax.swing.JComboBox();
        cmbOutput2 = new javax.swing.JComboBox();
        javax.swing.JButton btnQuit = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnConnect = new javax.swing.JButton();
        btnRun = new javax.swing.JButton();
        mnuMenuToolbar = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenu2 = new javax.swing.JMenu();
        mnuLog = new javax.swing.JMenuItem();
        mnuHUD = new javax.swing.JMenuItem();

        selectRawDataBase.add(rbHEX);
        selectRawDataBase.add(rbASCIInum);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnStart.setText("Start");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        btnStop.setText("Stop");
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        tpnTabPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tpnTabPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Reference Points"));

        btnUpdateReference.setText("Update");
        btnUpdateReference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateReferenceActionPerformed(evt);
            }
        });

        lblRef11.setText("Roll (φ):");

        lblRef12.setText("Pitch (θ):");

        lblRef13.setText("Yaw (ψ):");

        lblRef21.setText("North (x):");

        lblRef22.setText("East (y):");

        lblRef23.setText("Up (z):");

        cmbScenario.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Attitude Control", "Position Control", "Force Control" }));
        cmbScenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbScenarioActionPerformed(evt);
            }
        });

        spnRef11.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));

        spnRef12.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));

        spnRef13.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));

        spnRef21.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));

        spnRef22.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));

        spnRef23.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbScenario, 0, 0, Short.MAX_VALUE)
                    .addComponent(btnUpdateReference, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblRef12)
                            .addComponent(lblRef11)
                            .addComponent(lblRef13)
                            .addComponent(lblRef21)
                            .addComponent(lblRef22)
                            .addComponent(lblRef23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spnRef11)
                            .addComponent(spnRef12)
                            .addComponent(spnRef13)
                            .addComponent(spnRef21)
                            .addComponent(spnRef22)
                            .addComponent(spnRef23))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmbScenario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRef11)
                    .addComponent(spnRef11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRef12)
                    .addComponent(spnRef12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRef13)
                    .addComponent(spnRef13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRef21)
                    .addComponent(spnRef21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRef22)
                    .addComponent(spnRef22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRef23)
                    .addComponent(spnRef23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUpdateReference))
        );

        pnlChartW.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlChartW.setPreferredSize(new java.awt.Dimension(2, 259));

        javax.swing.GroupLayout pnlChartWLayout = new javax.swing.GroupLayout(pnlChartW);
        pnlChartW.setLayout(pnlChartWLayout);
        pnlChartWLayout.setHorizontalGroup(
            pnlChartWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 848, Short.MAX_VALUE)
        );
        pnlChartWLayout.setVerticalGroup(
            pnlChartWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlChartQ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlChartQ.setPreferredSize(new java.awt.Dimension(2, 250));

        javax.swing.GroupLayout pnlChartQLayout = new javax.swing.GroupLayout(pnlChartQ);
        pnlChartQ.setLayout(pnlChartQLayout);
        pnlChartQLayout.setHorizontalGroup(
            pnlChartQLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlChartQLayout.setVerticalGroup(
            pnlChartQLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        spnPlotRange.setValue(300);

        chkMovingRange.setText("Moving Range");
        chkMovingRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMovingRangeActionPerformed(evt);
            }
        });

        lblExtraInfo.setEditable(false);
        lblExtraInfo.setBackground(new java.awt.Color(238, 238, 238));
        lblExtraInfo.setColumns(20);
        lblExtraInfo.setRows(8);
        lblExtraInfo.setTabSize(5);
        lblExtraInfo.setAutoscrolls(false);
        lblExtraInfo.setRequestFocusEnabled(false);

        javax.swing.GroupLayout pnlControlPanelLayout = new javax.swing.GroupLayout(pnlControlPanel);
        pnlControlPanel.setLayout(pnlControlPanelLayout);
        pnlControlPanelLayout.setHorizontalGroup(
            pnlControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlChartW, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)
                    .addComponent(pnlChartQ, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chkMovingRange)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spnPlotRange)
                    .addComponent(lblExtraInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlControlPanelLayout.setVerticalGroup(
            pnlControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlControlPanelLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkMovingRange)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnPlotRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblExtraInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE))
                    .addGroup(pnlControlPanelLayout.createSequentialGroup()
                        .addComponent(pnlChartW, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlChartQ, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tpnTabPane.addTab("Control Panel", pnlControlPanel);

        txtRawData.setEditable(false);
        txtRawData.setColumns(20);
        txtRawData.setFont(new java.awt.Font("Courier", 0, 14)); // NOI18N
        txtRawData.setLineWrap(true);
        txtRawData.setRows(5);
        jScrollPane1.setViewportView(txtRawData);

        selectRawDataBase.add(rbASCIInum);
        rbASCIInum.setText("ASCII (num)");
        rbASCIInum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbASCIInumActionPerformed(evt);
            }
        });

        selectRawDataBase.add(rbHEX);
        rbHEX.setSelected(true);
        rbHEX.setText("HEX");
        rbHEX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbHEXActionPerformed(evt);
            }
        });

        btnClearRawData.setText("Clear");
        btnClearRawData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearRawDataActionPerformed(evt);
            }
        });

        chkAutoscroll.setSelected(true);
        chkAutoscroll.setText("Autoscroll");
        chkAutoscroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAutoscrollActionPerformed(evt);
            }
        });

        selectRawDataBase.add(rbASCIIchar);
        rbASCIIchar.setText("ASCII (char)");

        javax.swing.GroupLayout pnlRawDataLayout = new javax.swing.GroupLayout(pnlRawData);
        pnlRawData.setLayout(pnlRawDataLayout);
        pnlRawDataLayout.setHorizontalGroup(
            pnlRawDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRawDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 915, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRawDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRawDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(chkAutoscroll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClearRawData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rbASCIInum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rbHEX, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(rbASCIIchar))
                .addContainerGap())
        );
        pnlRawDataLayout.setVerticalGroup(
            pnlRawDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRawDataLayout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(pnlRawDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbASCIInum)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbASCIIchar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbHEX)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClearRawData)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAutoscroll)
                .addContainerGap(437, Short.MAX_VALUE))
        );

        tpnTabPane.addTab("Raw Data", pnlRawData);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Communications"));

        cmbBaudRate.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "9600", "57600", "115200" }));
        cmbBaudRate.setSelectedIndex(2);
        cmbBaudRate.setToolTipText("");
        cmbBaudRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBaudRateActionPerformed(evt);
            }
        });

        jLabel2.setText("XBee Baud Rate:");

        jLabel1.setText("XBee Serial Port:");

        cmbPortSelect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPortSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPortSelectActionPerformed(evt);
            }
        });

        jLabel4.setText("Transmit Interval:");

        spnTxSkip.setModel(new javax.swing.SpinnerNumberModel(10, 0, 255, 1));

        jLabel6.setText("Receive Interval:");

        spnRxSkip.setModel(new javax.swing.SpinnerNumberModel());

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel1))
                .addGap(15, 15, 15)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbPortSelect, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spnTxSkip)
                    .addComponent(cmbBaudRate, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spnRxSkip))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbPortSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cmbBaudRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(spnTxSkip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(spnRxSkip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Logging"));

        chkLogData.setText("Enable data logging");
        chkLogData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLogDataActionPerformed(evt);
            }
        });

        txtLogFilePath.setText("log.dat");
        txtLogFilePath.setEnabled(false);
        txtLogFilePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLogFilePathActionPerformed(evt);
            }
        });
        txtLogFilePath.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtLogFilePathKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLogFilePathKeyReleased(evt);
            }
        });

        btnLogFileBrowse.setText("Browse...");
        btnLogFileBrowse.setEnabled(false);

        jLabel3.setText("Log file:");

        spnSkipFactor.setEnabled(false);
        spnSkipFactor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                spnSkipFactorPropertyChange(evt);
            }
        });

        jLabel10.setText("Skip factor:");

        jLabel14.setFont(jLabel14.getFont().deriveFont(jLabel14.getFont().getSize()-3f));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Note: the log file is saved only after the application is closed");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(txtLogFilePath)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLogFileBrowse))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkLogData)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnSkipFactor, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkLogData)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLogFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogFileBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnSkipFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addContainerGap())
        );

        jLabel5.setText("Desired sampling time (ms):");

        spnSamplingTime.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(30), Integer.valueOf(15), null, Integer.valueOf(1)));

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("SETTINGS:\n==================================================================\nCOMMUNICATIONS\n==================================================================\nXBee Serial Port: \nThe serial port to which the host XBee is connected.\n----------------------------------------------------------------------------------\nXBee Baud Rate:\nThe communication speed in bits/s between the host PC, XBee and Arduino. Changing this requires configuring both the XBees and Arduino.\n----------------------------------------------------------------------------------\nTransmit Interval:\nThe number of samples between each automatic reference update. Set to 0 to tur automatic reference updating off.\n----------------------------------------------------------------------------------\nReceive interval:\nThe number of samples between each Arduino transmission. How often the graphs are updated.\n==================================================================\nDATA LOGGING\n==================================================================\nLog file:\nThe name of the file to which the logged data will be stored. The \"Browse\" button is not yet functional.\n----------------------------------------------------------------------------------\nSkip factor:\nThe number of samples between each logged point. Use for very fast sampling times/receive intervals so as to reduce processing load.\n==================================================================\nDesired sampling time:\nThe desired Arduino sampling time in milliseconds. Ensure that this is long enough for the Arduino to perform all required tasks.\n==================================================================");
        jTextArea1.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jTextArea1);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Output:"));

        jLabel7.setText("Graph 1 (Top):");

        jLabel8.setText("Graph 2 (Bottom): ");

        cmbOutput1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Attitude (Quaternion)", "Attitude (Euler)", "Angular Rates", "Torques", "Forces", "Servo Angles", "Motor Speeds", "Velocity", "Position", "Acceleration" }));

        cmbOutput2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Attitude (Quaternion)", "Attitude (Euler)", "Angular Rates", "Torques", "Forces", "Servo Angles", "Motor Speeds", "Velocity", "Position", "Acceleration" }));
        cmbOutput2.setSelectedIndex(2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbOutput2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbOutput1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cmbOutput1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cmbOutput2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout pnlSettingsLayout = new javax.swing.GroupLayout(pnlSettings);
        pnlSettings.setLayout(pnlSettingsLayout);
        pnlSettingsLayout.setHorizontalGroup(
            pnlSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlSettingsLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(spnSamplingTime, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlSettingsLayout.setVerticalGroup(
            pnlSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addGroup(pnlSettingsLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(spnSamplingTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(111, Short.MAX_VALUE))
        );

        tpnTabPane.addTab("Settings", pnlSettings);

        btnQuit.setText("Quit");
        btnQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitActionPerformed(evt);
            }
        });

        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnConnect.setText("Connect");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        btnRun.setText("Run");
        btnRun.setEnabled(false);
        btnRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunActionPerformed(evt);
            }
        });

        jMenu2.setText("Window");

        mnuLog.setText("Hide Session Log");
        mnuLog.setToolTipText("");
        mnuLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLogActionPerformed(evt);
            }
        });
        jMenu2.add(mnuLog);

        mnuHUD.setText("Hide HUD Window");
        mnuHUD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHUDActionPerformed(evt);
            }
        });
        jMenu2.add(mnuHUD);

        mnuMenuToolbar.add(jMenu2);

        setJMenuBar(mnuMenuToolbar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpnTabPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnReset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQuit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnStop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConnect, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .addComponent(btnRun, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnConnect, btnReset, btnRun, btnStart, btnStop});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnStart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReset)
                        .addGap(36, 36, 36)
                        .addComponent(btnConnect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnQuit))
                    .addComponent(tpnTabPane))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        sessionLog.addSessionLog("Stop...\n");
        byte[] msg = {(byte) 0xFF, (byte) 0xFE};
        if (connected) {
            SerialComms.send(msg);
        }
        hudInfo.started(false);
        SerialComms.disconnect();
        connected = false;
    }//GEN-LAST:event_btnStopActionPerformed

    private void rbASCIInumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbASCIInumActionPerformed
    }//GEN-LAST:event_rbASCIInumActionPerformed

    private void cmbBaudRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBaudRateActionPerformed
        String baud = (String) cmbBaudRate.getSelectedItem();
        hudInfo.setComInfo((String) cmbPortSelect.getSelectedItem(), baud);
    }//GEN-LAST:event_cmbBaudRateActionPerformed

    private void btnUpdateReferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateReferenceActionPerformed
        updateReference();
    }//GEN-LAST:event_btnUpdateReferenceActionPerformed

    private static void updateReference() {
        byte[] msg = new byte[26];
        long cs = 0;
        float ref[] = {
            (Float) spnRef11.getValue(),
            (Float) spnRef12.getValue(),
            (Float) spnRef13.getValue(),
            (Float) spnRef21.getValue(),
            (Float) spnRef22.getValue(),
            (Float) spnRef23.getValue()};
        if (cmbScenario.getSelectedIndex() == 2) {

            float rho[] = {0, 0, 0, 0, 0, 0};

            double kt = 5.6992e-07;
            double kf = 7.4566e-05;
            double map[][] = {{0, 0, 20 / (21 * kf), 0, 2 / (3 * kf), (20 * kt) / (21 * kf * kf)},
                {0, 0, 20 / (21 * kf), -Math.sqrt(3) / (3 * kf), -1 / (3 * kf), (20 * kt) / (21 * kf * kf)},
                {0, 0, 20 / (21 * kf), Math.sqrt(3) / (3 * kf), -1 / (3 * kf), (20 * kt) / (21 * kf * kf)},
                {0, -40 / (21 * kf), 0, 0, -(40 * kt) / (21 * kf * kf), 1 / (3 * kf)},
                {(20 * Math.sqrt(3)) / (21 * kf), 20 / (21 * kf), 0, (20 * Math.sqrt(3) * kt) / (21 * kf * kf), (20 * kt) / (21 * kf * kf), 1 / (3 * kf)},
                {-(20 * Math.sqrt(3)) / (21 * kf), 20 / (21 * kf), 0, -(20 * Math.sqrt(3) * kt) / (21 * kf * kf), (20 * kt) / (21 * kf * kf), 1 / (3 * kf)}};

            double alpha[] = {0, 0, 0};
            double omega[] = {0, 0, 0};

            for (int i = 0; i < 6; i++) {
                rho[i] = 0;
                for (int j = 0; j < 6; j++) {
                    rho[i] += map[i][j] * ref[j];
                }
            }

            for (int i = 0; i < 3; i++) {
                omega[i] = Math.pow(Math.pow(rho[i], 2) + Math.pow(rho[i + 3], 2), 0.25);
                alpha[i] = Math.atan2(rho[i], rho[i + 3]);
            }

            sessionLog.addSessionLog(
                    String.format("Projected Servo/Motor values:\nα1=%6.4f rads\t\tα2=%6.4f rads\t\tα3=%6.4f rads\nω1=%1.2f rads/s\tω2=%1.2f rads/s\tω3=%1.2f rads/s\n",
                    alpha[0], alpha[1], alpha[2], omega[0], omega[1], omega[2]));

        }

        int i = 1;
        msg[0] = (byte) 0xCC;
        cs = 0;
        for (int j = 0; j < 6; j++) {
            for (byte c : ControlluinoInterface.convFtoBA(ref[j])) {
                msg[i++] = c;
                cs += c;
            }
        }
        msg[25] = (byte) ((256 - (cs % 256)));

        String s = "t=" + t + ": New Reference Packet: ";
        for (byte c : msg) {
            String temp = Integer.toHexString(c & 0xFF).toUpperCase();
            if (temp.length() == 1) {
                temp = 0 + temp;
            }
            s += temp.toUpperCase() + " ";
        }
        sessionLog.addSessionLog(s + "\n");

        if (connected) {
            SerialComms.send(msg);
        } else {
            sessionLog.addSessionLog("Not connected. Cannot transmit...\n");
        }
    }

    private void rbHEXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbHEXActionPerformed
    }//GEN-LAST:event_rbHEXActionPerformed

    private void btnClearRawDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearRawDataActionPerformed
        txtRawData.setText("");
    }//GEN-LAST:event_btnClearRawDataActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        spnRef11.setValue(0.0f);
        spnRef12.setValue(0.0f);
        spnRef13.setValue(0.0f);
        spnRef21.setValue(0.0f);
        spnRef22.setValue(0.0f);
        spnRef23.setValue(0.0f);
        btnUpdateReferenceActionPerformed(evt);
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitActionPerformed
        if (ControlluinoInterface.logger != null) {
            try {
                ControlluinoInterface.logger.close();
            } catch (IOException ex) {
            }
        }
        System.exit(0);
    }//GEN-LAST:event_btnQuitActionPerformed

    private void chkAutoscrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAutoscrollActionPerformed
    }//GEN-LAST:event_chkAutoscrollActionPerformed

    private void chkLogDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLogDataActionPerformed

        boolean enableLogging = chkLogData.isSelected();
        if (enableLogging) {
            hudInfo.setLoggingInfo(enableLogging, getLogFile(), getLogSkipFactor());
        }
        btnLogFileBrowse.setEnabled(enableLogging);
        spnSkipFactor.setEnabled(enableLogging);
        txtLogFilePath.setEnabled(enableLogging);

    }//GEN-LAST:event_chkLogDataActionPerformed

    private void chkMovingRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMovingRangeActionPerformed
        spnPlotRange.setEnabled(!chkMovingRange.isSelected());
        if (chkMovingRange.isSelected()) {
            plotRange = (int) spnPlotRange.getValue();
            plotRangeMin = t - plotRange > 0 ? t - plotRange : 0;
        }
    }//GEN-LAST:event_chkMovingRangeActionPerformed

    private void cmbPortSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPortSelectActionPerformed
        if (ControlluinoInterface.hudOpen) {
            hudInfo.setComInfo((String) cmbPortSelect.getSelectedItem(), (String) cmbBaudRate.getSelectedItem());
        }
    }//GEN-LAST:event_cmbPortSelectActionPerformed

    private void txtLogFilePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLogFilePathActionPerformed
    }//GEN-LAST:event_txtLogFilePathActionPerformed

    private void spnSkipFactorPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_spnSkipFactorPropertyChange
    }//GEN-LAST:event_spnSkipFactorPropertyChange

    private void txtLogFilePathKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLogFilePathKeyTyped
        hudInfo.setLoggingInfo(getLoggingStatus(), getLogFile(), getLogSkipFactor());
    }//GEN-LAST:event_txtLogFilePathKeyTyped

    private void txtLogFilePathKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLogFilePathKeyReleased
        hudInfo.setLoggingInfo(getLoggingStatus(), getLogFile(), getLogSkipFactor());
    }//GEN-LAST:event_txtLogFilePathKeyReleased

    private void cmbScenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbScenarioActionPerformed
        switch (cmbScenario.getSelectedIndex()) {
            case 0:
                lblRef11.setText("Roll (φ)");
                lblRef12.setText("Pitch (θ)");
                lblRef13.setText("Yaw (ψ)");
                lblRef21.setText("p (dφ/dt)");
                lblRef22.setText("q (dθ/dt)");
                lblRef23.setText("r (dψ/dt)");
                lblExtraInfo.setText("Quaternion Norm: 0");
                this.setTitle("Controlluino Interface - Attitude Control");
                break;
            case 1:
                lblRef11.setText("Roll (φ)");
                lblRef12.setText("Pitch (θ)");
                lblRef13.setText("Yaw (ψ)");
                lblRef21.setText("North (x)");
                lblRef22.setText("East (y)");
                lblRef23.setText("Up (z)");
                lblExtraInfo.setText("Quaternion Norm: 0");
                this.setTitle("Controlluino Interface - Postition Control");
                break;
            case 2:
                lblRef11.setText("τx (Nm)");
                lblRef12.setText("τy (Nm)");
                lblRef13.setText("τz (Nm)");
                lblRef21.setText("fx (Nm)");
                lblRef22.setText("fy (Nm)");
                lblRef23.setText("fz (Nm)");
                lblExtraInfo.setText("Servo/Motor values:\n0 rads\n0 rads\n0 rads\n0 rads/s\n0 rads/s\n0 rads/s");
                this.setTitle("Controlluino Interface - Force Control");
                break;
            default:
                break;
        }
    }//GEN-LAST:event_cmbScenarioActionPerformed

    private void mnuLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLogActionPerformed
        ControlluinoInterface.sessionLogOpen = !ControlluinoInterface.sessionLogOpen;
        ControlluinoInterface.log.setVisible(ControlluinoInterface.sessionLogOpen);
        if (ControlluinoInterface.sessionLogOpen) {
            mnuLog.setText("Hide Session Log");
        } else {
            mnuLog.setText("Show Session Log");
        }

        setWindowSizes();
    }//GEN-LAST:event_mnuLogActionPerformed

    private void mnuHUDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHUDActionPerformed
        ControlluinoInterface.hudOpen = !ControlluinoInterface.hudOpen;
        ControlluinoInterface.hud.setVisible(ControlluinoInterface.hudOpen);
        if (ControlluinoInterface.hudOpen) {
            mnuHUD.setText("Hide HUD Window");
        } else {
            mnuHUD.setText("Show HUD Window");
        }

        setWindowSizes();

    }//GEN-LAST:event_mnuHUDActionPerformed

    private void setWindowSizes() {
        if (ControlluinoInterface.sessionLogOpen && ControlluinoInterface.hudOpen) {
            ControlluinoInterface.gui.setSize(ControlluinoInterface.screenW - ControlluinoInterface.hud.getSize().width, ControlluinoInterface.screenH - 300);
            ControlluinoInterface.log.setSize(ControlluinoInterface.screenW - ControlluinoInterface.hud.getSize().width, 300);
        } else if (ControlluinoInterface.sessionLogOpen && !ControlluinoInterface.hudOpen) {
            ControlluinoInterface.gui.setSize(ControlluinoInterface.screenW, ControlluinoInterface.screenH - 300);
            ControlluinoInterface.log.setSize(ControlluinoInterface.screenW, 300);
        } else if (!ControlluinoInterface.sessionLogOpen && ControlluinoInterface.hudOpen) {
            ControlluinoInterface.gui.setSize(ControlluinoInterface.screenW - ControlluinoInterface.hud.getSize().width, ControlluinoInterface.screenH);
        } else if (!ControlluinoInterface.sessionLogOpen && !ControlluinoInterface.hudOpen) {
            ControlluinoInterface.gui.setSize(ControlluinoInterface.screenW, ControlluinoInterface.screenH);
        }
    }

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        hudInfo.setComInfo((String) cmbPortSelect.getSelectedItem(), (String) cmbBaudRate.getSelectedItem());
        if (!connected) {
            connected = SerialComms.connect((String) cmbPortSelect.getSelectedItem(), Integer.parseInt((String) cmbBaudRate.getSelectedItem()));
        } else {
            SerialComms.disconnect();
            connected = false;
        }

        if (!connected) {
            btnConnect.setText("Connect");
        } else {
            btnConnect.setText("Disconnect");
        }
    }//GEN-LAST:event_btnConnectActionPerformed

    private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed
        cmbScenario.setEnabled(false);
        String logTitles = "";
        String plotTitles[][] = {{"", "", "", ""}, {"", "", "", ""}};
        String graphTitles[] = {"", ""};

        switch (cmbOutput1.getSelectedIndex()) {
            case 0: {
                plotTitles[0][0] = "q0";
                plotTitles[0][1] = "q1";
                plotTitles[0][2] = "q2";
                plotTitles[0][3] = "q3";
                wLength = 4;
                break;
            }
            case 1:
                plotTitles[0][0] = "φ";
                plotTitles[0][1] = "θ";
                plotTitles[0][2] = "ψ";
                wLength = 3;
                break;
            case 2:
                plotTitles[0][0] = "p";
                plotTitles[0][1] = "q";
                plotTitles[0][2] = "r";
                wLength = 3;
                break;
            case 3:
                plotTitles[0][0] = "τp";
                plotTitles[0][1] = "τq";
                plotTitles[0][2] = "τr";
                wLength = 3;
                break;
            case 4:
                plotTitles[0][0] = "fx";
                plotTitles[0][1] = "fy";
                plotTitles[0][2] = "fz";
                wLength = 3;
                break;
            case 5:
                plotTitles[0][0] = "α1";
                plotTitles[0][1] = "α2";
                plotTitles[0][2] = "α3";
                wLength = 3;
                break;
            case 6:
                plotTitles[0][0] = "ω1";
                plotTitles[0][1] = "ω2";
                plotTitles[0][2] = "ω3";
                wLength = 3;
                break;
            case 7:
                plotTitles[0][0] = "vx";
                plotTitles[0][1] = "vy";
                plotTitles[0][2] = "vz";
                wLength = 3;
                break;
            case 8:
                plotTitles[0][0] = "z";
                plotTitles[0][1] = "y";
                plotTitles[0][2] = "x";
                wLength = 3;
                break;
            case 9:
                plotTitles[0][0] = "ax";
                plotTitles[0][1] = "ay";
                plotTitles[0][2] = "az";
                wLength = 3;
                break;
        }

        switch (cmbOutput2.getSelectedIndex()) {
            case 0: {
                plotTitles[1][0] = "q0";
                plotTitles[1][1] = "q1";
                plotTitles[1][2] = "q2";
                plotTitles[1][3] = "q3";
                qLength = 4;
                break;
            }
            case 1:
                plotTitles[1][0] = "φ";
                plotTitles[1][1] = "θ";
                plotTitles[1][2] = "ψ";
                qLength = 3;
                break;
            case 2:
                plotTitles[1][0] = "p";
                plotTitles[1][1] = "q";
                plotTitles[1][2] = "r";
                qLength = 3;
                break;
            case 3:
                plotTitles[1][0] = "τp";
                plotTitles[1][1] = "τq";
                plotTitles[1][2] = "τr";
                qLength = 3;
                break;
            case 4:
                plotTitles[1][0] = "fx";
                plotTitles[1][1] = "fy";
                plotTitles[1][2] = "fz";
                qLength = 3;
                break;
            case 5:
                plotTitles[1][0] = "α1";
                plotTitles[1][1] = "α2";
                plotTitles[1][2] = "α3";
                qLength = 3;
                break;
            case 6:
                plotTitles[1][0] = "ω1";
                plotTitles[1][1] = "ω2";
                plotTitles[1][2] = "ω3";
                qLength = 3;
                break;
            case 7:
                plotTitles[1][0] = "vx";
                plotTitles[1][1] = "vy";
                plotTitles[1][2] = "vz";
                qLength = 3;
                break;
            case 8:
                plotTitles[1][0] = "z";
                plotTitles[1][1] = "y";
                plotTitles[1][2] = "x";
                qLength = 3;
                break;
            case 9:
                plotTitles[1][0] = "ax";
                plotTitles[1][1] = "ay";
                plotTitles[1][2] = "az";
                qLength = 3;
                break;
        }

        graphTitles[0] = (String) cmbOutput1.getSelectedItem();
        graphTitles[1] = (String) cmbOutput2.getSelectedItem();

        createPlots(plotTitles, graphTitles);
 
        if (connected) {

            new Thread() {
                @Override
                public void run() {

                    byte sH = (byte) ((int) spnSamplingTime.getValue());
                    byte sc = (byte) cmbScenario.getSelectedIndex();
                    byte rxi = (byte) rxSkip;
                    byte out1 = (byte) cmbOutput1.getSelectedIndex();
                    byte out2 = (byte) cmbOutput2.getSelectedIndex();
                    byte cs = (byte) ((256 - ((sH + sc + rxi + out1 + out2) % 256)));
                    byte[] msgSetup = {0x22, rxi, sc, sH, out1, out2, cs};

                    SerialComms.send(msgSetup);

                    sessionLog.addSessionLog("Sampling Time: " + msgSetup[3] + "ms\n");
                    sessionLog.addSessionLog("Arduino Transmission Interval: " + msgSetup[1] + "\n");
                    sessionLog.addSessionLog("Reference Update Interval: " + txSkip + "\n");
                    sessionLog.addSessionLog("Scenario: " + (String) cmbScenario.getSelectedItem() + " (" + msgSetup[2] + ")\n");
                    sessionLog.addSessionLog("Output 1 (Top): " + (String) cmbOutput1.getSelectedItem() + " (" + out1 + ")\n");
                    sessionLog.addSessionLog("Output 2 (Top): " + (String) cmbOutput2.getSelectedItem() + " (" + out2 + ")\n");

                    byte[] msgSensor = {(byte) 0xEE};
                    SerialComms.send(msgSensor);
                    sessionLog.addSessionLog("Initialise sensor...\n");
                    while (!mainGUI.sensorInit);

                    byte[] msgMotors = {(byte) 0xBB};
                    SerialComms.send(msgMotors);
                    sessionLog.addSessionLog("Initialise motors...\n");
                    while (!mainGUI.motorsInit);

                    byte[] msgStart = {(byte) 0xDD};
                    hudInfo.started(true);
                    SerialComms.send(msgStart);
                    sessionLog.addSessionLog("Start...\n");
                }
            }.start();

        }
    }//GEN-LAST:event_btnRunActionPerformed

    public static String getComPort() {
        return (String) cmbPortSelect.getSelectedItem();
    }

    public static int getDesiredSamplingTime() {
        return (int) spnSamplingTime.getValue();
    }

    public static String getBaudRate() {
        return (String) cmbBaudRate.getSelectedItem();
    }

    public static String getLogFile() {
        if (chkLogData.isSelected()) {
            return txtLogFilePath.getText();
        } else {
            return "";
        }
    }

    public static boolean getLoggingStatus() {
        return chkLogData.isSelected();
    }

    public static int getLogSkipFactor() {
        return (int) spnSkipFactor.getValue();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton btnConnect;
    private static javax.swing.JButton btnLogFileBrowse;
    javax.swing.JButton btnReset;
    private static javax.swing.JButton btnRun;
    javax.swing.JButton btnStart;
    javax.swing.JButton btnStop;
    private static javax.swing.JCheckBox chkAutoscroll;
    private static javax.swing.JCheckBox chkLogData;
    private static javax.swing.JCheckBox chkMovingRange;
    private static javax.swing.JComboBox cmbBaudRate;
    private static javax.swing.JComboBox cmbOutput1;
    private static javax.swing.JComboBox cmbOutput2;
    private static javax.swing.JComboBox cmbPortSelect;
    private static javax.swing.JComboBox cmbScenario;
    private static javax.swing.JPanel jPanel1;
    private static javax.swing.JTextArea lblExtraInfo;
    private static javax.swing.JLabel lblRef11;
    private static javax.swing.JLabel lblRef12;
    private static javax.swing.JLabel lblRef13;
    private static javax.swing.JLabel lblRef21;
    private static javax.swing.JLabel lblRef22;
    private static javax.swing.JLabel lblRef23;
    private static javax.swing.JMenuItem mnuHUD;
    private static javax.swing.JMenuItem mnuLog;
    private static javax.swing.JMenuBar mnuMenuToolbar;
    private static javax.swing.JPanel pnlChartQ;
    private static javax.swing.JPanel pnlChartW;
    private static javax.swing.JPanel pnlControlPanel;
    private static javax.swing.JRadioButton rbASCIIchar;
    private static javax.swing.JRadioButton rbASCIInum;
    private static javax.swing.JRadioButton rbHEX;
    javax.swing.ButtonGroup selectRawDataBase;
    private static javax.swing.JSpinner spnPlotRange;
    private static javax.swing.JSpinner spnRef11;
    private static javax.swing.JSpinner spnRef12;
    private static javax.swing.JSpinner spnRef13;
    private static javax.swing.JSpinner spnRef21;
    private static javax.swing.JSpinner spnRef22;
    private static javax.swing.JSpinner spnRef23;
    private static javax.swing.JSpinner spnRxSkip;
    private static javax.swing.JSpinner spnSamplingTime;
    private static javax.swing.JSpinner spnSkipFactor;
    private static javax.swing.JSpinner spnTxSkip;
    private static javax.swing.JTabbedPane tpnTabPane;
    private static javax.swing.JTextField txtLogFilePath;
    static javax.swing.JTextArea txtRawData;
    // End of variables declaration//GEN-END:variables
}
