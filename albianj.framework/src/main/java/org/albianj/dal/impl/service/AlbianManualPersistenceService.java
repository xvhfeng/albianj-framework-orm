package org.albianj.dal.impl.service;

import org.albianj.dal.api.context.ManualCtx;
import org.albianj.dal.impl.db.IManualCommandAdapter;
import org.albianj.dal.impl.db.IManualTransactionScope;
import org.albianj.dal.impl.db.ManualCommandAdapter;
import org.albianj.dal.impl.db.ManualTransactionScope;
import org.albianj.kernel.api.anno.serv.AblServiceRant;
import org.albianj.kernel.api.service.FreeAlbianService;
import org.albianj.dal.api.context.ManualCmd;
import org.albianj.dal.api.service.IAlbianManualPersistenceService;

import java.util.List;
import java.util.Vector;


/**
 * Created by xuhaifeng on 17/8/17.
 */
@AblServiceRant(Id = IAlbianManualPersistenceService.Name, Interface = IAlbianManualPersistenceService.class)
public class AlbianManualPersistenceService extends FreeAlbianService implements IAlbianManualPersistenceService {

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
