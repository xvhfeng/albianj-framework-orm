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

    /**
     * 当table中设置sql field使用转换下划线的时候，
     * 使用这个设置可以忽略table中设置的使用转换
     * @return
     */
    boolean IgnoreToUnderLine() default false;

    /**
     * 程序中属性set，get的名字
     * 当出现field名字和setter getter名字不一样的时候，用这个指定
     * @return
     */
    String PropertyName() default "";

    boolean IsAutoGenKey() default false;

    String Desc() default "";

}
