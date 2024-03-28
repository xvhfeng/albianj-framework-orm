package org.albianj.dal.api.object.rants;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblObjRant {
    boolean SqlFieldUseUnderline() default false;
    boolean TableNameUseUnderline() default false;
}
