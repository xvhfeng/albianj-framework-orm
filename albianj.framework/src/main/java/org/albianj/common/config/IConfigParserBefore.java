package org.albianj.common.config;

import java.util.Map;

public interface IConfigParserBefore {
    void initBefore(String filename,Map<?,?> map);
}
