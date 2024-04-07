package org.albianj.kernel.impl.core.resolvers;

import org.albianj.common.utils.SetUtil;
import org.albianj.common.values.RefArg;
import org.albianj.kernel.api.anno.proxy.AblAopPointAnno;
import org.albianj.kernel.api.anno.serv.*;
import org.albianj.common.mybp.Assert;
import org.albianj.common.utils.LangUtil;
import org.albianj.common.utils.ReflectUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.impl.core.resolvers.data.ResolverClassCached;
import org.albianj.scanner.*;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;


/**
 * 对于java bean class的解析
 * 因为生命周期的问题，所以必须从最底层的虚拟类开始解析
 */
public class AblServClassResolver {

    public ClassAttr parse(Class<?> clzz) {

        // 是否已经被解析过
        IResolverAttr attr = ResolverClassCached.get(clzz.getName());
        ItfAttr itfAttr = null;
        if(LangUtil.isNotNull(attr) && attr instanceof ClassAttr) {
            return (ClassAttr) attr;
        }

        Annotation[] annos = clzz.getAnnotations();
        Map<String, FieldAttr> bfrFieldMap = new LinkedHashMap<>();
        Map<String, FieldAttr> aftFieldMap = new LinkedHashMap<>();

        var attrb = ClassAttr.builder()
                    .clzzFullName(clzz.getName())
                .clzz(clzz)
                .annos(annos);

        /**
         * 在父子继承关系中，子类一般都会明确标注Anno
         * 但父类不一定会标注，并且父子class之间的Anno不一定就是一样的
         * 所以每次都需要判断
         */
        Annotation blgAnno = decideBlgAnno(clzz);
        attrb.blgAnno(blgAnno);

        /**
         * 对于fields来说，albianj不需要解析所有的fields
         * 只解析需要albianj管理的fields，因为这种fields会影响albianj的生命周期
         * 即只需要解析具有AblAutoSet标注的即可
         */
        parseFields(clzz, bfrFieldMap, aftFieldMap);
        attrb.fieldsTblOfBfr(bfrFieldMap).fieldsTblOfAft(aftFieldMap);

        /**
         * 解析class中的方法
         * 所有的方法都解析，防止AOP中有操作需求
         */
        RefArg<FuncAttr> ctorAttr = new RefArg<>();
        RefArg<FuncAttr> dtorAttr = new RefArg<>();
        Map<String, FuncAttr> factoryFnTbl = new LinkedHashMap<>();
        Map<String, FuncAttr> normalFnTbl = new LinkedHashMap<>();
        parseFns(clzz,ctorAttr,dtorAttr, factoryFnTbl, normalFnTbl);
        attrb.ctor(ctorAttr.getValue())
                .dtor(dtorAttr.getValue())
                .factoryFnsTbl(factoryFnTbl)
                .fnsTbl(normalFnTbl);

        Map<String, ItfAttr> itfAttrs = parseItfs(clzz);
        attrb.itfsAttr(itfAttrs);

        ClassAttr superCMate = null;
        Class<?> superClzz = clzz.getSuperclass();
        if(superClzz != Object.class){
            superCMate = parse(superClzz);
        }

        if(LangUtil.isNotNull(superCMate)) {
            attrb.superClassAttr(superCMate);
        }

        var classAttr =  attrb.build();
        ResolverClassCached.putIfAbsent(clzz.getName(),classAttr);
        return classAttr;
    }

    private void parseFns(Class<?> clzz,
                            RefArg<FuncAttr> ctorAttr,
                            RefArg<FuncAttr> dtorAttr,
                            Map<String, FuncAttr> factoryFnTbl,
                            Map<String, FuncAttr> normalFnTbl) {
        Method[] fns = clzz.getDeclaredMethods();
        if(SetUtil.isNotEmpty(fns)){
            Arrays.stream(fns).forEach(f -> {
                FuncAttr fnAttr = parseMethod(clzz,f);
                switch (fnAttr.getFnOpt()) {
                    case Factory: {
                        factoryFnTbl.put(fnAttr.getSign(),fnAttr);
                        break;
                    }
                    case Dtor: {
                        ctorAttr.setValue(fnAttr);
                        break;
                    }
                    case Ctor:{
                       dtorAttr.setValue(fnAttr);
                        break;
                    }
                    case Normal:
                    default:{
                        normalFnTbl.put(fnAttr.getSign(),fnAttr);
                        break;
                    }
                }
            });
        }
    }

