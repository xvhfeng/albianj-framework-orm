package org.albianj.api.kernel.anno.proxy;

import org.albianj.common.values.AblNull;

import java.lang.annotation.*;

/**
 * 标注这个类是一个aop的类
 * 处理被这个aop的类所监听的所有操作
 *
 * 当clzz与pkg都未配置的时候，默认为监听当前标注的类所在的pkg
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblAopAnno {

    /**
     * 所要监听的class集合
     * @return
     */
    Class<?>[] clzz() default AblNull.class;

    /**
     * 所要监听的所有pkg路径
     * @return
     */
    String[] pkg() default {};

    /*
     * 监听所有以当前项配置开始的方法
     */
    String bgn() default "";

    /*
     * 监听所有不以当前项配置开始的方法
     */
    String notBgn() default "";

    /*
     * 监听所有以当前项配置结束的方法
     */
    String end() default "";

    /*
     * 监听所有不以当前项配置结束的方法
     */
    String notEnd() default "";


    /*
     * 监听所有包含当前项配置的方法
     */
    String has() default "";

    /*
     * 监听所有不包含当前项配置的方法
     */
    String notHas() default "";

    /*
     * 监听所有的方法
     */
    boolean all() default false;

    /**
     * 能匹配方法名的正则表达式
     * @return
     */
    String expr() default "";

    /*
     * 所有需要监听的异常
     */
    Class<? extends Throwable>[] raise() default {};

}
