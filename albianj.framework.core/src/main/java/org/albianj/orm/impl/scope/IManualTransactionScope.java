package org.albianj.orm.impl.scope;


import org.albianj.orm.ctx.ManualContext;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public interface IManualTransactionScope {

    public boolean execute(ManualContext mctx) ;

}
