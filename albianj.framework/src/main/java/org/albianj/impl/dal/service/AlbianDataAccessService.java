package org.albianj.impl.dal.service;

import org.albianj.common.utils.SetUtil;
import org.albianj.api.dal.object.OdrBy;
import org.albianj.impl.dal.context.*;
import org.albianj.impl.dal.context.dactx.DMLCtx;
import org.albianj.impl.dal.context.dactx.DQLCtx;
import org.albianj.impl.dal.db.*;
import org.albianj.api.kernel.anno.serv.AblServRant;
import org.albianj.api.kernel.service.FreeAblServ;
import org.albianj.api.dal.context.ICompensateNotify;
import org.albianj.api.dal.context.IDalNotify;
import org.albianj.api.dal.context.RdrJob;
import org.albianj.api.dal.context.WrtJob;
import org.albianj.api.dal.context.dactx.IDMLCtx;
import org.albianj.api.dal.context.dactx.IDQLCtx;
import org.albianj.api.dal.db.CmdOpt;
import org.albianj.api.dal.db.SqlPara;
import org.albianj.api.dal.object.IAblObj;
import org.albianj.api.dal.object.OOpt;
import org.albianj.api.dal.object.RStgAttr;
import org.albianj.api.dal.object.filter.FltExpr;
import org.albianj.api.dal.object.filter.IChaExpr;
import org.albianj.api.dal.service.IAlbianDataAccessService;
import org.albianj.api.dal.service.DrOpt;

import java.math.BigInteger;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/12/22.
 */
@AblServRant(Id = IAlbianDataAccessService.Name, Interface = IAlbianDataAccessService.class)
public class AlbianDataAccessService extends FreeAblServ implements IAlbianDataAccessService {

    public String getServiceName() {
        return Name;
    }


    public boolean remove(String sessionId, IAblObj object)  {
        return this.remove(sessionId, object, null, null, null, null);
    }

    public boolean remove(String sessionId, IAblObj object, IDalNotify notifyCallback,
                          Object notifyCallbackObject, ICompensateNotify compensateCallback,
                          Object compensateCallbackObject)  {
        IWrtJobAdp ja = new WrtJobAdp();
        WrtJob job = ja.buildRemoved(sessionId, object);
        if (null != notifyCallback)
            job.setNotifyCallback(notifyCallback);
        if (null != notifyCallbackObject)
            job.setNotifyCallbackobject(notifyCallbackObject);
        if (null != compensateCallback)
            job.setCompensateCallback(compensateCallback);
        if (null != compensateCallbackObject)
            job.setCompensateCallbackObject(compensateCallbackObject);
        IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
        return tcs.execute(job);
    }

    public boolean remove(String sessionId, List<? extends IAblObj> objects)  {
        return this.remove(sessionId, objects, null, null, null, null);
    }

    public boolean remove(String sessionId, List<? extends IAblObj> objects, IDalNotify notifyCallback,
                          Object notifyCallbackObject, ICompensateNotify compensateCallback,
                          Object compensateCallbackObject)  {
        IWrtJobAdp ja = new WrtJobAdp();
        WrtJob job = ja.buildRemoved(sessionId, objects);
        if (null != notifyCallback)
            job.setNotifyCallback(notifyCallback);
        if (null != notifyCallbackObject)
            job.setNotifyCallbackobject(notifyCallbackObject);
        if (null != compensateCallback)
            job.setCompensateCallback(compensateCallback);
        if (null != compensateCallbackObject)
            job.setCompensateCallbackObject(compensateCallbackObject);
        IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
        return tcs.execute(job);
    }

    public boolean save(String sessionId, IAblObj object)  {
        return this.save(sessionId, object, null, null, null, null);
    }

    public boolean save(String sessionId, IAblObj object,
                        IDalNotify notifyCallback, Object notifyCallbackObject,
                        ICompensateNotify compensateCallback, Object compensateCallbackObject)
            {
        IWrtJobAdp ja = new WrtJobAdp();
                WrtJob job = ja.buildSaving(sessionId, object);
        if (null != notifyCallback)
            job.setNotifyCallback(notifyCallback);
        if (null != notifyCallbackObject)
            job.setNotifyCallbackobject(notifyCallbackObject);
        if (null != compensateCallback)
            job.setCompensateCallback(compensateCallback);
        if (null != compensateCallbackObject)
            job.setCompensateCallbackObject(compensateCallbackObject);
        IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
        return tcs.execute(job);
    }

    public boolean save(String sessionId, List<? extends IAblObj> objects)  {
        return this.save(sessionId, objects, null, null, null, null);
    }

