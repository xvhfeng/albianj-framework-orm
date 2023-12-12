package org.albianj.persistence.impl.db;

import org.albianj.persistence.context.IInternalManualCommand;
import org.albianj.persistence.context.IManualCommand;
import org.albianj.persistence.context.IManualContext;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.impl.toolkit.ListConvert;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.RunningStorageAttribute;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(ManualTransactionScope.class);

    protected void preExecute(IManualContext mctx) throws AlbianDataServiceException {
        List<IManualCommand> mcs = mctx.getCommands();
        List<IInternalManualCommand> imcs = mctx.getInternalCommands();
        IAlbianStorageParserService asps = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        IStorageAttribute storage = asps.getStorageAttribute(mctx.getStorageName());
        if (Validate.isNullOrEmptyOrAllSpace(mctx.getDatabaseName())) {
            mctx.setDatabaseName(storage.getDatabase());
        }
        IRunningStorageAttribute rsa = new RunningStorageAttribute(storage, mctx.getDatabaseName());
        mctx.setRunningStorage(rsa);
        Connection conn = asps.getConnection(mctx.getSessionId(), rsa,false);
        mctx.setConnection(conn);

        List<Statement> statements = new Vector<Statement>();
        try {
            for (IInternalManualCommand imc : imcs) {
                PreparedStatement prepareStatement =
                        conn.prepareStatement(imc.getSqlText());
                Map<Integer, String> map = imc.getParameterMapper();
                if (Validate.isNullOrEmpty(map)) {
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
            throw new AlbianDataServiceException("make manual sql command with mctx and the command is empty or null",
                e);
        }
        mctx.setStatements(statements);
    }


    protected void executeHandler(IManualContext mctx) throws AlbianDataServiceException {
        try {

            List<Integer> rcs = new Vector<>();
            List<Statement> statements = mctx.getStatements();
            List<IManualCommand> cmds = mctx.getCommands();
            for (int i = 0; i < statements.size(); i++) {
                try {
                    IManualCommand cmd = cmds.get(i);
                    logger.info("storage:{},sqltext:{},parars:{}", mctx.getStorageName(), cmd.getCommandText(),
                        ListConvert.toString(cmd.getCommandParameters()));
                    int rc = ((PreparedStatement)statements.get(i)).executeUpdate();
                    rcs.add(rc);
                } catch (SQLException e) {
                    throw new AlbianDataServiceException(
                        "execute manual command to storage:" + mctx.getStorageName() + " dtabase:" + mctx
                            .getDatabaseName() + " is fail.", e);
                }
            }

            mctx.setResults(rcs);

        } catch (Exception e) {

        }
        return;
    }

    protected void commit(IManualContext mctx) throws AlbianDataServiceException {
        try {
            mctx.getConnection().commit();
        } catch (SQLException e) {
            throw new AlbianDataServiceException(
                "commit manual command to storage:" + mctx.getStorageName() + " database:" + mctx.getDatabaseName()
                    + " is fail.", e);
        }
    }


    protected void exceptionHandler(IManualContext mctx) throws AlbianDataServiceException {
        try {
            mctx.getConnection().rollback();
        } catch (SQLException e) {
            throw new AlbianDataServiceException(
                "rollback manual command to storage:" + mctx.getStorageName() + " database:" + mctx.getDatabaseName()
                    + " is fail.", e);
        }
    }


    protected void unLoadExecute(IManualContext mctx) throws AlbianDataServiceException {
        boolean isThrow = false;
        try {
            List<Statement> statements = mctx.getStatements();
            for (Statement statement : statements) {
                try {
                    ((PreparedStatement) statement).clearParameters();
                    statement.close();
                } catch (Exception e) {
                    isThrow = true;
                    logger.error("clear the statement to storage:{} database:{} is fail.", mctx.getStorageName(),
                        mctx.getDatabaseName(), e);
                }
            }
            mctx.getConnection().close();
        } catch (Exception exc) {
            isThrow = true;
            logger.error("close the connect to storage:{} database:{} is fail.", mctx.getStorageName(),
                mctx.getDatabaseName(), exc);
        }
        if (isThrow)
            throw new AlbianDataServiceException("there is error in the unload trancation scope.");
    }

}
