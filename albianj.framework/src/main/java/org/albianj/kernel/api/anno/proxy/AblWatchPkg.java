package org.albianj.kernel.api.anno.proxy;

import org.albianj.common.values.AblAliasForAnno;

/**
 * Aop监听的包路径
 *  为了简单方便和容易理解，目前对于包路径的配置支持一下通配符
 *  1. * 星号表示匹配包路径种的任意多数段
 *  2. # 井号表示匹配包路径种的一段
 *      PS：段的定义。包中的.（点号）之间的路径名成为段。
 *          比如org.abl.test，这个包一共3个段，分别是org，abl，test
 *          如配置为*.test，即可以匹配org.test，也可以匹配org.abl.test，但是不能匹配org.test.impl
 *          如配置为#.test，即可以匹配org.test，不能匹配org.abl.test，也不能匹配org.test.impl
 *  另：
 *      若配置包路径为org.abl.test，则表示只对org.abl.test包下的一级直接class起作用，而不会对其子包及子包中的class起作用，如需要对于子包起作用，请使用org.abl.test.*
 *
 *  org.*.test   org.abl.test  org.abl.uou.test
 *
 *
 *  org.abl.uou.test
 *  org.abl.uou.test.u1
 *  org.abl.test.u1
 */
public @interface AblWatchPkg {
    /**
     * 需要监听的包路径
     * @return
     */
    String[] watch() default {};

    /**
     * 需要排除的包路径
     * @return
     */
    String[] exclusion() default {};
}