    public boolean save(String sessionId, List<? extends IAblObj> objects, IDalNotify notifyCallback,
                        Object notifyCallbackObject, ICompensateNotify compensateCallback,
                        Object compensateCallbackObject)  {
        IWrtJobAdp ja = new WrtJobAdp();
        WrtJob job = ja.buildSaving(sessionId, objects);
        if (null != notifyCallback)
            job.setNotifyCallback(notifyCallback);
        if (null != notifyCallbackObject)
            job.setNotifyCallbackobject(notifyCallbackObject);
        if (null != compensateCallback)
            job.setCompensateCallback(compensateCallback);
        if (null != compensateCallbackObject)
            job.setCompensateCallbackObject(compensateCallbackObject);
        IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
        return tcs.execute(job);
    }

    public <T extends IAblObj> T loadObject(String sessionId, Class<T> cls, DrOpt loadType, IChaExpr wheres)
            {
        return this.loadObject(sessionId, cls, loadType, null, wheres);
    }

    public <T extends IAblObj> T loadObject(String sessionId, Class<T> cls,
                                            DrOpt loadType, String rountingName, IChaExpr wheres)
            {

        List<T> list = doLoadObjects(sessionId, cls, DrOpt.Wtr == loadType, rountingName, 0, 0, wheres, null, null);
        if (SetUtil.isEmpty(list))
            return null;
        return list.get(0);

//        if (LoadType.exact == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, 0, 0, wheres, null,null);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
//            return list.get(0);
//        }
//
//        if (LoadType.dirty == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, 0, 0, wheres, null,null);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
//            return list.get(0);
//        }
//
////        T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
////        if (null != obj)
////            return obj;
//
//        T newObj = doLoadObject(sessionId, cls, false, rountingName, wheres);
//        if (null == newObj)
//            return null;
////        AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
//        return newObj;
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType, IChaExpr wheres)
            {
        return loadObjects(sessionId, cls, loadType, null, wheres, null);
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                   IChaExpr wheres, LinkedList<OdrBy> orderbys)
            {
        return loadObjects(sessionId, cls, loadType, null, wheres, orderbys);
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType, String rountingName,
                                                   IChaExpr wheres, LinkedList<OdrBy> orderbys)
            {
        return loadObjects(sessionId, cls, loadType, 0, 0, wheres, orderbys);
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                   int start, int step, IChaExpr wheres)
            {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, null);
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                   int start, int step, IChaExpr wheres, LinkedList<OdrBy> orderbys)
            {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, orderbys);
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType, String rountingName,
                                                   int start, int step, IChaExpr wheres, LinkedList<OdrBy> orderbys)
            {
        List<T> list = doLoadObjects(sessionId, cls, DrOpt.Wtr == loadType, rountingName, start, step, wheres, orderbys, null);
        if (SetUtil.isEmpty(list))
            return null;
        return list;

//        if (LoadType.exact == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, start, step, wheres, orderbys,null);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
//            return list;
//        }
//        if (LoadType.dirty == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,null);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
//            return list;
//        }

//        List<T> objs = AlbianPersistenceCache.findObjects(cls, start, step, wheres, orderbys);
//        if (null != objs)
//            return objs;

//        List<T> objs = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,null);
//        if (null == objs)
//            return null;
////        AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, objs);
//        return objs;
    }


    /**
     * 慎用
     *
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     */
    public <T extends IAblObj> List<T> loadAllObjects(String sessionId, Class<T> cls, DrOpt loadType)
            {
        return this.loadObjects(sessionId, cls, loadType, new FltExpr());
    }

    /**
     * 慎用
     *
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     */
    public <T extends IAblObj> List<T> loadAllObjects(String sessionId, Class<T> cls, DrOpt loadType, LinkedList<OdrBy> orderbys)
            {
        return this.loadObjects(sessionId, cls, loadType, new FltExpr(), orderbys);
    }

    /**
     * 慎用
     *
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     */
    public <T extends IAblObj> List<T> loadAllObjects(String sessionId, Class<T> cls, DrOpt loadType, String rountingName, LinkedList<OdrBy> orderbys)
            {
        return this.loadObjects(sessionId, cls, loadType, rountingName, new FltExpr(), orderbys);

    }

    public <T extends IAblObj> T loadObjectById(String sessionId, Class<T> cls, DrOpt loadType, BigInteger id)
            {
        IChaExpr ce = new FltExpr("id", OOpt.eq, id);
        return loadObject(sessionId, cls, loadType, ce);
    }

    public <T extends IAblObj> T loadObjectById(String sessionId, Class<T> cls, DrOpt loadType, String rountingName, BigInteger id)
            {
        IChaExpr ce = new FltExpr("id", OOpt.eq, id);
        return loadObject(sessionId, cls, loadType, rountingName, ce);
    }

    public <T extends IAblObj> long loadObjectsCount(String sessionId, Class<T> cls,
                                                     DrOpt loadType, IChaExpr wheres)
            {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres);
    }

    public <T extends IAblObj> long loadObjectsCount(String sessionId, Class<T> cls,
                                                     DrOpt loadType, String rountingName, IChaExpr wheres)
            {

        return doLoadPageingCount(sessionId, cls, DrOpt.Wtr == loadType, rountingName, wheres, null, null);
//        long count = 0;
//        if (LoadType.exact == loadType) {
//            count = doLoadPageingCount(sessionId, cls, true, rountingName, wheres, null,null);
////            AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
//        } else {
//            if (LoadType.dirty == loadType) {
//                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,null);
////                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
//            } else {
////                count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
////                if (0 <= count) {
////                    return count;
////                }
//                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,null);
////                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
//            }
//
//        }
//
//        return count;
    }

    public <T extends IAblObj> T loadObject(String sessionId, Class<T> cls, CmdOpt cmdType,
                                            Statement statement)  {
        List<T> list = doLoadObjects(sessionId, cls, cmdType, statement);
        if (SetUtil.isEmpty(list))
            return null;
        return list.get(0);
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, CmdOpt cmdType,
                                                   Statement statement)  {
        List<T> list = doLoadObjects(sessionId, cls, cmdType, statement);
        if (SetUtil.isEmpty(list))
            return null;
        return list;
    }

    protected <T extends IAblObj> List<T> doLoadObjects(String sessionId,
                                                        Class<T> cls, CmdOpt cmdType, Statement statement)
            {
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        List<T> list = null;
        list = scope.execute(sessionId, cls, cmdType, statement);
        return list;
    }

    protected <T extends IAblObj> T doLoadObject(String sessionId, Class<T> cls,
                                                 CmdOpt cmdType, Statement statement)
            {
        List<T> list = doLoadObjects(sessionId, cls, cmdType, statement);
        if (SetUtil.isEmpty(list))
            return null;
        return list.get(0);
    }

    protected <T extends IAblObj> T doLoadObject(String sessionId,
                                                 Class<T> cls, boolean isExact,
                                                 String routingName, IChaExpr wheres)
            {
        List<T> list = doLoadObjects(sessionId, cls, isExact, routingName, 0, 0, wheres, null, null);
        if (SetUtil.isEmpty(list))
            return null;
        return list.get(0);
    }

    protected <T extends IAblObj> List<T> doLoadObjects(String sessionId,
                                                        Class<T> cls, boolean isExact, String routingName, int start, int step,
                                                        IChaExpr wheres,
                                                        LinkedList<OdrBy> orderbys, String idxName)
            {
        IRdrJobAdp ad = new RdrJobAdp();
        List<T> list = null;
                RdrJob job = ad.buildReaderJob(sessionId, cls, isExact, null, null, routingName, start, step,
                wheres, orderbys, idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        list = scope.execute(cls, job);
        return list;
    }

    protected <T extends IAblObj> long doLoadPageingCount(String sessionId,
                                                          Class<T> cls, boolean isExact, String routingName,
                                                          IChaExpr wheres,
                                                          LinkedList<OdrBy> orderbys, String idxName)
            {
        IRdrJobAdp ad = new RdrJobAdp();
                RdrJob job = ad.buildReaderJob(sessionId, cls, isExact, null, null, routingName,
                wheres, orderbys, idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        Object o = scope.execute(job);
        return (long) o;
    }


    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, RStgAttr storage, CmdOpt cmdType,
                                                   String text, Map<String, SqlPara> paras)  {
        IRdrJobAdp ad = new RdrJobAdp();
        RdrJob job = ad.buildReaderJob(sessionId, cls, storage, cmdType,
                text, paras);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        List<T> list = scope.execute(cls, job);
        return list;
    }

    public <T extends IAblObj> List<T> loadObject(String sessionId, Class<T> cls, RStgAttr storage, CmdOpt cmdType,
                                                  String text, Map<String, SqlPara> paras)  {
        return loadObjects(sessionId, cls, storage, cmdType, text, paras);
    }


    //-------增加强制制定索引名字

    public <T extends IAblObj> T loadObject(String sessionId, Class<T> cls, DrOpt loadType, IChaExpr wheres, String idxName)
            {
        return this.loadObject(sessionId, cls, loadType, null, wheres, idxName);
    }

    public <T extends IAblObj> T loadObject(String sessionId, Class<T> cls,
                                            DrOpt loadType, String rountingName, IChaExpr wheres, String idxName)
            {
        List<T> list = doLoadObjects(sessionId, cls, DrOpt.Wtr == loadType, rountingName, 0, 0, wheres, null, idxName);
        if (SetUtil.isEmpty(list))
            return null;
        return list.get(0);

//        if (LoadType.exact == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, 0, 0, wheres, null,idxName);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
//            return list.get(0);
//        }
//
//        if (LoadType.dirty == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, 0, 0, wheres, null,idxName);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
//            return list.get(0);
//        }
//
////        T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
////        if (null != obj)
////            return obj;
//
//        T newObj = doLoadObject(sessionId, cls, false, rountingName, wheres);
//        if (null == newObj)
//            return null;
////        AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
//        return newObj;
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType, IChaExpr wheres, String idxName)
            {
        return loadObjects(sessionId, cls, loadType, null, wheres, null, idxName);
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                   IChaExpr wheres, LinkedList<OdrBy> orderbys, String idxName)
            {
        return loadObjects(sessionId, cls, loadType, null, wheres, orderbys, idxName);
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType, String rountingName,
                                                   IChaExpr wheres, LinkedList<OdrBy> orderbys, String idxName)
            {
        return loadObjects(sessionId, cls, loadType, 0, 0, wheres, orderbys, idxName);
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                   int start, int step, IChaExpr wheres, String idxName)
            {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, null, idxName);
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                   int start, int step, IChaExpr wheres, LinkedList<OdrBy> orderbys, String idxName)
            {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, orderbys, idxName);
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType, String rountingName,
                                                   int start, int step, IChaExpr wheres, LinkedList<OdrBy> orderbys, String idxName)
            {
        List<T> list = doLoadObjects(sessionId, cls, DrOpt.Wtr == loadType, rountingName, start, step, wheres, orderbys, idxName);
        if (SetUtil.isEmpty(list))
            return null;
        return list;
//        if (LoadType.exact == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, start, step, wheres, orderbys,idxName);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
//            return list;
//        }
//        if (LoadType.dirty == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,idxName);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
//            return list;
//        }
//
////        List<T> objs = AlbianPersistenceCache.findObjects(cls, start, step, wheres, orderbys);
////        if (null != objs)
////            return objs;
//
//        List<T> objs = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,idxName);
//        if (null == objs)
//            return null;
////        AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, objs);
//        return objs;
    }


    /**
     * 慎用
     *
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     */
    public <T extends IAblObj> List<T> loadAllObjects(String sessionId, Class<T> cls, DrOpt loadType, String idxName)
            {
        return this.loadObjects(sessionId, cls, loadType, new FltExpr(), idxName);
    }

    /**
     * 慎用
     *
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     */
    public <T extends IAblObj> List<T> loadAllObjects(String sessionId, Class<T> cls, DrOpt loadType, LinkedList<OdrBy> orderbys, String idxName)
            {
        return this.loadObjects(sessionId, cls, loadType, new FltExpr(), orderbys, idxName);
    }

    /**
     * 慎用
     *
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     */
    public <T extends IAblObj> List<T> loadAllObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                      String rountingName, LinkedList<OdrBy> orderbys, String idxName)
            {
        return this.loadObjects(sessionId, cls, loadType, rountingName, new FltExpr(), orderbys, idxName);

    }

    public <T extends IAblObj> T loadObjectById(String sessionId, Class<T> cls, DrOpt loadType, BigInteger id, String idxName)
            {
        IChaExpr ce = new FltExpr("id", OOpt.eq, id);
        return loadObject(sessionId, cls, loadType, ce, idxName);
    }

    public <T extends IAblObj> T loadObjectById(String sessionId, Class<T> cls, DrOpt loadType, String rountingName, BigInteger id, String idxName)
            {
        IChaExpr ce = new FltExpr("id", OOpt.eq, id);
        return loadObject(sessionId, cls, loadType, rountingName, ce, idxName);
    }

    public <T extends IAblObj> long loadObjectsCount(String sessionId, Class<T> cls,
                                                     DrOpt loadType, IChaExpr wheres, String idxName)
            {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres, idxName);
    }

    public <T extends IAblObj> long loadObjectsCount(String sessionId, Class<T> cls,
                                                     DrOpt loadType, String rountingName, IChaExpr wheres, String idxName)
            {
        return doLoadPageingCount(sessionId, cls, DrOpt.Wtr == loadType, rountingName, wheres, null, idxName);

//        long count = 0;
//        if (LoadType.exact == loadType) {
//            count = doLoadPageingCount(sessionId, cls, true, rountingName, wheres, null,idxName);
////            AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
//        } else {
//            if (LoadType.dirty == loadType) {
//                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,idxName);
////                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
//            } else {
////                count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
////                if (0 <= count) {
////                    return count;
////                }
//                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,idxName);
////                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
//            }
//
//        }
//
//        return count;
    }

    // save chain entity
    public IDMLCtx newDataAccessContext() {

        return new DMLCtx();
    }

    public IDQLCtx newQueryContext() {
        return new DQLCtx();
    }

}
