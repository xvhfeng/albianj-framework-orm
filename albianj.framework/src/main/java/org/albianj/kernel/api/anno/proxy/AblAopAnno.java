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
}
