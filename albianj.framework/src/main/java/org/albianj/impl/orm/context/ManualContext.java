package org.albianj.impl.orm.context;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.orm.context.InternalManualCommand;
import org.albianj.orm.context.ManualCommand;
import org.albianj.orm.context.WriterJobLifeTime;
import org.albianj.orm.object.RunningStorageAttribute;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * Created by xuhaifeng on 17/8/31.
 */
@Data
@NoArgsConstructor
public class ManualContext {

    private String sessionId;
    private List<ManualCommand> commands;
    private List<InternalManualCommand> internalCommands;
    private Connection connection;
    private List<Statement> statements;
    private String storageName;
    private RunningStorageAttribute runningStorageAttribute;
    private String databaseName;
    private List<Integer> results;
    private WriterJobLifeTime lifeTime = WriterJobLifeTime.Normal;

//    public String getSessionId() {
//        return this.sessionId;
//    }
//
//    public void setSessionId(String sessionId) {
//        this.sessionId = sessionId;
//    }
//
//    public List<IManualCommand> getCommands() {
//        return this.cmds;
//    }
//
//    public void setCommands(List<IManualCommand> cmds) {
//        this.cmds = cmds;
//    }
//
//    public List<IInternalManualCommand> getInternalCommands() {
//        return this.internalCmds;
//    }
//
//    public void setInternelCommands(List<IInternalManualCommand> cmds) {
//        this.internalCmds = cmds;
//    }
//
//    public Connection getConnection() {
//        return this.conn;
//    }
//
//    public void setConnection(Connection connection) {
//        this.conn = connection;
//    }
//
//    public List<Statement> getStatements() {
//        return this.statements;
//    }
//
//    public void setStatements(List<Statement> statements) {
//        this.statements = statements;
//    }
//
//    public String getStorageName() {
//        return this.storageName;
//    }
//
//    public void setStorageName(String storageName) {
//        this.storageName = storageName;
//    }
//
//    public IRunningStorageAttribute getRunningStorage() {
//        return this.rsa;
//    }
//
//    public void setRunningStorage(IRunningStorageAttribute rsa) {
//        this.rsa = rsa;
//    }
//
//    public String getDatabaseName() {
//        return this.dbName;
//    }
//
//    public void setDatabaseName(String dbName) {
//        this.dbName = dbName;
//    }
//
//    public List<Integer> getResults() {
//        return this.rcs;
//    }
//
//    public void setResults(List<Integer> rcs) {
//        this.rcs = rcs;
//    }
//
//    /**
//     * 得到写操作的生命周期
//     *
//     * @return
//     */
//    public WriterJobLifeTime getLifeTime() {
//        return this.lifeTime;
//    }
//
//    /**
//     * 设置写操作事务的生命周期
//     *
//     * @param lifeTime
//     */
//    public void setLifeTime(WriterJobLifeTime lifeTime) {
//        this.lifeTime = lifeTime;
//    }
}
