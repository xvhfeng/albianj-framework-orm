package org.albianj.dal.impl.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.albianj.ServRouter;
import org.albianj.dal.api.object.StgAttr;
import org.albianj.kernel.api.logger.LogLevel;
import org.albianj.dal.api.object.RStgAttr;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by xuhaifeng on 17/7/27.
 */
public class HCPWapper extends FreeDBP {

    public final static String DRIVER_CLASSNAME = "com.mysql.cj.jdbc.Driver";

    public HCPWapper() {
        ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Info,
                "use Hikari connection pool.");
    }

    @Override
    public Connection getConnection(String sessionId, RStgAttr rsa, boolean isAutoCommit)  {
        StgAttr sa = rsa.getStgAttr();
        String key = sa.getName() + rsa.getDatabase();
        DataSource ds = getDatasource(sessionId,key, rsa);
        HikariDataSource dsWarp = ( HikariDataSource) ds;

        ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Info,
                "Get the connection from storage::{} and database::{} by connection pool.", sa.getName(),
            rsa.getDatabase());
        try {
            Connection conn = ds.getConnection();
            if (null == conn) return null;
            if (Connection.TRANSACTION_NONE != sa.getTransactionLevel()) {
                conn.setTransactionIsolation(sa.getTransactionLevel());
            }
            conn.setAutoCommit(isAutoCommit);
            return conn;
        } catch (SQLException e) {
            ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Error,e,
                    "Get the connection with storage::{} and database::{} form connection pool is error.",
                sa.getName(), rsa.getDatabase());
            return null;
        }
    }

    @Override
    public DataSource setupDataSource(String sessionid,String key, RStgAttr rsa)  {
        HikariConfig config = new HikariConfig();
        try {
            StgAttr stgAttr = rsa.getStgAttr();
            String url = FreeAlbianStorageParserService
                    .generateConnectionUrl(rsa);
            config.setDriverClassName(DRIVER_CLASSNAME);
            config.setJdbcUrl(url);
            config.setUsername(stgAttr.getUser());
            config.setPassword(stgAttr.getPassword());

            config.setAutoCommit(false);
            config.setReadOnly(false);
            //            config.setTransactionIsolation(storageAttribute.getTransactionLevel());
            config.addDataSourceProperty("cachePrepStmts", true);
            config.addDataSourceProperty("prepStmtCacheSize", 500);
            config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            config.setConnectionTestQuery("SELECT 1");

            if (stgAttr.isPooling()) {
                //池中最小空闲链接数量
                config.setMinimumIdle(stgAttr.getMinSize());
                //池中最大链接数量
                config.setMaximumPoolSize(stgAttr.getMaxSize());
                config.setMaxLifetime(stgAttr.getIdelTime() * 1000);
                config.setConnectionTimeout(2 * 1000);//wait get connection from pool
            } else {
                //池中最小空闲链接数量
                config.setMinimumIdle(10);
                //池中最大链接数量
                config.setMaximumPoolSize(20);
                config.setMaxLifetime(150 * 1000);
                config.setConnectionTimeout(2 * 1000);
            }
            config.setValidationTimeout(1000);

        } catch (Exception e) {
            ServRouter.logAndThrowAgain(sessionid,LogLevel.Error,e,
                    "startup database connection pools is fail.");
            // return null;
        }

        HikariDataSource ds = null;
        try {
            ds = new HikariDataSource(config);
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(sessionid,LogLevel.Error,e,
                    "create dabasepool for storage:{} is fail.", key);
        }

        return ds;
    }
}
