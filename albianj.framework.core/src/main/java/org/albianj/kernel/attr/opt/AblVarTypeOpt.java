package org.albianj.kernel.attr.opt;

/**
 * Created by xuhaifeng on 2018/9/7.
 */
public enum AblVarTypeOpt {
    Char,
    Integer,
    Long,
    Float,
    Double,
    Boolean,

    String,
    BigInterger,
    BigDecimal,
    List,
    Map,

    Service,

    /**
     * 程序中的数据类型
     * 一般为过程中的数据量
     */
    Data,
    Bean;



//    private int style;
//    private T value;
//
//    AlbianServiceFieldTypeOpt(int style, T value) {
//        this.style = style;
//        this.value = value;
//    }

    //    /*
//     赋值的时候，使用标注的service的属性
//     */
//    Property,
//    /*
//    赋值的时候，使用标注的service 的字段
//     */
//    Field,
}
