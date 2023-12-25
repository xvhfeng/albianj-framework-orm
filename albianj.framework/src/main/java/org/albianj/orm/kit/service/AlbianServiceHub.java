package org.albianj.orm.kit.service;

import org.albianj.kernel.kit.service.AlbianServiceRouter;
import org.albianj.orm.kit.context.dactx.IDataAccessContext;
import org.albianj.orm.kit.context.dactx.IQueryContext;
import org.albianj.orm.kit.object.IAlbianObject;
import org.albianj.orm.kit.object.IOrderByCondition;
import org.albianj.orm.kit.object.OrderByCondition;
import org.albianj.orm.kit.object.filter.FilterExpression;
import org.albianj.orm.kit.object.filter.FilterGroupExpression;
import org.albianj.orm.kit.object.filter.IChainExpression;
import org.albianj.orm.kit.object.filter.IFilterGroupExpression;

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
