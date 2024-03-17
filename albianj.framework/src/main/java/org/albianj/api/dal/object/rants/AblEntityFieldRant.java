package org.albianj.api.dal.object.rants;


import java.lang.annotation.*;
import java.sql.Types;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface AblEntityFieldRant {

    String FieldName() default "";

    boolean IsAllowNull() default true;

    int Length() default -1;

    boolean IsPrimaryKey() default false;

    int DbType() default Types.OTHER;

    boolean IsSave() default true;

    /*
     * not scan by albianj persistence
     */
    boolean Ignore() default false;

    String PropertyName() default "";

    boolean IsAutoGenKey() default false;

    String Desc() default "";

}
