package org.albianj.kernel.servs.impl;

import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.anno.AblServAnno;
import org.albianj.kernel.attr.GlobalSettings;
import org.albianj.kernel.servs.IConfigServ;

import java.io.File;

@AblServAnno
public class ConfigServ implements IConfigServ {

    private static String[] supportedSuffix = {
            ".xml",
            ".properties",
            ".yaml",
            ".prop",
    };

    @Override
    public String decideConfigFilename(GlobalSettings settings, String filenameWithoutSuffix){
        String configPath =  settings.getConfigPath();
        String filename = null;
        for (String suffix: supportedSuffix) {
            String fname = (StringsUtil.nonIdxFormat("{}{}{}{}", configPath, File.separator, filenameWithoutSuffix, suffix));
           if(new File(fname).exists()){
               filename = fname;
               break;
           }
        }
        return filename;
    }

}
