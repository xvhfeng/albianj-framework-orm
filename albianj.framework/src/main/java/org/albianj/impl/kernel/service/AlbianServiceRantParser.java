package org.albianj.impl.kernel.service;

import org.albianj.ServRouter;
import org.albianj.api.kernel.service.IAblServ;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.api.kernel.anno.serv.AblServFieldRant;
import org.albianj.api.kernel.anno.serv.AblServRant;
import org.albianj.api.kernel.attr.AlbianServiceAopAttribute;
import org.albianj.api.kernel.anno.proxy.AlbianServiceProxyRant;
import org.albianj.api.kernel.anno.proxy.AlbianServiceProxyRants;
import org.albianj.api.kernel.attr.AlbianServiceAttribute;
import org.albianj.api.kernel.attr.AlbianServiceFieldAttribute;
import org.albianj.loader.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class AlbianServiceRantParser {

    public static HashMap<String, Object> scanPackage(String pkgName) throws Throwable {
        return AlbianClassScanner.filter(AlbianClassLoader.getInstance(),
                pkgName,

                new IAlbianClassFilter() {
                    @Override
                    public boolean verify(Class<?> cls) {
                        //must flag with anno and extends IAlbianService
                        // extends interface is compatibling the last version
                        return cls.isAnnotationPresent(AblServRant.class)
                                && IAblServ.class.isAssignableFrom(cls)
                                && !cls.isInterface()
                                && !Modifier.isAbstract(cls.getModifiers());
                    }
                },

                new IAlbianClassExcavator() {
                    @Override
                    public Object found(Class<?> clzz) {
                        return scanAlbianService(clzz);
                    }
                });
    }

    public static String lookupServId(Class<?> implClzz, AblServRant rant) {
        if(!StringsUtil.isNullEmptyTrimmed(rant.Id())) {
            return rant.Id();
        }

        if(IAblServ.class != rant.Interface()){
            return rant.Interface().getName();
        }

           Class<?>[] itfs =  implClzz.getInterfaces();
           if(null != itfs && 0 != itfs.length){
               for(Class<?> itf : itfs){
                   if(IAblServ.class.isAssignableFrom(itf) && itf != IAblServ.class){
                        return itf.getName();
                   }
               }
           }
        return null;
    }

    public static AlbianServiceAttribute scanAlbianService(Class<?> implClzz) {
        AlbianServiceAttribute asa = new AlbianServiceAttribute();
        AblServRant rant = implClzz.getAnnotation(AblServRant.class);
//        asa.setId(rant.Id());
//        if (StringsUtil.isNullEmptyTrimmed(rant.sInterface()) && null == rant.Interface()) {
//            asa.setItf(IAlbianService.class.getName());
//        } else {
//            asa.setItf(null != rant.Interface() ? rant.Interface().getName() : rant.sInterface());
//        }
        String id = lookupServId(implClzz,rant);
        if(null == id){
            ServRouter.throwIfNull( id,"service's:{} id is null.",implClzz.getName());
        }
        asa.setId(id);



//        if (StringsUtil.isNullEmptyTrimmed(rant.sInterface()) && null == rant.Interface()) {
//            asa.setItf(IAlbianService.class.getName());
//        } else {
            asa.setItf(null != rant.Interface() ? rant.Interface().getName() : IAblServ.class.getName());
//        }

        asa.setEnable(rant.Enable());
        asa.setType(implClzz.getName());
        asa.setServiceClass(implClzz.asSubclass(IAblServ.class));

        if (implClzz.isAnnotationPresent(AlbianServiceProxyRants.class)) {
            AlbianServiceProxyRants prants = implClzz.getAnnotation(AlbianServiceProxyRants.class);
            Map<String, AlbianServiceAopAttribute> asaas = new HashMap<>();
            for (AlbianServiceProxyRant prant : prants.Rants()) {
                AlbianServiceAopAttribute aspa = new AlbianServiceAopAttribute();
                aspa.setServiceName(prant.ServiceName());
                aspa.setProxyName(prant.ProxyName());

                if (!StringsUtil.isNullEmptyTrimmed(prant.BeginWith())) {
                    aspa.setBeginWith(prant.BeginWith());
                }
                if (!StringsUtil.isNullEmptyTrimmed(prant.NotBeginWith())) {
                    aspa.setNotBeginWith(prant.NotBeginWith());
                }

                if (!StringsUtil.isNullEmptyTrimmed(prant.EndWith())) {
                    aspa.setEndWith(prant.EndWith());
                }
                if (!StringsUtil.isNullEmptyTrimmed(prant.NotEndWith())) {
                    aspa.setNotEndWith(prant.NotEndWith());
                }

                if (!StringsUtil.isNullEmptyTrimmed(prant.Contain())) {
                    aspa.setContain(prant.Contain());
                }
                if (!StringsUtil.isNullEmptyTrimmed(prant.NotContain())) {
                    aspa.setNotContain(prant.NotContain());
                }
                if (!StringsUtil.isNullEmptyTrimmed(prant.FullName())) {
                    aspa.setFullName(prant.FullName());
                }
                aspa.setAll(prant.IsAll());
                asaas.put(aspa.getProxyName(), aspa);
            }

            asa.setAopAttributes(asaas);
        }

        Map<String, AlbianServiceFieldAttribute> fields = scanFields(implClzz);
        if (!SetUtil.isEmpty(fields)) {
            asa.setServiceFields(fields);
        }

        return asa;
    }

    private static Map<String, AlbianServiceFieldAttribute> scanFields(Class<?> clzz) {
        Class tempClass = clzz;
        List<Field> fields = new ArrayList<>() ;
        while (tempClass !=null && !tempClass.getName().toLowerCase().equals("java.lang.object") ) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        Map<String, AlbianServiceFieldAttribute> fieldsAttr = new HashMap<>();
        for (Field f : fields) {
            if (f.isAnnotationPresent(AblServFieldRant.class)) {
                f.setAccessible(true);
                AlbianServiceFieldAttribute aspa = new AlbianServiceFieldAttribute();
                AblServFieldRant frant = f.getAnnotation(AblServFieldRant.class);
                aspa.setName(f.getName());
                aspa.setType(frant.Type().name());
                aspa.setValue(frant.Value());
                aspa.setField(f);
                aspa.setAllowNull(frant.AllowNull());
                aspa.setSetterLifetime(frant.SetterLifetime());
//                if(AlbianServiceFieldType.Property == frant.Type() ||AlbianServiceFieldType.Field == frant.Type() ) {
//                    aspa.setSetterName(frant.SetterName());
//                }
                fieldsAttr.put(f.getName(), aspa);
            }
        }
        return 0 == fieldsAttr.size() ? null : fieldsAttr;
    }
}
