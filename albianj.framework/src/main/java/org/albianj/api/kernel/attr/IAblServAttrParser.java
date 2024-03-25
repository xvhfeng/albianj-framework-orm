package org.albianj.api.kernel.attr;

import org.albianj.scanner.AblBeanAttr;

public interface IAblServAttrParser {
    public void annoParser(Class<?> clzz, AblBeanAttr attr);
}
