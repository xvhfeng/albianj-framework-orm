package org.albianj.orm.impl.storage;


import org.albianj.kernel.service.FreeAlbianService;
import org.albianj.orm.impl.object.StorageAttribute;
import org.albianj.orm.object.RunningStorageAttribute;
import org.albianj.orm.service.AlbianServiceHub;
import org.albianj.orm.service.IAlbianOpenedStorageService;
import org.albianj.orm.service.IAlbianStorageParserService;

import java.sql.Connection;

public class AlbianOpenedStorageService extends FreeAlbianService implements IAlbianOpenedStorageService {

    @Override
    public RunningStorageAttribute getStorage(String sessionId, String storageName){
        IAlbianStorageParserService asps = AlbianServiceHub.getService(sessionId, IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        StorageAttribute sa = asps.getStorageAttribute(storageName);
        RunningStorageAttribute rsa = new RunningStorageAttribute(sa,sa.getDatabase());
        return rsa;
    }

    @Override
    public RunningStorageAttribute getStorage(String sessionId,String storageName, String dbAliasName){
        IAlbianStorageParserService asps = AlbianServiceHub.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        StorageAttribute sa = asps.getStorageAttribute(storageName);
        RunningStorageAttribute rsa = new RunningStorageAttribute(sa,dbAliasName);
        return rsa;
    }

    @Override
    public Connection getConnection(String sessionId, String storageName, boolean isAutoCommit)  {
        IAlbianStorageParserService asps = AlbianServiceHub.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        RunningStorageAttribute rsa = getStorage(sessionId,storageName);
        return asps.getConnection(sessionId,rsa,isAutoCommit);
    }

    @Override
    public Connection getConnection(String sessionId, String storageName, String dbAliasName, boolean isAutoCommit)  {
        IAlbianStorageParserService asps = AlbianServiceHub.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        RunningStorageAttribute rsa = getStorage(storageName,dbAliasName);
        return asps.getConnection(sessionId,rsa,isAutoCommit);
    }

    @Override
    public Connection getConnection(String sessionId, RunningStorageAttribute rsa, boolean isAutoCommit)  {
        IAlbianStorageParserService asps = AlbianServiceHub.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        return asps.getConnection(sessionId,rsa,isAutoCommit);
    }

    @Override
    public void returnConnection(String sessionId, RunningStorageAttribute rsa, Connection conn) {
        IAlbianStorageParserService asps = AlbianServiceHub.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        asps.returnConnection(sessionId,rsa,conn);
    }


}
