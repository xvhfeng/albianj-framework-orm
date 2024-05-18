package org.albianj.common.config;

import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.apache.commons.configuration2.Configuration;

import java.lang.reflect.Field;

public class ConfigUtil {
    private <T> T propToObj(Configuration cf, Class<T> cls) {
        try {
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

                        Object value =  cf.getProperty(cfName);
                        if(null  != value) {
                            f.set(newObj, cf.get(f.getType(), cfName));
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
