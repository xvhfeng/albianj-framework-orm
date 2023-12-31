package org.albianj.scanner;

import org.albianj.common.anno.AlbianCommentRant;

import java.lang.annotation.*;

@AlbianCommentRant("datarouter类存放的packages")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianDataRouterScanRant {
    @AlbianCommentRant("和value是一个值，在标签的时候如果需要明确指定可以使用该值名称")
    String[] DataRouterPkgs() default {};
    String[] value() default {};
}
