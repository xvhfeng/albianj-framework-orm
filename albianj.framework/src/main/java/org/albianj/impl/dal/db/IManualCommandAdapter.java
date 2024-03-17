package org.albianj.impl.dal.db;


import org.albianj.api.dal.context.ManualCtx;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public interface IManualCommandAdapter {

    ManualCtx createManualCommands(ManualCtx mctx);
}
