package org.albianj.kernel.servs.impl;

import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.ServRouter;
import org.albianj.kernel.anno.AblServAnno;
import org.albianj.kernel.attr.GlobalSettings;
import org.albianj.kernel.servs.IConfigServ;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.io.File;
import java.io.FileInputStream;

@AblServAnno
public class ConfigServ implements IConfigServ {

    private static String[] supportedSuffix = {
            ".xml",
            ".properties",
            ".yaml",
            ".prop",
            ".yam",

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

    /**
     * 统一规整配置文件
     * @param configFilename 配置文件全路径
     * @return
     */
    @Override
    public CompositeConfiguration neatConfigurtion(String configFilename) {
        Configurations configs = new Configurations();
        CompositeConfiguration config = new CompositeConfiguration();
        try {
            // 根据文件扩展名选择解析器
            if (configFilename.endsWith(".xml")) {
                Configuration xmlConfig = configs.xml(configFilename);
                config.addConfiguration(xmlConfig);
            } else if (configFilename.endsWith(".yaml") || configFilename.endsWith(".yml")) {
                YAMLConfiguration yamlConfig = new YAMLConfiguration();
                yamlConfig.read(new FileInputStream(configFilename));
                config.addConfiguration(yamlConfig);
            } else if (configFilename.endsWith(".properties") || configFilename.endsWith(".prop")) {
                Configuration propConfig = configs.properties(configFilename);
                config.addConfiguration(propConfig);
            } else {
                ServRouter.throwIax("configFilename",configFilename);
            }

            return config;
        } catch (Throwable e) {
            ServRouter.throwAgain(StringsUtil.nonIdxFormat("error by neat config file -> {}",configFilename),e);
        }
        return  null;
    }
}
