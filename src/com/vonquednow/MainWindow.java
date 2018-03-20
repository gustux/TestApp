package com.vonquednow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;


public class MainWindow {
    private JButton buttonDeploy;
    private JPanel panelMain;
    private JTextField txtProjectID;
    private JTextField txtVersion;
    private JLabel labelID;
    private JLabel labelVer;
    private JButton buttonClose;
    private JButton buttonLog;
    private JButton buttonClear;
    private String project;
    private String version;
    private String status = null;
    private String strOutput;
    private String strError;
    private String logmsg;
    private String STRPATH = "log3.csv";
    private LogFile logger;



    private MainWindow() {
        //Set Texts
        buttonDeploy.setText("Deploy");
        buttonLog.setText("View Log");
        buttonClose.setText("Close");
        buttonClear.setText("Clear");
        labelID.setText("Project ID:");
        labelVer.setText("Version No.:");

        //Create MsgBox components
        JOptionPane popupMsg = new JOptionPane();
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.setSize(500, 500);
        JTextArea txtAreaError = new JTextArea();
        JTextArea txtAreaLog = new JTextArea();
        JTextArea txtAreaOutput = new JTextArea();

        //Set Dimensions
        txtAreaOutput.setSize(500, 500);
        txtAreaError.setSize(500, 500);
        txtAreaLog.setSize(500,500);
        buttonClear.setSize(50,10);
        //Set Wrap
        txtAreaLog.setLineWrap(true);
        txtAreaOutput.setLineWrap(true);
        txtAreaError.setLineWrap(true);

        buttonDeploy.addActionListener(e -> {
            //get texts from boxes
            project = txtProjectID.getText();
            version = txtVersion.getText();
            if (project.length() < 1 || version.length() < 1 || version.matches("[A-Za-z]")){
                JOptionPane.showMessageDialog(null,
                        "Please enter a valid Project ID and Version No.",
                        "Information",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                //try to push app to test enviroment
                RunCommand deploy = new RunCommand(project, version);
                deploy.run();
                //create log writer
                logger = new LogFile(STRPATH, project, version, status);
                //Set console text readers
                BufferedReader output = new BufferedReader(new
                        InputStreamReader(deploy.process.getInputStream()));
                BufferedReader error = new BufferedReader(new
                        InputStreamReader(deploy.process.getErrorStream()));

                //Get console output
                strOutput = output.lines().collect(Collectors.joining("\n"));
                strError = error.lines().collect(Collectors.joining("\n"));

                //Display message depending on exit status
                if (strOutput.length() > 40) {
                    logger.setStatus("SUCCESS");
                    logger.write();
                    txtAreaOutput.setText(strOutput);
                    scrollpane.getViewport().add(txtAreaOutput);
                    JOptionPane.showMessageDialog(null,
                            scrollpane,
                            "Information",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    logger.setStatus("FAIL");
                    logger.write();
                    txtAreaError.setText(strError);
                    JOptionPane.showMessageDialog(null,
                            scrollpane,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        }
        );

        //Close button
        buttonClose.addActionListener(e -> System.exit(0));

        //Clear button
        buttonClear.addActionListener(e -> {
            txtProjectID.setText("");
            txtVersion.setText("");
        });

        //Log button
        buttonLog.addActionListener((ActionEvent e) -> {
            try {
                logmsg = Files.readAllLines (Paths.get(STRPATH)).toString();
            } catch (IOException e1) {
                int reply = JOptionPane.showConfirmDialog(null,
                        "Log file not found, create?",
                        "Information",
                        JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_NO_OPTION){
                    //IF YES create the File
                    try {
                        new FileOutputStream("log3.csv", true).close();
                        JOptionPane.showMessageDialog(null,"Log file created.");
                    } catch (IOException e2) {
                        JOptionPane.showMessageDialog(null,
                                "Error: " + e2.toString(),
                                "Information",
                                JOptionPane.WARNING_MESSAGE);
                        e2.printStackTrace();
                    }
                } else {
                  JOptionPane.showMessageDialog(null,"No Log file created!");
                }
                /*
                //Error message if file not found or any other exception.
                JOptionPane.showMessageDialog(null,
                        "Log not found: " + e1.toString(),
                        "Information",
                        JOptionPane.WARNING_MESSAGE);
                */
                e1.printStackTrace();
            }
            //pretty printo into JFrame
            //Using JTable and List
            JFrame frame = new JFrame("Log");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            T1Data newContentPane = new T1Data();
            frame.setContentPane(newContentPane);
            frame.setSize(700,170);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);

            /*
            //Using JFrame and List
            JFrame f = new JFrame("Log");
            List<String> items = Arrays.asList(logmsg.split("\\s*,\\s*"));
            String[] data = items.toArray(new String[0]);
            f.add(new JList(data));
            f.pack();
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
            */

            /*
            //Using scrollPane and List
            List<String> items = Arrays.asList(logmsg.split("\\s*,\\s*"));
            txtAreaLog.setText(items.toString());
            scrollpane.getViewport().add(txtAreaLog);
            JOptionPane.showMessageDialog(null,
                    scrollpane,
                    "Log",
                    JOptionPane.INFORMATION_MESSAGE);
            */
        });
    }

    public class T1Data extends JPanel {
        private final JTable table;

        T1Data() {
            super(new BorderLayout(3, 3));
            this.table = new JTable(new MyModel());
            this.table.setPreferredScrollableViewportSize(new Dimension(700, 70));
            this.table.setFillsViewportHeight(true);
            this.table.setCellSelectionEnabled(true);
            JPanel ButtonOpen = new JPanel(new FlowLayout(FlowLayout.CENTER));
            add(ButtonOpen, BorderLayout.SOUTH);
            // Create the scroll pane and add the table to it.
            JScrollPane scrollPane = new JScrollPane(table);
            // Add the scroll pane to this panel.
            add(scrollPane, BorderLayout.CENTER);
            // add a nice border
            setBorder(new EmptyBorder(5, 5, 5, 5));
            CSVFile Rd = new CSVFile();
            MyModel NewModel = new MyModel();
            this.table.setModel(NewModel);
            File DataFile = new File("log2.csv");
            ArrayList<String[]> Rs2 = Rd.ReadCSVfile(DataFile);
            NewModel.AddCSVData(Rs2);
            System.out.println("Rows: " + NewModel.getRowCount());
            System.out.println("Cols: " + NewModel.getColumnCount());
        }

        // Method for reading CSV file
        class CSVFile {
            private final ArrayList<String[]> Rs = new ArrayList<>();
            private String[] OneRow;

            ArrayList<String[]> ReadCSVfile(File DataFile) {
                try {
                    BufferedReader brd = new BufferedReader(new FileReader(DataFile));
                    while (brd.ready()) {
                        String st = brd.readLine();
                        OneRow = st.split(",|\\s|;");
                        Rs.add(OneRow);
                        System.out.println(Arrays.toString(OneRow));
                    } // end of while
                } // end of try
                catch (Exception e) {
                    String errmsg = e.getMessage();
                    System.out.println("File not found:" + errmsg);
                } // end of Catch
                return Rs;
            }// end of ReadFile method
        }// end of CSVFile class

        class MyModel extends AbstractTableModel {
            private final String[] columnNames = { "DATE", "TIME", "PROJECTID", "VERSION", "LDAP", "OUTCOME" };
            private ArrayList<String[]> Data = new ArrayList<>();

            void AddCSVData(ArrayList<String[]> DataIn) {
                this.Data = DataIn;
                this.fireTableDataChanged();
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;// length;
            }

            @Override
            public int getRowCount() {
                return Data.size();
            }

            @Override
            public String getColumnName(int col) {
                return columnNames[col];
            }

            @Override
            public Object getValueAt(int row, int col) {
                return Data.get(row)[col];
            }
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("TestApp");
        frame.setContentPane(new MainWindow().panelMain);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
