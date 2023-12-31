package org.albianj.orm.impl.context.dactx;

import org.albianj.common.utils.CheckUtil;
import org.albianj.kernel.AlbianRuntimeException;
import org.albianj.orm.context.IPersistenceCompensateNotify;
import org.albianj.orm.context.IPersistenceNotify;
import org.albianj.orm.context.dactx.AlbianObjectWarp;
import org.albianj.orm.context.dactx.IDataAccessContext;
import org.albianj.orm.impl.context.WriterJob;
import org.albianj.orm.impl.context.WriterJobAdapter;
import org.albianj.orm.impl.db.IPersistenceTransactionClusterScope;
import org.albianj.orm.impl.db.PersistenceTransactionClusterScope;
import org.albianj.orm.object.IAlbianObject;

import java.util.ArrayList;
import java.util.List;

public class DataAccessContext implements IDataAccessContext {
    List<AlbianObjectWarp> entitis = null;
    private boolean isSetQueryIdentity = false;
    private IPersistenceNotify notifyCallback;
    private Object notifyCallbackObject;
    private IPersistenceCompensateNotify compensateCallback;
    private Object compensateCallbackObject;
    private boolean rbkOnErr = false;

    public DataAccessContext() {
        entitis = new ArrayList<>();
    }

     @Override
     public IDataAccessContext addList(int opt, List<? extends IAlbianObject> entity) {
        entity.forEach(e -> {
            AlbianObjectWarp warp = new AlbianObjectWarp();
            warp.setEntry(e);
            warp.setPersistenceOpt(opt);
            entitis.add(warp);
        });

        return this;
    }

    @Override
    public IDataAccessContext addList(int opt, List<? extends IAlbianObject> entity, String storageAlias) {
         entity.forEach(e -> {
             AlbianObjectWarp warp = new AlbianObjectWarp();
            warp.setEntry(e);
            warp.setPersistenceOpt(opt);
             warp.setStorageAliasName(storageAlias);
            entitis.add(warp);
        });
        return this;
    }

    @Override
    public IDataAccessContext addList(int opt, List<? extends IAlbianObject> entity, String storageAlias, String tableAlias) {
        entity.forEach(e -> {
            AlbianObjectWarp warp = new AlbianObjectWarp();
            warp.setEntry(e);
            warp.setPersistenceOpt(opt);
             warp.setStorageAliasName(storageAlias);
             warp.setTableAliasName(tableAlias);
            entitis.add(warp);
        });
        return this;
    }

    @Override
    public IDataAccessContext add(int opt, IAlbianObject entity) {
        AlbianObjectWarp warp = new AlbianObjectWarp();
        warp.setEntry(entity);
        warp.setPersistenceOpt(opt);
        entitis.add(warp);
        return this;
    }

    @Override
    public IDataAccessContext add(int opt, IAlbianObject entity, String storageAlias) {
        AlbianObjectWarp warp = new AlbianObjectWarp();
        warp.setEntry(entity);
        warp.setPersistenceOpt(opt);
        warp.setStorageAliasName(storageAlias);
        entitis.add(warp);
        return this;

    }

    @Override
    public IDataAccessContext add(int opt, IAlbianObject entity, String storageAlias, String tableAlias) {
        AlbianObjectWarp warp = new AlbianObjectWarp();
        warp.setEntry(entity);
        warp.setPersistenceOpt(opt);
        warp.setStorageAliasName(storageAlias);
        warp.setTableAliasName(tableAlias);
        entitis.add(warp);
        return this;

    }

    public IDataAccessContext withQueryGenKey() {
        if (this.isSetQueryIdentity) {
            throw new AlbianRuntimeException("da-ctx exist query auto genkey");
        }
        entitis.get(entitis.size() - 1).setQueryIdentitry(true);
        this.isSetQueryIdentity = true;
        return this;
    }

    public IDataAccessContext setFinishNotify(IPersistenceNotify notifyCallback, Object notifyCallbackObject) {
        this.notifyCallback = notifyCallback;
        this.notifyCallbackObject = notifyCallbackObject;
        return this;
    }


    public IDataAccessContext setMakeupFor(IPersistenceCompensateNotify compensateCallback, Object compensateCallbackObject) {
        this.compensateCallback = compensateCallback;
        this.compensateCallbackObject = compensateCallbackObject;
        return this;
    }

    public IDataAccessContext setRollBackOnError() {
        this.rbkOnErr = true;
        return this;
    }

    @Override
    public boolean commit(String sessionId)  {
        WriterJobAdapter jobAdp = new WriterJobAdapter();
        WriterJob job = jobAdp.buildWriterJob(sessionId, this.entitis, this.rbkOnErr);
        IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
        return tcs.execute(job);
    }


    public long commitAndGenId(String sessionid) {
        return 0;
    }

    public void reset() {
        isSetQueryIdentity = false;
        this.rbkOnErr = false;
        this.notifyCallback = null;
        this.notifyCallbackObject = null;
        this.notifyCallback = null;
        this.notifyCallbackObject = null;
        if (!CheckUtil.isNullOrEmpty(entitis)) {
            entitis.clear();
        }
    }
}
