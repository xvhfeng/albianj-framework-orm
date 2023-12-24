package org.albianj.kernel.attr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.kernel.attr.opt.AlbianServiceFieldSetterLifecycleOpt;

import java.lang.reflect.Field;

/**
 * Created by xuhaifeng on 16/5/12.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbianServiceFieldAttr {
    String type;
    String value;
    String name;
    Field field;
    boolean allowNull = false;
    boolean ready = false;
    AlbianServiceFieldSetterLifecycleOpt setterLifetime = AlbianServiceFieldSetterLifecycleOpt.AfterLoading;
}
