package org.albianj.kernel.attr;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.kernel.attr.opt.AlbianVarTypeOpt;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbianMethodArgAttr {
    private String argName;
    private AlbianVarTypeOpt typeOpt;
    private Class<?> realType;
    private String value;
    private Object realValue;

    public AlbianMethodArgAttr(String argName, AlbianVarTypeOpt typeOpt, String value) {
        this.argName = argName;
        this.typeOpt = typeOpt;
        this.value = value;
    }
}
