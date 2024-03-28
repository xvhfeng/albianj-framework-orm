package org.albianj.kernel.api.anno.serv;

/**
 * field被设置的时间点
 */
public enum SetWhenOpt {
    /**
     * 在service/bean被实例化以后，但@init标注的方法还未运行
     */
    BeforeInit,

    /**
     * 在service/bean的@init标注的方法运行过后
     */
    AfterInit,
}
