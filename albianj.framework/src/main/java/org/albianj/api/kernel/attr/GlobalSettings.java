package org.albianj.api.kernel.attr;

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
    private String[] servAnnoPkgs;
    private String[] entityAnnoPkgs;

    private static GlobalSettings _inst;
    static {
        _inst = new GlobalSettings();
    }
    public static GlobalSettings getInst(){
        return _inst;
    }

    public <T> T getPropValue(String key,T defVal){
        if(null == kernelProps) {
            return defVal;
        }
        if(!kernelProps.containsKey(key)) {
            return defVal;
        }
        return (T) kernelProps.get(key);
    }

//    public GlobalSettings(Class<?> mainClzz,String configurtionFolder){
//        this.mainClzz = mainClzz;
//        this.configurtionFolder = configurtionFolder;
//    }
}
