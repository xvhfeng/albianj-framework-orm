package org.albianj.dal.impl.db;

import org.albianj.common.utils.StringsUtil;
import org.albianj.dal.api.object.DBOpt;

public class SqlField {
    public static String nonKeywords(int dbOpt,String fieldName){
        switch (dbOpt) {
            case DBOpt.SqlServer :{
                return StringsUtil.nonIdxFmt("[{}]",fieldName);
            }
            case DBOpt.RedShift:
            case DBOpt.PgSql: {
                return StringsUtil.nonIdxFmt("\"{}\"",fieldName);
            }
            case DBOpt.MySql:
            default:{
                return StringsUtil.nonIdxFmt("`{}`",fieldName);
            }
        }
    }
}
