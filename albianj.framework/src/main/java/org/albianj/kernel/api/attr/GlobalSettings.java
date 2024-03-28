package org.albianj.kernel.api.attr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Properties;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSettings {
    private Class<?> mainClzz;
    private String configurtionFolder;
    private Properties kernelProps;
    private int argc = 0;
    private String[] argv;

    public <T> T getPropValue(String key,T defVal){
        if(!kernelProps.containsKey(key)) {
            return defVal;
        }
        return (T) kernelProps.get(key);
    }

    public GlobalSettings(Class<?> mainClzz,String configurtionFolder){
        this.mainClzz = mainClzz;
        this.configurtionFolder = configurtionFolder;
    }
}
