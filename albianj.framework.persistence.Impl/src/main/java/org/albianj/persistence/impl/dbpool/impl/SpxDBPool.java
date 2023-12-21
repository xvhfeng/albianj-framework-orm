package org.albianj.persistence.impl.dbpool.impl;

import org.albianj.logger.LogLevel;
import org.albianj.logger.LogTarget;
import org.albianj.persistence.impl.dbpool.IPoolingConnection;
import org.albianj.persistence.impl.dbpool.ISpxDBPool;
import org.albianj.persistence.impl.dbpool.ISpxDBPoolConfig;
import org.albianj.service.AlbianServiceRouter;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.logging.Logger;

public class SpxDBPool implements ISpxDBPool {

    private ISpxDBPoolConfig cf = null;
    private Boolean isActive = true;
    private String name;

    private LinkedList<IPoolingConnection> freeConnections = new LinkedList<>();
    private LinkedList<IPoolingConnection> busyConnections = new LinkedList<>();
    private LinkedList<IPoolingConnection> remebyConnections = new LinkedList<>();

    private SpxDBPool() {
        super();
    }

    public static synchronized SpxDBPool createConnectionPool(String sessionId,ISpxDBPoolConfig cf) {

        AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Info,
                "create dbpool:{} with argument: minConnections :{},maxConnections :{}"
                + " waitTimeWhenGetMs :{}, lifeTimeMs :{}, freeTimeMs :{},maxRemedyConnectionCount :{},"
                + "max request timeout :{},cleanup timestamp :{}", cf.getPoolName(), cf.getMinConnections(),
            cf.getMaxConnections(), cf.getWaitTimeWhenGetMs(), cf.getLifeCycleTime(), cf.getWaitInFreePoolMs(),
            cf.getMaxRemedyConnectionCount(), cf.getMaxRequestTimeMs(), cf.getCleanupTimestampMs());

        SpxDBPool pool = new SpxDBPool();
        pool.cf = cf;
        pool.setPoolName(cf.getPoolName());

