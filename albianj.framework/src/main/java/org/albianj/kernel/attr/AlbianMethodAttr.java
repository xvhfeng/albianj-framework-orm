package org.albianj.kernel.attr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbianMethodAttr {
    private Class<?> returnType;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Method self;
    private List<AlbianMethodArgAttr> argumentValues;
}
