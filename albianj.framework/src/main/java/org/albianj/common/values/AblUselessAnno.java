package org.albianj.common.values;

import org.albianj.common.comment.Comments;

import java.lang.annotation.*;

@Comments("Albianj标注不再被使用")
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AblUselessAnno {
    String value() default "";
}
