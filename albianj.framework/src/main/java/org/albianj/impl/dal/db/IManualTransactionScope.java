package org.albianj.impl.dal.db;


import org.albianj.dal.context.ManualContext;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public interface IManualTransactionScope {

    public boolean execute(ManualContext mctx) ;

}
