package org.albianj.dal.api.service;

import org.albianj.common.utils.SetUtil;
import org.albianj.dal.api.object.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class AblDrDef extends FreeAblDr {

    @Override
    public List<DrAttr> mappingWriterRouting(
            Map<String, DrAttr> routings, IAblObj obj) {
        // TODO Auto-generated method stub
        if (SetUtil.isEmpty(routings)) return null;
        if (1 == routings.size()) {
            Set<String> keys = routings.keySet();
            if (null == keys || 1 != keys.size()) return null;
            Object[] skeys = keys.toArray();
            DrAttr dra = routings.get(skeys[0]);
            if (!dra.isEnable()) return null;
            List<DrAttr> ras = new Vector<DrAttr>();
            ras.add(dra);
            return ras;
        }
        return null;
    }

    @Override
    public DrAttr mappingReaderRouting(
            Map<String, DrAttr> routings,
            Map<String, IFltCdt> wheres,
            Map<String, OdrBy> orderbys) {
        // TODO Auto-generated method stub
        if (SetUtil.isEmpty(routings)) return null;
        if (1 == routings.size()) {
            Set<String> keys = routings.keySet();
            if (null == keys || 1 != keys.size()) return null;
            Object[] skeys = keys.toArray();
            DrAttr dra = routings.get(skeys[0]);
            if (!dra.isEnable()) return null;
            return dra;
        }
        return null;
    }

    @Override
    public String mappingWriterRoutingStorage(DrAttr routing,
                                              IAblObj obj) {
        // TODO Auto-generated method stub
        if (null == routing) return null;
        return routing.getStorageName();
    }

    @Override
    public String mappingWriterTable(DrAttr routing,
                                     IAblObj obj) {
        // TODO Auto-generated method stub
        if (null == routing) return null;
        return routing.getTableName();
    }

    @Override
    public String mappingReaderRoutingStorage(DrAttr routing,
                                              Map<String, IFltCdt> wheres,
                                              Map<String, OdrBy> orderbys) {
        // TODO Auto-generated method stub
        if (null == routing) return null;
        return routing.getStorageName();
    }

    @Override
    public String mappingReaderTable(DrAttr routing,
                                     Map<String, IFltCdt> wheres,
                                     Map<String, OdrBy> orderbys) {
        // TODO Auto-generated method stub
        if (null == routing) return null;
        return routing.getTableName();
    }

    public DrAttr mappingExactReaderRouting(
            Map<String, DrAttr> routings,
            Map<String, IFltCdt> wheres,
            Map<String, OdrBy> orderbys) {
        return mappingReaderRouting(routings, wheres, orderbys);
    }

    /**
     * @param routing
     * @param wheres
     * @param orderbys
     * @return
     */
    public String mappingExactReaderRoutingStorage(DrAttr routing,
                                                   Map<String, IFltCdt> wheres,
                                                   Map<String, OdrBy> orderbys) {
        return mappingReaderRoutingStorage(routing, wheres, orderbys);
    }

    /**
     * @param routing
     * @param wheres
     * @param orderbys
     * @return
     */
    public String mappingExactReaderTable(DrAttr routing,
                                          Map<String, IFltCdt> wheres,
                                          Map<String, OdrBy> orderbys) {
        return mappingReaderTable(routing, wheres, orderbys);
    }
}

