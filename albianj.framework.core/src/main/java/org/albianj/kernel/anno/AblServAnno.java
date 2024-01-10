package org.albianj.kernel.anno;

import org.albianj.common.utils.NullValue;

import java.lang.annotation.*;

/**
 * albian service rant
 * the same as service section in service.xml
 * it use to class when it as albian service
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblServAnno {
    /*
     * service id,it must not be null or empty.
     */
    String ServId() default "";

    /*
     * if value is true load this service to albian kernel,
     *  or false not load it.
     *  default is true
     */
    boolean Enable() default true;

    /*
     * Class object format for service's interface
     */
    Class<?> Interface() default NullValue.class;
}
