package org.albianj.anno;

import org.albianj.common.anno.AlbianCommentRant;
import org.albianj.common.utils.NullValue;

import java.lang.annotation.*;

@AlbianCommentRant("""
    指定统一管理anno配置的类
    anno配置可以放在main函数的类上
    不想在main-class上打anno，可以在main-class上打上@AlbianConfigurationRant，并且指定相应的配置anno的类
""")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianConfigurationRant {
    Class<?> value() default  NullValue.class;
}
