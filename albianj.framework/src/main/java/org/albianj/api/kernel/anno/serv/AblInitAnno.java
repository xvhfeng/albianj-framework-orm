package org.albianj.api.kernel.anno.serv;

import java.lang.annotation.*;


/**
 * 标注这个方法为service 初始化方法
 * 一个类中只能可以有一个init方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AblInitAnno {
    boolean throwIfError() default  false;
}
