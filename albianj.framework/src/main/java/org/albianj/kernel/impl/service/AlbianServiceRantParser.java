package org.albianj.kernel.impl.service;

import org.albianj.AlbianRuntimeException;
import org.albianj.common.scanner.AlbianClassScanner;
import org.albianj.common.scanner.IAlbianClassFilter;
import org.albianj.common.scanner.IAlbianClassParser;
import org.albianj.common.utils.*;
import org.albianj.kernel.anno.*;
import org.albianj.kernel.attr.*;
import org.albianj.kernel.attr.opt.AlbianBuiltinTypeOpt;
import org.albianj.kernel.kit.service.IAlbianService;
import org.albianj.loader.AlbianClassLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class AlbianServiceRantParser {

    public static HashMap<String, Object> scanPackage(String sessionId,String pkgName) throws Throwable {
        return AlbianClassScanner.findAllClassFromPkg(AlbianClassLoader.getInstance(),
                pkgName,

                new IAlbianClassFilter() {
                    @Override
                    public boolean lookup(Class<?> cls) {
                        //must flag with anno and extends IAlbianService
                        // extends interface is compatibling the last version
                        return cls.isAnnotationPresent(AlbianServRant.class)
                                && IAlbianService.class.isAssignableFrom(cls)
                                && !cls.isInterface()
                                && !Modifier.isAbstract(cls.getModifiers());
                    }
                },

                new IAlbianClassParser() {
                    @Override
                    public Object parser(Class<?> clzz) {
                        return scanAlbianService(sessionId,clzz);
                    }
                });
    }



//    public static AlbianServiceAttr scanAlbianService(Class<?> implClzz) {
//        AlbianServiceAttr asa = new AlbianServiceAttr();
//        AlbianServRant rant = implClzz.getAnnotation(AlbianServRant.class);
//        asa.setId(rant.Id());
//        if (CheckUtil.isNullOrEmptyOrAllSpace(rant.sInterface()) && null == rant.Interface()) {
//            asa.setItfClzzName(IAlbianService.class.getName());
//        } else {
//            asa.setItfClzzName(null != rant.Interface() ? rant.Interface().getName() : rant.sInterface());
//        }
//        asa.setEnable(rant.Enable());
//        asa.setType(implClzz.getName());
//        asa.setSelfClass(implClzz.asSubclass(IAlbianService.class));
//
//        if (implClzz.isAnnotationPresent(AlbianServAspectsRant.class)) {
//            Map<String, AlbianServiceAspectAttr> asaas = scanServAspectAttrs(implClzz.getAnnotation(AlbianServAspectsRant.class));
//
//            asa.setAspectAttrs(asaas);
//        }
//
//        Map<String, AlbianServiceFieldAttr> fields = scanFields(implClzz);
//        if (!CheckUtil.isNullOrEmpty(fields)) {
//            asa.setFieldAttrs(fields);
//        }
//
//        return asa;
//    }

    public static AlbianServiceAttr scanAlbianService(String sessionId,Class<?> implClzz) {
        AlbianServiceAttr asa = new AlbianServiceAttr();

        AlbianServRant rant = implClzz.getAnnotation(AlbianServRant.class);

        // 因为存在servid可能重叠，和一个接口被多个class实现的情况
        // 所以需要校验servid是否唯一，另外还要校验itfName为servid的也是否唯一
        String servId = deduceServId(implClzz,rant);
        asa.setId(servId);
        asa.setEnable(rant.Enable());
        asa.setType(implClzz.getName());
        asa.setSelfClass(implClzz);

        if (implClzz.isAnnotationPresent(AlbianServAspectsRant.class)) {
            Map<String, AlbianServiceAspectAttr> asaas = scanServAspectAttrs(implClzz.getAnnotation(AlbianServAspectsRant.class));
            asa.setAspectAttrs(asaas);
        }

        RefArg<AlbianMethodAttr> initFn = new RefArg<>();
        RefArg<AlbianMethodAttr> unloadFn = new RefArg<>();
        Map<String, AlbianMethodAttr> funcAttrs =  scanMethods(implClzz, initFn,unloadFn);
        if(initFn.hasValue()){
            asa.setInitFnAttr(initFn.getValue());
        }
        if(unloadFn.hasValue()){
            asa.setUnloadFnAttr(unloadFn.getValue());
        }
        asa.setFuncAttrs(funcAttrs);

        Map<String, AlbianServiceFieldAttr> fields = scanFields(implClzz);
        if (!CheckUtil.isNullOrEmpty(fields)) {
            asa.setFieldAttrs(fields);
        }

        return asa;
    }

    /**
     * 推断servId
     * @param implClzz
     * @param rant
     * @return
     */
    private static String deduceServId(Class<?> implClzz,AlbianServRant rant) {
        String servId = rant.value();
        if(!CheckUtil.isNullOrEmptyOrAllSpace(servId)) { // 配置了，直接使用配置的
            return servId;
        }

        if(rant.Interface() != NullValue.class) { // 配置了interface,使用接口名
            return rant.Interface().getName();
        }

        Class<?> itfs[] =  implClzz.getInterfaces();
        if(1 == itfs.length) { // 唯一接口，那么接口名可以当servId
            return  itfs[0].getName();
        }

        // 接口不唯一，判断是否有最终的根接口，如果有，那么根接口就是ServId，
        // 如果根接口有多个，那么必须配置servId，否则报异常
        Set<Class<?>> rootItfs = ReflectUtil.findRootInterfaces(implClzz);
        if (1 != rootItfs.size()) { // 多个root接口的，必须配置AlbianServRant的ServId,否则无法找到
            throw new AlbianRuntimeException(
                    StringsUtil.nonIdxFormat(
                            "{} have mulit root interfaces,so it's AlbianServRant(ServId) cannot NullOrEmpty.",
                            implClzz.getName()));
        }
        Class<?> rootItf = (Class<?>) rootItfs.toArray()[0];
        return  rootItf.getName();
    }

    /**
     * 扫描servide的所有字段，包括父类字段
     * @param clzz
     * @return
     */
    private static Map<String, AlbianServiceFieldAttr> scanFields(Class<?> clzz) {
        Class tempClass = clzz;
        List<Field> fields = new ArrayList<>() ;
        while (tempClass !=null && !tempClass.getName().toLowerCase().equals("java.lang.object") ) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        Map<String, AlbianServiceFieldAttr> fieldsAttr = new HashMap<>();
        for (Field f : fields) {
            if (f.isAnnotationPresent(AlbianServFieldRant.class)) {
                f.setAccessible(true);
                AlbianServiceFieldAttr aspa = new AlbianServiceFieldAttr();
                AlbianServFieldRant frant = f.getAnnotation(AlbianServFieldRant.class);
                aspa.setName(f.getName());
                aspa.setType(frant.Type().name());
                aspa.setValue(frant.Value());
                aspa.setField(f);
                aspa.setAllowNull(frant.AllowNull());
                aspa.setSetterLifetime(frant.SetterLifetime());
                fieldsAttr.put(f.getName(), aspa);
            }
        }
        return fieldsAttr.isEmpty() ? null : fieldsAttr;
    }

    private static  Map<String, AlbianMethodAttr> scanMethods(Class<?> clzz, RefArg<AlbianMethodAttr> initFn,RefArg<AlbianMethodAttr> unloadFn){
        Map<String, AlbianMethodAttr> methodAttrs = new HashMap<>();

        Class<?> clazz = clzz;
        // 获取类 A 及其父类的所有方法
        while (clazz != null && !clazz.getName().equalsIgnoreCase("java.lang.object") ) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                Class<?> returnType = method.getReturnType();
                Class<?>[] parameterTypes = method.getParameterTypes();
                AlbianMethodAttr methodAttr = new AlbianMethodAttr(returnType, methodName, parameterTypes,null);

                if(method.isAnnotationPresent(AlbianServInitRant.class) && null != initFn && !initFn.hasValue() ) {
                    List<AlbianMethodArgAttr> argAttrs =  parserInitMethodArgs(method);
                    methodAttr.setArgumentValues(argAttrs);
                    initFn.setValue(methodAttr);
                }

                // unload 不需要参数，以为一般unload都是对于当前自己的对象this进行操作，
                // unload函数可以直接在函数内自行获取
                if(method.isAnnotationPresent(AlbianServUnloadRant.class)) {
                    if(null != unloadFn && !unloadFn.hasValue()) {
                        unloadFn.setValue(methodAttr);
                    }
                }

                // 创建 MethodInfo 对象保存方法信息
                if(!methodAttrs.containsKey(methodName)) { // 子类重写父类方法,选子类
                    methodAttrs.put(methodName, methodAttr);
                }
            }
            // 获取父类的 Class 对象
            clazz = clazz.getSuperclass();
        }
        return methodAttrs;
    }

    /**
     * 解析init函数被调用的时候，传入的参数
     * @param method
     * @return
     */
    private static List<AlbianMethodArgAttr> parserInitMethodArgs(Method method) {
        AlbianServInitRant initRant = method.getAnnotation(AlbianServInitRant.class);
        AlbianMethodArgRant[] args = initRant.Args();
        if(null != args && 0 != args.length){
            List<AlbianMethodArgAttr> argAttrs = new ArrayList<>();
            for(AlbianMethodArgRant argRant : args){
               String name = argRant.Name();
               AlbianBuiltinTypeOpt typeOpt = argRant.Type();
               String value = argRant.Value();
               argAttrs.add(new AlbianMethodArgAttr(name,typeOpt,value));
            }
            return argAttrs;
        }
        return null;
    }

    private static Map<String, AlbianServiceAspectAttr> scanServAspectAttrs(AlbianServAspectsRant implClzz) {
        AlbianServAspectsRant prants = implClzz;
        Map<String, AlbianServiceAspectAttr> asaas = new HashMap<>();
        for (AlbianServAspectRant prant : prants.Aspects()) {
            AlbianServiceAspectAttr aspa = new AlbianServiceAspectAttr();
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
        return asaas;
    }

}
