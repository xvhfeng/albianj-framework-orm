package org.albianj.kernel.attr;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GlobalSettings {

    private String batchId;

    private ClassLoader classLoader;

    private Class<?> mainClass;

    private String configPath;

    private String machineKey;

    private List<String> servsPkgPath;

    private List<String> entitiyPkgPath;

    private List<String> drPkgPath;

}
