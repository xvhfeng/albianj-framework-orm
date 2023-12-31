package org.albianj.kernel.anno;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianServProxysRant {
    AlbianServProxyRant[] Rants() default {};
}
