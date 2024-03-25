package org.albianj.impl.kernel.core;

import org.albianj.api.kernel.anno.proxy.AblAopAnno;
import org.albianj.api.kernel.anno.serv.AblServAnno;
import org.albianj.api.kernel.attr.IAblServAttrParser;
import org.albianj.common.mybp.Assert;
import org.albianj.common.values.RefArg;
import org.albianj.scanner.AblAopAttr;
import org.albianj.scanner.AblBeanAttr;

import java.lang.annotation.Annotation;

public class AblAopAttrParser implements IAblServAttrParser {
    @Override
    public void annoParser(Class<?> clzz, AblBeanAttr attr) {
        if(clzz.isAnnotationPresent(AblAopAnno.class)) {
            Assert.isFalse(clzz.isAnnotationPresent(AblServAnno.class),
                    "AblAopAnno present class:{} must is AblServAnno class first.",
                    clzz.getName());

            RefArg<Annotation> aopAnno = new RefArg<>();
            AblAopAttr aopAttr = getAopAttr(clzz,aopAnno);
            attr.setBelongAnno(aopAnno.getValue());
            attr.setAopAttr(aopAttr);
        }
    }

    private AblAopAttr getAopAttr(Class<?> clzz, RefArg<Annotation> aopAnno) {
        AblAopAnno anno = clzz.getAnnotation(AblAopAnno.class);
        AblAopAttr aopAttr = new AblAopAttr();
        aopAttr.setClasses(anno.classes());
        aopAttr.setPkgs(anno.pkgs());
        aopAttr.setExclusionClasses(anno.exclusionClasses());
        aopAttr.setExclusionPkgs(anno.exclusionPkgs());
        aopAttr.setBeginWith(anno.beginWith());
        aopAttr.setNotBeginWith(anno.notBeginWith());
        aopAttr.setEndWith(anno.endWith());
        aopAttr.setNotEndWith(anno.notEndWith());
        aopAttr.setHas(anno.has());
        aopAttr.setNotHas(anno.notHas());
        aopAttr.setExpr(anno.expr());
        aopAttr.setRaises(anno.raises());
        aopAttr.setExclusionRaises(anno.exclusionRaises());
        aopAnno.setValue(anno);
        return aopAttr;
    }


}
