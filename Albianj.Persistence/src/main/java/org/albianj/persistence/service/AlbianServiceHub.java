package org.albianj.persistence.service;

import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.context.dactx.IDataAccessContext;
import org.albianj.persistence.context.dactx.IQueryContext;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.OrderByCondition;
import org.albianj.persistence.object.filter.FilterExpression;
import org.albianj.persistence.object.filter.FilterGroupExpression;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.object.filter.IFilterGroupExpression;
import org.albianj.persistence.service.AlbianObjectCreator;
import org.albianj.service.AlbianServiceRouter;

public final class AlbianServiceHub extends AlbianServiceRouter {

    public static IAlbianObject newInstance(String sessionId, String itf) {
        return AlbianObjectCreator.newInstance(sessionId, itf);
    }

    public static <T extends IAlbianObject> T newInstance(String sessionId, Class<T> clazz) {
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

    public static IDataAccessContext newDataAccessContext() {
        IAlbianDataAccessService das = getSingletonService(IAlbianDataAccessService.class,IAlbianDataAccessService.Name);
        return das.newDataAccessContext();
    }

    public static IQueryContext newQueryContext() {
        IAlbianDataAccessService das = getSingletonService(IAlbianDataAccessService.class,IAlbianDataAccessService.Name);
        return das.newQueryContext();
    }

    public static  IAlbianOpenedStorageService getDatabaseServiceThenDealBySelf(){
        IAlbianOpenedStorageService das = getSingletonService(IAlbianOpenedStorageService.class,IAlbianOpenedStorageService.Name);
        return das;
    }
}
