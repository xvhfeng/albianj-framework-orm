package org.albianj.kernel.impl.core;

import org.albianj.common.utils.LangUtil;
import org.albianj.kernel.api.anno.proxy.AblAopPointAnno;
import org.albianj.kernel.api.anno.serv.AblkServAnno;
import org.albianj.kernel.api.anno.serv.AblServAnno;
import org.albianj.common.mybp.Assert;
import org.albianj.kernel.api.attr.GlobalSettings;
import org.albianj.kernel.api.attr.IAblAnnoResolver;
import org.albianj.kernel.impl.core.resolvers.AblAopAnnoResolver;
import org.albianj.kernel.impl.core.resolvers.AblServClassResolver;
import org.albianj.kernel.impl.core.resolvers.AblKServAnnoResolver;
import org.albianj.kernel.impl.core.resolvers.AblServAnnoResolver;
import org.albianj.loader.IAblStarter;
import org.albianj.scanner.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * albianj的启动器
 * start是真正开始内核启动的地方
 */
public class AblKStarter implements IAblStarter {

    private final static String[] pkgs = {
            "org.albianj.api.kernel",
            "org.albianj.impl.kernel"
    };

    private  final static Map<String, IAblAnnoResolver> regAnnos = new HashMap<>(){{
        put(AblkServAnno.class.getName(), new AblKServAnnoResolver());
        put(AblAopPointAnno.class.getName(), new AblAopAnnoResolver());
        put(AblServAnno.class.getName(), new AblServAnnoResolver());
    }};



    @Override
    public void start(ClassLoader loader,Class<?> mainClzz, String configUrl) {
        GlobalSettings settings = GlobalSettings.builder()
                .mainClzz(mainClzz)
                .configurtionFolder(configUrl)
                .loader(loader)
                .build();
        /**
         *  扫描albianj内核的所有注册的anno
         *  启动这些anno标注的service，构建整个albianj service体系
         *
         */

        scanKPkgs(settings);

    }

    @Override
    public void stop() {

    }

    private void scanKPkgs(GlobalSettings gs) {
        try {
            AblScanner.filter(gs.getLoader(),
                    List.of(pkgs),
                    (clzz, annos) -> AblServClassResolver.decideBlgAnno(clzz).getClass(),
                    regAnnos,
                    attr -> {
                        if(LangUtil.isNull(attr)) {
                            return null;
                        }


                        /**
                         * 第一步扫描所有的fields
                         * 按照类和父类组成一个chain
                         */

                        /**
                         * 同上，解析所有的method
                         */

                        return null;
                    }
            );
        }catch (Throwable t){
            Assert.notNull(t,"scan kernel pckage is fail.");
        }
    }

//    /**
//     * 判断class属于哪一个anno，并且解析这个class的anno
//     * @param clzz
//     * @return
//     */
//    private Class<? extends Annotation> decideBelongAnno(Class<?> clzz) {
//        if(clzz.isAnnotationPresent(AblAopAnno.class)) {
//            Assert.isFalse(clzz.isAnnotationPresent(AblServAnno.class),
//                    "Absence annotation.AblAopAnno class must be AblServAnno first.Class:{}",clzz.getName());
//
//            return AblAopAnno.class;
//        }
//
//        if(clzz.isAnnotationPresent(AblkServAnno.class)) {
//            return AblkServAnno.class;
//        }
//
//        if(clzz.isAnnotationPresent(AblServAnno.class)) {
//            return AblServAnno.class;
//        }
//
//        return null;
//    }

    private ClassAttr parserClassAttr(Class<?> clzz, Class<? extends Annotation> belongAnno) {
        IAblAnnoResolver p = regAnnos.get(belongAnno.getName());
       p.parse(clzz);
    }
}
