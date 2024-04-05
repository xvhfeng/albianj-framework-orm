package org.albianj.scanner;

import java.lang.annotation.Annotation;

/**
 * 解析bean class的anno信息
 */
public interface IAblAnnoResolver {

    /**
     * 解析bean class的anno信息
     * 返回解析后的class attributes
     * @return
     */
    ClassAttr parseBeanClass(Class<?> clzz, Class<? extends Annotation> belongAnno);
}
