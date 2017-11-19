package com.vonquednow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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
    private String project, version, status = null, strOutput, strError, logmsg;
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
                    scrollpane.getViewport().add(txtAreaError);
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
                e1.printStackTrace();
            }
            List<String> items = Arrays.asList(logmsg.split("\\s*,\\s*"));
            txtAreaLog.setText(items.toString());
            scrollpane.getViewport().add(txtAreaLog);
            JOptionPane.showMessageDialog(null,
                    scrollpane,
                    "Log",
                    JOptionPane.INFORMATION_MESSAGE);
        });
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
