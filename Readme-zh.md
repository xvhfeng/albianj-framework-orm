# Albianj-framework-orm 介绍

    
    一款“非java-style”的ORM框架，它来自于我原先开发的[Albianj-framework](https://github.com/crosg/Albianj2) 中的ORM部分。

    [Albianj-framework](https://github.com/crosg/Albianj2)在我前公司中被使用，且已经使用了5年以上，承受了足够的线上压力，积累了足够的线上经验。
    在前公司中，[Albianj-framework](https://github.com/crosg/Albianj2)完成某业务的前后端代码框架任务，很好的支撑和完成了业务的开发。

    但是在这几年的使用过程中，发现了很多的问题和当初设计武断的地方。更有很多不足的地方因为设计的既定很难再去更改或者扩展。所以需要从头开始，重构+重写它。

    现在我离开了前公司，因为[Albianj-framework](https://github.com/crosg/Albianj2)以前在公司的时候就已经开源，它也是在公司还没成立的更早的2011年写成并且以前在googlecode开源，所以重新fork并无版权问题。

# 目前的功能
    
    目前Albianj-framework-orm已经支持的功能主要有：
        - 完整的ORM功能
        - 基于Entry和Database与Table的快速分库分表功能
        - 读写分离的支持
        - 一次批量保存多个Entry
        - 并不可靠的“伪” 分布式事务

# 最终的功能列表
    Albianj-framework-orm最终要达到的功能有：
        - 完整的ORM功能
        - 快速的分库分表的能力
        - 异常的精确定位和问题解决
        - 批量保存\更新多个实体到多个库\表功能
        - 一次保存一个实体到多个库\表的功能
        - 读写分离的支持
        - 开放执行自定义sql的接口
        - 返回数据值不一定为Entry，也可以为Map
        - SessionId的支持
        - join和in等操作的支持
        - sql语句的审计和限制功能
        - 可以和spring集成，也可以独立使用

# 目标

    Albianj-framework-orm并无什么“大志向”，它的存在只是一个个人的消遣乐子而已。

    不会有固定的排期和更新，忙的时候就少更新一些，不忙的时候就多更新一些。重要的是，希望能“快乐的编码”。

    重中之重：项目不会无限期的膨胀下去，所以不会形成“功能上的大杂烩”，它只是一个“小而美”的典型。这是我个人一直以来写程序的“准则”，严格执行“一个程序只对一部分需求有效”。所以Albianj-framework-orm只会对一部分适合的人用起来非常爽，而你可能正好是那个例外。

    我不是java程序员，写Albianj-framework的时候不是，现在也不是，以后...已经停止了进化。对我的经典评价就是：java基本不会！所以很多的java-style“可能”不会遵守（准确的说，不知道，确实是无知的表现）。我只想把Albianj-framework-orm做成心目中ORM该有的样子。

    最后：不更新并不代表不维护，希望能一直维护下去。。。

# 准则
    
    这是Albianj-framework-orm遵守的准则。
    - 尽可能少的引用jar依赖
    - 尽可能少的配置文件
    - 尽可能的减少使用的难度，特别是环境的配置等等这些
    - 尽可能的让需求切合使用者用的爽

# 计划
    
     - v1版本（功能不分先后）：
        - 去掉这几年来albianj-framework-orm未曾使用一次的功能
        - 增加多sql语句的batch功能
        - 增加join的支持和in的支持
        - 增加select时候的指定列
        - delete、update增加where条件（非主键依赖）的能力
        - 去掉启动时一定需要配置文件的存在
        - 将配置文件中的package配置用annotation取代
        - 去掉albianj-framework的依赖，使用spring boot或者直接裸用
        - 去掉必须实现自IAlbianObject的限制
        - 补齐重要的tests
        - 增加对敏感信息加密功能，并能自定义key
        - 补齐完整的示例程序
        - 补齐完整的文档

# 使用方法
        
    目前不要fork和使用，功能仍处于开发阶段。

# 贡献者

    期待你的大驾光临    

# 真实原因

    冠冕堂皇的话都讲完了，来几句牢骚吧！
    
    记在2023年12月12日。
    
    为什么要重新捡起来Albianj-framework-orm？

    目前的ORM如图：
    
    ![orm-tools-in-java2.png](/image/Form-tools-in-java2.png "orm-tools-in-java2") 图片来源于网络

    这几天自己写点小东西玩，试用了一下java的ORM框架，能选择的并不多。一般也就myb和hbm二选一，JPA使用的还是有一些少吧！国内的互联网大部分应该是myb的天下，所以就用了用。
    总结下来，就是“MT模式”对于快速开发来说，确实没有爽点。就算是加上一些比较火的插件，仍然get不到我的点。相反，他们都是功能的“集大成者”，复杂税一大堆，完全没有那种恰如其分的美。
    因为不是正统java程序员的原因吧，看ORM的视角也有点奇怪。
    这从另一面也说明一点：程序用的爽不爽和它是不是真的好用没有特别大的关系。主要还是取决于使用者的习惯程度。习惯了，就算是再复杂的也觉得理所当然，反之。。。

    




