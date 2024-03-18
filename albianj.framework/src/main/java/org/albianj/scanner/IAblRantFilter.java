package org.albianj.scanner;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 过滤是否具有指定anno标注的class
 * 如果是，得到所标注的annos
 */
public interface IAblRantFilter {
    /**
     * 找到annos指定标注的rants的类
     * 返回AblRantAttr（fullName，rants，clzz这3个对象被填充）
     * @param clzz
     * @param annos
     * @return
     */
    AblBeanAttr foundRants(Class<?> clzz, List<Class<? extends  Annotation>> annos);
}
