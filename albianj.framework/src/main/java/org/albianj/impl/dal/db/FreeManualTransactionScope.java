package org.albianj.impl.dal.db;


import org.albianj.ServRouter;
import org.albianj.api.dal.context.ManualCtx;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.api.dal.context.WrtLfcOpt;

/**
 * Created by xuhaifeng on 17/9/1.
 */
public abstract class FreeManualTransactionScope implements IManualTransactionScope {


    public boolean execute(ManualCtx mctx)  {
        boolean isSuccess = true;
        try {
            mctx.setLifeTime(WrtLfcOpt.NoStarted);
            this.preExecute(mctx);
            mctx.setLifeTime(WrtLfcOpt.Opened);
            this.executeHandler(mctx);
            mctx.setLifeTime(WrtLfcOpt.Runned);
            this.commit(mctx);
            mctx.setLifeTime(WrtLfcOpt.Commited);
        } catch (Exception e) {
            isSuccess = false;
            ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Error,e,
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
                        mctx.setLifeTime(WrtLfcOpt.AutoRollbacking);
                        try {
                            this.exceptionHandler(mctx);
                        } catch (Exception exc) {
                            ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,exc,
                                    "auto rollback  the manual command is fail.");
                        }
                        mctx.setLifeTime(WrtLfcOpt.Rollbacked);
                        break;
                    }
                    default:
                        break;
                }

            } catch (Exception exc) {
                ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,e,
                        "rollback the query the manual command is fail.");
            }
        } finally {
            try {
                unLoadExecute(mctx);
            } catch (Exception exc) {
                ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,exc,
                        "unload the manual command is fail.");
            }

        }

        return isSuccess;
    }


    protected abstract void preExecute(ManualCtx mctx) ;

    protected abstract void executeHandler(ManualCtx mctx) ;

    protected abstract void commit(ManualCtx mctx) ;

    protected abstract void exceptionHandler(ManualCtx mctx) ;

    protected abstract void unLoadExecute(ManualCtx mctx) ;


}
