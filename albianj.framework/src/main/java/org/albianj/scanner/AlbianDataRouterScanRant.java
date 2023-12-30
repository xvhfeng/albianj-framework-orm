package org.albianj.scanner;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianDataRouterScanRant {
    String[] DataRouterPkgs() default {};
    String[] value() default {};
}
