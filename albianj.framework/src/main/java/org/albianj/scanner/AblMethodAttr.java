package org.albianj.scanner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AblMethodAttr {
    /**
     * 方法的限定级别
     */
    private int modifier = Modifier.PUBLIC;

    /**
     * 方法所属的类
     */
    private Class<?> belongClass;

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
     * 方法的返回值类型
     */
    private Type rtnType;
    /**
     * 方法的参数
     */
    private Object[] args;
    /**
     * 方法可能会抛出的异常
     */
    private Class<? extends Throwable> raises;
}
