package org.albianj.api.kernel.anno.proxy;

import org.albianj.common.values.AblNull;

import java.lang.annotation.*;

/**
 * 标注这个类是一个aop的类
 * 处理被这个aop的类所监听的所有操作
 *
 * 当clzz与pkg都未配置的时候，默认为监听当前标注的类所在的pkg
 *
 * 注意：当标注pkg的时候，如果需要包括所有的子包，请标注为“pkg-name.*"
 *      如只标注为”pkg-name“，而不以”.*"结尾，则认为你只需要监听这个包而已
 *      例如有一个pkg的名字为org.albianj.service
 *      如果设定 pkg=“org.abianj”，则表示只监听“org.albianj”包以下的class，但是不包括子包。即org.albianj.service下不被监听
 *      如需要监听org.abianj包，即其以下所有的子包，请配置pkg=“org.abianj.*"
 *
 *      当配置pkg后，例如pkg=”org.albianj.*"，如果需要监听pkg中大部分的包，只有几个包不需要监听，请使用offPkg排除，规则类同pkg
 *      如果只是不想监听这个pkg中的几个class，请使用offclzz进行排除，规则类同clzz
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
    Class<?>[] classes() default AblNull.class;

    /**
     * 所要监听的所有pkg路径
     * @return
     */
    String[] pkgs() default {};

    /**
     * 排除不需要监听的类
     * 一般和pkg一起使用
     * @return
     */
    Class<?>[] exclusionClasses() default AblNull.class;

    /**
     * 不需要监听的包
     * @return
     */
    String[] exclusionPkgs() default {};

    /*
     * 监听所有以当前项配置开始的方法
     */
    String beginWith() default "";

    /*
     * 监听所有不以当前项配置开始的方法
     */
    String notBeginWith() default "";

    /*
     * 监听所有以当前项配置结束的方法
     */
    String endWith() default "";

    /*
     * 监听所有不以当前项配置结束的方法
     */
    String notEndWith() default "";

    /*
     * 监听所有包含当前项配置的方法
     */
    String has() default "";

    /*
     * 监听所有不包含当前项配置的方法
     */
    String notHas() default "";

    /**
     * 能匹配方法名的正则表达式
     * @return
     */
    String expr() default "";

    boolean public

    /*
     * 所有需要监听的异常
     */
    Class<? extends Throwable>[] raises() default {};

    /**
     * 配置的这些异常不需要监听
     * @return
     */
    Class<? extends Throwable>[] exclusionRaises() default {};

}
