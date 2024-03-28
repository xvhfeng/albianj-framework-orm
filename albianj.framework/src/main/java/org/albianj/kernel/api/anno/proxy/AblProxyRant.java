package org.albianj.kernel.api.anno.proxy;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblProxyRant {
    AlbianServiceProxyRant[] Rants() default {};
}
