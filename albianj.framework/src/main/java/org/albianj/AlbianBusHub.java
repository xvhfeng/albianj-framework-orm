package org.albianj;

import org.albianj.kernel.kit.service.AlbianServiceRouter;
import org.albianj.orm.kit.dactx.IDataAccessContext;
import org.albianj.orm.kit.dactx.IQueryContext;
import org.albianj.orm.kit.object.IAlbianObject;
import org.albianj.orm.kit.expr.IOrderByCondition;
import org.albianj.orm.kit.expr.OrderByCondition;
import org.albianj.orm.kit.expr.FilterExpression;
import org.albianj.orm.kit.expr.FilterGroupExpression;
import org.albianj.orm.kit.expr.IChainExpression;
import org.albianj.orm.kit.expr.IFilterGroupExpression;
import org.albianj.orm.kit.service.AlbianObjectCreator;
import org.albianj.orm.kit.service.IAlbianDataAccessService;
import org.albianj.orm.kit.service.IAlbianOpenedStorageService;

public final class AlbianBusHub extends AlbianServiceRouter {

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
