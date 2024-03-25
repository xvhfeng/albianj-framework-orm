package org.albianj.api.kernel.anno.proxy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * aop的上下文
 * 标注before，after，throw这3个anno的方法默认的参数就是这类型
 * 在方法被代理执行过程中，所有的信息会包含在这个上下文中进行传递
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AblAopCtx {

    /**
     * 被代理的真实的service
     */
    private Object serv;
    /**
     * 当前正在执行的方法信息
     */
    private Method method;
    /**
     * 传递给这个方法执行的参数信息
     */
    private Object[] args;
    /**
     * 开发者需要在代理被执行过程中自行管理的某些信息对象
     * 该部分完全开发者自己控制
     */
    private Object selfData;
    /**
     * 方法执行的最后结果
     */
    private Object result;
    /**
     * 方法的返回值类型
     */
    private Class<?> resultType;
    /**
     * 如果方法执行过程中发生异常，则为异常信息
     */
    private Throwable throwable;

}
