package org.albianj.api.dal.db;


import org.albianj.api.dal.object.RStgAttr;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Created by xuhaifeng on 17/2/26.
 */
public interface IDBP {

    /**
     * 从链接池中得到一个连接
     */
    public Connection getConnection(String sessionid, RStgAttr rsa, boolean isAutoCommit) ;


        /**
         * 返回一个读取数据库的链接到链接池
         *
         * @param conn
         * @param pst
         * @param rs
         */
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn, Statement pst, ResultSet rs);

    /**
     * 返回一直执行sql语句的链接到链接池
     *
     * @param conn
     * @param pst
     */
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn, List<Statement> pst);

    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn);

}
