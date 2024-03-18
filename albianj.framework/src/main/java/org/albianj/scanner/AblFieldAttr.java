package org.albianj.scanner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.api.kernel.anno.serv.SetWhenOpt;

import java.beans.PropertyDescriptor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AblFieldAttr {

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
     */
    private Class<?> clzz;

    /**
     * 如果field有getter方法的话，这个就是getter
     */
    private PropertyDescriptor getter;

    /**
     * 如果field有setter方法的话，这个就是setter
     */
    private PropertyDescriptor setter;

    /**
     * 字段如果被@AutoAnno标注的话，这个是AutoAnno标注的信息
     */
    private AblAutoAttr autoAttr;
}
