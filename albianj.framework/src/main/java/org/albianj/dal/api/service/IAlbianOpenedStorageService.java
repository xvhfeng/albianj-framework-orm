package org.albianj.dal.api.service;

import org.albianj.kernel.api.service.IAlbianService;
import org.albianj.dal.api.object.RStgAttr;

import java.sql.Connection;

public interface IAlbianOpenedStorageService extends IAlbianService {
    String Name = "AlbianOpenedStorageService";

    RStgAttr getStorage(String sessionId, String storageName);

    RStgAttr getStorage(String sessionId, String storageName, String dbAliasName);

    Connection getConnection(String sessionId, String storageName, boolean isAutoCommit) ;

    Connection getConnection(String sessionId, String storageName, String dbAliasName, boolean isAutoCommit) ;

    Connection getConnection(String sessionId, RStgAttr rsa, boolean isAutoCommit) ;

    void returnConnection(String sessionId, RStgAttr rsa, Connection conn);
}
