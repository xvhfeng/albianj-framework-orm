package org.albianj;

import org.albianj.dal.context.dactx.IIduCtx;
import org.albianj.dal.context.dactx.ISltCtx;
import org.albianj.dal.object.IAlbianObject;
import org.albianj.dal.object.OrderByCondition;
import org.albianj.dal.object.filter.FilterExpression;
import org.albianj.dal.object.filter.FilterGroupExpression;
import org.albianj.dal.object.filter.IChainExpression;
import org.albianj.dal.object.filter.IFilterGroupExpression;
import org.albianj.dal.service.AlbianObjectCreator;
import org.albianj.dal.service.IAlbianDataAccessService;
import org.albianj.dal.service.IAlbianOpenedStorageService;

public final class AblServRouter extends ServRouter {

    static {
        _FilterStackFrameClasses.add(AblServRouter.class.getName());
    }

    public static IAlbianObject newInstance(Object sessionId, String itf)  {
        return AlbianObjectCreator.newInstance(sessionId, itf);
    }

    public static <T extends IAlbianObject> T newInstance(Object sessionId, Class<T> clazz)  {
        return (T) newInstance(sessionId, clazz.getName());
    }

    public static IChainExpression newFilterExpression(){
        return  new FilterExpression();
    }

    public static OrderByCondition newOrderByCondition(){
        return new OrderByCondition();
    }

    public static  IFilterGroupExpression newFilterGroupExpression(){
        return new FilterGroupExpression();
    }

    public static IIduCtx newDataAccessContext(Object sessionId) {
        IAlbianDataAccessService das = getService(sessionId,IAlbianDataAccessService.class,IAlbianDataAccessService.Name);
        return das.newDataAccessContext();
    }

    public static ISltCtx newQueryContext(Object sessionId) {
        IAlbianDataAccessService das = getService(sessionId,IAlbianDataAccessService.class,IAlbianDataAccessService.Name);
        return das.newQueryContext();
    }

    public static IAlbianOpenedStorageService getDatabaseServiceThenDealBySelf(Object sessionId){
        IAlbianOpenedStorageService das = getService(sessionId,IAlbianOpenedStorageService.class,IAlbianOpenedStorageService.Name);
        return das;
    }


}
