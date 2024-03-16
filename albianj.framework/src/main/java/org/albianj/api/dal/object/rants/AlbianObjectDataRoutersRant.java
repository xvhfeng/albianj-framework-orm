package org.albianj.api.dal.object.rants;


import org.albianj.api.dal.service.AlbianObjectDataRouterDefaulter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianObjectDataRoutersRant {

    Class<?> DataRouter()  default AlbianObjectDataRouterDefaulter.class;

    boolean ReaderRoutersEnable() default true;

    boolean WriterRoutersEnable() default true;

    AlbianObjectDataRouterRant[] ReaderRouters() default {};

    AlbianObjectDataRouterRant[] WriterRouters() default {};

}
