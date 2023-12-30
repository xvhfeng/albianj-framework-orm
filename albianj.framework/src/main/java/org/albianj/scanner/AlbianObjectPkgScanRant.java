package org.albianj.scanner;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianObjectPkgScanRant {
    String[] EntityPkgs() default {};
    String[] value() default {};

}
