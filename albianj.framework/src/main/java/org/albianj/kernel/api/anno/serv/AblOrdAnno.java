package org.albianj.kernel.api.anno.serv;


import java.lang.annotation.*;

/**
 * 加载顺序
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblOrdAnno {
    int value() default 0;
}
