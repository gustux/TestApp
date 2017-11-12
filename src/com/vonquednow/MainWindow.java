package com.vonquednow;

import java.io.*;
import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

public class MainWindow {
    private JButton buttonDeploy;
    private JPanel panelMain;
    private JTextField txtProjectID;
    private JTextField txtVersion;
    private JLabel labelID;
    private JLabel labelVer;
    private JButton buttonClose;
    private String project, version, command, timestamp, user, strOutput, strError;

    private MainWindow() {
        //Set Texts
        buttonDeploy.setText("Deploy");
        buttonClose.setText("Close");
        labelID.setText("Project ID:");
        labelVer.setText("Version No.:");

        //Create MsgBox components
        JOptionPane popupMsg = new JOptionPane();
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.setSize(500,500);
        JTextArea txtAreaError = new JTextArea();
        JTextArea txtAreaOutput = new JTextArea();
        //Set Dimensions
        txtAreaOutput.setSize(200, 200);
        txtAreaOutput.setSize(500, 500);
        txtAreaError.setSize(500, 500);
        popupMsg.setSize(500,500);

        buttonDeploy.addActionListener(e -> {
            //Save CSV file with these columns: timestamp, projectid, version, username
            project = txtProjectID.getText();
            version = txtVersion.getText();
            user = System.getProperty("user.name");
            timestamp = new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date());
            try {
                final Path path = Paths.get("log.csv");
                Files.write(path, Collections.singletonList(timestamp + "," + project + "," + version + "," + user), StandardCharsets.UTF_8,
                        Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            } catch (final IOException ioe) {
                ioe.printStackTrace();
            }
            //Push app to test enviroment
            command = "gactions test-internal --project " + project + " --version " + version;
            try {
                //Create a process to run the command
                Process p = Runtime.getRuntime().exec(command);

                //Set console text readers
                BufferedReader output = new BufferedReader(new
                        InputStreamReader(p.getInputStream()));
                BufferedReader error = new BufferedReader(new
                        InputStreamReader(p.getErrorStream()));

                //Get console output
                strOutput =  output.lines().collect(Collectors.joining("\n"));
                strError =  error.lines().collect(Collectors.joining("\n"));

                //Display message depending on exit status
                if (strOutput.length() > 1) {
                    txtAreaOutput.setText(strOutput);
                    scrollpane.getViewport().add(txtAreaOutput);
                    JOptionPane.showMessageDialog(null,
                            scrollpane,
                            "Information",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    txtAreaError.setText(strError);
                    scrollpane.getViewport().add(txtAreaError);
                    JOptionPane.showMessageDialog(null,
                            scrollpane,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        buttonClose.addActionListener(e -> System.exit(0));
    }


    public static void main(String[] args){
        JFrame frame = new JFrame("TestApp");
        frame.setContentPane(new MainWindow().panelMain);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
