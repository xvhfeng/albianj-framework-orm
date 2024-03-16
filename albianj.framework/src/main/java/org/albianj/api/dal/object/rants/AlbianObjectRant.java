package org.albianj.api.dal.object.rants;


import org.albianj.api.dal.object.IAlbianObject;
import org.albianj.api.dal.service.AlbianObjectDataRouterDefaulter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianObjectRant {

//    Class<? extends IAlbianObject> Interface();

    AlbianObjectDataRoutersRant DataRouters() default @AlbianObjectDataRoutersRant(DataRouter = AlbianObjectDataRouterDefaulter.class);
}
