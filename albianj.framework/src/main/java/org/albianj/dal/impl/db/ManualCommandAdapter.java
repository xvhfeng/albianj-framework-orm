package org.albianj.dal.impl.db;

import org.albianj.dal.api.context.ManualCtx;
import org.albianj.dal.api.context.ItlManualCmd;
import org.albianj.dal.api.context.ManualCmd;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public class ManualCommandAdapter implements IManualCommandAdapter {
    @Override
    public ManualCtx createManualCommands(ManualCtx mctx) {
        List<ManualCmd> cmds = mctx.getCommands();
        List<ItlManualCmd> imcs = new LinkedList<>();
        for (ManualCmd cmd : cmds) {
            ItlManualCmd imc = PersistenceNamedParameter.parseSql(cmd);
            imc.setCmdType(cmd.getCmdType());
            imc.setCommandParameters(cmd.getCommandParameters());
            imcs.add(imc);
        }
        mctx.setInternalCommands(imcs);
        return mctx;
    }
}