    private void parseFields(Class<?> clzz,
                             Map<String, FieldAttr> bfrFieldMap,
                             Map<String, FieldAttr> aftFieldMap) {
        Field[] fields = clzz.getDeclaredFields();
        if(SetUtil.isNotEmpty(fields)) {
            Arrays.stream(fields).forEach(f -> {
                f.setAccessible(true);
                if(f.isAnnotationPresent(AblAutoSetAnno.class)) {
                    FieldAttr fieldAttr = parseAutoSetField(clzz,f);
                    if (SetWhenOpt.BeforeInit == fieldAttr.getAutoAttr().getSetWhenOpt()) {
                        bfrFieldMap.put(f.getName(),fieldAttr);
                    } else {
                        aftFieldMap.put(f.getName(),fieldAttr);
                    }
                }
            });
        }
    }

    private Map<String, ItfAttr> parseItfs(Class<?> clzz) {
        Map<String, ItfAttr> itfAttrs = new LinkedHashMap<>();
        Class<?>[] itfs =  clzz.getInterfaces();
        if(SetUtil.isNotEmpty(itfs)) {
            Arrays.stream(itfs).forEach(e -> {
                IResolverAttr attr = ResolverClassCached.get(e.getName());
                ItfAttr itfAttr = null;
                if(LangUtil.isNotNull(attr) && attr instanceof ItfAttr) {
                    itfAttr = (ItfAttr) attr;
                } else {
                    itfAttr = parseItf(e);
                    ResolverClassCached.putIfAbsent(e.getName(),itfAttr);
                }
                itfAttrs.put(itfAttr.getClzzFullName(),itfAttr);
            });
        }
        return itfAttrs;
    }

    /**
     * 解析接口
     * @param itf
     * @return
     */
    private ItfAttr parseItf(Class<?> itf) {
        var itfAttr =  ItfAttr.builder()
                .clzzFullName(itf.getName())
                .clzz(itf);
        Map<String, FuncAttr> factoryFnTbl = new LinkedHashMap<>();
        Map<String, FuncAttr> normalFnTbl = new LinkedHashMap<>();

        Method[] fns = itf.getDeclaredMethods();
        if(SetUtil.isNotEmpty(fns)){
            Arrays.stream(fns).forEach(f -> {
                if(f.isDefault()) {
                    FuncAttr fnAttr = parseMethod(itf, f);
                    switch (fnAttr.getFnOpt()) {
                        case Factory: {
                            factoryFnTbl.put(fnAttr.getSign(), fnAttr);
                            break;
                        }
                        case Dtor: {
                            itfAttr.dtor(fnAttr);

                            break;
                        }
                        case Ctor: {
                            itfAttr.ctor(fnAttr);
                            break;
                        }
                        case Normal:
                        default: {
                            normalFnTbl.put(fnAttr.getSign(), fnAttr);
                            break;
                        }
                    }
                }
            });
        }

        itfAttr.factoryFnsTbl(factoryFnTbl);
        itfAttr.fnsTbl(normalFnTbl);

        Class<?>[] itfs =  itf.getInterfaces();
        if(SetUtil.isNotEmpty(itfs)) {
            Arrays.stream(itfs).forEach(e -> {
                ItfAttr iMateAttr = parseItf(e);
            });
        }

        return itfAttr.build();
    }

