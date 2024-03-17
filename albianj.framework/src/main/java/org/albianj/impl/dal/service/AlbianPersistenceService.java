/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
package org.albianj.impl.dal.service;

import org.albianj.common.utils.SetUtil;
import org.albianj.api.dal.object.OdrBy;
import org.albianj.impl.dal.context.*;
import org.albianj.impl.dal.db.IPersistenceQueryScope;
import org.albianj.impl.dal.db.IPersistenceTransactionClusterScope;
import org.albianj.impl.dal.db.PersistenceQueryScope;
import org.albianj.impl.dal.db.PersistenceTransactionClusterScope;
import org.albianj.api.kernel.anno.serv.AlbianServiceRant;
import org.albianj.api.kernel.service.FreeAlbianService;
import org.albianj.api.dal.context.ICompensateNotify;
import org.albianj.api.dal.context.IDalNotify;
import org.albianj.api.dal.context.RdrJob;
import org.albianj.api.dal.context.WrtJob;
import org.albianj.api.dal.db.CmdOpt;
import org.albianj.api.dal.object.IAblObj;
import org.albianj.api.dal.object.IFltCdt;
import org.albianj.api.dal.object.filter.IChaExpr;
import org.albianj.api.dal.service.IAlbianPersistenceService;
import org.albianj.api.dal.service.DrOpt;

import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

@AlbianServiceRant(Id = IAlbianPersistenceService.Name, Interface = IAlbianPersistenceService.class)
public class AlbianPersistenceService extends FreeAlbianService implements IAlbianPersistenceService {

    @Deprecated
    protected static <T extends IAblObj> T doLoadObject(String sessionId,
                                                        Class<T> cls, boolean isExact,
                                                        String routingName, LinkedList<IFltCdt> wheres)
            {
        List<T> list = doLoadObjects(sessionId, cls, isExact, routingName, 0, 0, wheres, null, null);
        if (SetUtil.isNullOrEmpty(list))
            return null;
        return list.get(0);
    }

