package org.albianj.scanner;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianServicePkgScanRant {
    String[] ServicePkgs() default {};
    String[] value() default {};

}
