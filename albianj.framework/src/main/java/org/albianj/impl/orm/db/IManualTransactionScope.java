package org.albianj.impl.orm.db;


import org.albianj.orm.context.IManualContext;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public interface IManualTransactionScope {

    public boolean execute(IManualContext mctx) ;

}
