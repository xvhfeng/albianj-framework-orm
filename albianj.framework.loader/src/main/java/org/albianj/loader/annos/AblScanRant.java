package org.albianj.loader.annos;

/**
 * 扫描路径配置
 */
public @interface AblScanRant {
    String[] value() default {};
}
