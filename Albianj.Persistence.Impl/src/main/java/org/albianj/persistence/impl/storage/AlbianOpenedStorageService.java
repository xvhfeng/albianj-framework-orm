package org.albianj.persistence.impl.storage;

import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.RunningStorageAttribute;
import org.albianj.persistence.service.AlbianServiceHub;
import org.albianj.persistence.service.IAlbianOpenedStorageService;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.service.FreeAlbianService;

import java.sql.Connection;

public class AlbianOpenedStorageService extends FreeAlbianService implements IAlbianOpenedStorageService {

    @Override
    public IRunningStorageAttribute getStorage(String sessionId,String storageName){
        IAlbianStorageParserService asps = AlbianServiceHub.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        IStorageAttribute sa = asps.getStorageAttribute(storageName);
        IRunningStorageAttribute rsa = new RunningStorageAttribute(sa,sa.getDatabase());
        return rsa;
    }

    @Override
    public IRunningStorageAttribute getStorage(String sessionId,String storageName, String dbAliasName){
        IAlbianStorageParserService asps = AlbianServiceHub.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        IStorageAttribute sa = asps.getStorageAttribute(storageName);
        IRunningStorageAttribute rsa = new RunningStorageAttribute(sa,dbAliasName);
        return rsa;
    }

    @Override
    public Connection getConnection(String sessionId, String storageName, boolean isAutoCommit)  {
        IAlbianStorageParserService asps = AlbianServiceHub.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        IRunningStorageAttribute rsa = getStorage(sessionId,storageName);
        return asps.getConnection(sessionId,rsa,isAutoCommit);
    }

    @Override
    public Connection getConnection(String sessionId, String storageName, String dbAliasName, boolean isAutoCommit)  {
        IAlbianStorageParserService asps = AlbianServiceHub.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        IRunningStorageAttribute rsa = getStorage(storageName,dbAliasName);
        return asps.getConnection(sessionId,rsa,isAutoCommit);
    }

    @Override
    public Connection getConnection(String sessionId, IRunningStorageAttribute rsa, boolean isAutoCommit)  {
        IAlbianStorageParserService asps = AlbianServiceHub.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        return asps.getConnection(sessionId,rsa,isAutoCommit);
    }

    @Override
    public void returnConnection(String sessionId, IRunningStorageAttribute rsa, Connection conn) {
        IAlbianStorageParserService asps = AlbianServiceHub.getService(sessionId,IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
        asps.returnConnection(sessionId,rsa,conn);
    }


}
