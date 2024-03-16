package org.albianj.impl.dal.db;

import org.albianj.dal.context.ManualContext;
import org.albianj.dal.context.InternalManualCommand;
import org.albianj.dal.context.ManualCommand;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public class ManualCommandAdapter implements IManualCommandAdapter {
    @Override
    public ManualContext createManualCommands(ManualContext mctx) {
        List<ManualCommand> cmds = mctx.getCommands();
        List<InternalManualCommand> imcs = new LinkedList<>();
        for (ManualCommand cmd : cmds) {
            InternalManualCommand imc = PersistenceNamedParameter.parseSql(cmd);
            imc.setCmdType(cmd.getCmdType());
            imc.setCommandParameters(cmd.getCommandParameters());
            imcs.add(imc);
        }
        mctx.setInternalCommands(imcs);
        return mctx;
    }
}
