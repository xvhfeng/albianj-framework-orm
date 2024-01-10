package org.albianj.kernel.anno;

import org.albianj.common.utils.NullValue;
import org.albianj.kernel.attr.opt.AblBeanCreatorOpt;

import java.lang.annotation.*;

/**
 * bean的注解
 * 一般用在数据性class的表示
 * 和service区分开
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblBeanAnno {
    String BeanId() default "";
    Class<?> ItfClzz() default NullValue.class;

    /**
     * 创建该bean实例的类
     * 若使用工厂模式创建实例，即为工厂类
     * 若使用构造函数，即为bean类自己
     * @return
     */
    Class<?> CreatorClzz() default NullValue.class;

    /**
     * 当使用工厂模式的时候，即为创建该实例的方法
     * 当使用构造函数实例化的时候，该值为empty
     * @return
     */
    String CreatorMethod() default "";

    AblBeanCreatorOpt CreatorMode() default AblBeanCreatorOpt.Constructor;

    /**
     * 是否是单例模式
     * 注意：当前项会受factory方法的约束而可能失效
     * 比如设置IsSingleton为false，但开发者自己在factory method中进行了单例的操作，
     * 那context每次new的时候因为都调用factory method，故其实当前的bean为单例对象
     * @return
     */
    boolean IsSingleton() default false;

}
