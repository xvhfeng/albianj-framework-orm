package org.albianj.loader;

import lombok.Data;

import java.util.List;

@Data
public class GlobalSettings {

    private String configPath;

    private String machineKey;

    private List<String> servsPkgPath;

    private List<String> entryPkgPath;

    private List<String> drPkgPath;

}
