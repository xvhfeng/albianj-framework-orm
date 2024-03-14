package org.albianj.impl.orm.db;


import org.albianj.impl.orm.context.ManualContext;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public interface IManualCommandAdapter {

    ManualContext createManualCommands(ManualContext mctx);
}
