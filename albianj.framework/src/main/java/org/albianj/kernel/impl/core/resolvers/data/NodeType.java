package org.albianj.kernel.impl.core.resolvers.data;

/**
 * DFA节点的类型
 */
public enum NodeType {
    /**
     * 单纯只是数据节点
     * 即当前节点是一个classes或者interfaces的情况
     */
    Data,
    /**
     * 单纯只是叶子节点
     * 即当前节点只是package中的一个节点，底下不存在任何的类
     */
    Leaf,
    /**
     * 复合节点
     * 即当前节点即作为package中的一个节点且当前节点下还存在classes或者interfaces
     */
    Complex,
}
