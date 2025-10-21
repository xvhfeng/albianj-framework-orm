package org.albianj.impl.dal.expr.sql;

public enum JoinOpt {

    Inner(1,"INNER"),
    Left(2,"LEFT"),
    Right(3,"RIGHT"),
    Outer(4,"OUTER");

    /**
     * 关键字号
     */
    private int no;

    /**
     * 关键字命令
     */
    private String keyName;

    public int getNo() {
        return no;
    }

    public String getKeyName() {
        return keyName;
    }

    JoinOpt(int no,String keyName) {
        this.no = no;
        this.keyName = keyName;
    }
}
