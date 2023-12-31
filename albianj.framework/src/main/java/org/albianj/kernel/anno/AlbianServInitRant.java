package org.albianj.kernel.anno;

import org.albianj.common.anno.AlbianCommentRant;

import java.lang.annotation.*;

@AlbianCommentRant("标注当前方法为service的初始化方法，service在初始化的时候会调用这个方法")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AlbianServInitRant {
   AlbianMethodArgRant[] Args() default {};
}
