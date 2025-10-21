package org.albianj.impl.dal.expr.sql;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class From {
    private String table;
    private Class<?> tableClass;
    private String as;
    private Select queryExpr;
}
