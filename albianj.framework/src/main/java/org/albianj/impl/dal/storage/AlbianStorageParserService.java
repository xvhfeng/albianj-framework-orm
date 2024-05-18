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
package org.albianj.impl.dal.storage;

import org.albianj.AblThrowable;
import org.albianj.ServRouter;
import org.albianj.api.dal.object.StgTempAttr;
import org.albianj.common.utils.BeanUtil;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.common.utils.XmlUtil;
import org.albianj.api.dal.object.StgAttr;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.api.kernel.anno.serv.AblServRant;
import org.albianj.api.dal.db.IDBP;
import org.albianj.api.dal.object.DBOpt;
import org.albianj.api.dal.object.RStgAttr;
import org.albianj.api.dal.service.IAlbianStorageParserService;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.albianj.api.dal.object.DBPOpt.SpxDBCP;


@AblServRant(Id = IAlbianStorageParserService.Name, Interface = IAlbianStorageParserService.class)
public class AlbianStorageParserService extends FreeAlbianStorageParserService {

    public final static String DEFAULT_STORAGE_NAME = "!@#$%Albianj_Default_Storage%$#@!";
    private ConcurrentMap<String, IDBP> pools = null;

    // <Storage>
    // <Name>1thStorage</Name>
    // <DatabaseStyle>MySql</DatabaseStyle>
    // <Server>localhost</Server>
    // <Database>BaseInfo</Database>
    // <Uid>root</Uid>
    // <Password>xuhf</Password>
    // <Pooling>false</Pooling>
    // <MinPoolSize>10</MinPoolSize>
    // <MaxPoolSize>20</MaxPoolSize>
    // <Timeout>60</Timeout>
    // <Charset>gb2312</Charset>
    // <Transactional>true</Transactional>
    // <TransactionLevel>0</TransactinLevel>
    // </Storag

    public String getServiceName() {
        return Name;
    }

    @Override
    public void init()  {
        pools = new ConcurrentHashMap<>(64);
        super.init();
    }

