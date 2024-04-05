package org.albianj.scanner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * albianj kernel中管理的class（service，aop）的元数据信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassAttr implements IResolverAttr{

    public ClassAttr(Class<?> clzz) {
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

    /**
     * 最后优先级后归属的Rant
     * 一般来说，一个class只有一个anno
     * 但也可以支持多个anno
     * 在有多个anno，并且其中有anno同质的情况下会根据优先级进行anno的选择
     * 该项即为最终被选中的Anno
     */
    private Annotation blgAnno;

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
     * 必须把类极其这个类的所有的父类中的init函数全部解析
     */
    private FuncAttr ctor;

    /**
     * 卸载函数
     * 必须把类极其这个类的所有的父类中的dsy函数全部解析
     */
    private FuncAttr dtor;

    /**
     * 所有在init函数被调用前的field
     */
    private Map<String, FieldAttr> fieldsTblOfBfr;

    /**
     * 所有在init函数被调用前的field
     */
    private Map<String, FieldAttr> fieldsTblOfAft;

    /**
     * 所有factory方法
     */
    private Map<String, FuncAttr> factoryFnsTbl;

    /**
     * 所有普通方法
     */
    private Map<String, FuncAttr> fnsTbl;

    /**
     * 这个service是aop的话，该项为aop的attribute
     */
    private AopAnnoAttr aopAttr;

    /**
     * 父类的attr
     */
    private ClassAttr superClassAttr;

    /**
     * 接口的信息
     * 因为接口中会存在default函数,所以必须要解析接口
     */
    private Map<String, ItfAttr> itfsAttr;
}

