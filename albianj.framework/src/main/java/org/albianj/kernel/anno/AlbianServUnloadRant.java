package org.albianj.kernel.anno;

import org.albianj.common.anno.AlbianCommentRant;

import java.lang.annotation.*;

@AlbianCommentRant("标注当前方法为service的卸载的方法，service在卸载的时候会调用这个方法")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AlbianServUnloadRant {
}
