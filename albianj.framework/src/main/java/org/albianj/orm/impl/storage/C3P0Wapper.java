package org.albianj.orm.impl.storage;

import com.mchange.v2.c3p0.ComboPooledDataSource;
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
 * Created by xuhaifeng on 17/2/26.
 */
public class C3P0Wapper extends FreeDataBasePool {

    public final static String DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";

    public C3P0Wapper() {
    }

    @Override
    public Connection getConnection(String sessionid, IRunningStorageAttribute rsa, boolean isAutoCommit)  {
        IStorageAttribute sa = rsa.getStorageAttribute();
        String key = sa.getName() + rsa.getDatabase();
        DataSource ds = getDatasource(sessionid,key, rsa);
        AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,
                "Get the connection from storage::{} and database::{} by connection pool.", sa.getName(),
            rsa.getDatabase());
        try {
            Connection conn = ds.getConnection();
            if (null == conn)
                return null;
            if (Connection.TRANSACTION_NONE != sa.getTransactionLevel()) {
                conn.setTransactionIsolation(sa.getTransactionLevel());
            }
            conn.setAutoCommit(isAutoCommit);
            return conn;
        } catch (SQLException e) {
            AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,
                    "Get the connection with storage::{} and database::{} form connection pool is error.",
                sa.getName(), rsa.getDatabase(), e);
            return null;
        }
    }

    @Override
    public DataSource setupDataSource(String sessionid,String key, IRunningStorageAttribute rsa)  {
        ComboPooledDataSource ds = null;
        try {
            ds = new ComboPooledDataSource();
        } catch (Exception e) {
            AlbianServiceRouter.logAndThrowAgain(sessionid,LogTarget.Running,LogLevel.Error,e,
                    "create dabasepool for storage:{} is fail.", key);
        }
        try {
            IStorageAttribute storageAttribute = rsa.getStorageAttribute();
            String url = FreeAlbianStorageParserService.generateConnectionUrl(rsa);
            ds.setDriverClass(DRIVER_CLASSNAME);
            ds.setJdbcUrl(url);
            if (AlbianLevel.Debug == KernelSetting.getAlbianLevel()) {
                ds.setUser(storageAttribute.getUser());
                ds.setPassword(storageAttribute.getPassword());
            } else {
                IAlbianSecurityService ass = AlbianServiceRouter
                        .getService(sessionid,IAlbianSecurityService.class, IAlbianSecurityService.Name, false);
                if (null != ass) {
                    ds.setUser(ass.decryptDES(sessionid,storageAttribute.getUser()));
                    ds.setPassword(ass.decryptDES(sessionid,storageAttribute.getPassword()));
                } else {
                    ds.setUser(storageAttribute.getUser());
                    ds.setPassword(storageAttribute.getPassword());
                    throw new AlbianDataServiceException("the run level is release in the kernel config but security is null,so not use security service.");
                }
            }
            ds.setAutoCommitOnClose(false); //连接关闭时默认将所有未提交的操作回滚

            if (storageAttribute.getPooling()) {
                ds.setMaxPoolSize(storageAttribute.getMaxSize());
                ds.setMinPoolSize(storageAttribute.getMinSize());
                ds.setInitialPoolSize(storageAttribute.getMinSize());
                ds.setMaxIdleTime(storageAttribute.getAliveTime() - 5);
                ds.setMaxConnectionAge(storageAttribute.getAliveTime() - 5);

            } else {
                ds.setMaxPoolSize(8);
                ds.setMinPoolSize(4);
                ds.setInitialPoolSize(4);
                ds.setMaxIdleTime(50);
                ds.setMaxConnectionAge(50);
            }

            ds.setAcquireIncrement(2); //链接用完了自动增量2个
            ds.setAcquireRetryAttempts(3); //链接失败后重新试3次
            ds.setAcquireRetryDelay(1000); //两次连接中间隔1000毫秒
            ds.setCheckoutTimeout(1000); //程序从连接池checkout session的时候等待1000毫秒，超时则抛出异常
            ds.setIdleConnectionTestPeriod(30); //每30秒检查所有连接池中的空闲连接
            ds.setNumHelperThreads(3); //异步操作，提升性能通过多线程实现多个操作同时被执行。
            ds.setPreferredTestQuery("SELECT 1");
            ds.setMaxStatements(0); //定义了连接池内单个连接所拥有的最大缓存statements数
            ds.setDebugUnreturnedConnectionStackTraces(true);//打开链接池的泄露调试
            ds.setUnreturnedConnectionTimeout(120); //增加没有返回的链接超时机制，防止链接泄露，单位是秒
        } catch (Exception e) {
            AlbianServiceRouter.logAndThrowAgain(sessionid,LogTarget.Running,LogLevel.Error,e,
                    "startup database connection pools is fail.");
            //return null;
        }

        return ds;
    }

}
