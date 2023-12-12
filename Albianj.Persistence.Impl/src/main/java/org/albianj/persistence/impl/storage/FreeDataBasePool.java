package org.albianj.persistence.impl.storage;

import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.IDataBasePool;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xuhaifeng on 2017/11/10.
 */
public abstract class FreeDataBasePool implements IDataBasePool {

    private static final Logger logger = LoggerFactory.getLogger(FreeDataBasePool.class);
    private final ConcurrentMap<String, DataSource> _dataSource = new ConcurrentHashMap<>();

    protected DataSource getDatasource(final String key, IRunningStorageAttribute rsa) {
        DataSource ds = _dataSource.get(key);
        if (ds != null) {
            return ds;
        }

        synchronized (_dataSource) {
            ds = _dataSource.get(key);
            if (ds == null) {
                logger.info("create datasource storage::{} ,database::{}", rsa.getStorageAttribute().getName(),
                    rsa.getDatabase());
                try {
                    ds = setupDataSource(key, rsa);
                    _dataSource.putIfAbsent(key, ds);
                } catch (Throwable t) {
                    logger.error("setupDatasourceError|{}", key, t);
                    throw new AlbianDataServiceException("setup datasource error , key =" + key, t);
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

    protected abstract DataSource setupDataSource(final String key, final IRunningStorageAttribute rsa);

    //释放连接回连接池
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn,
        Statement pst, ResultSet rs) {

        logger.info("return the connection from storage::{} and database::{} by connection pool.", storageName,
            databaseName);
        try {
            if (rs != null) {
                rs.close();
            }

        } catch (SQLException e) {
            logger
                .error("close the result by connection to storage::{} database::{} is fail.", storageName, databaseName,
                    e);
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }

            } catch (SQLException e) {
                logger.error("close the statement by connection to storage::{} database::{} is fail.", storageName,
                    databaseName, e);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }

                } catch (SQLException e) {
                    logger
                        .error("close the  connection to storage::{} database::{} is fail.", storageName, databaseName,
                            e);
                }
            }
        }

    }

    //释放连接回连接池
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn,
        List<Statement> statements) {
        logger.info("return the connection from storage::{} and database::{} by connection pool.", storageName,
            databaseName);
        try {
            if (statements != null) {
                for (Statement statement : statements) {
                    try {
                        ((PreparedStatement)statement).clearParameters();
                    } catch (SQLException e) {
                        logger.error("close the statement to storage::{} database::{} is fail.", storageName,
                            databaseName, e);
                    } finally {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            logger.error("close the statconnectionement to storage::{} database::{} is fail.",
                                storageName, databaseName, e);
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
                logger
                    .error("close the  connection to storage::{} database::{} is fail.", storageName, databaseName, e);
            }
        }
    }

    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn) {
        logger.info("return the connection from storage::{} and database::{} by connection pool.", storageName,
            databaseName);
        try {
            if (conn != null) {
                conn.close();
            }

        } catch (SQLException e) {
            logger.error("close the  connection to storage::{} database::{} is fail.", storageName, databaseName, e);
        }
    }

}
