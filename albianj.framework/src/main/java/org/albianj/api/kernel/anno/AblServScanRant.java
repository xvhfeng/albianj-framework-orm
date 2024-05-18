package org.albianj.api.kernel.anno;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblServScanRant {
    String[] Paths() default {};
}
