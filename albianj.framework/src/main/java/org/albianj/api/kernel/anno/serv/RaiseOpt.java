package org.albianj.api.kernel.anno.serv;

import org.albianj.common.values.AblUselessAnno;

/**
 * 发生问题的时候/和程序运行时预料不一致的时候的处理方式
 */
@AblUselessAnno("目前没有被使用")
public enum RaiseOpt {

    /**
     * 忽略，就当什么都没发生
     * 一般发生在如果出现null或者无值，有默认值的情况
     */
    Ignore,
    /**
     * 警告
     * 程序继续运行，但是会记录一条日志提醒
     */
    Warn,
    /**
     * 抛出异常，当前线程不会在继续
     * 线程有可能会毁坏或者被join（未被join监听会发生内存泄漏）监听到或者自行回收
     */
    Throw,
}
