package org.albianj.impl.dal.db.localize;


import org.albianj.api.dal.db.localize.IDBClientSection;

import java.sql.Types;

public class SqlServerClientSection  implements IDBClientSection {

    public static String injectionArgumentEscaper(String argVal)
    {
        argVal = argVal.replace("", "");
        argVal = argVal.replace("[", "[[]"); // 这句话一定要在下面两个语句之前，否则作为转义符的方括号会被当作数据被再次处理
        argVal = argVal.replace("_", "[_]");
        argVal = argVal.replace("%", "[%]");
        return argVal;
    }

    public String toSqlValue(int sqlType,Object value,String charset){
        if(null == value)
            return null;
        switch (sqlType) {
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGVARCHAR:
                return  injectionArgumentEscaper(value.toString());
        }
        return value.toString();
    }
}
