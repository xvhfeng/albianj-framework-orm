package org.albianj.kernel.impl.service;

import org.albianj.kernel.common.utils.CheckUtil;
import org.albianj.kernel.aop.AlbianServiceProxyRant;
import org.albianj.kernel.aop.AlbianServiceProxyRants;
import org.albianj.kernel.aop.IAlbianServiceAopAttribute;
import org.albianj.kernel.impl.aop.AlbianServiceAopAttribute;
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

    public static IAlbianServiceAttribute scanAlbianService(Class<?> implClzz) {
        IAlbianServiceAttribute asa = new AlbianServiceAttribute();
        AlbianServiceRant rant = implClzz.getAnnotation(AlbianServiceRant.class);
        asa.setId(rant.Id());
        if (CheckUtil.isNullOrEmptyOrAllSpace(rant.sInterface()) && null == rant.Interface()) {
            asa.setInterface(IAlbianService.class.getName());
        } else {
            asa.setInterface(null != rant.Interface() ? rant.Interface().getName() : rant.sInterface());
        }
        asa.setEnable(rant.Enable());
        asa.setType(implClzz.getName());
        asa.setServiceClass(implClzz.asSubclass(IAlbianService.class));

        if (implClzz.isAnnotationPresent(AlbianServiceProxyRants.class)) {
            AlbianServiceProxyRants prants = implClzz.getAnnotation(AlbianServiceProxyRants.class);
            Map<String, IAlbianServiceAopAttribute> asaas = new HashMap<>();
            for (AlbianServiceProxyRant prant : prants.Rants()) {
                IAlbianServiceAopAttribute aspa = new AlbianServiceAopAttribute();
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
                aspa.setIsAll(prant.IsAll());
                asaas.put(aspa.getProxyName(), aspa);
            }

            asa.setAopAttributes(asaas);
        }

        Map<String, IAlbianServiceFieldAttribute> fields = scanFields(implClzz);
        if (!CheckUtil.isNullOrEmpty(fields)) {
            asa.setServiceFields(fields);
        }

        return asa;
    }

    private static Map<String, IAlbianServiceFieldAttribute> scanFields(Class<?> clzz) {
        Class tempClass = clzz;
        List<Field> fields = new ArrayList<>() ;
        while (tempClass !=null && !tempClass.getName().toLowerCase().equals("java.lang.object") ) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        Map<String, IAlbianServiceFieldAttribute> fieldsAttr = new HashMap<>();
        for (Field f : fields) {
            if (f.isAnnotationPresent(AlbianServiceFieldRant.class)) {
                f.setAccessible(true);
                IAlbianServiceFieldAttribute aspa = new AlbianServiceFieldAttribute();
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
