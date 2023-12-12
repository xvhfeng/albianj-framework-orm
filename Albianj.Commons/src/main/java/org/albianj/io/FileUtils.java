package org.albianj.io;

import java.io.File;

public class FileUtils {
    public static boolean isFileOrFolderExist(String path){
        File f = new File(path);
        return f.exists();
    }
}
