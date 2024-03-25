package org.albianj.scanner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AblAopAttr {

    /**
     * 监听的class集合
     */
    private Class<?>[] classes = null;
    /**
     * 监听的package的集合
     */
    private String[] pkgs = null;
    /**
     * 不需要监听的class的集合
     */
    private Class<?>[] exclusionClasses = null;
    /**
     * 不需要监听的包的集合
     */
    private String[] exclusionPkgs = null;
    /**
     * 以该项配置所开头的所有方法
     */
    private String beginWith;
    /**
     * 不以该项配置开头的所有方法
     */
    private String notBeginWith;
    /**
     * 以该项配置结束的所有方法
     */
    private String endWith;
    /**
     * 不以该项配置结束的所有方法
     */
    private String notEndWith;
    /**
     * 方法名称中含有该项配置的方法
     */
    private String has;
    /**
     * 方法名称中不含有该项配置的方法
     */
    private String notHas;
    /**
     * 对于监听方法的名称的正则表达式
     */
    private String expr;
    /**
     * 监听的所有异常
     */
    private Class<? extends Throwable>[] raises = null;
    /**
     * 不监听的所有异常
     */
    private Class<? extends Throwable>[] exclusionRaises = null;

}
