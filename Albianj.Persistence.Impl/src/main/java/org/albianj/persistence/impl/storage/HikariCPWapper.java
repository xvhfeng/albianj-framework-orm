package org.albianj.persistence.impl.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.albianj.kernel.AlbianLevel;
import org.albianj.kernel.KernelSetting;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.security.IAlbianSecurityService;
import org.albianj.service.AlbianServiceRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by xuhaifeng on 17/7/27.
 */
public class HikariCPWapper extends FreeDataBasePool {

    private static final Logger logger = LoggerFactory.getLogger(HikariCPWapper.class);
    public final static String DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";

    public HikariCPWapper() {
        logger.info("use Hikari connection pool.");
    }

    @Override
    public Connection getConnection(String sessionId, IRunningStorageAttribute rsa,boolean isAutoCommit) {
        IStorageAttribute sa = rsa.getStorageAttribute();
        String key = sa.getName() + rsa.getDatabase();
        DataSource ds = getDatasource(key, rsa);
        logger.info("Get the connection from storage::{} and database::{} by connection pool.", sa.getName(),
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
            logger.error("Get the connection with storage::{} and database::{} form connection pool is error.",
                sa.getName(), rsa.getDatabase(), e);
            return null;
        }
    }

    @Override
    public DataSource setupDataSource(String key, IRunningStorageAttribute rsa) {
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
                IAlbianSecurityService ass = AlbianServiceRouter.getSingletonService(IAlbianSecurityService.class, IAlbianSecurityService.Name, false);
                if (null != ass) {
                    config.setUsername(ass.decryptDES(storageAttribute.getUser()));
                    config.setPassword(ass.decryptDES(storageAttribute.getPassword()));
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
            throw new AlbianDataServiceException("startup database connection pools is fail.", e);
            // return null;
        }

        HikariDataSource ds = null;
        try {
            ds = new HikariDataSource(config);
        } catch (Exception e) {
            throw new AlbianDataServiceException("create dabasepool for storage:" + key + " is fail.", e);
        }

        return ds;
    }
}
