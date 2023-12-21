package org.albianj.kernel.aop;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianServiceProxyRants {
    AlbianServiceProxyRant[] Rants() default {};
}
