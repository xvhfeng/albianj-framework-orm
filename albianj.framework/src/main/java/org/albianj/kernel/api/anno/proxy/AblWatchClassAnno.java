package org.albianj.kernel.api.anno.proxy;

import org.albianj.common.values.AblAliasForAnno;

public @interface AblWatchClassAnno {
    @AblAliasForAnno("watch")
    Class<?>[] value() default {};

    @AblAliasForAnno("value")
    Class<?>[] watch() default {};

    Class<?>[] exclusion() default {};
}
