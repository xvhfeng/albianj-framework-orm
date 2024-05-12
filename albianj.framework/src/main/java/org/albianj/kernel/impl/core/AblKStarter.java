package org.albianj.kernel.impl.core;

import org.albianj.common.spring.Assert;
import org.albianj.common.spring.CollectionUtils;
import org.albianj.common.utils.ReflectUtil;
import org.albianj.kernel.api.anno.AblServScanAnno;
import org.albianj.kernel.api.attr.ClassAttr;
import org.albianj.kernel.api.attr.GlobalSettings;
import org.albianj.kernel.api.attr.IAblAnnoResolver;
import org.albianj.kernel.impl.core.resolvers.AblAopAnnoResolver;
import org.albianj.kernel.impl.core.resolvers.AblServClassResolver;
import org.albianj.kernel.impl.core.resolvers.AblKServAnnoResolver;
import org.albianj.kernel.impl.core.resolvers.AblServAnnoResolver;
import org.albianj.loader.IAblStarter;
import org.albianj.scanner.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * albianj的启动器
 * start是真正开始内核启动的地方
 */
public class AblKStarter implements IAblStarter {

//    private final static String[] pkgs = {
//            "org.albianj.api.kernel",
//            "org.albianj.impl.kernel"
//    };

//    private  final static Map<String, IAblAnnoResolver> regAnnos = new HashMap<>(){{
//        put(KservAnno.class.getName(), new AblKServAnnoResolver());
//        put(AblAopPointAnno.class.getName(), new AblAopAnnoResolver());
//        put(AblServAnno.class.getName(), new AblServAnnoResolver());
//    }};



    @Override
    public void start(ClassLoader loader,Class<?> mainClzz, String configUrl) {
        GlobalSettings settings = GlobalSettings.builder()
                .mainClzz(mainClzz)
                .configurtionFolder(configUrl)
                .loader(loader)
                .build();

        List<String> kPkgs = defineKservScanPkgs();
        List<String> bussPkgs = alyServScanPkgs(mainClzz);
        settings.setKrlPkgs(kPkgs);
        settings.setBssPkgs(bussPkgs);

        /**
         *  扫描albianj内核的所有注册的anno
         *  启动这些anno标注的service，构建整个albianj service体系
         *
         */
        scanPkgs(settings);

    }

    @Override
    public void stop() {

    }

    private void scanPkgs(GlobalSettings gs) {
        try {
            AblScanner.scan(gs.getLoader(),
                    gs.getKrlPkgs(),
                    (clzz) -> ReflectUtil.isClassOrInterface(clzz) ? clzz : null ,
                    (clzz) -> {
                        parseClzzThenCached(clzz);
                    }
            );
        }catch (Throwable t){
            Assert.notNull(t,"scan kernel pckage is fail.");
        }
    }

    private void parseClzzThenCached(Class<?> clzz){

    }

    /**
     * 定义系统service包的路径
     * @return
     */
    private List<String> defineKservScanPkgs(){
        return new ArrayList<>(){{
            add("org.albianj.kernel");
        }};
    }
    /**
     * 解析当前业务的service包路径
     * @param mainClzz
     * @return
     */
    private List<String> alyServScanPkgs(Class<?> mainClzz) {
        List<String> pkgs = new ArrayList<>();
        String mainPkgName =  mainClzz.getPackageName();
        pkgs.add(mainPkgName);

        if(mainClzz.isAnnotationPresent(AblServScanAnno.class)) {
            AblServScanAnno servScanAnno = mainClzz.getAnnotation(AblServScanAnno.class);
            String[] scanPkgs = servScanAnno.packages();
            CollectionUtils.mergeArrayIntoCollection(scanPkgs,pkgs);
        }

        return pkgs;
    }

    private ClassAttr parserClassAttr(Class<?> clzz, Class<? extends Annotation> belongAnno) {
        IAblAnnoResolver p = regAnnos.get(belongAnno.getName());
       p.parse(clzz);
    }
}
