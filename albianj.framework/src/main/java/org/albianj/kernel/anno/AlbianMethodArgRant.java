package org.albianj.kernel.anno;

import org.albianj.kernel.attr.opt.AlbianVarTypeOpt;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AlbianMethodArgRant {
    AlbianVarTypeOpt Type() default AlbianVarTypeOpt.String;
    String Name();
    String Value() default "";

}
