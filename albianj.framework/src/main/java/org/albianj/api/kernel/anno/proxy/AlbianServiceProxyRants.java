package org.albianj.api.kernel.anno.proxy;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianServiceProxyRants {
    AlbianServiceProxyRant[] Rants() default {};
}
