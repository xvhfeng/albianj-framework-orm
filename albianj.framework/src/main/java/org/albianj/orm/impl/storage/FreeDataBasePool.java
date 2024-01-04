package org.albianj.orm.impl.storage;


import org.albianj.AlbianRuntimeException;
import org.albianj.kernel.itf.builtin.logger.LogLevel;
import org.albianj.kernel.itf.builtin.logger.LogTarget;
import org.albianj.kernel.itf.service.AlbianServRouter;
import org.albianj.orm.itf.db.IDataBasePool;
import org.albianj.orm.attr.RunningStorageAttribute;

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

    protected DataSource getDatasource(String sessionid,final String key, RunningStorageAttribute rsa)  {
        DataSource ds = _dataSource.get(key);
        if (ds != null) {
            return ds;
        }

        synchronized (_dataSource) {
            ds = _dataSource.get(key);
            if (ds == null) {
                AlbianServRouter.log(AlbianServRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,
                        "create datasource storage::{} ,database::{}", rsa.getStorageAttribute().getName(),
                    rsa.getDatabase());
                try {
                    ds = setupDataSource(sessionid,key, rsa);
                    _dataSource.putIfAbsent(key, ds);
                } catch (Throwable t) {
                    AlbianServRouter.logAndThrowNew(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,t,
                            new AlbianRuntimeException("setup datasource error , key =" + key, t),
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

    protected abstract DataSource setupDataSource(String sessionid,final String key, final RunningStorageAttribute rsa) ;

    //释放连接回连接池
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn,
        Statement pst, ResultSet rs) {

        AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Info,
                "return the connection from storage::{} and database::{} by connection pool.",
                storageName, databaseName);
        try {
            if (rs != null) {
                rs.close();
            }

        } catch (SQLException e) {
            AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e,
                    "close the result by connection to storage::{} database::{} is fail.", storageName, databaseName);
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }

            } catch (SQLException e) {
                AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e,
                        "close the statement by connection to storage::{} database::{} is fail.",
                        storageName, databaseName);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }

                } catch (SQLException e) {
                    AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e,
                            "close the  connection to storage::{} database::{} is fail.", storageName, databaseName);
                }
            }
        }

    }

    //释放连接回连接池
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn,
        List<Statement> statements) {
        AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Info,
                "return the connection from storage::{} and database::{} by connection pool.", storageName,
            databaseName);
        try {
            if (statements != null) {
                for (Statement statement : statements) {
                    try {
                        ((PreparedStatement)statement).clearParameters();
                    } catch (SQLException e) {
                        AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e,
                                "close the statement to storage::{} database::{} is fail.",
                                storageName, databaseName);
                    } finally {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e,
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
                AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e,
                        "close the  connection to storage::{} database::{} is fail.", storageName, databaseName);
            }
        }
    }

    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn) {
        AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Info,
                "return the connection from storage::{} and database::{} by connection pool.",
                storageName, databaseName);
        try {
            if (conn != null) {
                conn.close();
            }

        } catch (SQLException e) {
            AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e,
                    "close the  connection to storage::{} database::{} is fail.",
                    storageName, databaseName);
        }
    }

}
