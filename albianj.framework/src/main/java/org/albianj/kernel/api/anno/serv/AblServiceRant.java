package org.albianj.kernel.api.anno.serv;

import org.albianj.kernel.api.service.IAlbianService;

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
public @interface AblServiceRant {
    /*
     * service id,it must not be null or empty.
     */
    String Id() default "";

    /*
     * if value is true load this service to albian kernel,
     *  or false not load it.
     *  default is true
     */
    boolean Enable() default true;

    /*
     * string format for service's interface
     * this interface must be IAlbianService's child class
     */
//    String sInterface() default IAlbianService.FullName;

    /*
     * Class object format for service's interface
     */
    Class<? extends IAlbianService> Interface() default IAlbianService.class;
}
