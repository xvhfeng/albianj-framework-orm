package org.albianj.impl.dal.context.dactx;

import org.albianj.AblThrowable;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.api.dal.object.OdrBy;
import org.albianj.impl.dal.context.IRdrJobAdp;
import org.albianj.api.dal.context.RdrJob;
import org.albianj.impl.dal.context.RdrJobAdp;
import org.albianj.impl.dal.db.IPersistenceQueryScope;
import org.albianj.impl.dal.db.PersistenceQueryScope;
import org.albianj.api.dal.context.dactx.IDQLCtx;
import org.albianj.api.dal.object.IAblObj;
import org.albianj.api.dal.object.filter.IChaExpr;
import org.albianj.api.dal.service.DrOpt;

import java.util.LinkedList;
import java.util.List;

public class DQLCtx implements IDQLCtx {

    LinkedList<OdrBy> orderbys = null;
    private int start = -1;
    private int pagesize = -1;
    private String idxName = null;
    private String storageAlias = null;
    private String tableAlias = null;
    private String drouterAlias = null;
    private Class<? extends IAblObj> itfClzz = null;
    private DrOpt loadType = DrOpt.Rdr;
    private IChaExpr wheres = null;

    @Override
    public IDQLCtx paging(int start, int pagesize) {
        this.start = start;
        this.pagesize = pagesize;
        return this;
    }

    @Override
    public IDQLCtx forceIndex(String idxName) {
        this.idxName = idxName;
        return this;
    }

    @Override
    public IDQLCtx orderby(LinkedList<OdrBy> orderbys) {
        this.orderbys = orderbys;
        return this;
    }

    @Override
    public IDQLCtx useStorage(String storageAlias) {
        this.storageAlias = storageAlias;
        return this;
    }

    @Override
    public IDQLCtx fromTable(String tableAlias) {
        this.tableAlias = tableAlias;
        return this;
    }

    @Override
    public IDQLCtx byRouter(String drouterAlias) {
        this.drouterAlias = drouterAlias;
        return this;
    }

    @Override
    public <T extends IAblObj> T loadObject(String sessionId, Class<T> itfClzz, DrOpt loadType, IChaExpr wheres)  {
        List<T> entities = loadObjects(sessionId, itfClzz, loadType, wheres);
        if (SetUtil.isEmpty(entities)) {
            return null;
        }
        return entities.get(0);
    }

    @Override
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> itfClzz, DrOpt loadType, IChaExpr wheres)  {
        this.itfClzz = itfClzz;
        this.loadType = loadType;
        this.wheres = wheres;

        if (!StringsUtil.isNullEmptyTrimmed(this.drouterAlias) && (!StringsUtil.isNullEmptyTrimmed(storageAlias) || !StringsUtil.isNullEmptyTrimmed(tableAlias))) {
            throw new AblThrowable("drouterAlias is not coexist with storageAlias or tableAlias.");
        }
        if (StringsUtil.isNullEmptyTrimmed(storageAlias) && !StringsUtil.isNullEmptyTrimmed(tableAlias)) {
            throw new AblThrowable("tableAlias exist but storageAlias is not exist.");
        }

        IRdrJobAdp ad = new RdrJobAdp();
        List<T> list = null;
        RdrJob job = ad.buildReaderJob(sessionId, itfClzz, loadType == DrOpt.Wtr, this.storageAlias, this.tableAlias, this.drouterAlias, start, pagesize,
                wheres, orderbys, idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        list = scope.execute(itfClzz, job);
        return list;
    }

    public long loadCounts(String sessionId, Class<? extends IAblObj> itfClzz, DrOpt loadType, IChaExpr wheres)  {
        this.itfClzz = itfClzz;
        this.loadType = loadType;
        this.wheres = wheres;

        if (!StringsUtil.isNullEmptyTrimmed(this.drouterAlias) && (!StringsUtil.isNullEmptyTrimmed(storageAlias) || !StringsUtil.isNullEmptyTrimmed(tableAlias))) {
            throw new AblThrowable("drouterAlias is not coexist with storageAlias or tableAlias.");
        }
        if (StringsUtil.isNullEmptyTrimmed(storageAlias) && !StringsUtil.isNullEmptyTrimmed(tableAlias)) {
            throw new AblThrowable("tableAlias exist but storageAlias is not exist.");
        }

        IRdrJobAdp ad = new RdrJobAdp();
        RdrJob job = ad.buildReaderJob(sessionId, itfClzz, loadType == DrOpt.Wtr, this.storageAlias, this.tableAlias, this.drouterAlias,
                wheres, orderbys, idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        Object count = scope.execute(job);
        return null == count ? 0 : (long) count;
    }

    public void reset() {
        start = -1;
        pagesize = -1;
        idxName = null;
        storageAlias = null;
        tableAlias = null;
        drouterAlias = null;
        itfClzz = null;
        loadType = DrOpt.Rdr;
        wheres = null;
        orderbys = null;
    }
}
