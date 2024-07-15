package org.albianj.common.config;

import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.io.File;
import java.lang.reflect.Field;

public class PropertiesFile {
    public <T> T toObject(String filename, Class<T> cls) {
        try {
            if(StringsUtil.isNullEmptyTrimmed(filename)){
                return null;
            }
            File file = new File(filename);
            if(!file.exists()) {
                return null;
            }
            Configurations configs = new Configurations();
            Configuration cfm = configs.properties(file);

            T newObj = cls.getConstructor().newInstance();
            Field[] fields = cls.getDeclaredFields();
            if(!SetUtil.isEmpty(fields)) {
                for(Field f : fields){
                    f.setAccessible(true);
                    String cfName = null;
                    if(f.isAnnotationPresent(ConfigItem.class)) {
                        ConfigItem cfi = f.getAnnotation(ConfigItem.class);
                        if(StringsUtil.isNullEmptyTrimmed(cfi.Name())) {
                            cfName = f.getName();
                        } else {
                            cfName = cfi.Name();
                        }

                        Object value =  cfm.getProperty(cfName);
                        if(null  != value) {
                            f.set(newObj, cfm.get(f.getType(), cfName));
                        }
                    }
                }
            }
            return newObj;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
