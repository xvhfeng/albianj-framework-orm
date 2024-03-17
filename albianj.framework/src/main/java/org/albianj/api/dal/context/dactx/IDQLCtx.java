package org.albianj.api.dal.context.dactx;

import org.albianj.api.dal.object.filter.IChaExpr;
import org.albianj.api.dal.object.IAblObj;
import org.albianj.api.dal.object.OdrBy;
import org.albianj.api.dal.service.DrOpt;

import java.util.LinkedList;
import java.util.List;

public interface IDQLCtx {

    /*
      别问我为啥叫这个结构，只因为再支持方法级别的功能，方法就要爆炸了
     */
    /*
     query condition:
         itfClzz,LoadType,Where,Order,PageStart,PageSize,UseIndex,routering,storageAlias,tableAlias
         result object or list<object.
     */

    IDQLCtx paging(int start, int pagesize);

    IDQLCtx forceIndex(String idxName);

    IDQLCtx orderby(LinkedList<OdrBy> orderbys);

    IDQLCtx useStorage(String storageAlias);

    IDQLCtx fromTable(String tableAlias);

    IDQLCtx byRouter(String drouterAlias);


    <T extends IAblObj> T loadObject(String sessionId, Class<T> itfClzz, DrOpt loadType, IChaExpr where) ;

    <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> itfClzz, DrOpt loadType, IChaExpr where) ;

    long loadCounts(String sessionId, Class<? extends IAblObj> itfClzz, DrOpt loadType, IChaExpr where) ;

    void reset();
}
