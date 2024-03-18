package org.albianj.api.kernel.anno.proxy;


import org.albianj.common.values.AblAliasForAnno;

import java.lang.annotation.*;

/**
 * 单纯的标注类，声明当前类、方法不适用AOP
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE , ElementType.METHOD,ElementType.CONSTRUCTOR})
@Inherited
@Documented
public @interface AblAopIgnoreAnno {

    /**
     * aop service的id
     * @return
     */
    @AblAliasForAnno(attribute = "id")
    String[] value() default {};

    /**
     * aop service的id
     * @return
     */
    @AblAliasForAnno(attribute = "value")
    String[] id() default {};

    /**
     * 当aop未标注id，使用clzz指定
     * @return
     */
    Class<?>[] clzz() default {};
}
