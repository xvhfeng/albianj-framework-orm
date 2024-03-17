package org.albianj;

import org.albianj.api.dal.context.dactx.IDMLCtx;
import org.albianj.api.dal.context.dactx.IDQLCtx;
import org.albianj.api.dal.object.IAblObj;
import org.albianj.api.dal.object.OdrBy;
import org.albianj.api.dal.object.filter.FltExpr;
import org.albianj.api.dal.object.filter.FltGExpr;
import org.albianj.api.dal.object.filter.IChaExpr;
import org.albianj.api.dal.object.filter.IFltGExpr;
import org.albianj.api.dal.service.AblCrt;
import org.albianj.api.dal.service.IAlbianDataAccessService;
import org.albianj.api.dal.service.IAlbianOpenedStorageService;

public final class AblServRouter extends ServRouter {

    static {
        _FilterStackFrameClasses.add(AblServRouter.class.getName());
    }

    public static IAblObj newInstance(Object sessionId, String itf)  {
        return AblCrt.newInst(sessionId, itf);
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
