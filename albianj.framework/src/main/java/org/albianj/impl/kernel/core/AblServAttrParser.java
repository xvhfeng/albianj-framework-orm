package org.albianj.impl.kernel.core;

import org.albianj.api.kernel.anno.serv.AblServAnno;
import org.albianj.api.kernel.attr.IAblServAttrParser;
import org.albianj.common.utils.StringsUtil;
import org.albianj.scanner.AblBeanAttr;
import org.albianj.scanner.IAblAnnoParser;

public class AblServAttrParser implements IAblServAttrParser {
    @Override
    public void annoParser(Class<?> clzz, AblBeanAttr attr) {
        if(clzz.isAnnotationPresent(AblServAnno.class)) {
            AblServAnno anno = clzz.getAnnotation(AblServAnno.class);
            attr.setBelongAnno(anno);
            String id = anno.id();
            if(StringsUtil.isNotNullEmptyTrimmed(id)) {
                attr.setId(id);
            } else {
                attr.setId(clzz.getName());
            }
        }
    }
}
