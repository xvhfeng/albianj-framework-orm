package org.albianj.impl.dal.expr.sql;

import org.albianj.common.utils.StringsUtil;
import org.albianj.impl.dal.expr.SqlExprOpt;

public class SqlFuncExpr implements ISqlFuncExpr{
    private SqlExprOpt exprOpt;
    private String sqlFieldName;

    public SqlFuncExpr(SqlExprOpt exprOpt,String sqlFieldName){
        this.exprOpt = exprOpt;
        this.sqlFieldName = sqlFieldName;
    }

//    public SqlFuncExpr(SqlExprOpt exprOpt,String sqlFieldName){
//        this.exprOpt = exprOpt;
//        this.sqlFieldName = sqlFieldName;
//    }

    @Override
    public String toSqlText() {
        return StringsUtil.nonIdxFmt("{}({})",
                exprOpt.getKeyName(),sqlFieldName);
    }
}
