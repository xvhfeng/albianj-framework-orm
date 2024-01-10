package org.albianj.common.scanner;

/*
 *  Albian中Class的解析器， 紧跟在IAlbianClassFilter之后执行
 *  它主要对于经过IAlbianClassFilter过滤的class进行解析
 *  通过反射拿到对于这个类的各种元数据（比如类型，名字等等），还有标注的anno，组合成当前class的attr进行返回
 */
public interface IAblClassParser {
    public Object parser(Class<?> clzz) ;
}
