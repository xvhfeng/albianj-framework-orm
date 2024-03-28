package org.albianj;

import org.albianj.dal.api.context.dactx.IDMLCtx;
import org.albianj.dal.api.context.dactx.IDQLCtx;
import org.albianj.dal.api.object.IAblObj;
import org.albianj.dal.api.object.OdrBy;
import org.albianj.dal.api.object.filter.FltExpr;
import org.albianj.dal.api.object.filter.FltGExpr;
import org.albianj.dal.api.object.filter.IChaExpr;
import org.albianj.dal.api.object.filter.IFltGExpr;
import org.albianj.dal.api.service.AblCrt;
import org.albianj.dal.api.service.IAlbianDataAccessService;
import org.albianj.dal.api.service.IAlbianOpenedStorageService;

public final class AblServRouter extends ServRouter {

    static {
        _FilterStackFrameClasses.add(AblServRouter.class.getName());
    }

    public static IAblObj newInstance(Object sessionId, String itf)  {
        return AblCrt.newInstance(sessionId, itf);
    }

    public static <T extends IAblObj> T newInstance(Object sessionId, Class<T> clazz)  {
        return (T) newInstance(sessionId, clazz.getName());
    }

    public static IChaExpr newFilterExpression(){
        return  new FltExpr();
    }

    public static OdrBy newOrderByCondition(){
        return new OdrBy();
    }

    public static IFltGExpr newFilterGroupExpression(){
        return new FltGExpr();
    }

    public static IDMLCtx newDataAccessContext(Object sessionId) {
        IAlbianDataAccessService das = getService(sessionId,IAlbianDataAccessService.class,IAlbianDataAccessService.Name);
        return das.newDataAccessContext();
    }

    public static IDQLCtx newQueryContext(Object sessionId) {
        IAlbianDataAccessService das = getService(sessionId,IAlbianDataAccessService.class,IAlbianDataAccessService.Name);
        return das.newQueryContext();
    }

    public static IAlbianOpenedStorageService getDatabaseServiceThenDealBySelf(Object sessionId){
        IAlbianOpenedStorageService das = getService(sessionId,IAlbianOpenedStorageService.class,IAlbianOpenedStorageService.Name);
        return das;
    }


}
