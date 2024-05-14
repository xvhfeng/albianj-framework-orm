package org.albianj.kernel.api.anno.serv;

import java.lang.annotation.*;

/**
 * 自定义插件的扫描路径
 * 插件将会在AblScanRant标注之前被加载
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblPlgScanRant {
    String[] value() default {};
}