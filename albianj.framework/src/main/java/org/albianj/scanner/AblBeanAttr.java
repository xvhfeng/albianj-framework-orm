package org.albianj.scanner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AblBeanAttr {

    public AblBeanAttr(Class<?> clzz) {
        this.clzz = clzz;
        this.clzzFullName = clzz.getName();
    }

    /**
     * class的class对象
     */
    private Class<?> clzz;

    /**
     * class的类名的全名
     */
    private String clzzFullName;

    /**
     * service的id
     */
    private String id;

//    /**
//     * 最后优先级后归属的Rant
//     * 一般来说，一个class只有一个anno
//     * 在有多个anno，并且其中有anno同质的情况下会根据优先级进行anno的选择
//     * 该项即为最终被选中的Anno的name
//     */
//    private String belongAnnoFullName;

    /**
     * 最后优先级后归属的Rant
     * 一般来说，一个class只有一个anno
     * 但也可以支持多个anno
     * 在有多个anno，并且其中有anno同质的情况下会根据优先级进行anno的选择
     * 该项即为最终被选中的Anno
     */
    private Annotation belongAnno;

    /**
     * class被标注的rants
     */
    private Annotation[] annos;

    /**
     * 以下为被解析出来的attr
     * 每个rant会有一个对应的parser来解析相应的class
     */

    /**
     * init函数
     */
    private Method initFn;

    /**
     * 卸载函数
     */
    private Method dsyFn;

    /**
     * 所有在init函数被调用前的field
     */
    private Map<String,AblFieldAttr> fieldsOfBeforeInit;

    /**
     * 所有在init函数被调用前的field
     */
    private Map<String,AblFieldAttr> fieldsOfAfterInit;

    /**
     * 所有factory方法
     */
    private Map<String,AblMethodAttr> factoryFns;

    /**
     * 所有普通方法
     */
    private Map<String,AblMethodAttr> fns;

    /**
     * 这个service是aop的话，该项为aop的attribute
     */
    private AblAopAttr aopAttr;

}