    @Override
    protected void parserStorages(@SuppressWarnings("rawtypes") List nodes, Map<String,StgTempAttr> maps)   {
        if (SetUtil.isEmpty(nodes)) {
            ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Error,
                    "Storage node is null or size is 0.");
            return;
        }
        for (int i = 0; i < nodes.size(); i++) {
            StgAttr storage = parserStorage((Element)nodes.get(i),maps);
            if (null == storage) {
                throw new AblThrowable(
                    "parser storage in the storage.xml is fail.xml:" + ((Element)nodes.get(i)).asXML() + ".");
            }
            addStorageAttribute(storage.getName(), storage);
            if (i == 0) {
                addStorageAttribute(DEFAULT_STORAGE_NAME, storage);
            }
        }
    }

    protected StgTempAttr parserStgTemp(Element node){

        StgTempAttr tempAttr = new StgTempAttr();

        String name = XmlUtil.getSingleChildNodeValue(node, "Name");
        if (null == name) {
            ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,
                    "There is no name attribute in the storage node.");
            return null;
        }
        tempAttr.setName(name);

        String databaseStyle = XmlUtil.getSingleChildNodeValue(node, "DatabaseStyle");
        if (null == databaseStyle) {
            tempAttr.setDatabaseStyle(DBOpt.MySql);
        } else {
            switch (databaseStyle.trim().toLowerCase()) {
                case "sqlserver": {
                    tempAttr.setDatabaseStyle(DBOpt.SqlServer);
                    break;
                }
                case "oracle": {
                    tempAttr.setDatabaseStyle(DBOpt.Oracle);
                    break;
                }
                case "pgsql": {
                    tempAttr.setDatabaseStyle(DBOpt.PgSql);
                    break;
                }
                case "redshift": {
                    tempAttr.setDatabaseStyle(DBOpt.RedShift);
                    break;
                }
                default: {
                    tempAttr.setDatabaseStyle(DBOpt.MySql);
                }
            }
        }

        String server = XmlUtil.getSingleChildNodeValue(node, "Server");
        if(!StringsUtil.isNullEmptyTrimmed(server)) {
            tempAttr.setServer(server);
        }

        String port = XmlUtil.getSingleChildNodeValue(node, "Port");
        if(!StringsUtil.isNullEmptyTrimmed(port)) {
            tempAttr.setPort(Integer.parseInt(port));
        }

        String user = XmlUtil.getSingleChildNodeValue(node, "User");
        if(!StringsUtil.isNullEmptyTrimmed(user)) {
            tempAttr.setUser(user);
        }

        String password = XmlUtil.getSingleChildNodeValue(node, "Password");
        if(!StringsUtil.isNullEmptyTrimmed(password)) {
            tempAttr.setPassword(password);
        }

        String pooling = XmlUtil.getSingleChildNodeValue(node, "Pooling");
        if(!StringsUtil.isNullEmptyTrimmed(pooling)) {
            tempAttr.setPooling(Boolean.parseBoolean(pooling));
        }

        String minPoolSize = XmlUtil.getSingleChildNodeValue(node, "MinPoolSize");
        if(!StringsUtil.isNullEmptyTrimmed(minPoolSize)) {
            int minsize = Integer.parseInt(minPoolSize);
            tempAttr.setMinSize(Math.max(minsize, 2));
        }

        String maxPoolSize = XmlUtil.getSingleChildNodeValue(node, "MaxPoolSize");
        if(!StringsUtil.isNullEmptyTrimmed(maxPoolSize)) {
            int maxsize = Integer.parseInt(maxPoolSize);
            tempAttr.setMaxSize(Math.max(maxsize, 20));
        }

        String timeout = XmlUtil.getSingleChildNodeValue(node, "Timeout");
        if(!StringsUtil.isNullEmptyTrimmed(timeout)) {
            int to = Integer.parseInt(timeout);
            tempAttr.setTimeout(to);
        }

        String charset = XmlUtil.getSingleChildNodeValue(node, "Charset");
        if(!StringsUtil.isNullEmptyTrimmed(charset)) {
            tempAttr.setCharset(charset);
        }

        String transactional = XmlUtil.getSingleChildNodeValue(node, "Transactional");
        if(!StringsUtil.isNullEmptyTrimmed(transactional)) {
            tempAttr.setTransactional(Boolean.parseBoolean(transactional));
        }

        String transactionLevel = XmlUtil.getSingleChildNodeValue(node, "TransactionLevel");
        if (tempAttr.isTransactional()) {
            if (!StringsUtil.isNotEmptyTrimmed(transactionLevel)) {
                if (transactionLevel.equalsIgnoreCase("READ_UNCOMMITTED")) {
                    tempAttr.setTransactionLevel(Connection.TRANSACTION_READ_UNCOMMITTED);
                } else if (transactionLevel.equalsIgnoreCase("READ_COMMITTED")) {
                    tempAttr.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
                } else if (transactionLevel.equalsIgnoreCase("REPEATABLE_READ")) {
                    tempAttr.setTransactionLevel(Connection.TRANSACTION_REPEATABLE_READ);
                } else if (transactionLevel.equalsIgnoreCase("SERIALIZABLE")) {
                    tempAttr.setTransactionLevel(Connection.TRANSACTION_SERIALIZABLE);
                } else {
                    // default level and do not means no suppert tran
                    tempAttr.setTransactionLevel(Connection.TRANSACTION_NONE);
                }
            }
        }

        String options = XmlUtil.getSingleChildNodeValue(node, "Options");
        if(!StringsUtil.isNullEmptyTrimmed(options)) {
            tempAttr.setOptions(options);
        }

        String sidleTime = XmlUtil.getSingleChildNodeValue(node, "AliveTime");
        if(!StringsUtil.isNullEmptyTrimmed(sidleTime)) {
            tempAttr.setIdelTime(Integer.parseInt(sidleTime));
        }

        String sDatabasePoolStyle = XmlUtil.getSingleChildNodeValue(node, "PoolStyle");
        if(!StringsUtil.isNullEmptyTrimmed(sDatabasePoolStyle)) {
            tempAttr.setDbps(sDatabasePoolStyle);
        }

        String sUrlParaments = XmlUtil.getSingleChildNodeValue(node, "UrlParaments");
        if(!StringsUtil.isNullEmptyTrimmed(sUrlParaments)) {
            tempAttr.setUrlParaments(sUrlParaments);
        }

        String sWaitTimeWhenGetMs = XmlUtil.getSingleChildNodeValue(node, "WaitTimeWhenGetMs");
        String sLifeCycleTime = XmlUtil.getSingleChildNodeValue(node, "LifeCycleTime");
        String sWaitInFreePoolMs = XmlUtil.getSingleChildNodeValue(node, "WaitInFreePoolMs");
        String sMaxRemedyConnectionCount = XmlUtil.getSingleChildNodeValue(node, "MaxRemedyConnectionCount");
        String sCleanupTimestampMs = XmlUtil.getSingleChildNodeValue(node, "CleanupTimestampMs");
        String sMaxRequestTimeMs = XmlUtil.getSingleChildNodeValue(node, "MaxRequestTimeMs");

        if (!StringsUtil.isNullEmptyTrimmed(sWaitTimeWhenGetMs)) {
            tempAttr.setWaitTimeWhenGetMs(Integer.parseInt(sWaitTimeWhenGetMs));
        }
        if (!StringsUtil.isNullEmptyTrimmed(sLifeCycleTime)) {
            tempAttr.setLifeCycleTime(Integer.parseInt(sLifeCycleTime));
        }
        if (!StringsUtil.isNullEmptyTrimmed(sWaitInFreePoolMs)) {
            tempAttr.setWaitInFreePoolMs(Integer.parseInt(sWaitInFreePoolMs));
        }
        if (!StringsUtil.isNullEmptyTrimmed(sMaxRemedyConnectionCount)) {
            tempAttr.setMaxRemedyConnectionCount(Integer.parseInt(sMaxRemedyConnectionCount));
        }
        if (!StringsUtil.isNullEmptyTrimmed(sCleanupTimestampMs)) {
            tempAttr.setCleanupTimestampMs(Integer.parseInt(sCleanupTimestampMs));
        }
        if (!StringsUtil.isNullEmptyTrimmed(sMaxRequestTimeMs)) {
            tempAttr.setMaxRequestTimeMs(Integer.parseInt(sMaxRequestTimeMs));
        }

        return tempAttr;
    }

    @Override
    protected StgAttr parserStorage(Element node, Map<String,StgTempAttr> maps) {
        StgAttr storage = new StgAttr();

        String name = XmlUtil.getSingleChildNodeValue(node, "Name");
        if (null == name) {
            ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,
                    "There is no name attribute in the storage node.");
            return null;
        }
        storage.setName(name);

        String database = XmlUtil.getSingleChildNodeValue(node, "Database");
        if (null == database) {
            ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,
                    "There is no database attribute in the storage node.");
            return null;
        }
        storage.setDatabase(database);

        String template = XmlUtil.getSingleChildNodeValue(node, "Template");
        if(!StringsUtil.isNullEmptyTrimmed(template)){
           StgTempAttr stgTempAttr =  maps.get(template);
           BeanUtil.copy(storage,stgTempAttr,new HashSet<String>(){{
               add("name");
               add("database");
           }}, null);
           storage.setTemplateName(template);
        }

        String databaseStyle = XmlUtil.getSingleChildNodeValue(node, "DatabaseStyle");
        if (null == databaseStyle) {
            storage.setDatabaseStyle(DBOpt.MySql);
        } else {
            switch (databaseStyle.trim().toLowerCase()) {
                case "sqlserver": {
                    storage.setDatabaseStyle(DBOpt.SqlServer);
                    break;
                }
                case "oracle": {
                    storage.setDatabaseStyle(DBOpt.Oracle);
                    break;
                }
                case "pgsql": {
                    storage.setDatabaseStyle(DBOpt.PgSql);
                    break;
                }
                case "redshift": {
                    storage.setDatabaseStyle(DBOpt.RedShift);
                    break;
                }
                default: {
                    storage.setDatabaseStyle(DBOpt.MySql);
                }
            }
        }

        String server = XmlUtil.getSingleChildNodeValue(node, "Server");
        if(!StringsUtil.isNullEmptyTrimmed(server)) {
            storage.setServer(server);
        }

        String port = XmlUtil.getSingleChildNodeValue(node, "Port");
        if(!StringsUtil.isNullEmptyTrimmed(port)) {
            storage.setPort(Integer.parseInt(port));
        }

        String user = XmlUtil.getSingleChildNodeValue(node, "User");
        if(!StringsUtil.isNullEmptyTrimmed(user)) {
            storage.setUser(user);
        }

        String password = XmlUtil.getSingleChildNodeValue(node, "Password");
        if(!StringsUtil.isNullEmptyTrimmed(password)) {
            storage.setPassword(password);
        }

        String pooling = XmlUtil.getSingleChildNodeValue(node, "Pooling");
        if(!StringsUtil.isNullEmptyTrimmed(pooling)) {
            storage.setPooling(Boolean.parseBoolean(pooling));
        }

        String minPoolSize = XmlUtil.getSingleChildNodeValue(node, "MinPoolSize");
        if(!StringsUtil.isNullEmptyTrimmed(minPoolSize)) {
            int minsize = Integer.parseInt(minPoolSize);
            storage.setMinSize(Math.max(minsize, 2));
        }

        String maxPoolSize = XmlUtil.getSingleChildNodeValue(node, "MaxPoolSize");
        if(!StringsUtil.isNullEmptyTrimmed(maxPoolSize)) {
            int maxsize = Integer.parseInt(maxPoolSize);
            storage.setMaxSize(Math.max(maxsize, 20));
        }

        String timeout = XmlUtil.getSingleChildNodeValue(node, "Timeout");
        if(!StringsUtil.isNullEmptyTrimmed(timeout)) {
            int to = Integer.parseInt(timeout);
            storage.setTimeout(to);
        }

        String charset = XmlUtil.getSingleChildNodeValue(node, "Charset");
        if(!StringsUtil.isNullEmptyTrimmed(charset)) {
            storage.setCharset(charset);
        }

        String transactional = XmlUtil.getSingleChildNodeValue(node, "Transactional");
        if(!StringsUtil.isNullEmptyTrimmed(transactional)) {
            storage.setTransactional(Boolean.parseBoolean(transactional));
        }

        String transactionLevel = XmlUtil.getSingleChildNodeValue(node, "TransactionLevel");
        if (storage.isTransactional()) {
            if (!StringsUtil.isNullEmptyTrimmed(transactionLevel)) {
                if (transactionLevel.equalsIgnoreCase("READ_UNCOMMITTED")) {
                    storage.setTransactionLevel(Connection.TRANSACTION_READ_UNCOMMITTED);
                } else if (transactionLevel.equalsIgnoreCase("READ_COMMITTED")) {
                    storage.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
                } else if (transactionLevel.equalsIgnoreCase("REPEATABLE_READ")) {
                    storage.setTransactionLevel(Connection.TRANSACTION_REPEATABLE_READ);
                } else if (transactionLevel.equalsIgnoreCase("SERIALIZABLE")) {
                    storage.setTransactionLevel(Connection.TRANSACTION_SERIALIZABLE);
                } else {
                    // default level and do not means no suppert tran
                    storage.setTransactionLevel(Connection.TRANSACTION_NONE);
                }
            }
        }

        String options = XmlUtil.getSingleChildNodeValue(node, "Options");
        if(!StringsUtil.isNullEmptyTrimmed(options)) {
            storage.setOptions(options);
        }

        String sidleTime = XmlUtil.getSingleChildNodeValue(node, "AliveTime");
        if(!StringsUtil.isNullEmptyTrimmed(sidleTime)) {
            storage.setIdelTime(Integer.parseInt(sidleTime));
        }

        String sDatabasePoolStyle = XmlUtil.getSingleChildNodeValue(node, "PoolStyle");
        if(!StringsUtil.isNullEmptyTrimmed(sDatabasePoolStyle)) {
            storage.setDbps(sDatabasePoolStyle);
        }

        String sUrlParaments = XmlUtil.getSingleChildNodeValue(node, "UrlParaments");
        if(!StringsUtil.isNullEmptyTrimmed(sUrlParaments)) {
            storage.setUrlParaments(sUrlParaments);
        }

        String sWaitTimeWhenGetMs = XmlUtil.getSingleChildNodeValue(node, "WaitTimeWhenGetMs");
        String sLifeCycleTime = XmlUtil.getSingleChildNodeValue(node, "LifeCycleTime");
        String sWaitInFreePoolMs = XmlUtil.getSingleChildNodeValue(node, "WaitInFreePoolMs");
        String sMaxRemedyConnectionCount = XmlUtil.getSingleChildNodeValue(node, "MaxRemedyConnectionCount");
        String sCleanupTimestampMs = XmlUtil.getSingleChildNodeValue(node, "CleanupTimestampMs");
        String sMaxRequestTimeMs = XmlUtil.getSingleChildNodeValue(node, "MaxRequestTimeMs");

        if (!StringsUtil.isNullEmptyTrimmed(sWaitTimeWhenGetMs)) {
            storage.setWaitTimeWhenGetMs(Integer.parseInt(sWaitTimeWhenGetMs));
        }
        if (!StringsUtil.isNullEmptyTrimmed(sLifeCycleTime)) {
            storage.setLifeCycleTime(Integer.parseInt(sLifeCycleTime));
        }
        if (!StringsUtil.isNullEmptyTrimmed(sWaitInFreePoolMs)) {
            storage.setWaitInFreePoolMs(Integer.parseInt(sWaitInFreePoolMs));
        }
        if (!StringsUtil.isNullEmptyTrimmed(sMaxRemedyConnectionCount)) {
            storage.setMaxRemedyConnectionCount(Integer.parseInt(sMaxRemedyConnectionCount));
        }
        if (!StringsUtil.isNullEmptyTrimmed(sCleanupTimestampMs)) {
            storage.setCleanupTimestampMs(Integer.parseInt(sCleanupTimestampMs));
        }
        if (!StringsUtil.isNullEmptyTrimmed(sMaxRequestTimeMs)) {
            storage.setMaxRequestTimeMs(Integer.parseInt(sMaxRequestTimeMs));
        }

