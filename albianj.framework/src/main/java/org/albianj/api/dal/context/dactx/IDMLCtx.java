package org.albianj.api.dal.context.dactx;

import org.albianj.api.dal.context.ICompensateNotify;
import org.albianj.api.dal.context.IDalNotify;
import org.albianj.api.dal.object.IAblObj;

import java.util.List;



public interface IIduCtx {

    IIduCtx addList(QryOpt opt, List<? extends IAblObj> entity);

    IIduCtx addList(QryOpt opt, List<? extends IAblObj> entity, String storageAlias);

    IIduCtx addList(QryOpt opt, List<? extends IAblObj> entity, String storageAlias, String tableAlias);

    IIduCtx add(QryOpt opt, IAblObj entiry);

    IIduCtx add(QryOpt opt, IAblObj entiry, String storageAliasName);

    IIduCtx add(QryOpt opt, IAblObj entiry, String storageAliasName, String tableAliasName);

    IIduCtx withQueryGenKey();

    IIduCtx setFinishNotify(IDalNotify notifyCallback, Object notifyCallbackObject);

    IIduCtx setMakeupFor(ICompensateNotify compensateCallback, Object compensateCallbackObject);

    IIduCtx setRollBackOnError();

    boolean commit(String sessionId) ;

    void reset();
}
