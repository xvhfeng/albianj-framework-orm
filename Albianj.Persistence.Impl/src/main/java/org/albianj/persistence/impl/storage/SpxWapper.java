package org.albianj.persistence.impl.storage;

import org.albianj.kernel.AlbianLevel;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.LogLevel;
import org.albianj.logger.LogTarget;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.impl.dbpool.ISpxDBPool;
import org.albianj.persistence.impl.dbpool.ISpxDBPoolConfig;
import org.albianj.persistence.impl.dbpool.impl.SpxDBPool;
import org.albianj.persistence.impl.dbpool.impl.SpxDBPoolConfig;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.security.IAlbianSecurityService;
import org.albianj.service.AlbianServiceRouter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SpxWapper extends FreeDataBasePool {

    public final static String DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";

    public Connection getConnection(String sessionId, IRunningStorageAttribute rsa,boolean isAutoCommit) throws Throwable {
        IStorageAttribute sa = rsa.getStorageAttribute();
        String key = sa.getName() + rsa.getDatabase();
        DataSource ds = getDatasource(sessionId,key, rsa);
        ISpxDBPool pool = (ISpxDBPool)ds;
        AlbianServiceRouter.log(sessionId, LogTarget.Sql, LogLevel.Info,
                "Get the connection from storage::{} and database::{} by connection pool.", sa.getName(),
            rsa.getDatabase());
        try {
            Connection conn = pool.getConnection(sessionId);
            if (null == conn)
                return null;
            if (Connection.TRANSACTION_NONE != sa.getTransactionLevel()) {
                conn.setTransactionIsolation(sa.getTransactionLevel());
            }
            conn.setAutoCommit(isAutoCommit);
            return conn;
        } catch (SQLException e) {
            AlbianServiceRouter.log(sessionId, LogTarget.Sql, LogLevel.Error,e,
                    "Get the connection with storage::{} and database::{} form connection pool is error.",
                sa.getName(), rsa.getDatabase());
            return null;
        }
    }

    @Override
    public DataSource setupDataSource(String sessionid,String key, IRunningStorageAttribute rsa) throws Throwable {
        ISpxDBPoolConfig cf = null;
        try {
            cf = new SpxDBPoolConfig();
        } catch (Exception e) {
            AlbianServiceRouter.logAndThrowAgain(sessionid,LogTarget.Sql,LogLevel.Error,e,
                    "create dabasepool for storage:{} is fail.",key);
        }
        try {
            IStorageAttribute stgAttr = rsa.getStorageAttribute();
            String url = FreeAlbianStorageParserService.generateConnectionUrl(rsa);
            cf.setDriverName(DRIVER_CLASSNAME);
            cf.setUrl(url);
            if (AlbianLevel.Debug == KernelSetting.getAlbianLevel()) {
                cf.setUsername(stgAttr.getUser());
                cf.setPassword(stgAttr.getPassword());
            } else {
                IAlbianSecurityService ass = AlbianServiceRouter
                        .getService(sessionid,IAlbianSecurityService.class, IAlbianSecurityService.Name, false);
                if (null != ass) {
                    cf.setUsername(ass.decryptDES(sessionid,stgAttr.getUser()));
                    cf.setPassword(ass.decryptDES(sessionid,stgAttr.getPassword()));
                } else {
                    cf.setUsername(stgAttr.getUser());
                    cf.setPassword(stgAttr.getPassword());
                    throw new AlbianDataServiceException(
                        "the run level is release in the kernel config but security is null,so not use security service.");
                }
            }

            cf.setMaxConnections(stgAttr.getMaxSize());
            cf.setMinConnections(stgAttr.getMinSize());

            cf.setCleanupTimestampMs(stgAttr.getCleanupTimestampMs());
            cf.setWaitInFreePoolMs(stgAttr.getWaitInFreePoolMs());
            cf.setLifeCycleTime(stgAttr.getLifeCycleTime());
            cf.setMaxRemedyConnectionCount(stgAttr.getMaxRemedyConnectionCount());
            cf.setMaxRequestTimeMs(stgAttr.getMaxRequestTimeMs());//最大单次执行sql时间为1分钟
            cf.setPoolName(key);
            cf.setWaitTimeWhenGetMs(stgAttr.getWaitTimeWhenGetMs());
        } catch (Exception e) {
            AlbianServiceRouter.logAndThrowAgain(sessionid,LogTarget.Sql,LogLevel.Error,e,
                    "startup database connection pools is fail.");
            //return null;
        }
        DataSource pool = SpxDBPool.createConnectionPool(sessionid,cf);
        return pool;
    }

    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn, List<Statement> statements) {
        for (Statement stmt : statements) {
            try {
                stmt.close();
            } catch (SQLException e) {

            }
        }
        this.returnConnection(sessionId, storageName, databaseName, conn);
    }

    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn, Statement pst, ResultSet rs) {
        try {
            pst.close();
        } catch (SQLException e) {

        }
        try {
            rs.close();
        } catch (SQLException e) {

        }
        this.returnConnection(sessionId, storageName, databaseName, conn);
    }

    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn) {
        try {
            String key = storageName + databaseName;
            DataSource ds = getDatasource(key);
            if (null == ds) {
                AlbianServiceRouter.log(sessionId,LogTarget.Sql,LogLevel.Info,
                        "return the connection from storage::{} and database::{} by connection pool.",
                        storageName, databaseName);
                conn.close();
            }
            ISpxDBPool pool = (ISpxDBPool) ds;
            pool.rtnConnection(conn);
        } catch (SQLException e) {
            AlbianServiceRouter.log(sessionId,LogTarget.Sql,LogLevel.Error,e,
                    "fail in return the connection from storage::{} and database::{} by connection pool.",
                    storageName, databaseName);
        }
    }
}
