package org.albianj.kernel.impl.core.resolvers;

import org.albianj.kernel.api.anno.serv.AblServAnno;
import org.albianj.kernel.api.attr.IAblAnnoResolver;
import org.albianj.scanner.ClassAttr;

public class AblServAnnoResolver implements IAblAnnoResolver {
    @Override
    public ClassAttr parse(Class<?> clzz) {
        if(clzz.isAnnotationPresent(AblServAnno.class)) {
//            AblServAnno anno = clzz.getAnnotation(AblServAnno.class);
//            attr.setBelongAnno(anno);
//            String id = anno.id();
//            if(StringsUtil.isNotNullEmptyTrimmed(id)) {
//                attr.setId(id);
//            } else {
//                attr.setId(clzz.getName());
//            }
        }
        return null;
    }
}