        for (int i = 0; i < pool.cf.getMinConnections(); i++) {
            try {
                IPoolingConnection conn = pool.newConnection(true);
                pool.freeConnections.add(conn);
            } catch (SQLException e) {
                AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Error,e,
                        "create dbpool：{} is fail.", cf.getPoolName());
                return null;
            }
        }

        pool.isActive = true;
        pool.regeditCleanupTask(sessionId);
        return pool;
    }

    public String getPoolName() {
        return name;
    }

    public void setPoolName(String name) {
        this.name = name;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    public ISpxDBPoolConfig getConfig() {
        return cf;
    }

    @Override
    public void setConfig(ISpxDBPoolConfig config) {
        this.cf = config;
    }

    @Override
    public int getBusyCount() {
        synchronized (busyConnections) {
            return this.busyConnections.size();
        }
    }

    @Override
    public int getFreeCount() {
        synchronized (freeConnections) {
            return this.freeConnections.size();
        }
    }

    public int getRemedyCount() {
        synchronized (remebyConnections) {
            return remebyConnections.size();
        }
    }

    private IPoolingConnection pollFreeConnection() {
        synchronized (freeConnections) {
            return freeConnections.pollFirst();
        }
    }

    private void pushFreeConnection(IPoolingConnection pconn) {
        synchronized (freeConnections) {
            freeConnections.addLast(pconn);
        }
    }

    private void removeFreeConnection(IPoolingConnection pconn) {
        synchronized (freeConnections) {
            freeConnections.remove(pconn);
        }
    }

    private void pushBusyConnection(IPoolingConnection pconn) {
        synchronized (busyConnections) {
            busyConnections.addLast(pconn);
        }
    }

    private void removeBusyConnection(IPoolingConnection pconn) {
        synchronized (busyConnections) {
            busyConnections.remove(pconn);
        }
    }

    private void pushRemedyConnection(IPoolingConnection pconn) {
        synchronized (remebyConnections) {
            remebyConnections.addLast(pconn);
        }
    }

    private void removeRemedyConnection(IPoolingConnection pconn) {
        synchronized (remebyConnections) {
            remebyConnections.remove(pconn);
        }
    }

    private IPoolingConnection newConnection(boolean isPooling) throws SQLException {
        IPoolingConnection pconn = null;
        long now = System.currentTimeMillis();
        if (this.cf != null) {
            Connection conn = DriverManager.getConnection(this.cf.getUrl(),
                    this.cf.getUsername(),
                    this.cf.getPassword());
            pconn = new PoolingConnection(conn, System.currentTimeMillis(), isPooling);
            pconn.setLastUsedTimeMs(now);
            pconn.setStartupTimeMs(now);
        }
        return pconn;
    }

    private void usePoolingConnection(String sessionId, IPoolingConnection pconn) {
        pushBusyConnection(pconn);
        pconn.setLastUsedTimeMs(System.currentTimeMillis());
        pconn.addReuseTimes();
        pconn.setSessionId(sessionId);
    }

    private void useRemedyConnection(String sessionId, IPoolingConnection pconn) {
        pushRemedyConnection(pconn);
        pconn.setLastUsedTimeMs(System.currentTimeMillis());
        pconn.setSessionId(sessionId);
    }

//    @Override
//    public Connection getConnection(String sessionId) throws SQLException {
//        AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Error,
//                "please use the same function with argument sessionid.:{}",
//                cf.getPoolName());
//        throw new SQLException("getConnection not implament");
//
//    }

    @Override
    public Connection getConnection(String sessionId) throws SQLException {
        IPoolingConnection pconn = null;
        long now = System.currentTimeMillis();
        pconn = pollFreeConnection();
        if (null != pconn) { // have free connection
            if (pconn.getLastUsedTimeMs() + this.cf.getWaitInFreePoolMs() <= now || !pconn.isValid()) {
                AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Warn,
                        "DBPOOL :{}.free time expired connection which lastUsedTime:{}, "
                        + "startup :{}, reuse:{},timout:{},valid :{}.close it and new pooling one.", cf.getPoolName(),
                    pconn.getLastUsedTimeMs(), pconn.getStartupTimeMs(), pconn.getReuseTimes(),
                    (now - pconn.getLastUsedTimeMs() - cf.getWaitInFreePoolMs()), pconn.isValid() ? "true" : "false");
                pconn.close();
                pconn = newConnection(true);
            }
            usePoolingConnection(sessionId, pconn);
            return pconn;
        }

        // not have free connection
        // new one and add to dbpool
        if (this.getBusyCount() < this.cf.getMaxConnections()) { // maybe not threadsafe but soso
            AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Info,
                    "DBPOOL :{}.not have free connection and new pooling one.", cf.getPoolName());
            pconn = newConnection(true);
            usePoolingConnection(sessionId, pconn);
            return pconn;
        }

        //all connection is busy
        if (cf.getWaitTimeWhenGetMs() <= 0) { // not wait and do remedy
            if (this.getRemedyCount() < cf.getMaxRemedyConnectionCount()) {
                AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Warn,
                        "DBPOOL :{}.all connection is busy,the config is not waitting and new remedy one.",
                    cf.getPoolName());
                pconn = newConnection(false);
                useRemedyConnection(sessionId, pconn);
                if (this.getRemedyCount() >= cf.getMaxRemedyConnectionCount() / 2) {
                    AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Info,
                            "DBPOOL :{}.the remedy connections count:{} over the half by max  remedy connections :{}."
                            + "Critical,maybe dbpool is overflow.", cf.getPoolName(), this.getRemedyCount(),
                        cf.getMaxRemedyConnectionCount());
                }
                return pconn;
            }
            AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Info,
                    "DBPOOL :{}.current remedy connections :{} over the maxsize :{},not connection can use."
                    + "Critical,maybe dbpool is overflow.",
                    cf.getPoolName(), this.getRemedyCount(), cf.getMaxRemedyConnectionCount());
            return null;
        }

        // wait
        long beginWait = System.currentTimeMillis();
        synchronized (this) {
            try {
                this.wait(cf.getWaitTimeWhenGetMs());
            } catch (InterruptedException e) {
                AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Error,e,
                        "DBPOOL:{}.get connection when wait was be Interrupted.",
                        cf.getPoolName());
            }
        }

        long endWait = System.currentTimeMillis();
        if (beginWait + cf.getWaitTimeWhenGetMs() > endWait) {
            //wakeup by notify
            return this.getConnection(sessionId);
        }

        // wait timeout and do remedy
        if (this.getRemedyCount() < cf.getMaxRemedyConnectionCount()) {
            AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Error,
                    "DBPOOL :{}.all connection is busy and wait timeout.try new remedy connection.",
                cf.getPoolName());
            pconn = newConnection(false);
            useRemedyConnection(sessionId, pconn);
            if (this.getRemedyCount() >= cf.getMaxRemedyConnectionCount() / 2) {
                AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Info,
                        "DBPOOL:{}.the remedy connections count:{} over the half by max  remedy connections :{}."
                        + "Critical,maybe dbpool is overflow.",
                        cf.getPoolName(), this.getRemedyCount(), cf.getMaxRemedyConnectionCount());
            }
            return pconn;
        }

        AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Info,
                "DBPOOL :{}.current remedy connections :{} over the maxsize:{},not connection can use."
                + "Critical,maybe dbpool is overflow.",
                cf.getPoolName(), this.getRemedyCount(), cf.getMaxRemedyConnectionCount());
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,
                "DBPOOL please use the same function with argument sessionid:{}.", cf.getPoolName());
        throw new SQLException("getConnection not implament");
    }

    @Override
    public synchronized void rtnConnection(Connection conn) throws SQLException {
        IPoolingConnection pconn = (IPoolingConnection) conn;
        String sessionId = pconn.getSessionId();
        if (!pconn.isPooling()) {
            AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Info,
                    "DBPOOL :{}.back remedy connecton.close it.", cf.getPoolName());
            removeRemedyConnection(pconn);
            pconn.close();
            return;
        }

        //back pooling connection
        removeBusyConnection(pconn);
        long now = System.currentTimeMillis();
        if (pconn.getStartupTimeMs() + cf.getLifeCycleTime() < now) {//over the max lifecycle,kill it
            AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Info,
                    "DBPOOL:{}.close pooling connection which over the maxlife.startup:{},now :{},max life :{}.reuse :{}.",
                cf.getPoolName(), pconn.getStartupTimeMs(), now, cf.getLifeCycleTime(), pconn.getReuseTimes());
            pconn.close();
            return;
        }
        if (pconn.isValid()) {
            AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Info,
                    "DBPOOL -> :{}.back pooling connection.startup -> :{},now -> :{},max life -> :{}.reuse -> :{}.",
                cf.getPoolName(), pconn.getStartupTimeMs(), now, cf.getLifeCycleTime(), pconn.getReuseTimes());
            pconn.setSessionId(null); // cleanup last sessionid
            pushFreeConnection(pconn);
        } else {
            AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Info,
                    "DBPOOL -> :{}.close pooling connection which valid is false.startup -> :{},now -> :{},max life -> :{}.reuse -> :{}.",
                cf.getPoolName(), pconn.getStartupTimeMs(), now, cf.getLifeCycleTime(), pconn.getReuseTimes());
            pconn.close();
        }
        this.notifyAll(); // keep wakeup sleep thread
    }

    @Override
    public synchronized void destroy(String sessionId) {
        AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Info,
                "DBPOOL destory the dbpool -> :{}.", this.getPoolName());
        synchronized (freeConnections) {
            for (IPoolingConnection pconn : this.freeConnections) {
                try {
                    if (pconn.isValid()) {
                        pconn.close();
                    }
                } catch (SQLException e) {
                    AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Error,e,
                            "SpxDBPool destroy SQLException  ");
                }
            }
            this.freeConnections.clear();
        }
        synchronized (busyConnections) {
            for (IPoolingConnection pconn : this.busyConnections) {
                try {
                    if (pconn.isValid()) {
                        pconn.close();
                    }
                } catch (SQLException e) {
                    AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Error,e,
                            "SpxDBPool destroy SQLException  ");
                }
            }
            this.busyConnections.clear();
        }
        synchronized (remebyConnections) {
            for (IPoolingConnection pconn : this.remebyConnections) {
                try {
                    if (pconn.isValid()) {
                        pconn.close();
                    }
                } catch (SQLException e) {
                    AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Error,e,
                            "SpxDBPool destroy SQLException  ");
                }
            }
            this.remebyConnections.clear();
        }
        this.isActive = false;
    }

    private void regeditCleanupTask(String sessionId) {
        AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Info,
                "DBPOOL regedit cleanup task for dbpool -> :{}.which startup every millisecond -> :{}.",
            this.getPoolName(), cf.getCleanupTimestampMs());
        new cleanupTask(this).start();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    class cleanupTask extends Thread {
        private SpxDBPool pool = null;

        public cleanupTask(SpxDBPool pool) {
            this.pool = pool;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(cf.getCleanupTimestampMs());
                } catch (InterruptedException e) {
                    AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,e,
                            "SpxDBPool run InterruptedException  ");
                }

                try {
                    AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,
                            "DBPOOL cleanup task is wakeup. pool -> :{},current state : busy -> :{},free -> :{},remedy -> :{}.. ",
                        pool.getPoolName(), pool.getBusyCount(), pool.getFreeCount(), pool.getRemedyCount());
                    long now = System.currentTimeMillis();
                    synchronized (busyConnections) {
                        for (IPoolingConnection pconn : busyConnections) {
                            try {
                                if (pconn.getLastUsedTimeMs() + cf.getMaxRequestTimeMs() < now) { // exec timeout
                                    AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,
                                            "DBPOOL -> :{} Cleanup Task. busy connection is request timeout,close it force.request time -> :{},now -> :{},timeout ->:{}.",
                                        pool.getPoolName(), pconn.getLastUsedTimeMs(), now, cf.getMaxRequestTimeMs());
                                    removeBusyConnection(pconn);
                                    pconn.close();
                                }
                            } catch (SQLException e) {
                                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,e,
                                        "SpxDBPool run SQLException  ");
                            }
                        }
                    }

                    synchronized (freeConnections) {
                        for (IPoolingConnection pconn : freeConnections) {
                            try {
                                if (pconn.getLastUsedTimeMs() + cf.getWaitInFreePoolMs() < now) { // free timeout
                                    AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,
                                            "DBPOOL -> :{} Cleanup Task.free connection is timeout,close it force.last used time -> :{},now -> :{},timeout -> :{}.",
                                        pool.getPoolName(), pconn.getLastUsedTimeMs(), now, cf.getWaitInFreePoolMs());
                                    removeFreeConnection(pconn);
                                    pconn.close();
                                }
                            } catch (SQLException e) {
                                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,e,
                                        "SpxDBPool run SQLException  ");
                            }
                        }
                    }

                    synchronized (remebyConnections) {
                        for (IPoolingConnection pconn : remebyConnections) {
                            try {
                                if (pconn.getLastUsedTimeMs() + cf.getMaxRequestTimeMs() < now) { // exec timeout
                                    AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,
                                            "DBPOOL -> :{} Cleanup Task. remedy connection is request timeout,close it force.begin time -> :{},now -> :{},timeout ->:{}.",
                                        pool.getPoolName(), pconn.getLastUsedTimeMs(), now, cf.getMaxRequestTimeMs());
                                    removeRemedyConnection(pconn);
                                    pconn.close();
                                }
                            } catch (SQLException e) {
                                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,e,
                                        "SpxDBPool run SQLException  ");
                            }
                        }
                    }


                    int currConnsCount = freeConnections.size() + busyConnections.size();//maybe not thread safe but soso
                    if (currConnsCount < cf.getMinConnections()) {
                        int sub = cf.getMinConnections() - currConnsCount;
                        for (int i = 0; i < sub; i++) {
                            try {
                                IPoolingConnection pconn = pool.newConnection(true);
                                pushFreeConnection(pconn);
                            } catch (SQLException e) {
                                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,e,
                                        "SpxDBPool run SQLException  ");
                            }
                        }
                    }
                } catch (Throwable t) {

                }
            }
        }
    }

}
