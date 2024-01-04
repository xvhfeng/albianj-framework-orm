package org.albianj.orm.impl.scope;


import org.albianj.kernel.itf.builtin.logger.LogLevel;
import org.albianj.kernel.itf.builtin.logger.LogTarget;
import org.albianj.kernel.itf.service.AlbianServRouter;
import org.albianj.orm.ctx.WriterJobLifeTime;
import org.albianj.orm.ctx.ManualContext;

/**
 * Created by xuhaifeng on 17/9/1.
 */
public abstract class FreeManualTransactionScope implements IManualTransactionScope {


    public boolean execute(ManualContext mctx)  {
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
            AlbianServRouter.log(AlbianServRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,e,
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
                            AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,exc,
                                    "auto rollback  the manual command is fail.");
                        }
                        mctx.setLifeTime(WriterJobLifeTime.Rollbacked);
                        break;
                    }
                    default:
                        break;
                }

            } catch (Exception exc) {
                AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e,
                        "rollback the query the manual command is fail.");
            }
        } finally {
            try {
                unLoadExecute(mctx);
            } catch (Exception exc) {
                AlbianServRouter.log(AlbianServRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,exc,
                        "unload the manual command is fail.");
            }

        }

        return isSuccess;
    }


    protected abstract void preExecute(ManualContext mctx) ;

    protected abstract void executeHandler(ManualContext mctx) ;

    protected abstract void commit(ManualContext mctx) ;

    protected abstract void exceptionHandler(ManualContext mctx) ;

    protected abstract void unLoadExecute(ManualContext mctx) ;


}
