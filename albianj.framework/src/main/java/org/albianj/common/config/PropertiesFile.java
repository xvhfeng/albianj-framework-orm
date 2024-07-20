package org.albianj.common.config;

import org.albianj.AblServRouter;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.common.spring.SpringPropertyPlaceholderHelper;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class PropertiesFile {
    private SpringPropertyPlaceholderHelper holderHelper = new SpringPropertyPlaceholderHelper("${", "}", ":", '$',false);
    private Map<String,String> map;

    public  <T> T load(String  filename,Class<T> cls){
        return load(filename,cls,null,null);
    }

    public  <T> T load( String  filename,Class<T> cls,IConfigParserBefore parerBefore,IConfigInitAfter initAfter) {
        map = new HashMap<>();

        if(null != parerBefore){
            parerBefore.initBefore(filename,map);
        }

        String path = FilenameUtils.getFullPathNoEndSeparator(filename);
        map.put("configpath",path);
        String userDir = System.getProperty("user.dir");
        map.put("workpath",userDir);

        String envName = System.getProperty("Env");
        if(StringsUtil.isNullEmptyTrimmed(envName)) {
            envName = System.getenv("Env");
        }
        if(StringsUtil.isNotEmptyTrimmed(envName)) {
            map.put("env",envName);
        }

        try {

            Configurations configs = new Configurations();
            Configuration cfm = configs.properties(new File(filename));

            T newObj = cls.getConstructor().newInstance();
            Field[] fields = cls.getDeclaredFields();
            if(!SetUtil.isEmpty(fields)) {
                for(Field f : fields){
                    try {
                        f.setAccessible(true);
                        String cfName = null;
                        if (f.isAnnotationPresent(ConfigItem.class)) {
                            ConfigItem cfi = f.getAnnotation(ConfigItem.class);
                            if(cfi.IsSkip()) {
                                continue;
                            }
                            if (StringsUtil.isNullEmptyTrimmed(cfi.Name())) {
                                cfName = f.getName();
                            } else {
                                cfName = cfi.Name();
                            }

                            Object value = cfm.getProperty(cfName);
                            if (!cfi.MaybeEmpty() && (null == value || StringsUtil.isNullEmptyTrimmed(value.toString()))) {
                                throw new RuntimeException(StringsUtil.nonIdxFmt("Config item:{} must have value.", cfName));
                            }
                            if (null != value && StringsUtil.isNotEmptyTrimmed(value.toString()) && f.getType().isAssignableFrom(String.class)) {
                                f.set(newObj, holderHelper.replacePlaceholders(value.toString(), map::get));
                            } else {
                                f.set(newObj, cfm.get(f.getType(), cfName));
                            }
                        }
                    }catch (Exception t){
                        AblServRouter.logAndThrowAgain("Startup", LogLevel.Error,t,"parser field by name:{} is fail.",f.getName());
                    }
                }
            }

            if(null != initAfter) {
                return initAfter.init(newObj);
            }
            return newObj;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
