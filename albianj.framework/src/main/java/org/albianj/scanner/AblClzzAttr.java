package org.albianj.scanner;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.List;

@Data
@NoArgsConstructor
public class AblClzzAttr {
    /**
     * class的类名的全名
     */
    private String clzzFullName;

    /**
     * 最后优先级后归属的Rant
     * 一般来说，一个class只有一个anno
     * 在有多个anno，并且其中有anno同质的情况下会根据优先级进行anno的选择
     * 该项即为最终被选中的Anno的name
     */
    private String belongRantFullName;

    /**
     * 最后优先级后归属的Rant
     * 一般来说，一个class只有一个anno
     * 在有多个anno，并且其中有anno同质的情况下会根据优先级进行anno的选择
     * 该项即为最终被选中的Anno
     */
    private Annotation belongRant;

    /**
     * class被标注的rants
     */
    private List<? extends Annotation> rants;

    /**
     * class的class对象
     */
    private Class<?> clzz;

    /**
     * 被解析出来的attr
     * 每个rant会有一个对应的parser来解析相应的class
     */
    private Object attr;

}
