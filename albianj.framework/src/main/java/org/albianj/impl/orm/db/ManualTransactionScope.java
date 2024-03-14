package org.albianj.impl.orm.db;

import org.albianj.AblThrowable;
import org.albianj.ServRouter;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.impl.orm.context.ManualContext;
import org.albianj.impl.orm.toolkit.ListConvert;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.orm.context.InternalManualCommand;
import org.albianj.orm.context.ManualCommand;
import org.albianj.orm.db.ISqlParameter;
import org.albianj.orm.object.IRunningStorageAttribute;
import org.albianj.orm.object.IStorageAttribute;
import org.albianj.orm.object.RunningStorageAttribute;
import org.albianj.orm.service.IAlbianStorageParserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public class ManualTransactionScope extends FreeManualTransactionScope {


    protected void preExecute(ManualContext mctx)  {
        List<ManualCommand> mcs = mctx.getCommands();
        List<InternalManualCommand> imcs = mctx.getInternalCommands();
        IAlbianStorageParserService asps = ServRouter.getService(mctx.getSessionId(),IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        IStorageAttribute storage = asps.getStorageAttribute(mctx.getStorageName());
        if (StringsUtil.isNullOrEmptyOrAllSpace(mctx.getDatabaseName())) {
            mctx.setDatabaseName(storage.getDatabase());
        }
        IRunningStorageAttribute rsa = new RunningStorageAttribute(storage, mctx.getDatabaseName());
        mctx.setRunningStorageAttribute(rsa);
        Connection conn = asps.getConnection(mctx.getSessionId(), rsa,false);
        mctx.setConnection(conn);

        List<Statement> statements = new Vector<Statement>();
        try {
            for (InternalManualCommand imc : imcs) {
                PreparedStatement prepareStatement =
                        conn.prepareStatement(imc.getSqlText());
                Map<Integer, String> map = imc.getParameterMapper();
                if (SetUtil.isNullOrEmpty(map)) {
                    continue;
                } else {
                    for (int i = 1; i <= map.size(); i++) {
                        String paraName = map.get(i);
                        ISqlParameter para = imc.getCommandParameters().get(paraName);
                        if (null == para.getValue()) {
                            prepareStatement.setNull(i, para.getSqlType());
                        } else {
                            prepareStatement.setObject(i, para.getValue(),
                                    para.getSqlType());
                        }
                    }
                }
                statements.add(prepareStatement);
            }
        } catch (SQLException e) {
            ServRouter.logAndThrowAgain(mctx.getSessionId(),  LogLevel.Error,e,
                    "make manual sql command with mctx and the command is empty or null" );
        }
        mctx.setStatements(statements);
    }


    protected void executeHandler(ManualContext mctx)  {

            List<Integer> rcs = new Vector<>();
            List<Statement> statements = mctx.getStatements();
            List<ManualCommand> cmds = mctx.getCommands();
            for (int i = 0; i < statements.size(); i++) {
                try {
                    ManualCommand cmd = cmds.get(i);
                    ServRouter.log(mctx.getSessionId(),  LogLevel.Info,
                            "storage:{},sqltext:{},parars:{}", mctx.getStorageName(), cmd.getCommandText(),
                        ListConvert.toString(cmd.getCommandParameters()));
                    int rc = ((PreparedStatement)statements.get(i)).executeUpdate();
                    rcs.add(rc);
                } catch (SQLException e) {
                    ServRouter.logAndThrowAgain(mctx.getSessionId(),LogLevel.Error,e,
                        "execute manual command to storage: {} dstabase:{} is fail.",
                                    mctx.getStorageName(), mctx.getDatabaseName() );
                }
            }

            mctx.setResults(rcs);

        return;
    }

    protected void commit(ManualContext mctx)  {
        try {
            mctx.getConnection().commit();
        } catch (SQLException e) {
            ServRouter.logAndThrowAgain(mctx.getSessionId(),LogLevel.Error,e,
                    "commit manual command to storage: {} dstabase:{} is fail.",
                    mctx.getStorageName(), mctx.getDatabaseName() );
        }
    }


    protected void exceptionHandler(ManualContext mctx)  {
        try {
            mctx.getConnection().rollback();
        } catch (SQLException e) {
            ServRouter.logAndThrowAgain(mctx.getSessionId(),LogLevel.Error,e,
                    "rollback manual command to storage: {} dstabase:{} is fail.",
                    mctx.getStorageName(), mctx.getDatabaseName() );
        }
    }


    protected void unLoadExecute(ManualContext mctx)  {
        boolean isThrow = false;
        try {
            List<Statement> statements = mctx.getStatements();
            for (Statement statement : statements) {
                try {
                    ((PreparedStatement) statement).clearParameters();
                    statement.close();
                } catch (Exception e) {
                    isThrow = true;
                    ServRouter.log(mctx.getSessionId(),  LogLevel.Error,e,
                            "clear the statement to storage:{} database:{} is fail.",
                            mctx.getStorageName(), mctx.getDatabaseName());
                }
            }
            mctx.getConnection().close();
        } catch (Exception exc) {
            isThrow = true;
            ServRouter.logAndThrowAgain(mctx.getSessionId(),  LogLevel.Error,exc,
                    "close the connect to storage:{} database:{} is fail.", mctx.getStorageName(),
                mctx.getDatabaseName());
        }
        if (isThrow)
            throw new AblThrowable("there is error in the unload trancation scope.");
    }

}