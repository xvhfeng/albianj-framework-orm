package org.albianj.common.utils;

/**
 * 默认的没有值常量
 * 一般用在null无法表示的场合
 */
public class NullValue {
    public final static String ClassName = NullValue.class.getName();

    public final static Class<?> Clazz = NullValue.class;

    public static NullValue Self ;
    static {
        Self = new NullValue();
    }

}
