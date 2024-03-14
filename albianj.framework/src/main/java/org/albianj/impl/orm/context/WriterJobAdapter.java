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
package org.albianj.impl.orm.context;

import org.albianj.AblThrowable;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.impl.orm.db.localize.MysqlClientSection;
import org.albianj.impl.orm.db.localize.SqlServerClientSection;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.ServRouter;
import org.albianj.orm.context.IWriterJob;
import org.albianj.orm.db.IPersistenceCommand;
import org.albianj.impl.orm.db.IPersistenceUpdateCommand;
import org.albianj.orm.object.*;
import org.albianj.orm.service.AlbianEntityMetadata;
import org.albianj.orm.service.IAlbianStorageParserService;

import java.util.*;

public class WriterJobAdapter extends FreeWriterJobAdapter {

    protected Map<String, Object> buildSqlParameter(String sessioId, IAlbianObject object,
                                                    IAlbianObjectAttribute albianObject,
                                                    Map<String, IAlbianEntityFieldAttribute> fieldsAttr)  {
        Map<String, Object> mapValue = new HashMap<String, Object>();
        String name = "";
        for (IAlbianEntityFieldAttribute fAttr : fieldsAttr.values()) {
            name = fAttr.getPropertyName();
            try {
                if ("string".equalsIgnoreCase(fAttr.getEntityField().getType()
                        .getSimpleName())) {
                    Object oValue = fAttr.getEntityField().get(object);
                    if (null == oValue) {
                        mapValue.put(name, null);
                    } else {
                        String value = oValue.toString();
                        if ((-1 == fAttr.getLength()) || (fAttr.getLength() >= value.length())) {
                            mapValue.put(name, value);
                        } else {
                            mapValue.put(name,
                                    value.substring(0, fAttr.getLength()));
                        }
                    }
                } else {
                    mapValue.put(name, fAttr.getEntityField().get(object));
                }
            } catch (Exception e) {
               ServRouter.logAndThrowAgain(sessioId,LogLevel.Error,e,
                      "invoke bean read method is error.the property is:{} .job id:{}.",
                       albianObject.getType(),name);
            }

        }

        return mapValue;
    }

    protected List<IDataRouterAttribute> parserRoutings(String sessionId, IAlbianObject object,
                                                        IDataRoutersAttribute routings, IAlbianObjectAttribute albianObject) {
        List<IDataRouterAttribute> useRoutings = new Vector<IDataRouterAttribute>();
        if (null == routings) {
            IDataRouterAttribute dra = albianObject.getDefaultRouting();
            ServRouter.log(sessionId,  LogLevel.Warn,
                    "albian-object:{} writer-data-routers are null then use default storage:{}.",
                albianObject.getType(), dra.getName());
            useRoutings.add(dra);
        } else {
            if (SetUtil.isNullOrEmpty(routings.getWriterRouters())) {
                IDataRouterAttribute dra = albianObject.getDefaultRouting();
                ServRouter.log(sessionId,  LogLevel.Warn,
                        "albian-object:{} writer-data-routers are null then use default storage:{}.",
                    albianObject.getType(), dra.getName());
                useRoutings.add(dra);
            } else {
                if (routings.getWriterRouterEnable()) {
                    IAlbianObjectDataRouter hashMapping = routings
                            .getDataRouter();
                    if (null == hashMapping) {
                        Map<String, IDataRouterAttribute> wrs = routings.getWriterRouters();
                        List<IDataRouterAttribute> ras = new Vector<IDataRouterAttribute>();
                        for (IDataRouterAttribute dra : wrs.values()) {
                            if (dra.getEnable()) {
                                ras = new Vector<IDataRouterAttribute>();
                                useRoutings.add(dra);
                                ServRouter.log(sessionId,  LogLevel.Warn,
                                    "albian-object:{} writer-data-router arithmetic is null then use default storage:{}.",
                                    albianObject.getType(), dra.getName());
                                break;
                            }
                        }
                    } else {
                        List<IDataRouterAttribute> writerRoutings = hashMapping
                                .mappingWriterRouting(
                                        routings.getWriterRouters(), object);
                        if (SetUtil.isNullOrEmpty(writerRoutings)) {
                            IDataRouterAttribute dra = albianObject.getDefaultRouting();
                            ServRouter.log(sessionId,  LogLevel.Warn,
                                "albian-object:{} writer-data-router arithmetic is null then use default storage:{}.",
                                albianObject.getType(), dra.getName());
                            useRoutings.add(dra);
                        } else {
                            for (IDataRouterAttribute writerRouting : writerRoutings) {
                                if (writerRouting.getEnable()) {
                                    useRoutings.add(writerRouting);
                                }
                            }
                            if (SetUtil.isNullOrEmpty(useRoutings)) {
                                IDataRouterAttribute dra = albianObject.getDefaultRouting();
                                ServRouter.log(sessionId,  LogLevel.Warn,
                                    "albian-object:{} writer-data-router arithmetic is disable then use default storage:{}.",
                                    albianObject.getType(), dra.getName());
                                useRoutings.add(dra);
                            }
                        }
                    }
                }
            }
        }
        return useRoutings;
    }

    protected String parserRoutingStorage(String jobId, IAlbianObject obj,
                                          IDataRouterAttribute routing, IAlbianObjectDataRouter hashMapping,
                                          IAlbianObjectAttribute albianObject)   {
        if (null == routing) {
            throw new AblThrowable("the writer data router of object:"+albianObject.getType()+" is null.");
        }
        if (null == hashMapping) {
            String name = routing.getStorageName();
            ServRouter.log(jobId,  LogLevel.Warn,
                    "albian-object:{} writer-data-router arithmetic is not found and use default storage:{}.",
                albianObject.getType(), name);
            return name;
        } else {
            String name = hashMapping.mappingWriterRoutingStorage(routing, obj);
            if (StringsUtil.isNullOrEmpty(name)) {
                String dname = routing.getStorageName();
                ServRouter.log(jobId,  LogLevel.Warn,
                        "albian-object:{} writer-data-router is not found by arithmetic and use default storage:{}.",
                        albianObject.getType(), dname);
                return dname;
            } else {
                return name;
            }
        }
    }

