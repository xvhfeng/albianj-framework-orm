package org.albianj.kernel.service;

import lombok.Data;
import lombok.NoArgsConstructor;

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
