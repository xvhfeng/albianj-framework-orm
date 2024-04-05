package org.albianj.scanner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.kernel.api.anno.serv.AblFnOpt;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuncAttr {
    /**
     * 方法的限定级别
     */
    private int modifier = 0;

    /**
     * 方法所属的类
     */
    private Class<?> blgClass;

    /**
     * 方法的简单名字
     */
    private String simpleName;
    /**
     * 方法的全限定名，形如：
     * class-full-name.method-name
     */
    private String fullName;

    /**
     * 函数的签名
     * 不会包括函数名称，只有返回值与参数类型
     */
    private String sign;
    /**
     * 方法的本体
     */
    private Method fn;
    /**
     * 方法的角色
     */
    private AblFnOpt fnOpt = AblFnOpt.Normal;
    /**
     * 方法的返回值类型
     */
    private Type rtnType;

    /**
     * 当方法的Opt为Factory的时候，返回对象的资源ID
     */
    private String resIdForFactory;
    /**
     * 方法的参数
     */
    private Parameter[] args;

    /**
     * 解析args的属性结构
     */
    private List<ArgAnnoAttr> argAttrs;
    /**
     * 方法可能会抛出的异常
     */
    private Class<?>[] raises;
}
