package org.albianj.orm.service;

import org.albianj.common.utils.SetUtil;
import org.albianj.orm.object.DataRouterAttribute;
import org.albianj.orm.object.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class AlbianObjectDataRouterDefaulter extends FreeAlbianObjectDataRouter {

    @Override
    public List<DataRouterAttribute> mappingWriterRouting(
            Map<String, DataRouterAttribute> routings, IAlbianObject obj) {
        // TODO Auto-generated method stub
        if (SetUtil.isNullOrEmpty(routings)) return null;
        if (1 == routings.size()) {
            Set<String> keys = routings.keySet();
            if (null == keys || 1 != keys.size()) return null;
            Object[] skeys = keys.toArray();
            DataRouterAttribute dra = routings.get(skeys[0]);
            if (!dra.isEnable()) return null;
            List<DataRouterAttribute> ras = new Vector<DataRouterAttribute>();
            ras.add(dra);
            return ras;
        }
        return null;
    }

    @Override
    public DataRouterAttribute mappingReaderRouting(
            Map<String, DataRouterAttribute> routings,
            Map<String, IFilterCondition> wheres,
            Map<String, IOrderByCondition> orderbys) {
        // TODO Auto-generated method stub
        if (SetUtil.isNullOrEmpty(routings)) return null;
        if (1 == routings.size()) {
            Set<String> keys = routings.keySet();
            if (null == keys || 1 != keys.size()) return null;
            Object[] skeys = keys.toArray();
            DataRouterAttribute dra = routings.get(skeys[0]);
            if (!dra.isEnable()) return null;
            return dra;
        }
        return null;
    }

    @Override
    public String mappingWriterRoutingStorage(DataRouterAttribute routing,
                                              IAlbianObject obj) {
        // TODO Auto-generated method stub
        if (null == routing) return null;
        return routing.getStorageName();
    }

    @Override
    public String mappingWriterTable(DataRouterAttribute routing,
                                     IAlbianObject obj) {
        // TODO Auto-generated method stub
        if (null == routing) return null;
        return routing.getTableName();
    }

    @Override
    public String mappingReaderRoutingStorage(DataRouterAttribute routing,
                                              Map<String, IFilterCondition> wheres,
                                              Map<String, IOrderByCondition> orderbys) {
        // TODO Auto-generated method stub
        if (null == routing) return null;
        return routing.getStorageName();
    }

    @Override
    public String mappingReaderTable(DataRouterAttribute routing,
                                     Map<String, IFilterCondition> wheres,
                                     Map<String, IOrderByCondition> orderbys) {
        // TODO Auto-generated method stub
        if (null == routing) return null;
        return routing.getTableName();
    }

    public DataRouterAttribute mappingExactReaderRouting(
            Map<String, DataRouterAttribute> routings,
            Map<String, IFilterCondition> wheres,
            Map<String, IOrderByCondition> orderbys) {
        return mappingReaderRouting(routings, wheres, orderbys);
    }

    /**
     * @param routing
     * @param wheres
     * @param orderbys
     * @return
     */
    public String mappingExactReaderRoutingStorage(DataRouterAttribute routing,
                                                   Map<String, IFilterCondition> wheres,
                                                   Map<String, IOrderByCondition> orderbys) {
        return mappingReaderRoutingStorage(routing, wheres, orderbys);
    }

    /**
     * @param routing
     * @param wheres
     * @param orderbys
     * @return
     */
    public String mappingExactReaderTable(DataRouterAttribute routing,
                                          Map<String, IFilterCondition> wheres,
                                          Map<String, IOrderByCondition> orderbys) {
        return mappingReaderTable(routing, wheres, orderbys);
    }
}

