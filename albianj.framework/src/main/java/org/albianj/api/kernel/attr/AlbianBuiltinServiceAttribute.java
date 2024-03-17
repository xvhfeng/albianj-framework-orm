package org.albianj.api.kernel.attr;

import lombok.Data;

@Data
public class AlbianBuiltinServiceAttribute {
    private String id;
    private String implClzz;
    private boolean required;
    private boolean loadOK;

    public AlbianBuiltinServiceAttribute(String id, String implClzz, boolean required) {
        this.id = id;
        this.implClzz = implClzz;
        this.required = required;
    }

}
