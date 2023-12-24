package org.albianj.kernel.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Field;

/**
 * Created by xuhaifeng on 16/5/12.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbianServiceFieldAttribute { //implements IAlbianServiceFieldAttribute {
    String type;
    String value;
    String name;
    Field field;
    boolean allowNull = false;
    boolean ready = false;
    AlbianServiceFieldSetterLifetime setterLifetime = AlbianServiceFieldSetterLifetime.AfterLoading;
}
