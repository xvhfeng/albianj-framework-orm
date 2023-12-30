package org.albianj.orm.anno;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianObjectDataRoutersRant {

    /*
        由原来的在entitiy上直接配置dr信息，改为在dr上配置entitiy信息

        这样的好处由以下几个
        1. 可以方便的标识该类为datarouter，并且可以不用使用接口标识
        2. scanner程序可以很容易的就scan到datarouter
        3. 可以完全的分开实体和数据路由，
        4. 完全可以由两拨人同时去开发相应的功能，互不打扰，
        5. 相对来说，dr配置entitiy的干扰因素比entitiy配置dr要小的多
        6. 对于程序的结构，框架的结构，框架的parser程序，都可以比较好的做到归一性
     */
    Class<?> EntitiyClass();

    boolean ReaderRoutersEnable() default true;

    boolean WriterRoutersEnable() default true;

    AlbianObjectDataRouterRant[] ReaderRouters() default {};

    AlbianObjectDataRouterRant[] WriterRouters() default {};

}
