package org.albianj.impl.dal.db;


import org.albianj.api.dal.context.ManualCtx;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public interface IManualTransactionScope {

    public boolean execute(ManualCtx mctx) ;

}
