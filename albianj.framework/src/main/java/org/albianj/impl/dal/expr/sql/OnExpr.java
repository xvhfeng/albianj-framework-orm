package org.albianj.impl.dal.expr.sql;

import lombok.*;
import org.albianj.api.dal.object.OOpt;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OnExpr {
    private String leftTable;
    private String leftAs;
    private String leftField;
    private OOpt opt;
    private String rightTable;
    private String rightAs;
    private String rightField;

    private Object value;

    private OnExpr and;


}
