package org.albianj.kernel.anno;

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

    String Value() default "";

    AblKeyValuePair[] List() default {};

    AblKeyValuePair[] Map() default {};

}
