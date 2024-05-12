package org.albianj.kernel.impl.core.resolvers.data;

import org.albianj.common.spring.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放解析classes元数据信息的DFA数据结构
 * 存放的结构如下：
 *  包名：DFATreeNode
 *           - | 下级子包名：DFATreeNode
 *                              - | 下级子包名：DFATreeNode
 *
 *
 */
public class DFATree {
    Map<String,DFATreeNode> map = CollectionUtils.newHashMap();

}
