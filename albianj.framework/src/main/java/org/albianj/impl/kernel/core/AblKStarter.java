package org.albianj.impl.kernel.core;

import org.albianj.api.kernel.anno.proxy.AblAopAnno;
import org.albianj.api.kernel.anno.serv.AblkServAnno;
import org.albianj.api.kernel.anno.serv.AblServAnno;
import org.albianj.common.mybp.Assert;
import org.albianj.common.utils.StringsUtil;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.loader.IAblStarter;
import org.albianj.scanner.*;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
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

    private  final static Map<String, AnnoData> regAnnos = new LinkedHashMap<>(){{
        put(AblkServAnno.class.getName(),
                new AnnoData(AblkServAnno.class.getName(),AblkServAnno.class,new AblServAttrParser()));
        put(AblAopAnno.class.getName(),
                new AnnoData(AblAopAnno.class.getName(),AblAopAnno.class,new AblAopAttrParser()));
        put(AblServAnno.class.getName(),
                new AnnoData(AblServAnno.class.getName(),AblServAnno.class,new AblServAttrParser()));
    }};



    @Override
    public void start(Class<?> mainClzz, String configUrl) {
        /**
         *  扫描albianj内核的所有注册的anno
         *  启动这些anno标注的service，构建整个albianj service体系
         *
         */

        scanKPkgs();

    }

    @Override
    public void stop() {

    }

    private void scanKPkgs() {
        try {
            AblPkgScanner.filter(AlbianClassLoader.getInstance(),
                    List.of(pkgs),
                    new IAblAnnoFilter() {
                        @Override
                        public AblBeanAttr found(Class<?> clzz, Map<String,AnnoData> annos) {
                            return decideBelongAnno(clzz,annos);
                        }
                    },
                    regAnnos,
                    new IAblAnnoParser() {
                        @Override
                        public AblBeanAttr parseBeanClass(AblBeanAttr attr) {


                        }
                    }
            );
        }catch (Throwable t){
            Assert.notNull(t,"scan kernel pckage is fail.");
        }
    }

    /**
     * 判断class属于哪一个anno，并且解析这个class的anno
     * @param clzz
     * @return
     */
    private AblBeanAttr decideBelongAnno(Class<?> clzz,Map<String,AnnoData> annos) {
        AblBeanAttr attr = new AblBeanAttr(clzz);
        Annotation[] selfAnnos = clzz.getAnnotations();
        if(0 == selfAnnos.length) {
            return null;
        }
        attr.setAnnos(selfAnnos);
        for(Map.Entry<String,AnnoData> kv : annos.entrySet()) {
            if(clzz.isAnnotationPresent(kv.getValue().getAnno())) {
                kv.getValue().getParser().annoParser(clzz, attr);
            }
        }

        /**
         * 如果id还是空的话，那么这个class并没有被任何的AblServ，AblkServ，AblAop标注
         * 所以不是albianj的services
         */
        if(StringsUtil.isNotNullEmptyTrimmed(attr.getId())) {
            return null;
        }
        return attr;
    }





}
