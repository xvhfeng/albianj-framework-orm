package org.albianj.kernel.attr;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.kernel.attr.config.KernelConfigAttr;

import java.util.List;

@Data
@NoArgsConstructor
public class GlobalSettings {

    private String batchId;

    private ClassLoader classLoader;

    private Class<?> mainClass;

    private String configPath;

    private List<String> servScanners;

    private List<String> entityScanners;

    private List<String> datarouterScanners;

    private KernelConfigAttr kernelConfigAttr;

}
