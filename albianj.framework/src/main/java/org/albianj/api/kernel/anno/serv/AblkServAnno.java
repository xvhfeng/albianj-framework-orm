package org.albianj.api.kernel.anno.serv;

import org.albianj.common.values.AblNull;
import org.albianj.common.values.AblAliasForAnno;

import java.lang.annotation.*;

/**
 * 标注这是一个albianj kernel级别的service
 * kernel级别的service是最先被初始化的
 *
 * 如果类只被标注了这个anno，并未指定value或者id，
 * 那么这个service在albianj将会使用这个class的全名作为key
 * 如果这个类有多个实现，那么请使用value或者id指明具体的key
 *
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblkServAnno {

    @AblAliasForAnno(attribute = "id")
    String value() default "";

    @AblAliasForAnno(attribute = "value")
    String id() default "";

    boolean enable() default true;

    Class<?> itfClzz() default AblNull.class;
}
