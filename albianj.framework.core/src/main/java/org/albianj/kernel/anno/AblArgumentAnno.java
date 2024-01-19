package org.albianj.kernel.anno;

import org.albianj.kernel.attr.opt.AblVarModeOpt;
import org.albianj.kernel.attr.opt.AblVarTypeOpt;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD , ElementType.FIELD})
@Inherited
@Documented
public @interface AblArgumentAnno {
    int Index() default 0;

    String Name() default "";

    AblVarTypeOpt Type() default AblVarTypeOpt.String;

    AblVarModeOpt Mode() default AblVarModeOpt.Direct;

    /**
     * 变量的值
     * 直接的量直接标注就可以
     * 如果是service，对象，bean，
     * 那么当为class@name时，name为函数名称
     * 当为class#name是，name为field名称
     * @return
     */
    String Value() default "";

    AblKeyValuePair[] List() default {};

    AblKeyValuePair[] Map() default {};

}
