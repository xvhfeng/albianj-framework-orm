package org.albianj.kernel.api.anno.serv;

import org.albianj.common.values.AblNull;

import java.lang.annotation.*;

/**
 * 参数变量的注解
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Inherited
@Documented
public @interface AblArgAnno {
    /**
     * 当参数为具体值的直接量时，使用value指定
     * @return
     */
    String value() default "";

    /**
     * 当参数为service时，使用id指定
     * @return
     */
    String id() default "";

    /**
     * 当参数为service，但参数的修饰类型与参数的具体类型不一致的时候，使用clzz指定
     * @return
     */
    Class<?> clzz() default AblNull.class;

    /**
     * 当参数的值为map，list中的一项
     * 或者为object的某个属性值
     * 或者是几个object之类的嵌套，类似于xx.xx.xx.xx这样的
     * 即需要使用表达式才能获取的值时，使用expr配置
     * @return
     */
    String expr() default "";
}