//        if(!StringsUtil.isNullEmptyTrimmed(timeout)) {
//            int to = Integer.parseInt(timeout);
//            storage.setMinSize(to < 2 ? 2 : to);
//        }
//
//        storage.setPooling(StringsUtil.isNullEmptyTrimmed(pooling) ? true :  Boolean.parseBoolean(pooling));
//        int minsize = StringsUtil.isNullEmptyTrimmed(minPoolSize) ? 2 : Integer.parseInt(minPoolSize);
//        minsize = 2 < minsize ? 2 : minsize;
//        storage.setMinSize(minsize);//固定数据库链接池最小的链接为2
//
//        storage.setMaxSize(StringsUtil.isNullEmptyTrimmed(maxPoolSize) ? 20 : Integer.parseInt(maxPoolSize));
//        storage.setTimeout(StringsUtil.isNullEmptyTrimmed(timeout) ? 30 : Integer.parseInt(timeout));
//        storage.setCharset(StringsUtil.isNullEmptyTrimmed(charset) ? null : charset);
//        storage.setTransactional(StringsUtil.isNullEmptyTrimmed(transactional) ? true :  Boolean.parseBoolean(transactional));
//        storage.setIdelTime(StringsUtil.isNullEmptyTrimmed(sidleTime) ? 120 : Integer.parseInt(sidleTime));
//        storage.setDbps(
//            StringsUtil.isNullEmptyTrimmed(sDatabasePoolStyle) ? SpxDBCP.name() : sDatabasePoolStyle);
//        storage.setUrlParaments(sUrlParaments);


