package org.albianj.kernel.api.anno.serv;

import java.lang.annotation.*;

/**
 * 标注该方法为albianj管理的方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AblFnAnno {
    /**
     * 指定函数返回的对象的id
     * 当Opt为Factory的时候起作用
     * @return
     */
    String value() default "";

    /**
     * 标注该方法在类中的角色
     * @return
     */
    AblFnOpt Opt() default AblFnOpt.Normal;
}
