package org.albianj.impl.dal.storage;


import org.albianj.AblServRouter;
import org.albianj.api.dal.object.StorageAttribute;
import org.albianj.api.kernel.service.FreeAlbianService;
import org.albianj.api.dal.object.RunningStorageAttribute;
import org.albianj.api.dal.service.IAlbianOpenedStorageService;
import org.albianj.api.dal.service.IAlbianStorageParserService;

import java.sql.Connection;

public class AlbianOpenedStorageService extends FreeAlbianService implements IAlbianOpenedStorageService {

    @Override
    public RunningStorageAttribute getStorage(String sessionId, String storageName){
        IAlbianStorageParserService asps = AblServRouter.getService(sessionId, IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        StorageAttribute sa = asps.getStorageAttribute(storageName);
        RunningStorageAttribute rsa = new RunningStorageAttribute(sa,sa.getDatabase());
        return rsa;
    }

    @Override
    public RunningStorageAttribute getStorage(String sessionId,String storageName, String dbAliasName){
        IAlbianStorageParserService asps = AblServRouter.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        StorageAttribute sa = asps.getStorageAttribute(storageName);
        RunningStorageAttribute rsa = new RunningStorageAttribute(sa,dbAliasName);
        return rsa;
    }

    @Override
    public Connection getConnection(String sessionId, String storageName, boolean isAutoCommit)  {
        IAlbianStorageParserService asps = AblServRouter.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        RunningStorageAttribute rsa = getStorage(sessionId,storageName);
        return asps.getConnection(sessionId,rsa,isAutoCommit);
    }

    @Override
    public Connection getConnection(String sessionId, String storageName, String dbAliasName, boolean isAutoCommit)  {
        IAlbianStorageParserService asps = AblServRouter.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        RunningStorageAttribute rsa = getStorage(storageName,dbAliasName);
        return asps.getConnection(sessionId,rsa,isAutoCommit);
    }

    @Override
    public Connection getConnection(String sessionId, RunningStorageAttribute rsa, boolean isAutoCommit)  {
        IAlbianStorageParserService asps = AblServRouter.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        return asps.getConnection(sessionId,rsa,isAutoCommit);
    }

    @Override
    public void returnConnection(String sessionId, RunningStorageAttribute rsa, Connection conn) {
        IAlbianStorageParserService asps = AblServRouter.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        asps.returnConnection(sessionId,rsa,conn);
    }


}
