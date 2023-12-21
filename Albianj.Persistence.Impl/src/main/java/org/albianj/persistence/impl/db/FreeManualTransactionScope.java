package org.albianj.persistence.impl.db;

import org.albianj.logger.LogLevel;
import org.albianj.logger.LogTarget;
import org.albianj.persistence.context.IManualContext;
import org.albianj.persistence.context.WriterJobLifeTime;
import org.albianj.service.AlbianServiceRouter;

/**
 * Created by xuhaifeng on 17/9/1.
 */
public abstract class FreeManualTransactionScope implements IManualTransactionScope {


    public boolean execute(IManualContext mctx) throws Throwable {
        boolean isSuccess = true;
        try {
            mctx.setLifeTime(WriterJobLifeTime.NoStarted);
            this.preExecute(mctx);
            mctx.setLifeTime(WriterJobLifeTime.Opened);
            this.executeHandler(mctx);
            mctx.setLifeTime(WriterJobLifeTime.Runned);
            this.commit(mctx);
            mctx.setLifeTime(WriterJobLifeTime.Commited);
        } catch (Exception e) {
            isSuccess = false;
            AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Sql, LogLevel.Error,e,
                    "Execute the manual command is fail.");
            try {
                switch (mctx.getLifeTime()) {
                    case Opened:
                    case Opening: {
                        break;
                    }
                    case Running:
                    case Runned:
                    case Commiting:
                    case Commited: {
                        // commited then manua rollback the data by albian
                        // and it can not keep the data consistency
                        mctx.setLifeTime(WriterJobLifeTime.AutoRollbacking);
                        try {
                            this.exceptionHandler(mctx);
                        } catch (Exception exc) {
                            AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Error,exc,
                                    "auto rollback  the manual command is fail.");
                        }
                        mctx.setLifeTime(WriterJobLifeTime.Rollbacked);
                        break;
                    }
                    default:
                        break;
                }

            } catch (Exception exc) {
                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Error,e,
                        "rollback the query the manual command is fail.");
            }
        } finally {
            try {
                unLoadExecute(mctx);
            } catch (Exception exc) {
                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId,LogTarget.Sql,LogLevel.Error,exc,
                        "unload the manual command is fail.");
            }

        }

        return isSuccess;
    }


    protected abstract void preExecute(IManualContext mctx) throws Throwable;

    protected abstract void executeHandler(IManualContext mctx) throws Throwable;

    protected abstract void commit(IManualContext mctx) throws Throwable;

    protected abstract void exceptionHandler(IManualContext mctx) throws Throwable;

    protected abstract void unLoadExecute(IManualContext mctx) throws Throwable;


}
