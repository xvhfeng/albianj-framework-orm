package org.albianj.api.kernel.anno.proxy;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AblAopThrowAnno {
//    /**
//     * 监听的异常信息
//     * @return
//     */
//    Class<? extends Throwable>[] throwables() default {};
}
