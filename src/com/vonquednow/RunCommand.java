package com.vonquednow;

import java.io.IOException;

public class RunCommand {
    //set fields
    private String command, project, version;
    Process process;

    //constructors
    public RunCommand(){

    }

    RunCommand(String project, String version){
        this.project = project;
        this.version = version;
        this.command = "gactions test-internal --project " + this.project + " --version " + this.version;
    }

    //mthods
    public void setProject(String newpoject){
        this.project = newpoject;
    }

    public void setVersion(String newversion){
        this.version = newversion;
    }

    void run(){
        try {
            this.process = Runtime.getRuntime().exec(this.command);
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
