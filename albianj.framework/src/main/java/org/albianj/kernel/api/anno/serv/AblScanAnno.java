package org.albianj.kernel.api.anno.serv;

import java.lang.annotation.*;

/**
 * 扫描路径配置
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblScanAnno {
    String[] value() default {};
}

