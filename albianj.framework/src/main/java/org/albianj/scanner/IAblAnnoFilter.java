package org.albianj.scanner;

import java.util.Map;

/**
 * 过滤是否具有指定anno标注的class
 * 如果是，得到所标注的annos
 */
public interface IAblAnnoFilter {
    /**
     * 找到annos指定标注的rants的类
     * 返回AblRantAttr（fullName，rants，clzz这3个对象被填充）
     * @param clzz
     * @param annos
     * @return
     */
    AblClassAttr found(Class<?> clzz, Map<String,AnnoData> annos);
}
