package org.albianj.scanner;

/**
 * 把已经被过滤出来的class进行anno的先后判断
 * 然后再根据最优先的Anno进行解析
 * 得到的结果保存在AblRantAttr的attr中
 * 该步骤需要填充AblRantAttr（belongRant，belongRantName，attr)
 */
public interface IAblRantParser {

    /**
     * 按照优先级选择最匹配的Anno
     * 然后根据这个Anno的注解解析这个class
     * 最后将结果保存到AblRantAttr对象后返回
     * @param attr
     * @return
     */
    AblBeanAttr parseClzz(AblBeanAttr attr);
}
