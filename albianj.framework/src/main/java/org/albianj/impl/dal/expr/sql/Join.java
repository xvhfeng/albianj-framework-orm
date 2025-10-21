package org.albianj.impl.dal.expr.sql;

import lombok.*;
import org.albianj.impl.dal.expr.ISqlExpr;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Join implements ISqlExpr {
    private JoinOpt joinOpt;
    private Class<?> tableClass;
    private String table;
    private String as;
    private OnExpr on;

    @Override
    public String toSqlText() {
        return null;
    }
}
