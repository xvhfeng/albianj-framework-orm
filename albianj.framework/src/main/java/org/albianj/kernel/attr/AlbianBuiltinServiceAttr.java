package org.albianj.kernel.attr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AlbianBuiltinServiceAttr {
    private String id;
    private String implClzz;
    private boolean required;
    private boolean loadOK;

    public AlbianBuiltinServiceAttr(String id, String implClzz, boolean required) {
        this.id = id;
        this.implClzz = implClzz;
        this.required = required;
    }
}
