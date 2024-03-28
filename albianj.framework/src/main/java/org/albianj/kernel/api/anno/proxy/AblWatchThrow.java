package org.albianj.kernel.api.anno.proxy;

import org.albianj.common.values.AblAliasForAnno;

public @interface AblWatchThrow {

    @AblAliasForAnno("watch")
    Class<? extends  Throwable>[] value() default {};

    @AblAliasForAnno("value")
    Class<? extends  Throwable>[] watch() default {};

    Class<? extends  Throwable>[] exclusion() default {};
}
