package org.albianj.kernel.itf.builtin.logger;

import org.albianj.common.anno.AblCommentAnno;

public enum LogTarget {

    @AblCommentAnno("非常重要的log信息，一般用在必须快速被查看的情况")
    Flag("FlagLogger", 0),

    @AblCommentAnno("一般的程序运行时的日志")
    Running("RunningLogger", 1),

//    @Comments("数据库sql执行日志")
//    Sql("SqlLogger", 2),

    @AblCommentAnno("自定义日志")
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
