package org.albianj.api.kernel.anno.proxy;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AblAopAfterAnno {
}
