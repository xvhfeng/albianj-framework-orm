package org.albianj.scanner;

import org.albianj.common.anno.AlbianCommentRant;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianServicePkgScanRant {

    @AlbianCommentRant("和value是一个值，在标签的时候如果需要明确指定可以使用该值名称")
    String[] ServicePkgs() default {};
    String[] value() default {};

}
