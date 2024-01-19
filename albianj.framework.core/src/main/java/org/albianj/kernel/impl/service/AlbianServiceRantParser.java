package org.albianj.kernel.impl.service;

import org.albianj.common.scanner.AblClassScanner;
import org.albianj.common.scanner.IAblClassFilter;
import org.albianj.common.scanner.IAblClassParser;
import org.albianj.common.utils.*;
import org.albianj.kernel.anno.*;
import org.albianj.kernel.attr.*;
import org.albianj.kernel.attr.opt.AblVarTypeOpt;
import org.albianj.kernel.ServRouter;
import org.albianj.kernel.itf.service.IAlbianService;
import org.albianj.loader.AlbianClassLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class AlbianServiceRantParser {

    public static HashMap<String, Object> scanPackage(String sessionId,String pkgName) throws Throwable {
        return AblClassScanner.findAllClassFromPkg(AlbianClassLoader.getInstance(),
                pkgName,

                new IAblClassFilter() {
                    @Override
                    public boolean lookup(Class<?> cls) {
                        //must flag with anno and extends IAlbianService
                        // extends interface is compatibling the last version
                        return cls.isAnnotationPresent(AblServAnno.class)
                                && IAlbianService.class.isAssignableFrom(cls)
                                && !cls.isInterface()
                                && !Modifier.isAbstract(cls.getModifiers());
                    }
                },

                new IAblClassParser() {
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

    public static ServiceAttr scanAlbianService(String sessionId, Class<?> implClzz) {
        ServiceAttr asa = new ServiceAttr();

        AblServAnno rant = implClzz.getAnnotation(AblServAnno.class);

        // 因为存在servid可能重叠，和一个接口被多个class实现的情况
        // 所以需要校验servid是否唯一，另外还要校验itfName为servid的也是否唯一
        Class<?> rootItf = deduceRootItf(implClzz,rant);
        String servId = deduceServId(rant,implClzz,rootItf);
        asa.setId(servId);
        asa.setEnable(rant.Enable());
//        asa.setType(implClzz.getName());
        asa.setSelfClass(implClzz);
        asa.setRootItfClass(rootItf);

        if (implClzz.isAnnotationPresent(AblServAspectsAnno.class)) {
            Map<String, ServiceAspectAttr> asaas = scanServAspect(implClzz.getAnnotation(AblServAspectsAnno.class));
            asa.setAspectAttrs(asaas);
        }

        RefArg<MethodAttr> initFn = new RefArg<>();
        RefArg<MethodAttr> unloadFn = new RefArg<>();
        Map<String, MethodAttr> funcAttrs =  scanMethods(implClzz, initFn,unloadFn);
        if(initFn.hasValue()){
            asa.setInitFnAttr(initFn.getValue());
        }
        if(unloadFn.hasValue()){
            asa.setUnloadFnAttr(unloadFn.getValue());
        }
        asa.setFuncAttrs(funcAttrs);

        Map<String, ServiceFieldAttr> fields = scanFields(implClzz);
        if (!CollectionUtil.isNullOrEmpty(fields)) {
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
    private static String deduceServId(AblServAnno rant, Class<?> implClzz, Class<?> rootItf) {
        String servId = rant.ServId();
        if(!StringsUtil.isNullOrEmptyOrAllSpace(servId)) { // 配置了，直接使用配置的
            return servId;
        }
        return rootItf.getName();
    }

    /**
     * 推断唯一的接口
     * @param implClzz
     * @param rant
     * @return
     */
    private static Class<?> deduceRootItf(Class<?> implClzz, AblServAnno rant) {
        if(rant.Interface() != NullValue.class) { // 配置了interface,使用接口名
            return rant.Interface();
        }

        Class<?> itfs[] =  implClzz.getInterfaces();
        if(1 == itfs.length) { // 唯一接口，那么接口名可以当servId
            return itfs[0];
        }

        // 接口不唯一，判断是否有最终的根接口，如果有，那么根接口就是ServId，
        // 如果根接口有多个，那么必须配置servId，否则报异常
        Set<Class<?>> rootItfs = ReflectUtil.findRootInterfaces(implClzz);

        ServRouter.throwIfTrue(CollectionUtil.isNullOrEmpty(rootItfs),
                StringsUtil.nonIdxFormat("{} have one root interfaces.",
                        implClzz.getName()));

        ServRouter.throwIfFalse(1 == rootItfs.size(),
                StringsUtil.nonIdxFormat("{} have mulit root interfaces,so it's AlbianServRant(ServId) cannot NullOrEmpty.",
                        implClzz.getName())
                );

        Class<?> rootItf = (Class<?>) rootItfs.toArray()[0];
        return rootItf;
    }

    /**
     * 扫描servide的所有字段，包括父类字段
     * @param clzz
     * @return
     */
    private static Map<String, ServiceFieldAttr> scanFields(Class<?> clzz) {
        Class tempClass = clzz;
        List<Field> fields = new ArrayList<>() ;
        while (tempClass !=null && !tempClass.getName().equalsIgnoreCase("java.lang.object") ) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        Map<String, ServiceFieldAttr> fieldsAttr = new HashMap<>();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(AblServFieldAnno.class)) {
                ServiceFieldAttr aspa = new ServiceFieldAttr();
                AblServFieldAnno frant = f.getAnnotation(AblServFieldAnno.class);
                String name = f.getName();
                aspa.setName(name);
                aspa.setTypeOpt(frant.Type());
                String value = deduceFieldValueWhenRef(f,frant);
                aspa.setValue(value);
                aspa.setField(f);
                aspa.setItfClzz(frant.itfClzz());
                aspa.setFieldType(f.getType());
                aspa.setAllowNull(frant.AllowNull());
                aspa.setSetterLifetime(frant.SetWhen());
                fieldsAttr.put(f.getName(), aspa);
            }
        }
        return fieldsAttr.isEmpty() ? null : fieldsAttr;
    }

    private static String deduceFieldValueWhenRef(Field f, AblServFieldAnno frant ){
        if(StringsUtil.isNullOrEmptyOrAllSpace(frant.Value())) {
            // 直接配置了
            return frant.Value();
        }
        if(AblVarTypeOpt.Service == frant.Type()) {
            Class<?> itf = frant.itfClzz();
            if(null != itf && itf != NullValue.class && itf.isInterface()) {
                // 引用service赋值，没有配置name，但是配置了接口
                // 那么不管是不是root接口，直接使用该接口
                return itf.getName();
            }

            //只是打了anno，没有配置任何信息
            //首先通过field的类型来判断，如果是接口，直接使用，不管是不是root 接口
            Class<?> clzz = f.getType();
            if(clzz.isInterface()) {
                return clzz.getName();
            }

            //如果都不是，那么尽力了，只能通过field的name来去碰碰运气
        }
        return f.getName();
    }

    private static  Map<String, MethodAttr> scanMethods(Class<?> clzz, RefArg<MethodAttr> initFn, RefArg<MethodAttr> unloadFn){
        Map<String, MethodAttr> methodAttrs = new HashMap<>();

        Class<?> clazz = clzz;
        // 获取类 A 及其父类的所有方法
        while (clazz != null && !clazz.getName().equalsIgnoreCase("java.lang.object") ) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                Class<?> returnType = method.getReturnType();
                Class<?>[] parameterTypes = method.getParameterTypes();
                MethodAttr methodAttr = new MethodAttr(returnType, methodName, parameterTypes,method,null);

                if(method.isAnnotationPresent(AblServInitAnno.class) && null != initFn && !initFn.hasValue() ) {
                    List<MethodArgAttr> argAttrs =  parserInitMethodArgs(method);
                    methodAttr.setArgumentValues(argAttrs);
                    initFn.setValue(methodAttr);
                }

                // unload 不需要参数，以为一般unload都是对于当前自己的对象this进行操作，
                // unload函数可以直接在函数内自行获取
                if(method.isAnnotationPresent(AblServUnloadAnno.class)) {
                    if(null != unloadFn && !unloadFn.hasValue()) {
                        unloadFn.setValue(methodAttr);
                    }
                }

                String methodSign = makeMethodSignName(methodName,parameterTypes);

                // 创建 MethodInfo 对象保存方法信息
                if(!methodAttrs.containsKey(methodSign)) { // 子类重写父类方法,选子类
                    methodAttrs.put(methodSign, methodAttr);
                }
            }
            // 获取父类的 Class 对象
            clazz = clazz.getSuperclass();
        }
        return methodAttrs;
    }

    public static  String makeMethodSignName(String methodName,Class<?>[] parameterTypes) {
        StringBuilder sbParas = new StringBuilder(methodName);
        Arrays.stream(parameterTypes).forEach(e -> sbParas.append("-").append(e.getName()));
        return sbParas.toString();
    }

    /**
     * 解析init函数被调用的时候，传入的参数
     * @param method
     * @return
     */
    private static List<MethodArgAttr> parserInitMethodArgs(Method method) {
        AblServInitAnno initRant = method.getAnnotation(AblServInitAnno.class);
        AblArgumentAnno[] args = initRant.Args();
        if(null != args && 0 != args.length){
            List<MethodArgAttr> argAttrs = new ArrayList<>();
            for(AblArgumentAnno argRant : args){
               String name = argRant.Name();
               AblVarTypeOpt typeOpt = argRant.Type();
               String value = argRant.Value();
               argAttrs.add(new MethodArgAttr(name,typeOpt,value));
            }
            return argAttrs;
        }
        return null;
    }

    private static Map<String, ServiceAspectAttr> scanServAspect(AblServAspectsAnno implClzz) {
        AblServAspectsAnno prants = implClzz;
        Map<String, ServiceAspectAttr> asaas = new HashMap<>();
        for (AblServAspectAnno prant : prants.Aspects()) {
            ServiceAspectAttr aspa = new ServiceAspectAttr();
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
        return asaas;
    }

}
