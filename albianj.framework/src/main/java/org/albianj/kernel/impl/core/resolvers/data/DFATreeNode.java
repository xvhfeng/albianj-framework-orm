package org.albianj.kernel.impl.core.resolvers.data;

import lombok.Builder;
import lombok.Data;
import org.albianj.kernel.api.attr.IResolverAttr;

import java.util.Map;

@Data
@Builder
public class DFATreeNode {
    /**
     * 当前节点的类型
     */
    private NodeType nodeType;
    /**
     * 当前节点下的classes或者interface的元数据
     * key：simplename
     * value：解析的元数据信息
     */
    private Map<String,IResolverAttr> classes;

    /**
     * 字包路径
     */
    private Map<String,DFATreeNode> child;
}
