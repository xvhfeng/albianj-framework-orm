package org.albianj.loader.annos;

import java.lang.annotation.*;

/**
 * 扫描路径配置
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblScanRant {
    String[] value() default {};
}
