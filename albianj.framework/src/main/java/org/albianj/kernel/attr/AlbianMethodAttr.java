package org.albianj.kernel.attr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbianMethodAttr {
    private Class<?> returnType;
    private String methodName;
    private Class<?>[] parameterTypes;
    private List<AlbianMethodArgAttr> argumentValues;
}
