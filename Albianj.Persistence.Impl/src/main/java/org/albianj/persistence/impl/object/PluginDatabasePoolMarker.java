package org.albianj.persistence.impl.object;

import org.albianj.logger.LogLevel;
import org.albianj.logger.LogTarget;
import org.albianj.persistence.db.IDataBasePool;
import org.albianj.persistence.object.DatabasePoolMaker;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.ServiceLoaderUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * project : com.yuewen.nrzx.albianj
 *
 * @ccversion 新建 - liyuqi 2019-07-01 17:29</br>
 */
public class PluginDatabasePoolMarker implements DatabasePoolMaker {
    private List<DatabasePoolMaker> supportMarkers = new ArrayList<>();

    public PluginDatabasePoolMarker() {
        try {
            Iterator<DatabasePoolMaker> its = ServiceLoaderUtils.listService(DatabasePoolMaker.class);
            while (its.hasNext()) {
                supportMarkers.add(its.next());
            }
        } catch (Throwable t) {
            AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Sql, LogLevel.Error,t,
                    "load databasePoolMarker error");
        }
    }

    @Override
    public IDataBasePool support(String style) {
        for (DatabasePoolMaker maker : supportMarkers) {
            IDataBasePool pool = maker.support(style);
            if (pool != null) {
                return pool;
            }
        }
        return null;
    }
}
