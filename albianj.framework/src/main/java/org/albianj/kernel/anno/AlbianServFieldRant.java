package org.albianj.kernel.anno;

import org.albianj.kernel.attr.opt.AlbianServiceFieldSetterLifecycleOpt;
import org.albianj.kernel.attr.opt.AlbianServiceFieldTypeOpt;

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
public @interface AlbianServiceFieldRant {
    /*
     * field value type
     * default is string
     */
    AlbianServiceFieldTypeOpt Type() default AlbianServiceFieldTypeOpt.String;

    /*
     * field value
     * because annotation rule,the value cannot be object,
     * so we replace it with string.
     * default is Empty
     * when type is ref,this value is the service id
     */
    String Value() default "";

    boolean AllowNull() default false;

    AlbianServiceFieldSetterLifecycleOpt SetterLifetime() default AlbianServiceFieldSetterLifecycleOpt.AfterNew;
}
