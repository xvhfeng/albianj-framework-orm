package org.albianj.orm.kit.db.localize;

public interface IDBClientSection {
    String toSqlValue(int sqlType,Object value,String charset);
}
