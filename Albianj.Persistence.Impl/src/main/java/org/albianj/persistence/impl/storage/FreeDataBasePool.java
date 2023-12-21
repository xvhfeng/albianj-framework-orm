package org.albianj.persistence.impl.storage;

import org.albianj.logger.LogLevel;
import org.albianj.logger.LogTarget;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.IDataBasePool;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.service.AlbianServiceRouter;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xuhaifeng on 2017/11/10.
 */
public abstract class FreeDataBasePool implements IDataBasePool {

    private final ConcurrentMap<String, DataSource> _dataSource = new ConcurrentHashMap<>();

    protected DataSource getDatasource(String sessionid,final String key, IRunningStorageAttribute rsa) throws Throwable {
        DataSource ds = _dataSource.get(key);
        if (ds != null) {
            return ds;
        }

        synchronized (_dataSource) {
            ds = _dataSource.get(key);
            if (ds == null) {
                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,
                        "create datasource storage::{} ,database::{}", rsa.getStorageAttribute().getName(),
                    rsa.getDatabase());
                try {
                    ds = setupDataSource(sessionid,key, rsa);
                    _dataSource.putIfAbsent(key, ds);
                } catch (Throwable t) {
                    AlbianServiceRouter.logAndThrowNew(AlbianServiceRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,t,
                            new AlbianDataServiceException("setup datasource error , key =" + key, t),
                            "setupDatasourceError|{}", key);
                }
            }
        }
        return ds;
    }

    protected DataSource getDatasource(String key) {
        DataSource ds = _dataSource.get(key);
        if (ds != null) {
            return ds;
        }

        synchronized (_dataSource) {
            ds = _dataSource.get(key);
        }
        return ds;
    }

    protected abstract DataSource setupDataSource(String sessionid,final String key, final IRunningStorageAttribute rsa) throws Throwable;

    //释放连接回连接池
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn,
        Statement pst, ResultSet rs) {

        AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Info,
                "return the connection from storage::{} and database::{} by connection pool.",
                storageName, databaseName);
        try {
            if (rs != null) {
                rs.close();
            }

        } catch (SQLException e) {
            AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Error,e,
                    "close the result by connection to storage::{} database::{} is fail.", storageName, databaseName);
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }

            } catch (SQLException e) {
                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Error,e,
                        "close the statement by connection to storage::{} database::{} is fail.",
                        storageName, databaseName);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }

                } catch (SQLException e) {
                    AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Error,e,
                            "close the  connection to storage::{} database::{} is fail.", storageName, databaseName);
                }
            }
        }

    }

    //释放连接回连接池
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn,
        List<Statement> statements) {
        AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Info,
                "return the connection from storage::{} and database::{} by connection pool.", storageName,
            databaseName);
        try {
            if (statements != null) {
                for (Statement statement : statements) {
                    try {
                        ((PreparedStatement)statement).clearParameters();
                    } catch (SQLException e) {
                        AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Error,e,
                                "close the statement to storage::{} database::{} is fail.",
                                storageName, databaseName);
                    } finally {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Error,e,
                                    "close the statconnectionement to storage::{} database::{} is fail.",
                                storageName, databaseName);
                        }
                    }
                }
            }
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException e) {
                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Error,e,
                        "close the  connection to storage::{} database::{} is fail.", storageName, databaseName);
            }
        }
    }

    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn) {
        AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Info,
                "return the connection from storage::{} and database::{} by connection pool.",
                storageName, databaseName);
        try {
            if (conn != null) {
                conn.close();
            }

        } catch (SQLException e) {
            AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Error,e,
                    "close the  connection to storage::{} database::{} is fail.",
                    storageName, databaseName);
        }
    }

}
