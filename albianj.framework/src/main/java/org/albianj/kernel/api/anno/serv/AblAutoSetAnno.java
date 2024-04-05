package org.albianj.kernel.api.anno.serv;


import java.lang.annotation.*;

/**
 * 类中字段自动赋值anno
 * 使用这个anno标注的field都会在类中自动被赋值
 *
 * value支持OGNL表达式，以可以直接调用静态变量，方法，实例化方法，变量等得到值
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
public @interface AblAutoSetAnno {
    /**
     * field设定的值
     * 目前支持以下几种情形：
     *  1. 空字符串。当value不被指定或为空时，使用field的类型全名即为value，表示使用此类型全名的service赋值，注意：此情况不支持泛型
     *  2. OGNL表达式，可以直接调用静态变量，方法，实例化方法，变量等得到值
     * @return
     */
    String value() default "";

    /**
     * field被设置值的时间点
     * 目前支持2种情况：
     *  1. SetWhenOpt.AfterInit，为AblCtor的Anno标注的函数运行后，此为默认情况
     *  2. SetWhenOpt.BeforeInit，为class的初始化函数被调用（即new）之后，而AblCtor的Anno标注的函数运行之前
     * @return
     */
    SetWhenOpt when() default SetWhenOpt.AfterInit;

    /**
     * 当找不到对应的值的时候，是否抛出异常
     * 为true的时候，抛出异常
     * 默认为false
     * @return
     */
    boolean throwIfNull() default false;
}
