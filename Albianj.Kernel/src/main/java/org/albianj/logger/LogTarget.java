package org.albianj.logger;

import org.albianj.comment.Comments;

public enum LogTarget {

    @Comments("非常重要的log信息，一般用在必须快速被查看的情况")
    Flag("FlagLogger", 0),

    @Comments("一般的程序运行时的日志")
    Running("RunningLogger", 1),

    @Comments("数据库sql执行日志")
    Sql("SqlLogger", 2),

    @Comments("自定义日志")
    Custom("CustomLogger", 3);

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
