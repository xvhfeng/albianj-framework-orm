package org.albianj.kernel.impl.core.resolvers;

import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.api.anno.proxy.AblAopPointAnno;
import org.albianj.kernel.api.anno.proxy.AblWatchClassAnno;
import org.albianj.kernel.api.anno.proxy.AblWatchPkg;
import org.albianj.kernel.api.anno.proxy.AblWatchThrow;
import org.albianj.kernel.api.anno.serv.AblServAnno;
import org.albianj.kernel.api.attr.IAblAnnoResolver;
import org.albianj.common.mybp.Assert;
import org.albianj.common.values.RefArg;
import org.albianj.scanner.AopAnnoAttr;
import org.albianj.scanner.ClassAttr;

import java.lang.annotation.Annotation;
import java.util.*;

public class AblAopAnnoResolver implements IAblAnnoResolver {
    @Override
    public ClassAttr parse(Class<?> clzz) {
        if(clzz.isAnnotationPresent(AblAopPointAnno.class)) {
            Assert.isFalse(clzz.isAnnotationPresent(AblServAnno.class),
                    "AblAopAnno present class:{} must is AblServAnno class first.",
                    clzz.getName());

            RefArg<Annotation> aopAnno = new RefArg<>();
            AopAnnoAttr aopAttr = getAopAttr(clzz,aopAnno);
//            attr.setBelongAnno(aopAnno.getValue());
//            attr.setAopAttr(aopAttr);
        }

        return null;
    }

    private AopAnnoAttr getAopAttr(Class<?> clzz, RefArg<Annotation> aopAnno) {
        AblAopPointAnno anno = clzz.getAnnotation(AblAopPointAnno.class);
        AopAnnoAttr aopAttr = new AopAnnoAttr();

        AblWatchClassAnno[] watchClassAnno = anno.classes();
        Set<Class<?>> onClasses = new HashSet<>();
        Set<Class<?>> offClasses = new HashSet<>();
        if(SetUtil.isNotNullEmpty(watchClassAnno)){
            Arrays.stream(watchClassAnno).forEach(e ->{
                Class<?>[] on = e.value();
                Class<?>[] watch = e.watch();
                Class<?>[] off = e.exclusion();
                if(SetUtil.isNotNullEmpty(on)){
                    Arrays.stream(on).forEach(t ->{
                        onClasses.add(t);
                    });
                }
                if(SetUtil.isNotNullEmpty(watch)){
                    Arrays.stream(watch).forEach(t ->{
                        onClasses.add(t);
                    });
                }
                if(SetUtil.isNotNullEmpty(off)){
                    Arrays.stream(off).forEach(t ->{
                        offClasses.add(t);
                    });
                }
            });
        }
        aopAttr.setClasses((Class<?>[]) onClasses.toArray());
        aopAttr.setExclusionClasses((Class<?>[]) offClasses.toArray());

        AblWatchPkg[] watchPkgs = anno.pkgs();
        Set<String> onPkgs = new HashSet<>();
        Set<String> offPkgs = new HashSet<>();
        if(SetUtil.isNotNullEmpty(watchPkgs)){
            Arrays.stream(watchPkgs).forEach(e ->{
                String[] on = e.value();
                String[] watch = e.watch();
                String[] off = e.exclusion();
                if(SetUtil.isNotNullEmpty(on)){
                    Arrays.stream(on).forEach(t ->{
                        if(StringsUtil.isNotNullEmptyTrimmed(t)) {
                            onPkgs.add(t);
                        }
                    });
                }
                if(SetUtil.isNotNullEmpty(watch)){
                    Arrays.stream(watch).forEach(t ->{
                        if(StringsUtil.isNotNullEmptyTrimmed(t)) {
                            onPkgs.add(t);
                        }
                    });
                }
                if(SetUtil.isNotNullEmpty(off)){
                    Arrays.stream(off).forEach(t ->{
                        if(StringsUtil.isNotNullEmptyTrimmed(t)) {
                            offPkgs.add(t);
                        }
                    });
                }
            });
        }
        aopAttr.setPkgs((String[]) onPkgs.toArray());
        aopAttr.setExclusionPkgs((String[]) offPkgs.toArray());


        aopAttr.setBeginWith(anno.beginWith());
        aopAttr.setNotBeginWith(anno.notBeginWith());
        aopAttr.setEndWith(anno.endWith());
        aopAttr.setNotEndWith(anno.notEndWith());
        aopAttr.setHas(anno.has());
        aopAttr.setNotHas(anno.notHas());
        aopAttr.setExpr(anno.expr());

        AblWatchThrow[] watchThrows = anno.raises();
        Set<Class<? extends  Throwable>> onThrows = new HashSet<>();
        Set<Class<? extends  Throwable>> offThrows = new HashSet<>();
        if(SetUtil.isNotNullEmpty(watchThrows)){
            Arrays.stream(watchThrows).forEach(e ->{
                Class<? extends  Throwable>[] on = e.value();
                Class<? extends  Throwable>[] watch = e.watch();
                Class<? extends  Throwable>[] off = e.exclusion();
                if(SetUtil.isNotNullEmpty(on)){
                    Arrays.stream(on).forEach(t ->{
                        onThrows.add(t);
                    });
                }
                if(SetUtil.isNotNullEmpty(watch)){
                    Arrays.stream(watch).forEach(t ->{
                        onThrows.add(t);
                    });
                }
                if(SetUtil.isNotNullEmpty(off)){
                    Arrays.stream(off).forEach(t ->{
                        offThrows.add(t);
                    });
                }
            });
        }
        aopAttr.setRaises((Class<? extends  Throwable>[]) onThrows.toArray());
        aopAttr.setExclusionRaises((Class<? extends  Throwable>[]) offThrows.toArray());

//        aopAttr.setRaises(anno.raises());
//        aopAttr.setExclusionRaises(anno.exclusionRaises());
        aopAnno.setValue(anno);
        return aopAttr;
    }


}
