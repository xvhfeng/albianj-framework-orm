package org.albianj.orm.impl.adapter.cmd;

import org.albianj.orm.ctx.InternalManualCommand;
import org.albianj.orm.ctx.ManualCommand;
import org.albianj.orm.ctx.ManualContext;
import org.albianj.orm.impl.scope.IManualCommandAdapter;
import org.albianj.orm.utils.PersistenceNamedParameter;

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
