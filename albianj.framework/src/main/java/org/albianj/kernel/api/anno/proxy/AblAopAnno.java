package org.albianj.kernel.api.anno.proxy;

import java.lang.annotation.*;

/**
 * 标注该类为AOP的service类
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblAopAnno {
    /**
     * 监视与不用监视的classes
     * @return
     */
    AblWatchClassAnno[] classes() default {};

    /**
     * 所要监听与排除监视的所有pkg路径
     * @return
     */
    AblWatchPkg[] pkgs() default {};
}
