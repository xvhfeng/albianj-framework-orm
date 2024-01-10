package org.albianj.anno;

import org.albianj.common.anno.AblCommentAnno;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblServiceScannerAnno {
    @AblCommentAnno("service所在的package名字")
    String[] Packages() default {};
    @AblCommentAnno("service支持配置在配置在配置文件中，该项指定文件的路径")
    String FileName() default  "";

}
