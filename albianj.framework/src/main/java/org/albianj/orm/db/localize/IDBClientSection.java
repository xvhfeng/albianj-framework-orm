package org.albianj.orm.db.localize;

public interface IDBClientSection {
    String toSqlValue(int sqlType,Object value,String charset);
}
