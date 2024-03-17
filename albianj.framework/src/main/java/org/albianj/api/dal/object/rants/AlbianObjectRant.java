package org.albianj.api.dal.object.rants;


import org.albianj.api.dal.object.IAlbianObject;
import org.albianj.api.dal.service.AlbianObjectDataRouterDefaulter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianObjectRant {
    boolean SqlFieldUseUnderline() default false;
    boolean TableNameUseUnderline() default false;
}