    protected String parserRoutingDatabase(String jobId, IAlbianObject obj,
                                           IStorageAttribute storage, IAlbianObjectDataRouter hashMapping,
                                           IAlbianObjectAttribute albianObject)   {
        if (null == storage) {
            throw new AblThrowable(
                "the writer data router of object:" + albianObject.getType() + " is null.");
        }
        if (null == hashMapping) {
            String name = storage.getDatabase();
            ServRouter.log(jobId,  LogLevel.Warn,
                    "albian-object:{} writer-data-router arithmetic is not found and use default database:{}.",
                albianObject.getType(), name);
            return name;
        } else {
            String name = hashMapping.mappingWriterRoutingDatabase(storage, obj);
            if (StringsUtil.isNullOrEmpty(name)) {
                String dname = storage.getDatabase();
                ServRouter.log(jobId,  LogLevel.Warn,
                        "albian-object:{} writer-data-router is not found by arithmetic and use default database:{}.",
                        albianObject.getType(), dname);
                return dname;
            } else {
                return name;
            }
        }
    }


    protected void buildWriterJob(String sessionId, IWriterJob job, IAlbianObject entity,
                                  String storageAlias, String tableAlias,
                                  IPersistenceUpdateCommand cmd)  {
        Class<?> cls = entity.getClass();
        String className = cls.getName();
        IAlbianObjectAttribute objAttr = AlbianEntityMetadata.getEntityMetadataByType(cls);

        Map<String, IAlbianEntityFieldAttribute> fieldsAttr = objAttr.getFields();
        if (SetUtil.isNullOrEmpty(fieldsAttr)) {
            throw new AblThrowable("albian-object:" + className + " PropertyDescriptor is not found.");
        }
        Map<String, Object> sqlParaVals = buildSqlParameter(job.getId(), entity,
                objAttr, fieldsAttr);

        IAlbianStorageParserService asps = ServRouter.getService(sessionId,IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        if (!StringsUtil.isNullOrEmptyOrAllSpace(storageAlias)) {
            String tableName = StringsUtil.isNullOrEmptyOrAllSpace(tableAlias)
                    ? objAttr.getImplClzz().getSimpleName()
                    : tableAlias;
            IStorageAttribute stgAttr = asps.getStorageAttribute(storageAlias);
            IPersistenceCommand pstCmd = cmd.buildPstCmd(job.getId(), stgAttr.getDatabaseStyle(),
                    tableName, entity, objAttr, sqlParaVals, job.isRollbackOnError());
            addWrtTsk(job, stgAttr, stgAttr.getDatabase(), pstCmd);
        } else {
            IDataRoutersAttribute drtsAttr = objAttr.getDataRouters();
            List<IDataRouterAttribute> sltDrtAttr = parserRoutings(job.getId(), entity,
                    drtsAttr, objAttr);
            for (IDataRouterAttribute drtAttr : sltDrtAttr) {
                IAlbianObjectDataRouter drouter = null == drtsAttr ? null : drtsAttr.getDataRouter();
                String storageName = parserRoutingStorage(job.getId(), entity, drtAttr,
                        drouter, objAttr);
                IStorageAttribute stgAttr = asps.getStorageAttribute(storageName);
                String database = parserRoutingDatabase(job.getId(), entity, stgAttr,
                        drouter, objAttr);

                String tableName = drouter.mappingWriterTable(drtAttr, entity);

                IPersistenceCommand pstCmd = cmd.buildPstCmd(job.getId(), stgAttr.getDatabaseStyle(),
                        tableName, entity, objAttr, sqlParaVals, job.isRollbackOnError());
                if (null == cmd)
                    continue;// no the upload operator

                addWrtTsk(job, stgAttr, database, pstCmd);
            }
        }
    }

    private void addWrtTsk(IWriterJob job, IStorageAttribute storage, String database, IPersistenceCommand pstCmd) {
        String key = storage.getName() + database;
        if (SetUtil.isNull(job.getWriterTasks())) {
            Map<String, WriterTask> tasks = new LinkedHashMap<>();
            WriterTask task = new WriterTask();
            List<IPersistenceCommand> cmds = new Vector<>();
            cmds.add(pstCmd);
            task.setCommands(cmds);
            task.setStorage(new RunningStorageAttribute(storage, database));
            tasks.put(key, task);
            if(PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
                task.setClientSection(new MysqlClientSection());
            } else if(PersistenceDatabaseStyle.SqlServer == storage.getDatabaseStyle()) {
                task.setClientSection(new SqlServerClientSection());
            }
            job.setWriterTasks(tasks);
        } else {
            if (job.getWriterTasks().containsKey(key)) {
                job.getWriterTasks().get(key).getCommands().add(pstCmd);
            } else {
                WriterTask task = new WriterTask();
                List<IPersistenceCommand> cmds = new Vector<>();
                cmds.add(pstCmd);
                task.setCommands(cmds);
                task.setStorage(new RunningStorageAttribute(storage, database));
                if(PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
                    task.setClientSection(new MysqlClientSection());
                } else if(PersistenceDatabaseStyle.SqlServer == storage.getDatabaseStyle()) {
                    task.setClientSection(new SqlServerClientSection());
                }
                job.getWriterTasks().put(key, task);
            }
        }

    }

}
