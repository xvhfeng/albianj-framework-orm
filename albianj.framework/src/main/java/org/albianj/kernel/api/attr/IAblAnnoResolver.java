package org.albianj.kernel.api.attr;

import org.albianj.scanner.ClassAttr;

public interface IAblAnnoResolver {
     ClassAttr parse(Class<?> clzz);
}
