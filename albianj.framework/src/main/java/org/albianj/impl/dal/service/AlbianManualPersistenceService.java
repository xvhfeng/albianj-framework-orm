package org.albianj.impl.dal.service;

import org.albianj.api.dal.context.ManualCtx;
import org.albianj.api.dal.service.IAlbianManualPersistenceService;
import org.albianj.impl.dal.db.IManualCommandAdapter;
import org.albianj.impl.dal.db.IManualTransactionScope;
import org.albianj.impl.dal.db.ManualCommandAdapter;
import org.albianj.impl.dal.db.ManualTransactionScope;
import org.albianj.api.kernel.anno.serv.AblServRant;
import org.albianj.api.kernel.service.FreeAblServ;
import org.albianj.api.dal.context.ManualCmd;

import java.util.List;
import java.util.Vector;


/**
 * Created by xuhaifeng on 17/8/17.
 */
@AblServRant(Id = IAlbianManualPersistenceService.Name, Interface = IAlbianManualPersistenceService.class)
public class AlbianManualPersistenceService extends FreeAblServ implements IAlbianManualPersistenceService {

    public String getServiceName() {
        return Name;
    }


    public int execute(String sessionId, String storageName, String dbName, ManualCmd cmd)  {
        List<ManualCmd> mcs = new Vector<>();
        mcs.add(cmd);
        List<Integer> rcs = execute(sessionId, storageName, dbName, mcs);
        return rcs.get(0);
    }

    public int execute(String sessionId, String storageName, ManualCmd cmd)  {
        List<ManualCmd> mcs = new Vector<>();
        mcs.add(cmd);
        List<Integer> rcs = execute(sessionId, storageName, null, mcs);
        return rcs.get(0);
    }

    public List<Integer> execute(String sessionId, String storageName, String dbName, List<ManualCmd> cmds)  {

        ManualCtx mctx = new ManualCtx();


        mctx.setSessionId(sessionId);
        mctx.setCommands(cmds);

        IManualCommandAdapter mcd = new ManualCommandAdapter();
        mctx = mcd.createManualCommands(mctx);

        IManualTransactionScope mts = new ManualTransactionScope();
        mts.execute(mctx);
        return mctx.getResults();

    }

    public List<Integer> execute(String sessionId, String storageName, List<ManualCmd> cmds)  {

        return execute(sessionId, storageName, null, cmds);

    }

}
