package org.albianj.dal.api.object.rants;


import org.albianj.dal.api.service.AblDrDef;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AblDrsRant {

    Class<?> DataRouter()  default AblDrDef.class;

    boolean ReaderRoutersEnable() default true;

    boolean WriterRoutersEnable() default true;

    AblDrRant[] ReaderRouters() default {};

    AblDrRant[] WriterRouters() default {};

}
