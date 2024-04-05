package org.albianj.scanner;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ArgAnnoAttr {
    /**
     * 参数的名字
     */
    private String name;
    /**
     * 参数的序号
     */
    private int idx;
    /**
     * 参数的class类型
     */
    private Class<?> clazz;

    /**
     * 是否是可变参数
     */
    private boolean varArgs;

    /**
     * 自动赋值的value
     * 支持OGNL表达式
     */
    private String value;

    /**
     * 参数的值为service且value为空时
     * 可以指定具体的实现类来决定获取正确的service
     */
    private Class<?> clazzOfValue;

}
