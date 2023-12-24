package org.albianj.kernel.impl.service;

import org.albianj.common.utils.CheckUtil;
import org.albianj.kernel.anno.AlbianServiceFieldRant;
import org.albianj.kernel.anno.AlbianServiceProxyRant;
import org.albianj.kernel.anno.AlbianServiceProxysRant;
import org.albianj.kernel.anno.AlbianServiceRant;
import org.albianj.kernel.attr.AlbianServiceAopAttr;
import org.albianj.kernel.attr.AlbianServiceAttr;
import org.albianj.kernel.attr.AlbianServiceFieldAttr;
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

    public static AlbianServiceAttr scanAlbianService(Class<?> implClzz) {
        AlbianServiceAttr asa = new AlbianServiceAttr();
        AlbianServiceRant rant = implClzz.getAnnotation(AlbianServiceRant.class);
        asa.setId(rant.Id());
        if (CheckUtil.isNullOrEmptyOrAllSpace(rant.sInterface()) && null == rant.Interface()) {
            asa.setItfClzzName(IAlbianService.class.getName());
        } else {
            asa.setItfClzzName(null != rant.Interface() ? rant.Interface().getName() : rant.sInterface());
        }
        asa.setEnable(rant.Enable());
        asa.setType(implClzz.getName());
        asa.setServiceClass(implClzz.asSubclass(IAlbianService.class));

        if (implClzz.isAnnotationPresent(AlbianServiceProxysRant.class)) {
            AlbianServiceProxysRant prants = implClzz.getAnnotation(AlbianServiceProxysRant.class);
            Map<String, AlbianServiceAopAttr> asaas = new HashMap<>();
            for (AlbianServiceProxyRant prant : prants.Rants()) {
                AlbianServiceAopAttr aspa = new AlbianServiceAopAttr();
                aspa.setServiceName(prant.ServiceName());
                aspa.setProxyName(prant.ProxyName());

                if (!CheckUtil.isNullOrEmptyOrAllSpace(prant.BeginWith())) {
                    aspa.setBeginWith(prant.BeginWith());
                }
                if (!CheckUtil.isNullOrEmptyOrAllSpace(prant.NotBeginWith())) {
                    aspa.setNotBeginWith(prant.NotBeginWith());
                }

                if (!CheckUtil.isNullOrEmptyOrAllSpace(prant.EndWith())) {
                    aspa.setEndWith(prant.EndWith());
                }
                if (!CheckUtil.isNullOrEmptyOrAllSpace(prant.NotEndWith())) {
                    aspa.setNotEndWith(prant.NotEndWith());
                }

                if (!CheckUtil.isNullOrEmptyOrAllSpace(prant.Contain())) {
                    aspa.setContain(prant.Contain());
                }
                if (!CheckUtil.isNullOrEmptyOrAllSpace(prant.NotContain())) {
                    aspa.setNotContain(prant.NotContain());
                }
                if (!CheckUtil.isNullOrEmptyOrAllSpace(prant.FullName())) {
                    aspa.setFullName(prant.FullName());
                }
                aspa.setAll(prant.IsAll());
                asaas.put(aspa.getProxyName(), aspa);
            }

            asa.setAopAttributes(asaas);
        }

        Map<String, AlbianServiceFieldAttr> fields = scanFields(implClzz);
        if (!CheckUtil.isNullOrEmpty(fields)) {
            asa.setServiceFields(fields);
        }

        return asa;
    }

    private static Map<String, AlbianServiceFieldAttr> scanFields(Class<?> clzz) {
        Class tempClass = clzz;
        List<Field> fields = new ArrayList<>() ;
        while (tempClass !=null && !tempClass.getName().toLowerCase().equals("java.lang.object") ) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        Map<String, AlbianServiceFieldAttr> fieldsAttr = new HashMap<>();
        for (Field f : fields) {
            if (f.isAnnotationPresent(AlbianServiceFieldRant.class)) {
                f.setAccessible(true);
                AlbianServiceFieldAttr aspa = new AlbianServiceFieldAttr();
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
