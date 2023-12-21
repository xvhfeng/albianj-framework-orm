package org.albianj.orm.impl.storage;

import org.albianj.kernel.core.AlbianLevel;
import org.albianj.kernel.core.KernelSetting;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.kernel.logger.LogTarget;
import org.albianj.kernel.security.IAlbianSecurityService;
import org.albianj.kernel.service.AlbianServiceRouter;
import org.albianj.orm.db.AlbianDataServiceException;
import org.albianj.orm.object.IRunningStorageAttribute;
import org.albianj.orm.object.IStorageAttribute;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//import org.apache.commons.dbcp.BasicDataSource;

/**
 * Created by xuhaifeng on 17/2/26.
 */
public class DBCPWapper extends FreeDataBasePool {

    public final static String DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";

    public DBCPWapper() {

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
            if (null == conn)
                return null;
            if (Connection.TRANSACTION_NONE != sa.getTransactionLevel()) {
                conn.setTransactionIsolation(sa.getTransactionLevel());
            }
            conn.setAutoCommit(isAutoCommit);
            return conn;
        } catch (SQLException e) {
            AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e,
                    "Get the connection with storage::{} and database::{} form connection pool is error.",
                sa.getName(), rsa.getDatabase());
            return null;
        }
    }

    @Override
    public DataSource setupDataSource(String sessionid,String key, IRunningStorageAttribute rsa)  {
        BasicDataSource ds = null;
        try {
            ds = new BasicDataSource();
        } catch (Exception e) {
            AlbianServiceRouter.logAndThrowAgain(sessionid,LogTarget.Running,LogLevel.Error,e,
                    "create dabasepool for storage:{} is fail.", key);
        }
        try {
            IStorageAttribute storageAttribute = rsa.getStorageAttribute();
            String url = FreeAlbianStorageParserService
                    .generateConnectionUrl(rsa);
            ds.setDriverClassName(DRIVER_CLASSNAME);
            ds.setUrl(url);

            if (AlbianLevel.Debug == KernelSetting.getAlbianLevel()) {
                ds.setUsername(storageAttribute.getUser());
                ds.setPassword(storageAttribute.getPassword());
            } else {
                IAlbianSecurityService ass = AlbianServiceRouter.getService(sessionid,IAlbianSecurityService.class, IAlbianSecurityService.Name, false);
                if (null != ass) {
                    ds.setUsername(ass.decryptDES(sessionid,storageAttribute.getUser()));
                    ds.setPassword(ass.decryptDES(sessionid,storageAttribute.getPassword()));
                } else {
                    ds.setUsername(storageAttribute.getUser());
                    ds.setPassword(storageAttribute.getPassword());
                    throw new AlbianDataServiceException("the run level is release in the kernel config but security is null,so not use security service.");
                }
            }

            if (storageAttribute.getTransactional()) {
                ds.setDefaultAutoCommit(false);
                if (Connection.TRANSACTION_NONE != storageAttribute
                        .getTransactionLevel()) {
                    ds.setDefaultTransactionIsolation(storageAttribute
                            .getTransactionLevel());
                }
            }
            ds.setDefaultReadOnly(false);
            if (storageAttribute.getPooling()) {
                ds.setInitialSize(storageAttribute.getMinSize());
                ds.setMaxTotal(storageAttribute.getMaxSize());
                ds.setMaxIdle(2);//设定最大空闲时间
                ds.setNumTestsPerEvictionRun(storageAttribute.getMaxSize());
                ds.setMinEvictableIdleTimeMillis((storageAttribute.getAliveTime() - 30) * 1000);//-5s空闲链接就被回收 ，数据库连接是60s


            } else {
                ds.setInitialSize(1);
                ds.setMaxTotal(8);
                ds.setMaxIdle(2);//设定最大空闲时间
                ds.setNumTestsPerEvictionRun(storageAttribute.getMaxSize());
                ds.setMinEvictableIdleTimeMillis(60000);//60s空闲链接就被回收 ，数据库连接是60s
            }
            //            ds.setMaxConnLifetimeMillis((storageAttribute.getAliveTime() -5) * 1000);
            ds.setRemoveAbandonedTimeout(1);
            ds.setMaxWaitMillis(1000);
            /*
            超时时间(以秒数为单位)设置超时时间有一个要注意的地方，
            超时时间=现在的时间-程序中创建Connection的时间，如果
            maxActive比较大，比如超过100，那么removeAbandonedTimeout
            可以设置长一点比如180， 也就是三分钟无响应的连接进行回收，
            当然应用的不同设置长度也不同
            */
            //            ds.setLogAbandoned(true);
            ds.setValidationQuery("SELECT 1");
            ds.setValidationQueryTimeout(1);//1s验证超时
            //            ds.setTestOnBorrow(true);//损耗性能，不行的话就换成c3p0连接池，先把这句注释了，性能可能有问题
            ds.setTestWhileIdle(true);
            ds.setTimeBetweenEvictionRunsMillis(10000); //在空闲连接回收器线程运行期间休眠的时间值,以毫秒为单位


        } catch (Exception e) {
            AlbianServiceRouter.logAndThrowAgain(sessionid,LogTarget.Running,LogLevel.Error,e,
                    "startup database connection pools is fail.");
            //return null;
        }

        return ds;
    }

}
