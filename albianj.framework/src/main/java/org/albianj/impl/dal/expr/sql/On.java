package org.albianj.impl.dal.expr.sql;

import lombok.*;
import org.albianj.impl.dal.expr.ISqlExpr;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class On implements ISqlExpr {

    private OnExpr condExpr;
    private OnExpr and;
    private OnExpr or;

    @Override
    public String toSqlText() {
        return null;
    }
}
