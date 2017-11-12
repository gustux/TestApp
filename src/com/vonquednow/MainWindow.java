package com.vonquednow;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class MainWindow {
    private JButton buttonDeploy;
    private JPanel panelMain;
    private JTextField txtProjectID;
    private JTextField txtVersion;
    private JLabel labelID;
    private JLabel labelVer;
    private JButton buttonClose;
    private String project, version, status = null, strOutput, strError;

    private MainWindow() {
        //Set Texts
        buttonDeploy.setText("Deploy");
        buttonClose.setText("Close");
        labelID.setText("Project ID:");
        labelVer.setText("Version No.:");

        //Create MsgBox components
        JOptionPane popupMsg = new JOptionPane();
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.setSize(500, 500);
        JTextArea txtAreaError = new JTextArea();
        JTextArea txtAreaOutput = new JTextArea();
        //Set Dimensions
        txtAreaOutput.setSize(200, 200);
        txtAreaOutput.setSize(500, 500);
        txtAreaError.setSize(500, 500);
        popupMsg.setSize(500, 500);

        buttonDeploy.addActionListener(e -> {
            //get texts from boxes
            project = txtProjectID.getText();
            version = txtVersion.getText();
            //try to push app to test enviroment
            RunCommand deploy = new RunCommand(project, version);
            deploy.run();
            //create log writer
            AppendCSV write2file = new AppendCSV("log3.csv", project, version, status);
            //Set console text readers
            BufferedReader output = new BufferedReader(new
                    InputStreamReader(deploy.process.getInputStream()));
            BufferedReader error = new BufferedReader(new
                    InputStreamReader(deploy.process.getErrorStream()));

            //Get console output
            strOutput = output.lines().collect(Collectors.joining("\n"));
            strError = error.lines().collect(Collectors.joining("\n"));

            //Display message depending on exit status
            if (strOutput.length() > 1) {
                write2file.setStatus("SUCCESS");
                write2file.write();
                txtAreaOutput.setText(strOutput);
                scrollpane.getViewport().add(txtAreaOutput);
                JOptionPane.showMessageDialog(null,
                        scrollpane,
                        "Information",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                write2file.setStatus("FAIL");
                write2file.write();
                txtAreaError.setText(strError);
                scrollpane.getViewport().add(txtAreaError);
                JOptionPane.showMessageDialog(null,
                        scrollpane,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        });
        buttonClose.addActionListener(e -> System.exit(0));
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
