package org.albianj.orm.impl.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.albianj.kernel.core.AlbianLevel;
import org.albianj.kernel.core.KernelSetting;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.kernel.logger.LogTarget;
import org.albianj.kernel.security.IAlbianSecurityService;
import org.albianj.kernel.service.AlbianServiceRouter;
import org.albianj.orm.db.AlbianDataServiceException;
import org.albianj.orm.object.IRunningStorageAttribute;
import org.albianj.orm.object.IStorageAttribute;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by xuhaifeng on 17/7/27.
 */
public class HikariCPWapper extends FreeDataBasePool {

    public final static String DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";

    public HikariCPWapper() {
        AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,
                "use Hikari connection pool.");
    }

    @Override
    public Connection getConnection(String sessionId, IRunningStorageAttribute rsa, boolean isAutoCommit)  {
        IStorageAttribute sa = rsa.getStorageAttribute();
        String key = sa.getName() + rsa.getDatabase();
        DataSource ds = getDatasource(sessionId,key, rsa);
        AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,
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
            AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,e,
                    "Get the connection with storage::{} and database::{} form connection pool is error.",
                sa.getName(), rsa.getDatabase());
            return null;
        }
    }

    @Override
    public DataSource setupDataSource(String sessionid,String key, IRunningStorageAttribute rsa)  {
        HikariConfig config = new HikariConfig();
        try {
            IStorageAttribute storageAttribute = rsa.getStorageAttribute();
            String url = FreeAlbianStorageParserService
                    .generateConnectionUrl(rsa);
            config.setDriverClassName(DRIVER_CLASSNAME);
            config.setJdbcUrl(url);
            if (AlbianLevel.Debug == KernelSetting.getAlbianLevel()) {
                config.setUsername(storageAttribute.getUser());
                config.setPassword(storageAttribute.getPassword());
            } else {
                IAlbianSecurityService ass = AlbianServiceRouter.getService(sessionid,IAlbianSecurityService.class, IAlbianSecurityService.Name, false);
                if (null != ass) {
                    config.setUsername(ass.decryptDES(sessionid,storageAttribute.getUser()));
                    config.setPassword(ass.decryptDES(sessionid,storageAttribute.getPassword()));
                } else {
                    config.setUsername(storageAttribute.getUser());
                    config.setPassword(storageAttribute.getPassword());
                    throw new AlbianDataServiceException(
                        "the run level is release in the kernel config but security is null,so not use security service.");
                }
            }
            config.setAutoCommit(false);
            config.setReadOnly(false);
            //            config.setTransactionIsolation(storageAttribute.getTransactionLevel());
            config.addDataSourceProperty("cachePrepStmts", true);
            config.addDataSourceProperty("prepStmtCacheSize", 500);
            config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            config.setConnectionTestQuery("SELECT 1");

            if (storageAttribute.getPooling()) {
                //池中最小空闲链接数量
                config.setMinimumIdle(storageAttribute.getMinSize());
                //池中最大链接数量
                config.setMaximumPoolSize(storageAttribute.getMaxSize());
                config.setMaxLifetime(storageAttribute.getAliveTime() * 1000);
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
            AlbianServiceRouter.logAndThrowAgain(sessionid,LogTarget.Running,LogLevel.Error,e,
                    "startup database connection pools is fail.");
            // return null;
        }

        HikariDataSource ds = null;
        try {
            ds = new HikariDataSource(config);
        } catch (Exception e) {
            AlbianServiceRouter.logAndThrowAgain(sessionid,LogTarget.Running,LogLevel.Error,e,
                    "create dabasepool for storage:{} is fail.", key);
        }

        return ds;
    }
}
