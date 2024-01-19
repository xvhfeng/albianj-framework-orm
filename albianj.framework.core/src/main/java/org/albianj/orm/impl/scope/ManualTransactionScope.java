package org.albianj.orm.impl.scope;

import org.albianj.common.utils.CollectionUtil;
import org.albianj.AblThrowable;
import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.itf.builtin.logger.LogLevel;
import org.albianj.kernel.itf.builtin.logger.LogTarget;
import org.albianj.kernel.ServRouter;
import org.albianj.orm.ctx.InternalManualCommand;
import org.albianj.orm.ctx.ManualCommand;
import org.albianj.orm.ctx.ManualContext;
import org.albianj.orm.attr.StorageAttribute;
import org.albianj.orm.itf.db.SqlParameter;
import org.albianj.orm.utils.ListConvert;
import org.albianj.orm.attr.RunningStorageAttribute;
import org.albianj.orm.itf.service.IAlbianStorageParserService;

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
        List<ManualCommand> mcs = mctx.getCmds();
        List<InternalManualCommand> imcs = mctx.getInternalCmds();
        IAlbianStorageParserService asps = ServRouter.getService(mctx.getSessionId(),IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        StorageAttribute storage = asps.getStorageAttribute(mctx.getStorageName());
        if (StringsUtil.isNullOrEmptyOrAllSpace(mctx.getDbName())) {
            mctx.setDbName(storage.getDatabase());
        }
        RunningStorageAttribute rsa = new RunningStorageAttribute(storage, mctx.getDbName());
        mctx.setRunStgAttr(rsa);
        Connection conn = asps.getConnection(mctx.getSessionId(), rsa,false);
        mctx.setConn(conn);

        List<Statement> statements = new Vector<Statement>();
        try {
            for (InternalManualCommand imc : imcs) {
                PreparedStatement prepareStatement =
                        conn.prepareStatement(imc.getSqlText());
                Map<Integer, String> map = imc.getParameterMapper();
                if (CollectionUtil.isNullOrEmpty(map)) {
                    continue;
                } else {
                    for (int i = 1; i <= map.size(); i++) {
                        String paraName = map.get(i);
                        SqlParameter para = imc.getCommandParameters().get(paraName);
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
            ServRouter.logAndThrowAgain(mctx.getSessionId(), LogTarget.Running, LogLevel.Error,e,
                    "make manual sql command with mctx and the command is empty or null" );
        }
        mctx.setStatements(statements);
    }


    protected void executeHandler(ManualContext mctx)  {

            List<Integer> rcs = new Vector<>();
            List<Statement> statements = mctx.getStatements();
            List<ManualCommand> cmds = mctx.getCmds();
            for (int i = 0; i < statements.size(); i++) {
                try {
                    ManualCommand cmd = cmds.get(i);
                    ServRouter.log(mctx.getSessionId(), LogTarget.Running, LogLevel.Info,
                            "storage:{},sqltext:{},parars:{}", mctx.getStorageName(), cmd.getCmdText(),
                        ListConvert.toString(cmd.getCmdParameters()));
                    int rc = ((PreparedStatement)statements.get(i)).executeUpdate();
                    rcs.add(rc);
                } catch (SQLException e) {
                    ServRouter.logAndThrowAgain(mctx.getSessionId(),LogTarget.Running,LogLevel.Error,e,
                        "execute manual command to storage: {} dstabase:{} is fail.",
                                    mctx.getStorageName(), mctx.getDbName() );
                }
            }

            mctx.setRcs(rcs);

        return;
    }

    protected void commit(ManualContext mctx)  {
        try {
            mctx.getConn().commit();
        } catch (SQLException e) {
            ServRouter.logAndThrowAgain(mctx.getSessionId(),LogTarget.Running,LogLevel.Error,e,
                    "commit manual command to storage: {} dstabase:{} is fail.",
                    mctx.getStorageName(), mctx.getDbName() );
        }
    }


    protected void exceptionHandler(ManualContext mctx)  {
        try {
            mctx.getConn().rollback();
        } catch (SQLException e) {
            ServRouter.logAndThrowAgain(mctx.getSessionId(),LogTarget.Running,LogLevel.Error,e,
                    "rollback manual command to storage: {} dstabase:{} is fail.",
                    mctx.getStorageName(), mctx.getDbName() );
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
                    ServRouter.log(mctx.getSessionId(), LogTarget.Running, LogLevel.Error,e,
                            "clear the statement to storage:{} database:{} is fail.",
                            mctx.getStorageName(), mctx.getDbName());
                }
            }
            mctx.getConn().close();
        } catch (Exception exc) {
            isThrow = true;
            ServRouter.logAndThrowAgain(mctx.getSessionId(), LogTarget.Running, LogLevel.Error,exc,
                    "close the connect to storage:{} database:{} is fail.", mctx.getStorageName(),
                mctx.getDbName());
        }
        if (isThrow)
            throw new AblThrowable("there is error in the unload trancation scope.");
    }

}
