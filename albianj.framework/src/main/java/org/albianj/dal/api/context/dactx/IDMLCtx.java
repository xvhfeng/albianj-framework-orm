package org.albianj.dal.api.context.dactx;

import org.albianj.dal.api.object.IAblObj;
import org.albianj.dal.api.context.ICompensateNotify;
import org.albianj.dal.api.context.IDalNotify;

import java.util.List;



public interface IDMLCtx {

    IDMLCtx addList(QryOpt opt, List<? extends IAblObj> entity);

    IDMLCtx addList(QryOpt opt, List<? extends IAblObj> entity, String storageAlias);

    IDMLCtx addList(QryOpt opt, List<? extends IAblObj> entity, String storageAlias, String tableAlias);

    IDMLCtx add(QryOpt opt, IAblObj entiry);

    IDMLCtx add(QryOpt opt, IAblObj entiry, String storageAliasName);

    IDMLCtx add(QryOpt opt, IAblObj entiry, String storageAliasName, String tableAliasName);

    IDMLCtx withQueryGenKey();

    IDMLCtx setFinishNotify(IDalNotify notifyCallback, Object notifyCallbackObject);

    IDMLCtx setMakeupFor(ICompensateNotify compensateCallback, Object compensateCallbackObject);

    IDMLCtx setRollBackOnError();

    boolean commit(String sessionId) ;

    boolean needUpd();

    void reset();
}
