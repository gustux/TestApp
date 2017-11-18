package com.vonquednow;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class LogFile {
    //set fields
    private Path path;
    private String version, project, status;
    private String user;
    private String timestamp;

    //constructors
    public LogFile() {

    }

    LogFile(String Strpath, String project, String version, String status){
        this.path = Paths.get(Strpath);
        this.project = project;
        this.version = version;
        this.status = status;
        this.user = System.getProperty("user.name");
        this.timestamp = new SimpleDateFormat("dd/MM/yyyy HH.mm.ss").format(new Date());
    }

    //methods
    public void setPath(String newpath) {
        this.path = Paths.get(newpath);
    }

    public void setVersion(String newversion){
        this.version = newversion;
    }

    public void setProject(String newproject){
        this.project = newproject;
    }

    void setStatus(String newstatus){
        this.status = newstatus;
    }

    void write(){
        try{
            this.user = System.getProperty("user.name");
            this.timestamp = new SimpleDateFormat("dd/MM/yyyy HH.mm.ss").format(new Date());
            Files.write(path, Collections.singletonList(timestamp + "," + project + "," + version + "," + user + "," + status), StandardCharsets.UTF_8,
                    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (final IOException ioe){
            ioe.printStackTrace();
        }
    }
}