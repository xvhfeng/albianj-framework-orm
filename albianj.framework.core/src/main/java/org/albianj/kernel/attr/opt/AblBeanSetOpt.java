package org.albianj.kernel.attr.opt;

public enum AblBeanSetOpt {
    /**
     * 设置的时候,调用Creator method得到的对象来赋值
     * 如果bean本身是通过factory来初始化，factory中进行了单例操作，这时候该选项将自动变为Singeton
     */
    CallCreator,
    /**
     * 全站只有一个实例
     */
    GetSingleton

}
