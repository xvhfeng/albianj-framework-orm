package org.albianj.kernel.impl.core;

import org.albianj.kernel.api.anno.serv.AblAutoAnno;
import org.albianj.common.mybp.Assert;
import org.albianj.common.utils.LangUtil;
import org.albianj.common.utils.ReflectUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.common.values.RefArg;
import org.albianj.scanner.AblAutoAttr;
import org.albianj.scanner.AblFieldAttr;
import org.albianj.scanner.AblMethodAttr;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 对于java bean class的解析
 * 因为考虑到性能的问题（多次遍历），所以使用函数参数的方式来一次性的获取所需要的属性
 */
public class AblClassParser {
    public void parserAllFields(Class<?> clzz,
                                Map<String,AblFieldAttr> fieldAttrMapOfBeforeInit,
                                Map<String,AblFieldAttr> fieldAttrMapOfAfterInit) {
        List<Field> fields = ReflectUtil.getAllFields(clzz);
        fields.stream().parallel().forEach(e -> {
            // 只解析AblAutoAnno修饰的字段
            if(e.isAnnotationPresent(AblAutoAnno.class)) {
                //首先解析字段的基本程序信息
                e.setAccessible(true);
                AblFieldAttr fieldAttr = new AblFieldAttr();
                fieldAttr.setField(e);
                fieldAttr.setSimpleName(e.getName());
                fieldAttr.setFullName(StringsUtil.nonIdxFmt("{}.{}",clzz.getName(),e.getName()));
                fieldAttr.setClzz(e.getType());
                fieldAttr.setType(e.getGenericType());
                try {
                    PropertyDescriptor pd = ReflectUtil.getBeanPropertyDescriptor(clzz, e.getName());
                    if (LangUtil.isNotNull(pd.getReadMethod())) {
                        fieldAttr.setGetter(pd.getReadMethod());
                    }
                    if (LangUtil.isNotNull(pd.getWriteMethod())) {
                        fieldAttr.setSetter(pd.getWriteMethod());
                    }
                }catch (Throwable t) {
                    Assert.isRaise(t,"field:{} of service:{} parser getter/setter propertryDescriptor is raise.",
                            e.getName(),clzz.getName());
                }

                // 再解析字段的AblAutoAnno信息
                AblAutoAnno anno = e.getAnnotation(AblAutoAnno.class);
                AblAutoAttr autoAttr = new AblAutoAttr();
                Assert.isTrue(StringsUtil.isNullEmptyTrimmed(anno.value()) && StringsUtil.isNullEmptyTrimmed(anno.id()),
                    "field:{} of service:{} AblAutoAnno's id/value bose empty or trimmed.",
                        e.getName(),clzz.getName());

                String realId = StringsUtil.isNotNullEmptyTrimmed(anno.id()) ? anno.id() : anno.value();
                autoAttr.setId(realId);
                autoAttr.setSetWhenOpt(anno.when());
                autoAttr.setThrowIfNull(anno.throwIfNull());
                fieldAttr.setAutoAttr(autoAttr);
                switch (anno.when()) {
                    case BeforeInit: {
                        fieldAttrMapOfBeforeInit.put(e.getName(),fieldAttr);
                        break;
                    }
                    case AfterInit:
                    default:{
                        fieldAttrMapOfAfterInit.put(e.getName(),fieldAttr);
                        break;
                    }
                }
            }
        });
    }

    public void parserAllMethods(Class<?> clzz,
                                 RefArg<AblMethodAttr> initFn,
                                 RefArg<AblMethodAttr> dsyFn,
                                 Map<String, AblMethodAttr> factoryFns,
                                 Map<String,AblMethodAttr> fns) {



    }


}
