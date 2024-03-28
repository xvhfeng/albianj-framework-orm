package org.albianj.dal.impl.context.dactx;

import org.albianj.AblThrowable;
import org.albianj.common.utils.SetUtil;
import org.albianj.dal.impl.db.IPersistenceTransactionClusterScope;
import org.albianj.dal.impl.context.IWrtJobAdp;
import org.albianj.dal.api.context.WrtJob;
import org.albianj.dal.impl.context.WrtJobAdp;
import org.albianj.dal.impl.db.PersistenceTransactionClusterScope;
import org.albianj.dal.api.context.ICompensateNotify;
import org.albianj.dal.api.context.IDalNotify;
import org.albianj.dal.api.context.dactx.AblObjWarp;
import org.albianj.dal.api.context.dactx.IDMLCtx;
import org.albianj.dal.api.context.dactx.QryOpt;
import org.albianj.dal.api.object.IAblObj;

import java.util.ArrayList;
import java.util.List;

public class DMLCtx implements IDMLCtx {
    List<AblObjWarp> entitis = null;
    private boolean isSetQueryIdentity = false;
    private IDalNotify notifyCallback;
    private Object notifyCallbackObject;
    private ICompensateNotify compensateCallback;
    private Object compensateCallbackObject;
    private boolean rbkOnErr = false;

    public DMLCtx() {
        entitis = new ArrayList<>();
    }

     @Override
     public IDMLCtx addList(QryOpt opt, List<? extends IAblObj> entity) {
        entity.forEach(e -> {
            AblObjWarp warp = new AblObjWarp();
            warp.setEntry(e);
            warp.setQryOpt(opt);
            entitis.add(warp);
        });

        return this;
    }

    @Override
    public IDMLCtx addList(QryOpt opt, List<? extends IAblObj> entity, String storageAlias) {
         entity.forEach(e -> {
             AblObjWarp warp = new AblObjWarp();
            warp.setEntry(e);
            warp.setQryOpt(opt);
             warp.setStorageAliasName(storageAlias);
            entitis.add(warp);
        });
        return this;
    }

    @Override
    public IDMLCtx addList(QryOpt opt, List<? extends IAblObj> entity, String storageAlias, String tableAlias) {
        entity.forEach(e -> {
            AblObjWarp warp = new AblObjWarp();
            warp.setEntry(e);
            warp.setQryOpt(opt);
             warp.setStorageAliasName(storageAlias);
             warp.setTableAliasName(tableAlias);
            entitis.add(warp);
        });
        return this;
    }

    @Override
    public IDMLCtx add(QryOpt opt, IAblObj entity) {
        AblObjWarp warp = new AblObjWarp();
        warp.setEntry(entity);
        warp.setQryOpt(opt);
        entitis.add(warp);
        return this;
    }

    @Override
    public IDMLCtx add(QryOpt opt, IAblObj entity, String storageAlias) {
        AblObjWarp warp = new AblObjWarp();
        warp.setEntry(entity);
        warp.setQryOpt(opt);
        warp.setStorageAliasName(storageAlias);
        entitis.add(warp);
        return this;

    }

    @Override
    public IDMLCtx add(QryOpt opt, IAblObj entity, String storageAlias, String tableAlias) {
        AblObjWarp warp = new AblObjWarp();
        warp.setEntry(entity);
        warp.setQryOpt(opt);
        warp.setStorageAliasName(storageAlias);
        warp.setTableAliasName(tableAlias);
        entitis.add(warp);
        return this;

    }

    public IDMLCtx withQueryGenKey() {
        if (this.isSetQueryIdentity) {
            throw new AblThrowable("da-ctx exist query auto genkey");
        }
        entitis.get(entitis.size() - 1).setQueryAutoId(true);
        this.isSetQueryIdentity = true;
        return this;
    }

    public IDMLCtx setFinishNotify(IDalNotify notifyCallback, Object notifyCallbackObject) {
        this.notifyCallback = notifyCallback;
        this.notifyCallbackObject = notifyCallbackObject;
        return this;
    }


    public IDMLCtx setMakeupFor(ICompensateNotify compensateCallback, Object compensateCallbackObject) {
        this.compensateCallback = compensateCallback;
        this.compensateCallbackObject = compensateCallbackObject;
        return this;
    }

    public IDMLCtx setRollBackOnError() {
        this.rbkOnErr = true;
        return this;
    }

    @Override
    public boolean commit(String sessionId)  {
        IWrtJobAdp jobAdp = new WrtJobAdp();
        WrtJob job = jobAdp.buildWriterJob(sessionId, this.entitis, this.rbkOnErr);
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
        if (!SetUtil.isNullOrEmpty(entitis)) {
            entitis.clear();
        }
    }
}