    @Deprecated
    protected static <T extends IAblObj> List<T> doLoadObjects(String sessionId,
                                                               Class<T> cls, boolean isExact, String routingName, int start, int step,
                                                               LinkedList<IFltCdt> wheres,
                                                               LinkedList<OdrBy> orderbys, String idxName)
            {
        IRdrJobAdp ad = new RdrJobAdp();
        List<T> list = null;
                RdrJob job = ad.buildReaderJob(sessionId, cls, isExact, routingName, start, step,
                wheres, orderbys, idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        list = scope.execute(cls, job);
        return list;
    }

    @Deprecated
    protected static <T extends IAblObj> long doLoadPageingCount(String sessionId,
                                                                 Class<T> cls, boolean isExact, String routingName,
                                                                 LinkedList<IFltCdt> wheres,
                                                                 LinkedList<OdrBy> orderbys, String idxName)
            {
        IRdrJobAdp ad = new RdrJobAdp();
                RdrJob job = ad.buildReaderJob(sessionId, cls, isExact, routingName,
                wheres, orderbys, idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        Object o = scope.execute(job);
        return (long) o;
    }

    public String getServiceName() {
        return Name;
    }

    public String makeDetailLogSessionId(String sessionId) {
        return sessionId + "_SPX_LOG";
    }

    @Deprecated
    public boolean create(String sessionId, IAblObj object)  {
        return create(sessionId, object, null, null, null, null);
    }

    @Deprecated
    public boolean create(String sessionId, IAblObj object, IDalNotify notifyCallback,
                          Object notifyCallbackObject, ICompensateNotify compensateCallback,
                          Object compensateCallbackObject)  {
        IWrtJobAdp ja = new WrtJobAdp();
        WrtJob job = ja.buildCreation(sessionId, object);
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

    @Deprecated
    public boolean create(String sessionId, List<? extends IAblObj> objects)  {
        return this.create(sessionId, objects, null, null, null, null);
    }

    @Deprecated
    public boolean create(String sessionId, List<? extends IAblObj> objects, IDalNotify notifyCallback,
                          Object notifyCallbackObject, ICompensateNotify compensateCallback,
                          Object compensateCallbackObject)  {
        IWrtJobAdp ja = new WrtJobAdp();
        WrtJob job = ja.buildCreation(sessionId, objects);
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

    @Deprecated
    public boolean modify(String sessionId, IAblObj object)  {
        return this.modify(sessionId, object, null, null, null, null);
    }

    @Deprecated
    public boolean modify(String sessionId, IAblObj object, IDalNotify notifyCallback,
                          Object notifyCallbackObject, ICompensateNotify compensateCallback,
                          Object compensateCallbackObject)  {
        IWrtJobAdp ja = new WrtJobAdp();
        WrtJob job = ja.buildModification(sessionId, object);
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

    @Deprecated
    public boolean modify(String sessionId, List<? extends IAblObj> objects)  {
        return this.modify(sessionId, objects, null, null, null, null);
    }

    @Deprecated
    public boolean modify(String sessionId, List<? extends IAblObj> objects,
                          IDalNotify notifyCallback,
                          Object notifyCallbackObject, ICompensateNotify compensateCallback,
                          Object compensateCallbackObject)  {
        IWrtJobAdp ja = new WrtJobAdp();
        WrtJob job = ja.buildModification(sessionId, objects);
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
        if (DrOpt.Wtr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, 0, 0, wheres, null, null);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

        if (DrOpt.Rdr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, 0, 0, wheres, null, null);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

//        T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
//        if (null != obj)
//            return obj;

        T newObj = doLoadObject(sessionId, cls, false, rountingName, wheres, null);
        if (null == newObj)
            return null;
//        AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
        return newObj;
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
        if (DrOpt.Wtr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, start, step, wheres, orderbys, null);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }
        if (DrOpt.Rdr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys, null);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }

//        List<T> objs = AlbianPersistenceCache.findObjects(cls, start, step, wheres, orderbys);
//        if (null != objs)
//            return objs;

        List<T> objs = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys, null);
        if (null == objs)
            return null;
//        AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, objs);
        return objs;
    }

    public <T extends IAblObj> long loadObjectsCount(String sessionId, Class<T> cls,
                                                     DrOpt loadType, IChaExpr wheres)
            {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres);
    }

    public <T extends IAblObj> long loadObjectsCount(String sessionId, Class<T> cls,
                                                     DrOpt loadType, String rountingName, IChaExpr wheres)
            {
        long count = 0;
        if (DrOpt.Wtr == loadType) {
            count = doLoadPageingCount(sessionId, cls, true, rountingName, wheres, null, null);
//            AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
        } else {
            if (DrOpt.Rdr == loadType) {
                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null, null);
//                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
            } else {
//                count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
//                if (0 <= count) {
//                    return count;
//                }
                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null, null);
//                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
            }

        }

        return count;
    }

    @Deprecated
    public <T extends IAblObj> T loadObject(String sessionId, Class<T> cls, DrOpt loadType, LinkedList<IFltCdt> wheres)
            {
        return this.loadObject(sessionId, cls, loadType, null, wheres);
    }

    @Deprecated
    public <T extends IAblObj> T loadObject(String sessionId, Class<T> cls,
                                            DrOpt loadType, String rountingName, LinkedList<IFltCdt> wheres)
            {
        if (DrOpt.Wtr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, 0, 0, wheres, null, null);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

        if (DrOpt.Rdr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, 0, 0, wheres, null, null);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

//        T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
//        if (null != obj)
//            return obj;

        T newObj = doLoadObject(sessionId, cls, false, rountingName, wheres);
        if (null == newObj)
            return null;
//        AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
        return newObj;
    }

    public <T extends IAblObj> T loadObject(String sessionId, Class<T> cls, CmdOpt cmdType,
                                            Statement statement)  {
        List<T> list = doLoadObjects(sessionId, cls, cmdType, statement);
        if (SetUtil.isNullOrEmpty(list))
            return null;
        return list.get(0);
    }

    @Deprecated
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType, LinkedList<IFltCdt> wheres)
            {
        return loadObjects(sessionId, cls, loadType, 0, 0, wheres, null, null);
    }

    @Deprecated
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                   LinkedList<IFltCdt> wheres, LinkedList<OdrBy> orderbys)
            {
        return loadObjects(sessionId, cls, loadType, 0, 0, wheres, orderbys);
    }

    @Deprecated
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType, String rountingName,
                                                   LinkedList<IFltCdt> wheres, LinkedList<OdrBy> orderbys)
            {
        return loadObjects(sessionId, cls, loadType, rountingName, 0, 0, wheres, orderbys);
    }

    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, CmdOpt cmdType,
                                                   Statement statement)  {
        List<T> list = doLoadObjects(sessionId, cls, cmdType, statement);
        if (SetUtil.isNullOrEmpty(list))
            return null;
        return list;
    }

    @Deprecated
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                   int start, int step, LinkedList<IFltCdt> wheres)
            {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, null);
    }

    @Deprecated
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                   int start, int step, LinkedList<IFltCdt> wheres, LinkedList<OdrBy> orderbys)
            {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, orderbys);
    }

    @Deprecated
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType, String rountingName,
                                                   int start, int step, LinkedList<IFltCdt> wheres, LinkedList<OdrBy> orderbys)
            {
        if (DrOpt.Wtr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, start, step, wheres, orderbys, null);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }
        if (DrOpt.Rdr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys, null);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }

