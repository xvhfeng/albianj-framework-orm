package org.albianj.anno;

import org.albianj.common.anno.AblCommentAnno;

import java.lang.annotation.*;

@AblCommentAnno("datarouter类存放的packages")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblDataRouterScannerAnno {
    @AblCommentAnno("data router所在的package")
    String[] Packages() default {};
    @AblCommentAnno("data router支持配置在配置在配置文件中，该项指定文件的路径")
    String FileName() default "datarouter.xml";
}