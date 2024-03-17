package org.albianj.impl.dal.service;

import org.albianj.common.utils.SetUtil;
import org.albianj.api.dal.object.OrderByCondition;
import org.albianj.impl.dal.context.*;
import org.albianj.impl.dal.context.dactx.IduCtx;
import org.albianj.impl.dal.context.dactx.SltCtx;
import org.albianj.impl.dal.db.*;
import org.albianj.api.kernel.anno.serv.AlbianServiceRant;
import org.albianj.api.kernel.service.FreeAlbianService;
import org.albianj.api.dal.context.IPersistenceCompensateNotify;
import org.albianj.api.dal.context.IPersistenceNotify;
import org.albianj.api.dal.context.ReaderJob;
import org.albianj.api.dal.context.WriterJob;
import org.albianj.api.dal.context.dactx.IIduCtx;
import org.albianj.api.dal.context.dactx.ISltCtx;
import org.albianj.api.dal.db.CommandOpt;
import org.albianj.api.dal.db.SqlParameter;
import org.albianj.api.dal.object.IAlbianObject;
import org.albianj.api.dal.object.OperatorOpt;
import org.albianj.api.dal.object.RunningStorageAttribute;
import org.albianj.api.dal.object.filter.FilterExpression;
import org.albianj.api.dal.object.filter.IChainExpression;
import org.albianj.api.dal.service.IAlbianDataAccessService;
import org.albianj.api.dal.service.QueryToOpt;

import java.math.BigInteger;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/12/22.
 */
@AlbianServiceRant(Id = IAlbianDataAccessService.Name, Interface = IAlbianDataAccessService.class)
public class AlbianDataAccessService extends FreeAlbianService implements IAlbianDataAccessService {

    public String getServiceName() {
        return Name;
    }


    public boolean remove(String sessionId, IAlbianObject object)  {
        return this.remove(sessionId, object, null, null, null, null);
    }

    public boolean remove(String sessionId, IAlbianObject object, IPersistenceNotify notifyCallback,
                          Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
                          Object compensateCallbackObject)  {
        IWriterJobAdapter ja = new WriterJobAdapter();
        WriterJob job = ja.buildRemoved(sessionId, object);
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

    public boolean remove(String sessionId, List<? extends IAlbianObject> objects)  {
        return this.remove(sessionId, objects, null, null, null, null);
    }

    public boolean remove(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
                          Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
                          Object compensateCallbackObject)  {
        IWriterJobAdapter ja = new WriterJobAdapter();
        WriterJob job = ja.buildRemoved(sessionId, objects);
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

    public boolean save(String sessionId, IAlbianObject object)  {
        return this.save(sessionId, object, null, null, null, null);
    }

    public boolean save(String sessionId, IAlbianObject object,
                        IPersistenceNotify notifyCallback, Object notifyCallbackObject,
                        IPersistenceCompensateNotify compensateCallback, Object compensateCallbackObject)
            {
        IWriterJobAdapter ja = new WriterJobAdapter();
                WriterJob job = ja.buildSaving(sessionId, object);
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

    public boolean save(String sessionId, List<? extends IAlbianObject> objects)  {
        return this.save(sessionId, objects, null, null, null, null);
    }

    public boolean save(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
                        Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
                        Object compensateCallbackObject)  {
        IWriterJobAdapter ja = new WriterJobAdapter();
        WriterJob job = ja.buildSaving(sessionId, objects);
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

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, QueryToOpt loadType, IChainExpression wheres)
            {
        return this.loadObject(sessionId, cls, loadType, null, wheres);
    }

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls,
                                                  QueryToOpt loadType, String rountingName, IChainExpression wheres)
            {

        List<T> list = doLoadObjects(sessionId, cls, QueryToOpt.WriterRouter == loadType, rountingName, 0, 0, wheres, null, null);
        if (SetUtil.isNullOrEmpty(list))
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

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, QueryToOpt loadType, IChainExpression wheres)
            {
        return loadObjects(sessionId, cls, loadType, null, wheres, null);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, QueryToOpt loadType,
                                                         IChainExpression wheres, LinkedList<OrderByCondition> orderbys)
            {
        return loadObjects(sessionId, cls, loadType, null, wheres, orderbys);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, QueryToOpt loadType, String rountingName,
                                                         IChainExpression wheres, LinkedList<OrderByCondition> orderbys)
            {
        return loadObjects(sessionId, cls, loadType, 0, 0, wheres, orderbys);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, QueryToOpt loadType,
                                                         int start, int step, IChainExpression wheres)
            {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, null);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, QueryToOpt loadType,
                                                         int start, int step, IChainExpression wheres, LinkedList<OrderByCondition> orderbys)
            {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, orderbys);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, QueryToOpt loadType, String rountingName,
                                                         int start, int step, IChainExpression wheres, LinkedList<OrderByCondition> orderbys)
            {
        List<T> list = doLoadObjects(sessionId, cls, QueryToOpt.WriterRouter == loadType, rountingName, start, step, wheres, orderbys, null);
        if (SetUtil.isNullOrEmpty(list))
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
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, QueryToOpt loadType)
            {
        return this.loadObjects(sessionId, cls, loadType, new FilterExpression());
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
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, QueryToOpt loadType, LinkedList<OrderByCondition> orderbys)
            {
        return this.loadObjects(sessionId, cls, loadType, new FilterExpression(), orderbys);
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
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, QueryToOpt loadType, String rountingName, LinkedList<OrderByCondition> orderbys)
            {
        return this.loadObjects(sessionId, cls, loadType, rountingName, new FilterExpression(), orderbys);

    }

