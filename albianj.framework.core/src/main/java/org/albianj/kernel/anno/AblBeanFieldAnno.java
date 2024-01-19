package org.albianj.kernel.anno;

import org.albianj.common.utils.NullValue;
import org.albianj.kernel.attr.opt.AblBeanSetOpt;
import org.albianj.kernel.attr.opt.AblFieldSetWhenOpt;

import java.lang.annotation.*;

/**
 * bean/service中的字段为bean类型时，使用该特性标注
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface AblBeanFieldAnno {

    Class<?> itfClzz() default NullValue.class;

    /**
     * 引用的bean的id
     */
    String BeanId() default "";

    AblBeanSetOpt SetMode() default AblBeanSetOpt.CallCreator;

    boolean AllowNull() default false;

    AblFieldSetWhenOpt SetStage() default AblFieldSetWhenOpt.AfterNew;

    AblArgumentAnno[] Args() default {};
}
