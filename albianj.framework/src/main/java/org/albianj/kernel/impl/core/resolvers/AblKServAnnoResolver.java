package org.albianj.kernel.impl.core.resolvers;

import org.albianj.kernel.api.anno.serv.AblkServAnno;
import org.albianj.kernel.api.attr.IAblAnnoResolver;
import org.albianj.scanner.ClassAttr;

public class AblKServAnnoResolver implements IAblAnnoResolver {
    @Override
    public ClassAttr parse(Class<?> clzz) {
        if (clzz.isAnnotationPresent(AblkServAnno.class)) {
//            AblkServAnno anno = clzz.getAnnotation(AblkServAnno.class);
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
