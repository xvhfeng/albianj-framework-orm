package org.albianj.kernel.api.anno.serv;

import java.lang.annotation.*;

/**
 * 当前class的实例化对象强依赖于
 * 必须depend标注的class先实例化后，AblDpdAnno标注的class才可以实例化
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblDpdAnno {
    Class<?>[] depend() default {};
}
