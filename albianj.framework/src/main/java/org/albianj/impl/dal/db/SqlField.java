package org.albianj.impl.dal.db;

import org.albianj.api.dal.object.DBOpt;
import org.albianj.common.utils.StringsUtil;

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
