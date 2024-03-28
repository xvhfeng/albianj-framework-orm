package org.albianj.kernel.api.anno.proxy;

import org.albianj.common.values.AblAliasForAnno;

public @interface AblWatchPkg {
    @AblAliasForAnno("watch")
    String[] value() default {};

    @AblAliasForAnno("value")
    String[] watch() default {};

    String[] exclusion() default {};
}
