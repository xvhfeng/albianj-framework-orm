package org.albianj.anno;

import org.albianj.common.anno.AblCommentAnno;

import java.lang.annotation.*;

@AblCommentAnno("数据库实体类存放的packages")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblMappingScannerAnno {
    @AblCommentAnno("entity类所在的package名字")
    String[] Packages() default {};
    @AblCommentAnno("entity-table映射关系支持配置在配置在配置文件中，该项指定文件的路径")
    String FileName() default "mapping.xml";
}
