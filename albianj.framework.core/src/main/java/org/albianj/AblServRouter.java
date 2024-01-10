package org.albianj;

import org.albianj.kernel.ServRouter;
import org.albianj.orm.itf.dactx.IDataAccessContext;
import org.albianj.orm.itf.dactx.IQueryContext;
import org.albianj.orm.itf.object.IAlbianObject;
import org.albianj.orm.itf.expr.IOrderByCondition;
import org.albianj.orm.itf.expr.OrderByCondition;
import org.albianj.orm.itf.expr.FilterExpression;
import org.albianj.orm.itf.expr.FilterGroupExpression;
import org.albianj.orm.itf.expr.IChainExpression;
import org.albianj.orm.itf.expr.IFilterGroupExpression;
import org.albianj.orm.itf.service.AlbianObjectCreator;
import org.albianj.orm.itf.service.IAlbianDataAccessService;
import org.albianj.orm.itf.service.IAlbianOpenedStorageService;

public final class AblServRouter extends ServRouter {

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

    public static IAlbianOpenedStorageService getDatabaseServiceThenDealBySelf(Object sessionId){
        IAlbianOpenedStorageService das = getService(sessionId,IAlbianOpenedStorageService.class,IAlbianOpenedStorageService.Name);
        return das;
    }


}
