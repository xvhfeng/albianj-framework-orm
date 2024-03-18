package org.albianj.impl.kernel.core;

import org.albianj.api.kernel.anno.proxy.AblAopAnno;
import org.albianj.api.kernel.anno.serv.AblkServAnno;
import org.albianj.api.kernel.anno.serv.AblServAnno;
import org.albianj.common.mybp.Assert;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.loader.IAlbianTransmitterService;
import org.albianj.scanner.AblBeanAttr;
import org.albianj.scanner.AblPkgScanner;
import org.albianj.scanner.IAblRantFilter;
import org.albianj.scanner.IAblRantParser;

import java.lang.annotation.Annotation;
import java.util.List;

public class AblkStarter implements IAlbianTransmitterService {

    private final static String[] pkgs = {
            "org.albianj.api.kernel",
            "org.albianj.impl.kernel"
    };

    private final static Class<? extends Annotation>[] annos = new Class[]{
            AblkServAnno.class, // 内核serv只能是唯一标注
            AblAopAnno.class,   // 如果是aop serv，那么必须首先也是serv
            AblServAnno.class   //  普通serv
    };

    @Override
    public void start(Class<?> mainClzz, String configUrl) {

    }

    @Override
    public void unload() {

    }

    private void scanKernelPkg() {
        try {
            AblPkgScanner.filter(AlbianClassLoader.getInstance(),
                    List.of(pkgs),
                    new IAblRantFilter() {
                        @Override
                        public AblBeanAttr foundRants(Class<?> clzz, List<Class<? extends Annotation>> annos) {
                            Class<? extends  Annotation> anno = null;
                            AblBeanAttr clzzAttr = new AblBeanAttr();
                            clzzAttr.setClzz(clzz);
                            clzzAttr.setClzzFullName(clzz.getName());

                            /*
                                根据annos的顺序，得到最上面的一个annos
                                这个annos即为class在albianj中的定位所在
                             */
                            for(Class<? extends  Annotation> a : annos) {
                                if(clzz.isAnnotationPresent(a)){
                                    Annotation annoAttr = clzz.getAnnotation(a);
                                    clzzAttr.setBelongRant(annoAttr);
                                    clzzAttr.setBelongRantFullName(a.getName());
                                    break;
                                }
                            }

                            clzzAttr.setRants(clzz.getAnnotations());
                            return clzzAttr;
                        }
                    },
                    List.of(annos),
                    new IAblRantParser() {
                        @Override
                        public AblBeanAttr parseClzz(AblBeanAttr attr) {
                            return null;
                        }
                    }
            );
        }catch (Throwable t){
            Assert.notNull(t,"scan kernel pckage is fail.");
        }
    }
}
