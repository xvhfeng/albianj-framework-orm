package org.albianj.api.kernel.attr;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.api.kernel.anno.serv.AblServFieldSetterOpt;

import java.lang.reflect.Field;

@Data
@NoArgsConstructor
public class AlbianServiceFieldAttribute   {
    private String type;
    private String value;
    private String name;
    private Field field;
    private boolean allowNull = false;
    private boolean ready = false;
    private AblServFieldSetterOpt setterLifetime = AblServFieldSetterOpt.AfterLoading;
}
