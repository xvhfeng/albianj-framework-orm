package org.albianj.kernel.anno;

import org.albianj.kernel.attr.opt.AlbianBuiltinTypeOpt;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AlbianMethodArgRant {
    AlbianBuiltinTypeOpt Type() default AlbianBuiltinTypeOpt.String;
    String Name();
    String Value() default "";

}
