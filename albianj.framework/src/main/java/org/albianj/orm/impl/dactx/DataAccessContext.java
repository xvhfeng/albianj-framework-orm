package org.albianj.orm.impl.dactx;

import org.albianj.common.utils.CheckUtil;
import org.albianj.AlbianRuntimeException;
import org.albianj.orm.kit.context.ICompensateNotify;
import org.albianj.orm.kit.context.IPersistenceNotify;
import org.albianj.orm.ctx.AlbianObjectWarp;
import org.albianj.orm.kit.dactx.IDataAccessContext;
import org.albianj.orm.ctx.WriterJob;
import org.albianj.orm.impl.adapter.job.WriterJobAdapter;
import org.albianj.orm.impl.scope.ITransactionScope;
import org.albianj.orm.impl.scope.TransactionScope;
import org.albianj.orm.kit.object.IAlbianObject;

import java.util.ArrayList;
import java.util.List;

public class DataAccessContext implements IDataAccessContext {
    List<AlbianObjectWarp> entitis = null;
    private boolean isSetQueryIdentity = false;
    private IPersistenceNotify notifyCallback;
    private Object notifyCallbackObject;
    private ICompensateNotify compensateCallback;
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


    public IDataAccessContext setMakeupFor(ICompensateNotify compensateCallback, Object compensateCallbackObject) {
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
        ITransactionScope tcs = new TransactionScope();
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
