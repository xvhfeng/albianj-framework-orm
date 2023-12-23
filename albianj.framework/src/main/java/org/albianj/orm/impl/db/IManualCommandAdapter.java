package org.albianj.orm.impl.db;


import org.albianj.orm.impl.context.ManualContext;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public interface IManualCommandAdapter {

    ManualContext createManualCommands(ManualContext mctx);
}
