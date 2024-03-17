package org.albianj.api.dal.object.rants;


import org.albianj.api.dal.service.AblDrDef;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianObjectDataRoutersRant {

    Class<?> DataRouter()  default AblDrDef.class;

    boolean ReaderRoutersEnable() default true;

    boolean WriterRoutersEnable() default true;

    AlbianObjectDataRouterRant[] ReaderRouters() default {};

    AlbianObjectDataRouterRant[] WriterRouters() default {};

}
