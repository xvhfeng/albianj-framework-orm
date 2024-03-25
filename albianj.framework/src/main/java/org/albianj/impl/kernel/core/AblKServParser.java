package org.albianj.impl.kernel.core;

import org.albianj.api.kernel.anno.proxy.AblAopAnno;
import org.albianj.api.kernel.anno.serv.AblServAnno;
import org.albianj.api.kernel.anno.serv.AblkServAnno;
import org.albianj.api.kernel.attr.IAblServAttrParser;
import org.albianj.common.mybp.Assert;
import org.albianj.common.utils.StringsUtil;
import org.albianj.common.values.RefArg;
import org.albianj.scanner.AblAopAttr;
import org.albianj.scanner.AblBeanAttr;

import java.lang.annotation.Annotation;

public class AblKServParser implements IAblServAttrParser {
    @Override
    public void annoParser(Class<?> clzz, AblBeanAttr attr) {
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
