package org.albianj.kernel.anno;

import org.albianj.common.utils.NullValue;
import org.albianj.kernel.attr.opt.AblFieldSetWhenOpt;
import org.albianj.kernel.attr.opt.AblVarModeOpt;
import org.albianj.kernel.attr.opt.AblVarTypeOpt;

import java.lang.annotation.*;

/**
 * service field setter rant
 * if you use this rant to field,the field must have value,
 * and not allow the field set to NULL.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface AblServFieldAnno {
    /*
     * field value type
     * default is string
     */
    AblVarTypeOpt Type() default AblVarTypeOpt.Service;

    Class<?> itfClzz() default NullValue.class;

    /*
     * field value
     * because annotation rule,the value cannot be object,
     * so we replace it with string.
     * default is Empty
     * when type is ref,this value is the service id
     */
    String Value() default "";

    boolean AllowNull() default false;

    AblVarModeOpt Mode() default AblVarModeOpt.Direct;

    AblFieldSetWhenOpt SetWhen() default AblFieldSetWhenOpt.AfterNew;

    String MethodName() default "";
}
