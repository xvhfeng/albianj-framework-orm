package org.albianj.kernel.api.anno.serv;

import org.albianj.common.values.AblAliasForAnno;

import java.lang.annotation.*;

/*
 * Albianj内核使用的创建对象的工厂方法
 * 这个anno标注的方法会创建一个对象，并且返回一个对象，而这个对象一般就是service或者单纯的bean对象
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AblFactoryAnno {
    /**
     * 返回标注的函数被调用后返回的对象的id
     *
     * 当id或者value被忽略的时候
     * 被该anno标注的方法的返回值类型全名为对象的id
     *
     * @return
     */
    @AblAliasForAnno(attribute = "Id")
    String value() default "";

    @AblAliasForAnno(attribute = "value")
    String Id() default "";
}
