package org.albianj.scanner;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 过滤是否具有指定anno标注的class
 * 如果是，得到所标注的annos
 */
public interface IAblAnnoFilter {
    /**
     * 找到annos指定标注的rants的类
     * 返回Anno的class信息
     * 如果clzz没有在found的annos中，则返回null
     * @param clzz
     * @return
     */
    Class<? extends Annotation> found(Class<?> clzz);
}
