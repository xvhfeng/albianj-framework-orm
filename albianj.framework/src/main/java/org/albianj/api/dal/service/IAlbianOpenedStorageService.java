package org.albianj.api.dal.service;

import org.albianj.api.kernel.service.IAlbianService;
import org.albianj.api.dal.object.RunningStorageAttribute;

import java.sql.Connection;

public interface IAlbianOpenedStorageService extends IAlbianService {
    String Name = "AlbianOpenedStorageService";

    RunningStorageAttribute getStorage(String sessionId, String storageName);

    RunningStorageAttribute getStorage(String sessionId,String storageName, String dbAliasName);

    Connection getConnection(String sessionId, String storageName, boolean isAutoCommit) ;

    Connection getConnection(String sessionId, String storageName, String dbAliasName, boolean isAutoCommit) ;

    Connection getConnection(String sessionId, RunningStorageAttribute rsa, boolean isAutoCommit) ;

    void returnConnection(String sessionId, RunningStorageAttribute rsa, Connection conn);
}
