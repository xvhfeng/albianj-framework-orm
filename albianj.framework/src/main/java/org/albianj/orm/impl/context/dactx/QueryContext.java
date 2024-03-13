package org.albianj.orm.impl.context.dactx;

import org.albianj.AblThrowable;
import org.albianj.kernel.common.utils.SetUtil;
import org.albianj.kernel.common.utils.StringsUtil;
import org.albianj.orm.context.IReaderJob;
import org.albianj.orm.context.dactx.IQueryContext;
import org.albianj.orm.impl.context.IReaderJobAdapter;
import org.albianj.orm.impl.context.ReaderJobAdapter;
import org.albianj.orm.impl.db.IPersistenceQueryScope;
import org.albianj.orm.impl.db.PersistenceQueryScope;
import org.albianj.orm.object.IAlbianObject;
import org.albianj.orm.object.IOrderByCondition;
import org.albianj.orm.object.filter.IChainExpression;
import org.albianj.orm.service.LoadType;

import java.util.LinkedList;
import java.util.List;

public class QueryContext implements IQueryContext {

    LinkedList<IOrderByCondition> orderbys = null;
    private int start = -1;
    private int pagesize = -1;
    private String idxName = null;
    private String storageAlias = null;
    private String tableAlias = null;
    private String drouterAlias = null;
    private Class<? extends IAlbianObject> itfClzz = null;
    private LoadType loadType = LoadType.quickly;
    private IChainExpression wheres = null;

    @Override
    public IQueryContext paging(int start, int pagesize) {
        this.start = start;
        this.pagesize = pagesize;
        return this;
    }

    @Override
    public IQueryContext forceIndex(String idxName) {
        this.idxName = idxName;
        return this;
    }

    @Override
    public IQueryContext orderby(LinkedList<IOrderByCondition> orderbys) {
        this.orderbys = orderbys;
        return this;
    }

    @Override
    public IQueryContext useStorage(String storageAlias) {
        this.storageAlias = storageAlias;
        return this;
    }

    @Override
    public IQueryContext fromTable(String tableAlias) {
        this.tableAlias = tableAlias;
        return this;
    }

    @Override
    public IQueryContext byRouter(String drouterAlias) {
        this.drouterAlias = drouterAlias;
        return this;
    }

    @Override
    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> itfClzz, LoadType loadType, IChainExpression wheres)  {
        List<T> entities = loadObjects(sessionId, itfClzz, loadType, wheres);
        if (SetUtil.isNullOrEmpty(entities)) {
            return null;
        }
        return entities.get(0);
    }

    @Override
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> itfClzz, LoadType loadType, IChainExpression wheres)  {
        this.itfClzz = itfClzz;
        this.loadType = loadType;
        this.wheres = wheres;

        if (!StringsUtil.isNullOrEmptyOrAllSpace(this.drouterAlias) && (!StringsUtil.isNullOrEmptyOrAllSpace(storageAlias) || !StringsUtil.isNullOrEmptyOrAllSpace(tableAlias))) {
            throw new AblThrowable("drouterAlias is not coexist with storageAlias or tableAlias.");
        }
        if (StringsUtil.isNullOrEmptyOrAllSpace(storageAlias) && !StringsUtil.isNullOrEmptyOrAllSpace(tableAlias)) {
            throw new AblThrowable("tableAlias exist but storageAlias is not exist.");
        }

        IReaderJobAdapter ad = new ReaderJobAdapter();
        List<T> list = null;
        IReaderJob job = ad.buildReaderJob(sessionId, itfClzz, loadType == LoadType.exact, this.storageAlias, this.tableAlias, this.drouterAlias, start, pagesize,
                wheres, orderbys, idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        list = scope.execute(itfClzz, job);
        return list;
    }

    public long loadCounts(String sessionId, Class<? extends IAlbianObject> itfClzz, LoadType loadType, IChainExpression wheres)  {
        this.itfClzz = itfClzz;
        this.loadType = loadType;
        this.wheres = wheres;

        if (!StringsUtil.isNullOrEmptyOrAllSpace(this.drouterAlias) && (!StringsUtil.isNullOrEmptyOrAllSpace(storageAlias) || !StringsUtil.isNullOrEmptyOrAllSpace(tableAlias))) {
            throw new AblThrowable("drouterAlias is not coexist with storageAlias or tableAlias.");
        }
        if (StringsUtil.isNullOrEmptyOrAllSpace(storageAlias) && !StringsUtil.isNullOrEmptyOrAllSpace(tableAlias)) {
            throw new AblThrowable("tableAlias exist but storageAlias is not exist.");
        }

        IReaderJobAdapter ad = new ReaderJobAdapter();
        IReaderJob job = ad.buildReaderJob(sessionId, itfClzz, loadType == LoadType.exact, this.storageAlias, this.tableAlias, this.drouterAlias,
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
        loadType = LoadType.quickly;
        wheres = null;
        orderbys = null;
    }
}
