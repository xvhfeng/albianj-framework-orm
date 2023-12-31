package org.albianj.kernel.kit.logger;

import org.albianj.common.anno.AlbianCommentRant;

public enum LogTarget {

    @AlbianCommentRant("非常重要的log信息，一般用在必须快速被查看的情况")
    Flag("FlagLogger", 0),

    @AlbianCommentRant("一般的程序运行时的日志")
    Running("RunningLogger", 1),

//    @Comments("数据库sql执行日志")
//    Sql("SqlLogger", 2),

    @AlbianCommentRant("自定义日志")
    Custom("CustomLogger", 2);

    private String name = "D";
    private int type = 0;

    LogTarget(String name, int type) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getType() {
        return this.type;
    }


}
