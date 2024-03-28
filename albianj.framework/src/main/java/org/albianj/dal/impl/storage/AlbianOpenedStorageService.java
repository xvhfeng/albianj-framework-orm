package org.albianj.dal.impl.storage;


import org.albianj.AblServRouter;
import org.albianj.dal.api.object.StgAttr;
import org.albianj.kernel.api.service.FreeAlbianService;
import org.albianj.dal.api.object.RStgAttr;
import org.albianj.dal.api.service.IAlbianOpenedStorageService;
import org.albianj.dal.api.service.IAlbianStorageParserService;

import java.sql.Connection;

public class AlbianOpenedStorageService extends FreeAlbianService implements IAlbianOpenedStorageService {

    @Override
    public RStgAttr getStorage(String sessionId, String storageName){
        IAlbianStorageParserService asps = AblServRouter.getService(sessionId, IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        StgAttr sa = asps.getStorageAttribute(storageName);
        RStgAttr rsa = new RStgAttr(sa,sa.getDatabase());
        return rsa;
    }

    @Override
    public RStgAttr getStorage(String sessionId, String storageName, String dbAliasName){
        IAlbianStorageParserService asps = AblServRouter.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        StgAttr sa = asps.getStorageAttribute(storageName);
        RStgAttr rsa = new RStgAttr(sa,dbAliasName);
        return rsa;
    }

    @Override
    public Connection getConnection(String sessionId, String storageName, boolean isAutoCommit)  {
        IAlbianStorageParserService asps = AblServRouter.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        RStgAttr rsa = getStorage(sessionId,storageName);
        return asps.getConnection(sessionId,rsa,isAutoCommit);
    }

    @Override
    public Connection getConnection(String sessionId, String storageName, String dbAliasName, boolean isAutoCommit)  {
        IAlbianStorageParserService asps = AblServRouter.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        RStgAttr rsa = getStorage(storageName,dbAliasName);
        return asps.getConnection(sessionId,rsa,isAutoCommit);
    }

    @Override
    public Connection getConnection(String sessionId, RStgAttr rsa, boolean isAutoCommit)  {
        IAlbianStorageParserService asps = AblServRouter.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        return asps.getConnection(sessionId,rsa,isAutoCommit);
    }

    @Override
    public void returnConnection(String sessionId, RStgAttr rsa, Connection conn) {
        IAlbianStorageParserService asps = AblServRouter.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        asps.returnConnection(sessionId,rsa,conn);
    }


}
