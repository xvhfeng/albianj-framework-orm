package org.albianj.impl.dal.db;


import org.albianj.api.dal.context.ManualContext;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public interface IManualCommandAdapter {

    ManualContext createManualCommands(ManualContext mctx);
}
