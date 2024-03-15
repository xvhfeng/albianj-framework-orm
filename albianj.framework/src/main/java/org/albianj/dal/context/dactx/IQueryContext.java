package org.albianj.dal.context.dactx;

import org.albianj.dal.object.IAlbianObject;
import org.albianj.dal.object.IOrderByCondition;
import org.albianj.dal.object.filter.IChainExpression;
import org.albianj.dal.service.QueryToOpt;

import java.util.LinkedList;
import java.util.List;

public interface IQueryContext {

    /*
      别问我为啥叫这个结构，只因为再支持方法级别的功能，方法就要爆炸了
     */
    /*
     query condition:
         itfClzz,LoadType,Where,Order,PageStart,PageSize,UseIndex,routering,storageAlias,tableAlias
         result object or list<object.
     */

    IQueryContext paging(int start, int pagesize);

    IQueryContext forceIndex(String idxName);

    IQueryContext orderby(LinkedList<IOrderByCondition> orderbys);

    IQueryContext useStorage(String storageAlias);

    IQueryContext fromTable(String tableAlias);

    IQueryContext byRouter(String drouterAlias);


    <T extends IAlbianObject> T loadObject(String sessionId, Class<T> itfClzz, QueryToOpt loadType, IChainExpression where) ;

    <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> itfClzz, QueryToOpt loadType, IChainExpression where) ;

    long loadCounts(String sessionId, Class<? extends IAlbianObject> itfClzz, QueryToOpt loadType, IChainExpression where) ;

    void reset();
}
