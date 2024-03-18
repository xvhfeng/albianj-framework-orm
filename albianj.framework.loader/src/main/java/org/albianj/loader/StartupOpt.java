package org.albianj.loader;

public enum StartupOpt {
    CommandLine(1,"CommandLine"),
    Application(2,"Application");

    private int key;
    private String label;

    StartupOpt(int key,String label) {
        this.key = key;
        this.label = label;
    }

    public String getLabel(){
        return this.label;
    }
}
