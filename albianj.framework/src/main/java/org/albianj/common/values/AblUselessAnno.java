package org.albianj.common.values;

import org.albianj.common.comment.Comments;

import java.lang.annotation.*;

@Comments("Albianj内核使用的创建对象的工厂方法，这个anno标注的方法会创建一个对象，并且返回一个对象，而这个对象一般就是service或者单纯的bean对象")
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AblUselessAnno {
    String value() default "";
}
