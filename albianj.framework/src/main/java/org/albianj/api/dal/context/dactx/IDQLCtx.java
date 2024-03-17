package org.albianj.api.dal.context.dactx;

import org.albianj.api.dal.object.filter.IChaExpr;
import org.albianj.api.dal.object.IAblObj;
import org.albianj.api.dal.object.OdrBy;
import org.albianj.api.dal.service.DrOpt;

import java.util.LinkedList;
import java.util.List;

public interface ISltCtx {

    /*
      别问我为啥叫这个结构，只因为再支持方法级别的功能，方法就要爆炸了
     */
    /*
     query condition:
         itfClzz,LoadType,Where,Order,PageStart,PageSize,UseIndex,routering,storageAlias,tableAlias
         result object or list<object.
     */

    ISltCtx paging(int start, int pagesize);

    ISltCtx forceIndex(String idxName);

    ISltCtx orderby(LinkedList<OdrBy> orderbys);

    ISltCtx useStorage(String storageAlias);

    ISltCtx fromTable(String tableAlias);

    ISltCtx byRouter(String drouterAlias);


    <T extends IAblObj> T loadObject(String sessionId, Class<T> itfClzz, DrOpt loadType, IChaExpr where) ;

    <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> itfClzz, DrOpt loadType, IChaExpr where) ;

    long loadCounts(String sessionId, Class<? extends IAblObj> itfClzz, DrOpt loadType, IChaExpr where) ;

    void reset();
}
