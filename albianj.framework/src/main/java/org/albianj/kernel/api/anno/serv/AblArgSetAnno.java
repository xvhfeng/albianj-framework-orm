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
public @interface AblArgSetAnno {
    /**
     * 变量的值，支持OGNL表达式
     * 支持直接调用bean 静态方法，直接量，service，bean中的属性等
     * 支持对象级联嵌套
     * @return
     */
    String value() default "";

    /**
     * 当参数为service，但参数的修饰类型与参数的具体类型不一致的时候，使用clzz指定
     * 常见的情况比如service为class的全限定名为id，但参数以接口为类型
     * @return
     */
    Class<?> clzz() default AblNull.class;
}
