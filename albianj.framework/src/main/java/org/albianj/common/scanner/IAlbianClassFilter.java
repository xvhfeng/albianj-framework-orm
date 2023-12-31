package org.albianj.common.scanner;

/*
 *   从pkg中寻找所有符合条件的class时候的过滤器
 *  一般这个过滤器会进行class的anno的校验，找到当前阶段具有某个特定anno修饰的class
 *   比如对于AlbianService，则需要把具有AlbianServRant修饰的class全部找出来
 *  当然，你也可以加入你自己的判断规则
 */
public interface IAlbianClassFilter {
    public boolean lookup(Class<?> cls);
}
