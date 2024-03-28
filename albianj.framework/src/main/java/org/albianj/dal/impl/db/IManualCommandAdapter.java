package org.albianj.dal.impl.db;


import org.albianj.dal.api.context.ManualCtx;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public interface IManualCommandAdapter {

    ManualCtx createManualCommands(ManualCtx mctx);
}
