package org.albianj.orm.anno;


import java.lang.annotation.*;
import java.sql.Types;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface AlbianObjectDataFieldRant {

    String DbFieldName() default "";

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

    /**
     * 是否需要启用驼峰风格转下划线风格
     * @return
     */
    boolean CamelUnderlineSwitch() default false;

    /**
     * 首字母大写
     * @return
     */
    boolean InitcapSwitch() default false;

    /*
     全部小写
     */
    boolean LowercaseSwitch() default  false;

    /*
    全部大写
     */
    boolean UppercaseSwitch() default false;

}