//        List<T> objs = AlbianPersistenceCache.findObjects(cls, start, step, wheres, orderbys);
//        if (null != objs)
//            return objs;

        List<T> objs = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys, null);
        if (null == objs)
            return null;
//        AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, objs);
        return objs;
    }

    @Deprecated
    public <T extends IAblObj> long loadObjectsCount(String sessionId, Class<T> cls,
                                                     DrOpt loadType, LinkedList<IFltCdt> wheres)
            {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres);
    }

    @Deprecated
    public <T extends IAblObj> long loadObjectsCount(String sessionId, Class<T> cls,
                                                     DrOpt loadType, String rountingName, LinkedList<IFltCdt> wheres)
            {
        if (DrOpt.Wtr == loadType) {
            long count = doLoadPageingCount(sessionId, cls, true, rountingName, wheres, null, null);
            return count;
        }
        if (DrOpt.Rdr == loadType) {
            long count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null, null);
            return count;
        }

//        long count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
//        if (0 <= count) {
//            return count;
//        }
        long count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null, null);

//        AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
        return count;
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
        if (SetUtil.isNullOrEmpty(list))
            return null;
        return list.get(0);
    }

    protected <T extends IAblObj> T doLoadObject(String sessionId,
                                                 Class<T> cls, boolean isExact,
                                                 String routingName, IChaExpr wheres, String idxName)
            {
        List<T> list = doLoadObjects(sessionId, cls, isExact, routingName, 0, 0, wheres, null, idxName);
        if (SetUtil.isNullOrEmpty(list))
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


//----- add by 木木，强行指定索引名字

    public <T extends IAblObj> T loadObject(String sessionId, Class<T> cls, DrOpt loadType, IChaExpr wheres, String idxName)
            {
        return this.loadObject(sessionId, cls, loadType, null, wheres, idxName);
    }

    public <T extends IAblObj> T loadObject(String sessionId, Class<T> cls,
                                            DrOpt loadType, String rountingName, IChaExpr wheres, String idxName)
            {
        if (DrOpt.Wtr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, 0, 0, wheres, null, idxName);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

        if (DrOpt.Rdr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, 0, 0, wheres, null, idxName);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

//        T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
//        if (null != obj)
//            return obj;

        T newObj = doLoadObject(sessionId, cls, false, rountingName, wheres, idxName);
        if (null == newObj)
            return null;
//        AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
        return newObj;
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
        if (DrOpt.Wtr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, start, step, wheres, orderbys, idxName);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }
        if (DrOpt.Rdr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys, idxName);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }

//        List<T> objs = AlbianPersistenceCache.findObjects(cls, start, step, wheres, orderbys);
//        if (null != objs)
//            return objs;

        List<T> objs = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys, idxName);
        if (null == objs)
            return null;
