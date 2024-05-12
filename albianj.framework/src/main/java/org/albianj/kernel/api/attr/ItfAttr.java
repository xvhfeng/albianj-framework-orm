package org.albianj.kernel.api.attr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItfAttr implements IResolverAttr {
    /**
     * class的class对象
     */
    private Class<?> clzz;

    /**
     * class的类名的全名
     */
    private String clzzFullName;

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
     * 所有factory方法
     */
    private Map<String, FuncAttr> factoryFnsTbl;

    /**
     * 所有普通方法
     */
    private Map<String, FuncAttr> fnsTbl;

    /**
     * 接口的信息
     * 因为接口中会存在default函数,所以必须要解析接口
     */
    private Map<String, ItfAttr> itfsAttr;
}