//        storage.setOptions(options);
//
//        if (storage.isTransactional()) {
//            if (StringsUtil.isNullEmptyTrimmed(transactionLevel)) {
//                // default level and do not means no suppert tran
//                storage.setTransactionLevel(Connection.TRANSACTION_NONE);
//            } else {
//                if (transactionLevel.equalsIgnoreCase("READ_UNCOMMITTED")) {
//                    storage.setTransactionLevel(Connection.TRANSACTION_READ_UNCOMMITTED);
//                } else if (transactionLevel.equalsIgnoreCase("READ_COMMITTED")) {
//                    storage.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
//                } else if (transactionLevel.equalsIgnoreCase("REPEATABLE_READ")) {
//                    storage.setTransactionLevel(Connection.TRANSACTION_REPEATABLE_READ);
//                } else if (transactionLevel.equalsIgnoreCase("SERIALIZABLE")) {
//                    storage.setTransactionLevel(Connection.TRANSACTION_SERIALIZABLE);
//                } else {
//                    // default level and do not means no suppert tran
//                    storage.setTransactionLevel(Connection.TRANSACTION_NONE);
//                }
//            }
//        }
//
//        if (!StringsUtil.isNullEmptyTrimmed(port)) {
//            storage.setPort(Integer.parseInt(port));
//        }

        return storage;
    }

    public IDBP getDatabasePool(String sessionId, RStgAttr rsa) {
        final StgAttr sa = rsa.getStgAttr();
        String key = sa.getName();
        IDBP dbp = pools.get(key);
        if (dbp != null) {
            return dbp;
        }
        try {
            synchronized (rsa.getStgAttr()) {
                //double check
                dbp = pools.get(key);
                if (dbp != null) {
                    return dbp;
                }
                switch (sa.getDbps().toUpperCase()) {
                    case "HIKARICP": {
                        dbp = new HCPWapper();
                        break;
                    }
                    case "SpxDBCP": {
                        dbp = new SpxWapper();
                        break;
                    }
                    default: {
                        if (dbp == null) {
                            dbp = new SpxWapper();
                        }
                        break;
                    }
                }
                pools.putIfAbsent(key, dbp);
            }

            return dbp;
        } catch (Exception e) {
            ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,e,
                    "Get the database connection pool with storage::{} and database::{}  is error.",
                    sa.getName(), rsa.getDatabase());
            return null;
        }
    }

