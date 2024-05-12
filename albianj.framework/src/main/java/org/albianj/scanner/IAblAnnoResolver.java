package org.albianj.scanner;

import java.lang.annotation.Annotation;

/**
 * 解析bean class的anno信息
 */
public interface IAblAnnoResolver {

    /**
     * 解析bean class的anno信息
     * 解析后的数据自行解决处理
     * @return
     */
    void parse(Class<?> clzz);
}
