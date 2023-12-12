package org.albianj.persistence.impl.db;

import org.albianj.persistence.context.IManualContext;
import org.albianj.persistence.context.WriterJobLifeTime;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xuhaifeng on 17/9/1.
 */
public abstract class FreeManualTransactionScope implements IManualTransactionScope {

    private static final Logger logger = LoggerFactory.getLogger(FreeManualTransactionScope.class);

    public boolean execute(IManualContext mctx) {
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
            logger.error("Execute the manual command is fail.", e);
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
                            logger.error("auto rollback  the manual command is fail.", exc);
                        }
                        mctx.setLifeTime(WriterJobLifeTime.Rollbacked);
                        break;
                    }
                    default:
                        break;
                }

            } catch (Exception exc) {
                logger.error("rollback the query the manual command is fail.", exc);
            }
        } finally {
            try {
                unLoadExecute(mctx);
            } catch (Exception exc) {
                logger.error("unload the manual command is fail.", exc);
            }

        }

        return isSuccess;
    }


    protected abstract void preExecute(IManualContext mctx) throws AlbianDataServiceException;

    protected abstract void executeHandler(IManualContext mctx) throws AlbianDataServiceException;

    protected abstract void commit(IManualContext mctx) throws AlbianDataServiceException;

    protected abstract void exceptionHandler(IManualContext mctx) throws AlbianDataServiceException;

    protected abstract void unLoadExecute(IManualContext mctx) throws AlbianDataServiceException;


}
