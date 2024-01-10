package org.albianj.kernel.anno;

import org.albianj.kernel.attr.opt.AblVarTypeOpt;

public @interface AblKeyValuePair {
    String Name() default "";
    AblVarTypeOpt Type() default AblVarTypeOpt.String;
    String Value() default "";
}
