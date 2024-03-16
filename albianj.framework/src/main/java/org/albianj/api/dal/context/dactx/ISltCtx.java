package org.albianj.api.dal.context.dactx;

import org.albianj.api.dal.object.filter.IChainExpression;
import org.albianj.api.dal.object.IAlbianObject;
import org.albianj.api.dal.object.OrderByCondition;
import org.albianj.api.dal.service.QueryToOpt;

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

    ISltCtx orderby(LinkedList<OrderByCondition> orderbys);

    ISltCtx useStorage(String storageAlias);

    ISltCtx fromTable(String tableAlias);

    ISltCtx byRouter(String drouterAlias);


    <T extends IAlbianObject> T loadObject(String sessionId, Class<T> itfClzz, QueryToOpt loadType, IChainExpression where) ;

    <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> itfClzz, QueryToOpt loadType, IChainExpression where) ;

    long loadCounts(String sessionId, Class<? extends IAlbianObject> itfClzz, QueryToOpt loadType, IChainExpression where) ;

    void reset();
}
