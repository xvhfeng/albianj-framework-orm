package org.albianj.orm.service;

import org.albianj.kernel.service.IAlbianService;
import org.albianj.orm.object.IRunningStorageAttribute;

import java.sql.Connection;

public interface IAlbianOpenedStorageService extends IAlbianService {
    String Name = "AlbianOpenedStorageService";

    IRunningStorageAttribute getStorage(String sessionId, String storageName);

    IRunningStorageAttribute getStorage(String sessionId,String storageName, String dbAliasName);

    Connection getConnection(String sessionId, String storageName, boolean isAutoCommit) ;

    Connection getConnection(String sessionId, String storageName, String dbAliasName, boolean isAutoCommit) ;

    Connection getConnection(String sessionId, IRunningStorageAttribute rsa, boolean isAutoCommit) ;

    void returnConnection(String sessionId, IRunningStorageAttribute rsa, Connection conn);
}
