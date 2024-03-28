package org.albianj.kernel.impl.core;

import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.api.anno.proxy.AblAopAnno;
import org.albianj.kernel.api.anno.proxy.AblWatchClassAnno;
import org.albianj.kernel.api.anno.proxy.AblWatchPkg;
import org.albianj.kernel.api.anno.proxy.AblWatchThrow;
import org.albianj.kernel.api.anno.serv.AblServAnno;
import org.albianj.kernel.api.anno.serv.SetWhenOpt;
import org.albianj.kernel.api.attr.IAblServAttrParser;
import org.albianj.common.mybp.Assert;
import org.albianj.common.values.RefArg;
import org.albianj.scanner.AblAopAttr;
import org.albianj.scanner.AblClassAttr;

import java.lang.annotation.Annotation;
import java.util.*;

public class AblAopAttrParser implements IAblServAttrParser {
    @Override
    public void annoParser(Class<?> clzz, AblClassAttr attr) {
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
