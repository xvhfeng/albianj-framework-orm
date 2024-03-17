package org.albianj.api.dal.db.localize;

public interface IDBClientSection {
    String toSqlValue(int sqlType,Object value,String charset);
}
