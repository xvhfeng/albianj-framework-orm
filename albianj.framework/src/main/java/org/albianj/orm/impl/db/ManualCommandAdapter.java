package org.albianj.orm.impl.db;

import org.albianj.orm.kit.context.InternalManualCommand;
import org.albianj.orm.kit.context.ManualCommand;
import org.albianj.orm.impl.context.ManualContext;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public class ManualCommandAdapter implements IManualCommandAdapter {
    @Override
    public ManualContext createManualCommands(ManualContext mctx) {
        List<ManualCommand> cmds = mctx.getCmds();
        List<InternalManualCommand> imcs = new LinkedList<>();
        for (ManualCommand cmd : cmds) {
            InternalManualCommand imc = PersistenceNamedParameter.parseSql(cmd);
            imc.setCmdType(cmd.getCmdType());
            imc.setCommandParameters(cmd.getCmdParameters());
            imcs.add(imc);
        }
        mctx.setInternalCmds(imcs);
        return mctx;
    }
}
