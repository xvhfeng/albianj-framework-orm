package org.albianj.kernel.impl.core;

import org.albianj.kernel.api.anno.serv.AblkServAnno;
import org.albianj.kernel.api.attr.IAblServAttrParser;
import org.albianj.common.utils.StringsUtil;
import org.albianj.scanner.AblClassAttr;

public class AblKServParser implements IAblServAttrParser {
    @Override
    public void annoParser(Class<?> clzz, AblClassAttr attr) {
        if(clzz.isAnnotationPresent(AblkServAnno.class)) {
            AblkServAnno anno = clzz.getAnnotation(AblkServAnno.class);
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
