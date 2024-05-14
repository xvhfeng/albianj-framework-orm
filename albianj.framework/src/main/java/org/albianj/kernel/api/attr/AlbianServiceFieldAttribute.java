package org.albianj.kernel.api.attr;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.kernel.api.anno.serv.AlbianServiceFieldSetterLifetime;

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
    private AlbianServiceFieldSetterLifetime setterLifetime = AlbianServiceFieldSetterLifetime.AfterLoading;
}