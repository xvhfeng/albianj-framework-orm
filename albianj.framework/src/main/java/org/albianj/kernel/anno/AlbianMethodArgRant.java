package org.albianj.kernel.anno;

import org.albianj.kernel.attr.opt.AlbianBuiltinTypeOpt;

public @interface AlbianMethodParaRant {
    AlbianBuiltinTypeOpt Type() default AlbianBuiltinTypeOpt.String;
    String Name();
    String Value() default "";

}
