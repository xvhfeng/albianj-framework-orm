package org.albianj.api.kernel.anno.serv;


import java.lang.annotation.*;

/**
 * 类中字段自动赋值anno
 * 使用这个anno标注的field都会在类中自动被赋值
 *
 * 如果value或者id被赋值，那么使用value或id进行查找
 * 如果value和id并未被赋值，那么使用AblResource标注的对象的类型进行查找
 *
 * 当赋值的时候发生异常，且throwIfNull为真的时候，抛出异常，否则继续运行
 *
 * 默认被标注的field会在函数调用初始化函数后，在调用@AblInitRant方法被调用之前被赋值
 * 如果有field会在@AblInitRant标注的方法中所需要的资源才被准备好，请明确设置when为SetWhenOpt.AfterInit
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface AblAutoAnno {
    String value() default "";
    String id() default "";
    SetWhenOpt when() default SetWhenOpt.BeforeInit;
    boolean throwIfNull() default false;
}
