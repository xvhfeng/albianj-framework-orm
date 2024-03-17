package org.albianj.api.kernel.attr;

public class ApplicationSettings {
    private static GlobalSettings globalSettings;

    public static GlobalSettings getGlobalSettings(){
        return globalSettings;
    }

    public static void setGlobalSettings(GlobalSettings globalSettings){
        ApplicationSettings.globalSettings = globalSettings;
    }
}
