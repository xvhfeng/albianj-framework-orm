package org.albianj.orm.service;

import org.albianj.kernel.AlbianServiceRouter;
import org.albianj.orm.context.dactx.IDataAccessContext;
import org.albianj.orm.context.dactx.IQueryContext;
import org.albianj.orm.object.IAlbianObject;
import org.albianj.orm.object.IOrderByCondition;
import org.albianj.orm.object.OrderByCondition;
import org.albianj.orm.object.filter.FilterExpression;
import org.albianj.orm.object.filter.FilterGroupExpression;
import org.albianj.orm.object.filter.IChainExpression;
import org.albianj.orm.object.filter.IFilterGroupExpression;

public final class AlbianServiceHub extends AlbianServiceRouter {

    public static IAlbianObject newInstance(Object sessionId, String itf)  {
        return AlbianObjectCreator.newInstance(sessionId, itf);
    }

    public static <T extends IAlbianObject> T newInstance(Object sessionId, Class<T> clazz)  {
        return (T) newInstance(sessionId, clazz.getName());
    }

    public static IChainExpression newFilterExpression(){
        return  new FilterExpression();
    }

    public static IOrderByCondition newOrderByCondition(){
        return new OrderByCondition();
    }

    public static  IFilterGroupExpression newFilterGroupExpression(){
        return new FilterGroupExpression();
    }

    public static IDataAccessContext newDataAccessContext(Object sessionId) {
        IAlbianDataAccessService das = getService(sessionId,IAlbianDataAccessService.class,IAlbianDataAccessService.Name);
        return das.newDataAccessContext();
    }

    public static IQueryContext newQueryContext(Object sessionId) {
        IAlbianDataAccessService das = getService(sessionId,IAlbianDataAccessService.class,IAlbianDataAccessService.Name);
        return das.newQueryContext();
    }

    public static  IAlbianOpenedStorageService getDatabaseServiceThenDealBySelf(Object sessionId){
        IAlbianOpenedStorageService das = getService(sessionId,IAlbianOpenedStorageService.class,IAlbianOpenedStorageService.Name);
        return das;
    }


}
