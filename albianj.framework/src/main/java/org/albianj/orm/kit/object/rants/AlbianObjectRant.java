package org.albianj.orm.kit.object.rants;


import org.albianj.orm.kit.object.IAlbianObject;
import org.albianj.orm.kit.service.AlbianObjectDataRouterDefaulter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianObjectRant {

    Class<? extends IAlbianObject> Interface();

    AlbianObjectDataRoutersRant DataRouters() default @AlbianObjectDataRoutersRant(DataRouter = AlbianObjectDataRouterDefaulter.class);
}
