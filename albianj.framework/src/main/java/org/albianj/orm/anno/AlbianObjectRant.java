package org.albianj.orm.anno;


import org.albianj.orm.itf.object.IAlbianObject;
import org.albianj.orm.itf.service.AlbianObjectDataRouterDefaulter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianObjectRant {

    String StorageName() default "";

    String TableName() default "";

    String TableOwner() default "";

    /**
     * 是否需要启用驼峰风格转下划线风格
     * 这是整个实体都启作用
     * @return
     */
    boolean CamelUnderlineSwitch() default false;

    /**
     * 首字母大写
     * @return
     */
    boolean InitcapSwitch() default false;

    /*
     全部小写
     */
    boolean LowercaseSwitch() default  false;

    /*
    全部大写
     */
    boolean UppercaseSwitch() default false;


    // 以下是要删除的，为了编译先留着
    Class<? extends IAlbianObject> Interface();

    AlbianObjectDataRoutersRant DataRouters() default @AlbianObjectDataRoutersRant(EntitiyClass = AlbianObjectDataRouterDefaulter.class);
}
