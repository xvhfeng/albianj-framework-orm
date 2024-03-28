package org.albianj.dal.impl.db;

import org.albianj.AblThrowable;
import org.albianj.ServRouter;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.dal.api.context.ManualCtx;
import org.albianj.dal.api.db.SqlPara;
import org.albianj.dal.api.object.StgAttr;
import org.albianj.dal.impl.toolkit.SetConv;
import org.albianj.kernel.api.logger.LogLevel;
import org.albianj.dal.api.context.ItlManualCmd;
import org.albianj.dal.api.context.ManualCmd;
import org.albianj.dal.api.object.RStgAttr;
import org.albianj.dal.api.service.IAlbianStorageParserService;

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


    protected void preExecute(ManualCtx mctx)  {
        List<ManualCmd> mcs = mctx.getCommands();
        List<ItlManualCmd> imcs = mctx.getInternalCommands();
        IAlbianStorageParserService asps = ServRouter.getService(mctx.getSessionId(),IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        StgAttr storage = asps.getStorageAttribute(mctx.getStorageName());
        if (StringsUtil.isNullEmptyTrimmed(mctx.getDatabaseName())) {
            mctx.setDatabaseName(storage.getDatabase());
        }
        RStgAttr rsa = new RStgAttr(storage, mctx.getDatabaseName());
        mctx.setRStgAttr(rsa);
        Connection conn = asps.getConnection(mctx.getSessionId(), rsa,false);
        mctx.setConnection(conn);

        List<Statement> statements = new Vector<Statement>();
        try {
            for (ItlManualCmd imc : imcs) {
                PreparedStatement prepareStatement =
                        conn.prepareStatement(imc.getSqlText());
                Map<Integer, String> map = imc.getParameterMapper();
                if (SetUtil.isNullOrEmpty(map)) {
                    continue;
                } else {
                    for (int i = 1; i <= map.size(); i++) {
                        String paraName = map.get(i);
                        SqlPara para = imc.getCommandParameters().get(paraName);
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


    protected void executeHandler(ManualCtx mctx)  {

            List<Integer> rcs = new Vector<>();
            List<Statement> statements = mctx.getStatements();
            List<ManualCmd> cmds = mctx.getCommands();
            for (int i = 0; i < statements.size(); i++) {
                try {
                    ManualCmd cmd = cmds.get(i);
                    ServRouter.log(mctx.getSessionId(),  LogLevel.Info,
                            "storage:{},sqltext:{},parars:{}", mctx.getStorageName(), cmd.getCommandText(),
                        SetConv.toString(cmd.getCommandParameters()));
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

    protected void commit(ManualCtx mctx)  {
        try {
            mctx.getConnection().commit();
        } catch (SQLException e) {
            ServRouter.logAndThrowAgain(mctx.getSessionId(),LogLevel.Error,e,
                    "commit manual command to storage: {} dstabase:{} is fail.",
                    mctx.getStorageName(), mctx.getDatabaseName() );
        }
    }


    protected void exceptionHandler(ManualCtx mctx)  {
        try {
            mctx.getConnection().rollback();
        } catch (SQLException e) {
            ServRouter.logAndThrowAgain(mctx.getSessionId(),LogLevel.Error,e,
                    "rollback manual command to storage: {} dstabase:{} is fail.",
                    mctx.getStorageName(), mctx.getDatabaseName() );
        }
    }


    protected void unLoadExecute(ManualCtx mctx)  {
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