    public <T extends IAlbianObject> T loadObjectById(String sessionId, Class<T> cls, QueryToOpt loadType, BigInteger id)
            {
        IChainExpression ce = new FilterExpression("id", OperatorOpt.eq, id);
        return loadObject(sessionId, cls, loadType, ce);
    }

    public <T extends IAlbianObject> T loadObjectById(String sessionId, Class<T> cls, QueryToOpt loadType, String rountingName, BigInteger id)
            {
        IChainExpression ce = new FilterExpression("id", OperatorOpt.eq, id);
        return loadObject(sessionId, cls, loadType, rountingName, ce);
    }

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           QueryToOpt loadType, IChainExpression wheres)
            {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres);
    }

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           QueryToOpt loadType, String rountingName, IChainExpression wheres)
            {

        return doLoadPageingCount(sessionId, cls, QueryToOpt.WriterRouter == loadType, rountingName, wheres, null, null);
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

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, CommandOpt cmdType,
                                                  Statement statement)  {
        List<T> list = doLoadObjects(sessionId, cls, cmdType, statement);
        if (SetUtil.isNullOrEmpty(list))
            return null;
        return list.get(0);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, CommandOpt cmdType,
                                                         Statement statement)  {
        List<T> list = doLoadObjects(sessionId, cls, cmdType, statement);
        if (SetUtil.isNullOrEmpty(list))
            return null;
        return list;
    }

    protected <T extends IAlbianObject> List<T> doLoadObjects(String sessionId,
                                                              Class<T> cls, CommandOpt cmdType, Statement statement)
            {
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        List<T> list = null;
        list = scope.execute(sessionId, cls, cmdType, statement);
        return list;
    }

    protected <T extends IAlbianObject> T doLoadObject(String sessionId, Class<T> cls,
                                                       CommandOpt cmdType, Statement statement)
            {
        List<T> list = doLoadObjects(sessionId, cls, cmdType, statement);
        if (SetUtil.isNullOrEmpty(list))
            return null;
        return list.get(0);
    }

    protected <T extends IAlbianObject> T doLoadObject(String sessionId,
                                                       Class<T> cls, boolean isExact,
                                                       String routingName, IChainExpression wheres)
            {
        List<T> list = doLoadObjects(sessionId, cls, isExact, routingName, 0, 0, wheres, null, null);
        if (SetUtil.isNullOrEmpty(list))
            return null;
        return list.get(0);
    }

    protected <T extends IAlbianObject> List<T> doLoadObjects(String sessionId,
                                                              Class<T> cls, boolean isExact, String routingName, int start, int step,
                                                              IChainExpression wheres,
                                                              LinkedList<OrderByCondition> orderbys, String idxName)
            {
        IReaderJobAdapter ad = new ReaderJobAdapter();
        List<T> list = null;
                ReaderJob job = ad.buildReaderJob(sessionId, cls, isExact, null, null, routingName, start, step,
                wheres, orderbys, idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        list = scope.execute(cls, job);
        return list;
    }

    protected <T extends IAlbianObject> long doLoadPageingCount(String sessionId,
                                                                Class<T> cls, boolean isExact, String routingName,
                                                                IChainExpression wheres,
                                                                LinkedList<OrderByCondition> orderbys, String idxName)
            {
        IReaderJobAdapter ad = new ReaderJobAdapter();
                ReaderJob job = ad.buildReaderJob(sessionId, cls, isExact, null, null, routingName,
                wheres, orderbys, idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        Object o = scope.execute(job);
        return (long) o;
    }


    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, RunningStorageAttribute storage, CommandOpt cmdType,
                                                         String text, Map<String, SqlParameter> paras)  {
        IReaderJobAdapter ad = new ReaderJobAdapter();
        ReaderJob job = ad.buildReaderJob(sessionId, cls, storage, cmdType,
                text, paras);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        List<T> list = scope.execute(cls, job);
        return list;
    }

    public <T extends IAlbianObject> List<T> loadObject(String sessionId, Class<T> cls, RunningStorageAttribute storage, CommandOpt cmdType,
                                                        String text, Map<String, SqlParameter> paras)  {
        return loadObjects(sessionId, cls, storage, cmdType, text, paras);
    }


    //-------增加强制制定索引名字

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, QueryToOpt loadType, IChainExpression wheres, String idxName)
            {
        return this.loadObject(sessionId, cls, loadType, null, wheres, idxName);
    }

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls,
                                                  QueryToOpt loadType, String rountingName, IChainExpression wheres, String idxName)
            {
        List<T> list = doLoadObjects(sessionId, cls, QueryToOpt.WriterRouter == loadType, rountingName, 0, 0, wheres, null, idxName);
        if (SetUtil.isNullOrEmpty(list))
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

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, QueryToOpt loadType, IChainExpression wheres, String idxName)
            {
        return loadObjects(sessionId, cls, loadType, null, wheres, null, idxName);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, QueryToOpt loadType,
                                                         IChainExpression wheres, LinkedList<OrderByCondition> orderbys, String idxName)
            {
        return loadObjects(sessionId, cls, loadType, null, wheres, orderbys, idxName);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, QueryToOpt loadType, String rountingName,
                                                         IChainExpression wheres, LinkedList<OrderByCondition> orderbys, String idxName)
            {
        return loadObjects(sessionId, cls, loadType, 0, 0, wheres, orderbys, idxName);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, QueryToOpt loadType,
                                                         int start, int step, IChainExpression wheres, String idxName)
            {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, null, idxName);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, QueryToOpt loadType,
                                                         int start, int step, IChainExpression wheres, LinkedList<OrderByCondition> orderbys, String idxName)
            {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, orderbys, idxName);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, QueryToOpt loadType, String rountingName,
                                                         int start, int step, IChainExpression wheres, LinkedList<OrderByCondition> orderbys, String idxName)
            {
        List<T> list = doLoadObjects(sessionId, cls, QueryToOpt.WriterRouter == loadType, rountingName, start, step, wheres, orderbys, idxName);
        if (SetUtil.isNullOrEmpty(list))
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
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, QueryToOpt loadType, String idxName)
            {
        return this.loadObjects(sessionId, cls, loadType, new FilterExpression(), idxName);
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
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, QueryToOpt loadType, LinkedList<OrderByCondition> orderbys, String idxName)
            {
        return this.loadObjects(sessionId, cls, loadType, new FilterExpression(), orderbys, idxName);
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
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, QueryToOpt loadType,
                                                            String rountingName, LinkedList<OrderByCondition> orderbys, String idxName)
            {
        return this.loadObjects(sessionId, cls, loadType, rountingName, new FilterExpression(), orderbys, idxName);

    }

    public <T extends IAlbianObject> T loadObjectById(String sessionId, Class<T> cls, QueryToOpt loadType, BigInteger id, String idxName)
            {
        IChainExpression ce = new FilterExpression("id", OperatorOpt.eq, id);
        return loadObject(sessionId, cls, loadType, ce, idxName);
    }

    public <T extends IAlbianObject> T loadObjectById(String sessionId, Class<T> cls, QueryToOpt loadType, String rountingName, BigInteger id, String idxName)
            {
        IChainExpression ce = new FilterExpression("id", OperatorOpt.eq, id);
        return loadObject(sessionId, cls, loadType, rountingName, ce, idxName);
    }

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           QueryToOpt loadType, IChainExpression wheres, String idxName)
            {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres, idxName);
    }

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           QueryToOpt loadType, String rountingName, IChainExpression wheres, String idxName)
            {
        return doLoadPageingCount(sessionId, cls, QueryToOpt.WriterRouter == loadType, rountingName, wheres, null, idxName);

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
    public IIduCtx newDataAccessContext() {

        return new IduCtx();
    }

    public ISltCtx newQueryContext() {
        return new SltCtx();
    }

}
