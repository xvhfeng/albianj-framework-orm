package org.albianj.orm.context.dactx;

import org.albianj.orm.context.IPersistenceCompensateNotify;
import org.albianj.orm.context.IPersistenceNotify;
import org.albianj.orm.object.IAlbianObject;

import java.util.List;

/*
    对于单个数据库，单表，支持in
    PreparedStatement statement = connection.prepareStatement("Select * from test where field in (?)");
Array array = statement.getConnection().createArrayOf("VARCHAR", new Object[]{"A1", "B2","C3"});
statement.setArray(1, array);
    对于多数据库支持自增主键，但是每次ctx只有一个自增主键的获取
    支持一个事务中，CUD操作
 */

public interface IDataAccessContext {

    IDataAccessContext addList(QueryOpt opt, List<? extends IAlbianObject> entity);

    IDataAccessContext addList(QueryOpt opt, List<? extends IAlbianObject> entity, String storageAlias);

    IDataAccessContext addList(QueryOpt opt, List<? extends IAlbianObject> entity, String storageAlias, String tableAlias);

    IDataAccessContext add(QueryOpt opt, IAlbianObject entiry);

    IDataAccessContext add(QueryOpt opt, IAlbianObject entiry, String storageAliasName);

    IDataAccessContext add(QueryOpt opt, IAlbianObject entiry, String storageAliasName, String tableAliasName);

    IDataAccessContext withQueryGenKey();

    IDataAccessContext setFinishNotify(IPersistenceNotify notifyCallback, Object notifyCallbackObject);

    IDataAccessContext setMakeupFor(IPersistenceCompensateNotify compensateCallback, Object compensateCallbackObject);

    IDataAccessContext setRollBackOnError();

    boolean commit(String sessionId) ;

    void reset();
}
