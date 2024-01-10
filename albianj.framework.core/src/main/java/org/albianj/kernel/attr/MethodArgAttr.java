package org.albianj.kernel.attr;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.kernel.attr.opt.AblVarTypeOpt;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MethodArgAttr {
    private String argName;
    private AblVarTypeOpt typeOpt;
    private Class<?> realType;
    private String value;
    private Object realValue;

    public MethodArgAttr(String argName, AblVarTypeOpt typeOpt, String value) {
        this.argName = argName;
        this.typeOpt = typeOpt;
        this.value = value;
    }
}
