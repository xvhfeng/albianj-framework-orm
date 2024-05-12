package org.albianj.kernel.api.attr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalSettings {
    private ClassLoader loader;
    private Class<?> mainClzz;
    private String configurtionFolder;
    private Properties kernelProps;
    private int argc = 0;
    private String[] argv;
    private List<String> krlPkgs;
    private List<String> plgPkgs;
    private List<String> bssPkgs;


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