//        AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, objs);
        return objs;
    }

    public <T extends IAblObj> long loadObjectsCount(String sessionId, Class<T> cls,
                                                     DrOpt loadType, IChaExpr wheres, String idxName)
            {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres, idxName);
    }

    public <T extends IAblObj> long loadObjectsCount(String sessionId, Class<T> cls,
                                                     DrOpt loadType, String rountingName, IChaExpr wheres, String idxName)
            {
        long count = 0;
        if (DrOpt.Wtr == loadType) {
            count = doLoadPageingCount(sessionId, cls, true, rountingName, wheres, null, idxName);
//            AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
        } else {
            if (DrOpt.Rdr == loadType) {
                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null, idxName);
//                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
            } else {
//                count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
//                if (0 <= count) {
//                    return count;
//                }
                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null, idxName);
//                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
            }

        }

        return count;
    }

    @Deprecated
    public <T extends IAblObj> T loadObject(String sessionId, Class<T> cls, DrOpt loadType, LinkedList<IFltCdt> wheres, String idxName)
            {
        return this.loadObject(sessionId, cls, loadType, null, wheres, idxName);
    }

    @Deprecated
    public <T extends IAblObj> T loadObject(String sessionId, Class<T> cls,
                                            DrOpt loadType, String rountingName, LinkedList<IFltCdt> wheres, String idxName)
            {
        if (DrOpt.Wtr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, 0, 0, wheres, null, idxName);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

        if (DrOpt.Rdr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, 0, 0, wheres, null, idxName);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

//        T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
//        if (null != obj)
//            return obj;

        T newObj = doLoadObject(sessionId, cls, false, rountingName, wheres);
        if (null == newObj)
            return null;
//        AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
        return newObj;
    }

    @Deprecated
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType, LinkedList<IFltCdt> wheres, String idxName)
            {
        return loadObjects(sessionId, cls, loadType, 0, 0, wheres, null, idxName);
    }

    @Deprecated
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                   LinkedList<IFltCdt> wheres, LinkedList<OdrBy> orderbys, String idxName)
            {
        return loadObjects(sessionId, cls, loadType, 0, 0, wheres, orderbys, idxName);
    }

    @Deprecated
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType, String rountingName,
                                                   LinkedList<IFltCdt> wheres, LinkedList<OdrBy> orderbys, String idxName)
            {
        return loadObjects(sessionId, cls, loadType, rountingName, 0, 0, wheres, orderbys, idxName);
    }


    @Deprecated
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                   int start, int step, LinkedList<IFltCdt> wheres, String idxName)
            {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, null, idxName);
    }

    @Deprecated
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType,
                                                   int start, int step, LinkedList<IFltCdt> wheres, LinkedList<OdrBy> orderbys, String idxName)
            {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, orderbys, idxName);
    }

    @Deprecated
    public <T extends IAblObj> List<T> loadObjects(String sessionId, Class<T> cls, DrOpt loadType, String rountingName,
                                                   int start, int step, LinkedList<IFltCdt> wheres, LinkedList<OdrBy> orderbys, String idxName)
            {
        if (DrOpt.Wtr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, start, step, wheres, orderbys, idxName);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }
        if (DrOpt.Rdr == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys, idxName);
            if (SetUtil.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }

//        List<T> objs = AlbianPersistenceCache.findObjects(cls, start, step, wheres, orderbys);
//        if (null != objs)
//            return objs;

        List<T> objs = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys, idxName);
        if (null == objs)
            return null;
//        AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, objs);
        return objs;
    }

    @Deprecated
    public <T extends IAblObj> long loadObjectsCount(String sessionId, Class<T> cls,
                                                     DrOpt loadType, LinkedList<IFltCdt> wheres, String idxName)
            {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres, idxName);
    }

    @Deprecated
    public <T extends IAblObj> long loadObjectsCount(String sessionId, Class<T> cls,
                                                     DrOpt loadType, String rountingName, LinkedList<IFltCdt> wheres, String idxName)
            {
        if (DrOpt.Wtr == loadType) {
            long count = doLoadPageingCount(sessionId, cls, true, rountingName, wheres, null, idxName);
            return count;
        }
        if (DrOpt.Rdr == loadType) {
            long count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null, idxName);
            return count;
        }

//        long count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
//        if (0 <= count) {
//            return count;
//        }
        long count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null, idxName);

//        AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
        return count;
    }
}
