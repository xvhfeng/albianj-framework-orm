package org.albianj.kernel.api.attr;

import org.albianj.scanner.AblClassAttr;

public interface IAblServAttrParser {
    public void annoParser(Class<?> clzz, AblClassAttr attr);
}
