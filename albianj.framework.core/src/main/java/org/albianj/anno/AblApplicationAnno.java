package org.albianj.anno;

import org.albianj.common.anno.AblCommentAnno;

import java.lang.annotation.*;

@AblCommentAnno("配置application元数据")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblApplicationAnno {
    String MachineKey() default "";

    String MachineId() default "";

    String Name() default "";

}