    /**
     * 判断class属于哪一个anno，并且解析这个class的anno
     * @param clzz
     * @return
     */
    public static Annotation decideBlgAnno(Class<?> clzz) {
        if(clzz.isAnnotationPresent(AblAopPointAnno.class)) {
            Assert.isFalse(clzz.isAnnotationPresent(AblServAnno.class),
                    "Absence annotation.AblAopAnno class must be AblServAnno first.Class:{}",clzz.getName());

            return clzz.getAnnotation(AblAopPointAnno.class);
        }

        if(clzz.isAnnotationPresent(AblkServAnno.class)) {
            return clzz.getAnnotation(AblkServAnno.class);
        }

        if(clzz.isAnnotationPresent(AblServAnno.class)) {
            return clzz.getAnnotation(AblServAnno.class);
        }

        return null;
    }

    /**
     * 解析自动赋值的字段
     * @param clzz
     * @param f
     * @return
     */
    private FieldAttr parseAutoSetField(Class<?> clzz, Field f) {
            FieldAttr fieldAttr = new FieldAttr();
            //首先解析字段的基本程序信息
            fieldAttr.setField(f);
            fieldAttr.setSimpleName(f.getName());
            fieldAttr.setFullName(StringsUtil.nonIdxFmt("{}.{}",clzz.getName(),f.getName()));
            fieldAttr.setType(f.getType());
            fieldAttr.setGetGenericType(f.getGenericType());
            try {
                PropertyDescriptor pd = ReflectUtil.getBeanPropertyDescriptor(clzz, f.getName());
                if (LangUtil.isNotNull(pd.getReadMethod())) {
                    fieldAttr.setGetter(pd.getReadMethod());
                }
                if (LangUtil.isNotNull(pd.getWriteMethod())) {
                    fieldAttr.setSetter(pd.getWriteMethod());
                }
            }catch (Throwable t) {
                Assert.isRaise(t,"field:{} of service:{} parser getter/setter propertryDescriptor is raise.",
                        f.getName(),clzz.getName());
            }

            // 再解析字段的AblAutoAnno信息
            AblAutoSetAnno anno = f.getAnnotation(AblAutoSetAnno.class);
            AutoSetAnnoAttr autoAttr = new AutoSetAnnoAttr();
            String vaule = anno.value();
            if(StringsUtil.isNullEmptyTrimmed(vaule)) {
                vaule = f.getType().getName();
            }
            autoAttr.setValue(vaule);
            autoAttr.setSetWhenOpt(anno.when());
            autoAttr.setThrowIfNull(anno.throwIfNull());
            fieldAttr.setAutoAttr(autoAttr);

            return fieldAttr;
    }

