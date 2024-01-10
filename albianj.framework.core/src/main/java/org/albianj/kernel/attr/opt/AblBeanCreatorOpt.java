package org.albianj.kernel.attr.opt;

/**
 * bean 创建实例的方法
 */
public enum AblBeanCreatorOpt {
    /**
     * 直接调用构造函数new一个
     */
    Constructor,
    /**
     * 工厂静态方法
     */
    StaticFactory,
    /**
     * 工厂实例方法
     */
    InstanceFactory;

}
