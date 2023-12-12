package org.albianj.xml;

/**
 * xml解析器的hashmap签名接口
 * 所有可重复的配置节点的实体bean必须实现该接口
 * Created by xuhaifeng on 17/2/5.
 */
public interface IAlbianXmlPairNode<T> extends IAlbianXml2ObjectSigning {
    void addNode(String key, T value);
}
