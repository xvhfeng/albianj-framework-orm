package org.albianj.scanner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.api.kernel.anno.serv.SetWhenOpt;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AblFieldAttr {

    private Field field;

    /**
     * 仅仅只是field名字
     * 不包括pkg-path和class-name
     */
    private String simpleName;

    /**
     * 全名，
     * pkg-path.class-name.field-name
     */
    private String fullName;

    /**
     * 字段的类型class
     * 如果是泛型，尽可能拿到泛型的准确描述
     */
    private Type type;

    /**
     * 字段的类型class
     * 与type不同，该项仅仅可以得到最外层类型
     */
    private Class<?> clzz;


    /**
     * 如果field有getter方法的话，这个就是getter
     */
    private Method getter;

    /**
     * 如果field有setter方法的话，这个就是setter
     */
    private Method setter;

    /**
     * 字段如果被@AutoAnno标注的话，这个是AutoAnno标注的信息
     */
    private AblAutoAttr autoAttr;
}
