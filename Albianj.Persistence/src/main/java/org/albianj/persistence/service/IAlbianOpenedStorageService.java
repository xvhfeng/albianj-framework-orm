package org.albianj.persistence.service;

import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.service.IAlbianService;

import java.sql.Connection;

public interface IAlbianOpenedStorageService extends IAlbianService {
    String Name = "AlbianOpenedStorageService";

    IRunningStorageAttribute getStorage(String sessionId,String storageName);

    IRunningStorageAttribute getStorage(String sessionId,String storageName, String dbAliasName);

    Connection getConnection(String sessionId, String storageName, boolean isAutoCommit) throws Throwable;

    Connection getConnection(String sessionId, String storageName, String dbAliasName, boolean isAutoCommit) throws Throwable;

    Connection getConnection(String sessionId, IRunningStorageAttribute rsa, boolean isAutoCommit) throws Throwable;

    void returnConnection(String sessionId, IRunningStorageAttribute rsa, Connection conn);
}