//    public Connection getConnection(String sessionId,IRunningStorageAttribute rsa, boolean isAutoCommit) {
//        return getConnection(sessionId, rsa, isAutoCommit);
//    }

    public Connection getConnection(String sessionId, RStgAttr rsa, boolean isAutoCommit)  {
        StgAttr sa = rsa.getStgAttr();
        //            String key = sa.getName() + rsa.getDatabase();
        try {

            IDBP dbp = getDatabasePool(sessionId, rsa);
            if (null == dbp) {
                ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,
                        "Get the database connection pool with storage::{} and database::{}  is error.",
                    sa.getName(), rsa.getDatabase());
                return null;
            }
            return dbp.getConnection(sessionId, rsa, isAutoCommit);

        } catch (Exception e) {
            ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,e,
                    "Get the connection with storage::{} and database::{} form connection pool is error.",
                sa.getName(), rsa.getDatabase());
            return null;
        }

    }

    public Connection getConnection(String sessionId, IDBP pool, RStgAttr rsa,
                                    boolean isAutoCommit)  {
        StgAttr sa = rsa.getStgAttr();
        try {
            if (null == pool) {
                ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,
                        "Get the database connection pool with storage::{} and database::{}  is error.",
                    sa.getName(), rsa.getDatabase());
                return null;
            }
            return pool.getConnection(sessionId, rsa, isAutoCommit);

        } catch (Exception e) {
            ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,e,
                    "Get the connection with storage::{} and database::{} form connection pool is error.",
                sa.getName(), rsa.getDatabase());
            return null;
        }
    }

    /**
     * @param sessionId
     * @param rsa
     * @param conn
     */
    public void returnConnection(String sessionId, RStgAttr rsa, Connection conn) {
        IDBP dbp = getDatabasePool(sessionId, rsa);
        dbp.returnConnection(sessionId, rsa.getStgAttr().getName(), rsa.getDatabase(), conn);
    }

}
