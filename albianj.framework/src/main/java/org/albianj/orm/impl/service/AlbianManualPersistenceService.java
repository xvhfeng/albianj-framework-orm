package org.albianj.orm.impl.service;

import org.albianj.kernel.anno.AlbianServiceRant;
import org.albianj.kernel.service.FreeAlbianService;
import org.albianj.orm.context.ManualCommand;
import org.albianj.orm.impl.context.ManualContext;
import org.albianj.orm.impl.db.IManualCommandAdapter;
import org.albianj.orm.impl.db.IManualTransactionScope;
import org.albianj.orm.impl.db.ManualCommandAdapter;
import org.albianj.orm.impl.db.ManualTransactionScope;
import org.albianj.orm.service.IAlbianManualPersistenceService;

import java.util.List;
import java.util.Vector;


/**
 * Created by xuhaifeng on 17/8/17.
 */
@AlbianServiceRant(Id = IAlbianManualPersistenceService.Name, Interface = IAlbianManualPersistenceService.class)
public class AlbianManualPersistenceService extends FreeAlbianService implements IAlbianManualPersistenceService {

    public String getServiceName() {
        return Name;
    }


    public int execute(String sessionId, String storageName, String dbName, ManualCommand cmd)  {
        List<ManualCommand> mcs = new Vector<>();
        mcs.add(cmd);
        List<Integer> rcs = execute(sessionId, storageName, dbName, mcs);
        return rcs.get(0);
    }

    public int execute(String sessionId, String storageName, ManualCommand cmd)  {
        List<ManualCommand> mcs = new Vector<>();
        mcs.add(cmd);
        List<Integer> rcs = execute(sessionId, storageName, null, mcs);
        return rcs.get(0);
    }

    public List<Integer> execute(String sessionId, String storageName, String dbName, List<ManualCommand> cmds)  {

        ManualContext mctx = new ManualContext();


        mctx.setSessionId(sessionId);
        mctx.setCmds(cmds);

        IManualCommandAdapter mcd = new ManualCommandAdapter();
        mctx = mcd.createManualCommands(mctx);

        IManualTransactionScope mts = new ManualTransactionScope();
        mts.execute(mctx);
        return mctx.getRcs();

    }

    public List<Integer> execute(String sessionId, String storageName, List<ManualCommand> cmds)  {

        return execute(sessionId, storageName, null, cmds);

    }

}
