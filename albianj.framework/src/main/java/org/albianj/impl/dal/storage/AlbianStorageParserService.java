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
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.common.utils.XmlUtil;
import org.albianj.api.dal.object.StgAttr;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.api.kernel.anno.serv.AblServiceRant;
import org.albianj.api.dal.db.IDBP;
import org.albianj.api.dal.object.DBOpt;
import org.albianj.api.dal.object.RStgAttr;
import org.albianj.api.dal.service.IAlbianStorageParserService;
import org.dom4j.Element;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.albianj.api.dal.object.DBPOpt.SpxDBCP;


@AblServiceRant(Id = IAlbianStorageParserService.Name, Interface = IAlbianStorageParserService.class)
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
    protected void parserStorages(@SuppressWarnings("rawtypes") List nodes)   {
        if (SetUtil.isNullOrEmpty(nodes)) {
            ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Error,
                    "Storage node is null or size is 0.");
            return;
        }
        for (int i = 0; i < nodes.size(); i++) {
            StgAttr storage = parserStorage((Element)nodes.get(i));
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

    @Override
    protected StgAttr parserStorage(Element node) {
        String name = XmlUtil.getSingleChildNodeValue(node, "Name");
        if (null == name) {
            ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,
                    "There is no name attribute in the storage node.");
            return null;
        }
        String databaseStyle = XmlUtil.getSingleChildNodeValue(node, "DatabaseStyle");
        String server = XmlUtil.getSingleChildNodeValue(node, "Server");
        if (null == server) {
            ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,
                    "There is no server attribute in the storage node.");
            return null;
        }
        String database = XmlUtil.getSingleChildNodeValue(node, "Database");
        if (null == database) {
            ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,
                    "There is no database attribute in the storage node.");
            return null;
        }
        String user = XmlUtil.getSingleChildNodeValue(node, "User");
        if (null == user) {
            ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,
                    "There is no uid attribute in the storage node.");
            return null;
        }
        String password = XmlUtil.getSingleChildNodeValue(node, "Password");
        String pooling = XmlUtil.getSingleChildNodeValue(node, "Pooling");
        String minPoolSize = XmlUtil.getSingleChildNodeValue(node, "MinPoolSize");
        String maxPoolSize = XmlUtil.getSingleChildNodeValue(node, "MaxPoolSize");
        String timeout = XmlUtil.getSingleChildNodeValue(node, "Timeout");
        String charset = XmlUtil.getSingleChildNodeValue(node, "Charset");
        String transactional = XmlUtil.getSingleChildNodeValue(node, "Transactional");
        String transactionLevel = XmlUtil.getSingleChildNodeValue(node, "TransactionLevel");
        String port = XmlUtil.getSingleChildNodeValue(node, "Port");

        String options = XmlUtil.getSingleChildNodeValue(node, "Options");

        String sidleTime = XmlUtil.getSingleChildNodeValue(node, "AliveTime");

        String sDatabasePoolStyle = XmlUtil.getSingleChildNodeValue(node, "PoolStyle");
        String sUrlParaments = XmlUtil.getSingleChildNodeValue(node, "UrlParaments");

        StgAttr storage = new StgAttr();
        storage.setName(name);
        if (null == databaseStyle) {
            storage.setDatabaseStyle(DBOpt.MySql);
        } else {
            String style = databaseStyle.trim().toLowerCase();
            storage.setDatabaseStyle("sqlserver".equalsIgnoreCase(style) ? DBOpt.SqlServer :
                "oracle".equalsIgnoreCase(style) ? DBOpt.Oracle : DBOpt.MySql);
        }
        storage.setServer(server);
        storage.setDatabase(database);
        storage.setUser(user);
        storage.setPassword(StringsUtil.isNullOrEmptyOrAllSpace(password) ? "" : password);
        storage.setPooling(StringsUtil.isNullOrEmptyOrAllSpace(pooling) ? true :  Boolean.parseBoolean(pooling));
        int minsize = StringsUtil.isNullOrEmptyOrAllSpace(minPoolSize) ? 2 : Integer.parseInt(minPoolSize);
        minsize = 2 < minsize ? 2 : minsize;
        storage.setMinSize(minsize);//固定数据库链接池最小的链接为2
        storage.setMaxSize(StringsUtil.isNullOrEmptyOrAllSpace(maxPoolSize) ? 20 : Integer.parseInt(maxPoolSize));
        storage.setTimeout(StringsUtil.isNullOrEmptyOrAllSpace(timeout) ? 30 : Integer.parseInt(timeout));
        storage.setCharset(StringsUtil.isNullOrEmptyOrAllSpace(charset) ? null : charset);
        storage.setTransactional(StringsUtil.isNullOrEmptyOrAllSpace(transactional) ? true :  Boolean.parseBoolean(transactional));
        storage.setIdelTime(StringsUtil.isNullOrEmptyOrAllSpace(sidleTime) ? 120 : Integer.parseInt(sidleTime));
        storage.setDbps(
            StringsUtil.isNullOrEmptyOrAllSpace(sDatabasePoolStyle) ? SpxDBCP.name() : sDatabasePoolStyle);
        storage.setUrlParaments(sUrlParaments);

        String sWaitTimeWhenGetMs = XmlUtil.getSingleChildNodeValue(node, "WaitTimeWhenGetMs");
        String sLifeCycleTime = XmlUtil.getSingleChildNodeValue(node, "LifeCycleTime");
        String sWaitInFreePoolMs = XmlUtil.getSingleChildNodeValue(node, "WaitInFreePoolMs");
        String sMaxRemedyConnectionCount = XmlUtil.getSingleChildNodeValue(node, "MaxRemedyConnectionCount");
        String sCleanupTimestampMs = XmlUtil.getSingleChildNodeValue(node, "CleanupTimestampMs");
        String sMaxRequestTimeMs = XmlUtil.getSingleChildNodeValue(node, "MaxRequestTimeMs");

        if (!StringsUtil.isNullOrEmptyOrAllSpace(sWaitTimeWhenGetMs)) {
            storage.setWaitTimeWhenGetMs(Integer.parseInt(sWaitTimeWhenGetMs));
        }
        if (!StringsUtil.isNullOrEmptyOrAllSpace(sLifeCycleTime)) {
            storage.setLifeCycleTime(Integer.parseInt(sLifeCycleTime));
        }
        if (!StringsUtil.isNullOrEmptyOrAllSpace(sWaitInFreePoolMs)) {
            storage.setWaitInFreePoolMs(Integer.parseInt(sWaitInFreePoolMs));
        }
        if (!StringsUtil.isNullOrEmptyOrAllSpace(sMaxRemedyConnectionCount)) {
            storage.setMaxRemedyConnectionCount(Integer.parseInt(sMaxRemedyConnectionCount));
        }
        if (!StringsUtil.isNullOrEmptyOrAllSpace(sCleanupTimestampMs)) {
            storage.setCleanupTimestampMs(Integer.parseInt(sCleanupTimestampMs));
        }
        if (!StringsUtil.isNullOrEmptyOrAllSpace(sMaxRequestTimeMs)) {
            storage.setMaxRequestTimeMs(Integer.parseInt(sMaxRequestTimeMs));
        }
        storage.setOptions(options);

        if (storage.isTransactional()) {
            if (StringsUtil.isNullOrEmpty(transactionLevel)) {
                // default level and do not means no suppert tran
                storage.setTransactionLevel(Connection.TRANSACTION_NONE);
            } else {
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

        if (!StringsUtil.isNullOrEmptyOrAllSpace(port)) {
            storage.setPort(Integer.parseInt(port));
        }

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
