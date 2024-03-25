package org.albianj.api.kernel.anno.serv;


import org.albianj.common.values.AblAliasForAnno;

import java.lang.annotation.*;

/**
 * 类中字段自动赋值anno
 * 使用这个anno标注的field都会在类中自动被赋值
 *
 * 如果value或者id被赋值，那么使用value或id进行查找
 * 如果value和id并未被赋值，那么使用AblResource标注的对象的类型进行查找
 *
 * value、id均支持OGNL表达式，以可以直接调用静态变量，方法，实例化方法，变量等得到值
 *
 * 当赋值的时候发生异常，且throwIfNull为真的时候，抛出异常，否则继续运行
 *
 * 默认被标注的field会在@init标注的函数运行后被赋值
 * 如果有field会在@AblInitAnno标注的方法中所需要被使用，请明确设置when为SetWhenOpt.BeforeInit
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface AblAutoAnno {
    @AblAliasForAnno("id")
    String value() default "";

    @AblAliasForAnno("value")
    String id() default "";

    SetWhenOpt when() default SetWhenOpt.AfterInit;

    boolean throwIfNull() default false;
}
