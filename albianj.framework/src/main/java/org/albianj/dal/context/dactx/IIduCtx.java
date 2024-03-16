package org.albianj.dal.context.dactx;

import org.albianj.dal.context.IPersistenceCompensateNotify;
import org.albianj.dal.context.IPersistenceNotify;
import org.albianj.dal.object.IAlbianObject;

import java.util.List;



public interface IIduCtx {

    IIduCtx addList(QueryOpt opt, List<? extends IAlbianObject> entity);

    IIduCtx addList(QueryOpt opt, List<? extends IAlbianObject> entity, String storageAlias);

    IIduCtx addList(QueryOpt opt, List<? extends IAlbianObject> entity, String storageAlias, String tableAlias);

    IIduCtx add(QueryOpt opt, IAlbianObject entiry);

    IIduCtx add(QueryOpt opt, IAlbianObject entiry, String storageAliasName);

    IIduCtx add(QueryOpt opt, IAlbianObject entiry, String storageAliasName, String tableAliasName);

    IIduCtx withQueryGenKey();

    IIduCtx setFinishNotify(IPersistenceNotify notifyCallback, Object notifyCallbackObject);

    IIduCtx setMakeupFor(IPersistenceCompensateNotify compensateCallback, Object compensateCallbackObject);

    IIduCtx setRollBackOnError();

    boolean commit(String sessionId) ;

    void reset();
}
