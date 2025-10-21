package org.albianj.impl.dal.expr;

import lombok.Data;

/**
 * sql命令关键字
 */
public enum SqlExprOpt {
    Select(1,"SELECT"),
    From(2,"FROM"),
    Where(3,"WHERE"),


    ON(8,"ON"),

    As(2,"AS"),
    Distinct(1,"DISTINCT"),
    Limit(9,"Limit"),
    OrderBy(4,"ORDER BY"),
    GroupBy(5,"GROUP BY"),
    Having(6,"HAVING"),
    Like(1,"LIKE"),
    In(1,"IN"),


    Insert(10 ,"INSERT"),
    Into(0,"INTO"),
    Set(11,"SET"),
    Values(12,"VALUES"),
    Delete(13,"DELETE"),
    Update(1,"UPDATE"),
    DuplicateKey(1,"ON DUPLICATE KEY UPDATE"),
    Conflict(1,"ON CONFLICT"),
    DoUpdateSet(1,"DO UPDATE SET"),


    Between(1,"BETWEEN"),
    And(1,"AND"),
    Or(1,"OR"),
    Exists(1,"EXISTS"),
    NotExists(1,"NOT EXISTS"),

    Null(1,"NULL"),
    Is(1,"IS"),
    Not(1,"NOT"),

    Length(1,"LENGTH"),
    Len(1,"LEN"),
    Round(1,"ROUND"),
    Format(1,"FORMAT"),
    Avg(1,"AVG"),
    Count(1,"AVG"),
    Sum(1,"AVG"),
    Max(1,"AVG"),
    Min(1,"AVG"),
    First(1,"AVG"),
    Last(1,"AVG"),
    Upper(1,"UPPER"),
    Lower(1,"LOWER"),
    Now(1,"NOW()"),

    Call(1,"CALL"),
    UseStg(1,""),
    Choose(1,"");

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

    SqlExprOpt(int no,String keyName) {
        this.no = no;
        this.keyName = keyName;
    }
}
