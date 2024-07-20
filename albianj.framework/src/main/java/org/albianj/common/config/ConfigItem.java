package org.albianj.common.config;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface ConfigItem {
    String Name() default "";
    String Desc() default "";
    boolean MaybeEmpty() default true;
    boolean IsSkip() default false;
}