    /**
     * 解析函数
     * @param clzz
     * @param fn
     * @return
     */
    private FuncAttr parseMethod(Class<?> clzz, Method fn) {
        if(fn.isBridge() || fn.isSynthetic() ||  Modifier.isAbstract(fn.getModifiers())) {
            /**
             * 桥接方法，编译期自动生成的合成方法，抽象方法
             * 忽略
             */
            return null;
        }
        fn.setAccessible(true);
        var fnAttr = FuncAttr.builder();
        String sign = ReflectUtil.makeMethodSignJVMForm(fn);
        String fullName = StringsUtil.nonIdxFmt("{}.{}",clzz.getName(),fn.getName());
        fnAttr.fn(fn)
                .blgClass(clzz)
                .modifier(fn.getModifiers())
                .simpleName(fn.getName())
                .fullName(fullName)
                .sign(sign);

        Class<?> rtnClzz = fn.getReturnType();
        fnAttr.rtnType(rtnClzz);

        Parameter[] paras = fn.getParameters();
        List<ArgAnnoAttr> argAttrs = new ArrayList<>(paras.length);

        if(SetUtil.isNotEmpty(paras)) {
            for(int i = 0;i < paras.length;i++) {
                Parameter p = paras[i];
                Class<?> pc = p.getType();
                var a3b = ArgAnnoAttr.builder()
                        .clazz(pc)
                        .idx(i)
                        .name(p.getName())
                        .varArgs(p.isVarArgs());

                String resId = null;
                if(p.isAnnotationPresent(AblArgSetAnno.class)) {
                    AblArgSetAnno argAnno = p.getAnnotation(AblArgSetAnno.class);
                    if(StringsUtil.isNotEmptyTrimmed(argAnno.value())) {
                        resId = argAnno.value();
                    } else {
                        if(LangUtil.isNotNull(argAnno.clzz())) {
                            resId = argAnno.clzz().getName();
                        }
                    }
                }

                // 当只有AblArgAetAnno标注，但是并未给任何值的时候
                // 默认使用参数的类型来当资源的id
                if(StringsUtil.isNotEmptyTrimmed(resId)) {
                    resId = pc.getName();
                }
                a3b.value(resId);

                argAttrs.add(a3b.build());
            }
        }
        fnAttr.args(paras);
        fnAttr.argAttrs(argAttrs);

        Class<?>[] raises = fn.getExceptionTypes();
        fnAttr.raises(raises);

        if(fn.isAnnotationPresent(AblFnAnno.class)) {
            AblFnAnno fnAnno = fn.getAnnotation(AblFnAnno.class);
            AblFnOpt fnOpt = fnAnno.Opt();
            fnAttr.fnOpt(fnOpt);
            if(fnOpt == AblFnOpt.Factory) {
                if(StringsUtil.isNotEmptyTrimmed(fnAnno.value())) {
                    fnAttr.resIdForFactory(fnAnno.value());
                } else {
                    fnAttr.resIdForFactory(rtnClzz.getName());
                }
            }
        }

        return fnAttr.build();
    }


//    public void parserAllFields(Class<?> clzz,
//                                Map<String,AblFieldAttr> fieldAttrMapOfBeforeInit,
//                                Map<String,AblFieldAttr> fieldAttrMapOfAfterInit) {
//        List<Field> fields = ReflectUtil.getAllFields(clzz);
//
//        fields.stream().parallel().forEach(e -> {
//            // 只解析AblAutoAnno修饰的字段
//            if(e.isAnnotationPresent(AblAutoAnno.class)) {
//                //首先解析字段的基本程序信息
//                e.setAccessible(true);
//                AblFieldAttr fieldAttr = new AblFieldAttr();
//                fieldAttr.setField(e);
//                fieldAttr.setSimpleName(e.getName());
//                fieldAttr.setFullName(StringsUtil.nonIdxFmt("{}.{}",clzz.getName(),e.getName()));
//                fieldAttr.setClzz(e.getType());
//                fieldAttr.setType(e.getGenericType());
//                try {
//                    PropertyDescriptor pd = ReflectUtil.getBeanPropertyDescriptor(clzz, e.getName());
//                    if (LangUtil.isNotNull(pd.getReadMethod())) {
//                        fieldAttr.setGetter(pd.getReadMethod());
//                    }
//                    if (LangUtil.isNotNull(pd.getWriteMethod())) {
//                        fieldAttr.setSetter(pd.getWriteMethod());
//                    }
//                }catch (Throwable t) {
//                    Assert.isRaise(t,"field:{} of service:{} parser getter/setter propertryDescriptor is raise.",
//                            e.getName(),clzz.getName());
//                }
//
//                // 再解析字段的AblAutoAnno信息
//                AblAutoAnno anno = e.getAnnotation(AblAutoAnno.class);
//                AblAutoAttr autoAttr = new AblAutoAttr();
//                Assert.isTrue(StringsUtil.isNullEmptyTrimmed(anno.value()) && StringsUtil.isNullEmptyTrimmed(anno.id()),
//                    "field:{} of service:{} AblAutoAnno's id/value bose empty or trimmed.",
//                        e.getName(),clzz.getName());
//
//                String realId = StringsUtil.isNotNullEmptyTrimmed(anno.id()) ? anno.id() : anno.value();
//                autoAttr.setValue(realId);
//                autoAttr.setSetWhenOpt(anno.when());
//                autoAttr.setThrowIfNull(anno.throwIfNull());
//                fieldAttr.setAutoAttr(autoAttr);
//                switch (anno.when()) {
//                    case BeforeInit: {
//                        fieldAttrMapOfBeforeInit.put(e.getName(),fieldAttr);
//                        break;
//                    }
//                    case AfterInit:
//                    default:{
//                        fieldAttrMapOfAfterInit.put(e.getName(),fieldAttr);
//                        break;
//                    }
//                }
//            }
//        });
//    }




}
