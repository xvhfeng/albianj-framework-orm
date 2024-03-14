package org.albianj.impl.kernel.service;

import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.anno.serv.AlbianServiceFieldRant;
import org.albianj.kernel.anno.serv.AlbianServiceRant;
import org.albianj.kernel.attr.AlbianServiceAopAttribute;
import org.albianj.kernel.anno.proxy.AlbianServiceProxyRant;
import org.albianj.kernel.anno.proxy.AlbianServiceProxyRants;
import org.albianj.kernel.attr.AlbianServiceAttribute;
import org.albianj.kernel.attr.AlbianServiceFieldAttribute;
import org.albianj.kernel.service.*;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.loader.AlbianClassScanner;
import org.albianj.loader.IAlbianClassExcavator;
import org.albianj.loader.IAlbianClassFilter;

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
                        return cls.isAnnotationPresent(AlbianServiceRant.class)
                                && IAlbianService.class.isAssignableFrom(cls)
                                && !cls.isInterface()
                                && !Modifier.isAbstract(cls.getModifiers());
                    }
                },

                new IAlbianClassExcavator() {
                    @Override
                    public Object finder(Class<?> clzz) {
                        return scanAlbianService(clzz);
                    }
                });
    }

    public static AlbianServiceAttribute scanAlbianService(Class<?> implClzz) {
        AlbianServiceAttribute asa = new AlbianServiceAttribute();
        AlbianServiceRant rant = implClzz.getAnnotation(AlbianServiceRant.class);
        asa.setId(rant.Id());
        if (StringsUtil.isNullOrEmptyOrAllSpace(rant.sInterface()) && null == rant.Interface()) {
            asa.setItf(IAlbianService.class.getName());
        } else {
            asa.setItf(null != rant.Interface() ? rant.Interface().getName() : rant.sInterface());
        }
        asa.setEnable(rant.Enable());
        asa.setType(implClzz.getName());
        asa.setServiceClass(implClzz.asSubclass(IAlbianService.class));

        if (implClzz.isAnnotationPresent(AlbianServiceProxyRants.class)) {
            AlbianServiceProxyRants prants = implClzz.getAnnotation(AlbianServiceProxyRants.class);
            Map<String, AlbianServiceAopAttribute> asaas = new HashMap<>();
            for (AlbianServiceProxyRant prant : prants.Rants()) {
                AlbianServiceAopAttribute aspa = new AlbianServiceAopAttribute();
                aspa.setServiceName(prant.ServiceName());
                aspa.setProxyName(prant.ProxyName());

                if (!StringsUtil.isNullOrEmptyOrAllSpace(prant.BeginWith())) {
                    aspa.setBeginWith(prant.BeginWith());
                }
                if (!StringsUtil.isNullOrEmptyOrAllSpace(prant.NotBeginWith())) {
                    aspa.setNotBeginWith(prant.NotBeginWith());
                }

                if (!StringsUtil.isNullOrEmptyOrAllSpace(prant.EndWith())) {
                    aspa.setEndWith(prant.EndWith());
                }
                if (!StringsUtil.isNullOrEmptyOrAllSpace(prant.NotEndWith())) {
                    aspa.setNotEndWith(prant.NotEndWith());
                }

                if (!StringsUtil.isNullOrEmptyOrAllSpace(prant.Contain())) {
                    aspa.setContain(prant.Contain());
                }
                if (!StringsUtil.isNullOrEmptyOrAllSpace(prant.NotContain())) {
                    aspa.setNotContain(prant.NotContain());
                }
                if (!StringsUtil.isNullOrEmptyOrAllSpace(prant.FullName())) {
                    aspa.setFullName(prant.FullName());
                }
                aspa.setAll(prant.IsAll());
                asaas.put(aspa.getProxyName(), aspa);
            }

            asa.setAopAttributes(asaas);
        }

        Map<String, AlbianServiceFieldAttribute> fields = scanFields(implClzz);
        if (!SetUtil.isNullOrEmpty(fields)) {
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
            if (f.isAnnotationPresent(AlbianServiceFieldRant.class)) {
                f.setAccessible(true);
                AlbianServiceFieldAttribute aspa = new AlbianServiceFieldAttribute();
                AlbianServiceFieldRant frant = f.getAnnotation(AlbianServiceFieldRant.class);
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
