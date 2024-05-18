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
package org.albianj.impl.dal.db;

import org.albianj.AblThrowable;
import org.albianj.ServRouter;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.api.dal.context.RdrJob;
import org.albianj.api.dal.db.PCmd;
import org.albianj.api.dal.db.SqlPara;
import org.albianj.api.dal.object.AblEntityFieldAttr;
import org.albianj.api.dal.object.AblEntityAttr;
import org.albianj.impl.dal.sqlpara.DbValueFormatter;
import org.albianj.impl.dal.toolkit.SetConv;
import org.albianj.impl.dal.toolkit.RstConv;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.api.dal.db.IDBP;

import org.albianj.api.dal.db.CmdOpt;
import org.albianj.api.dal.object.IAblObj;
import org.albianj.api.dal.object.RStgAttr;
import org.albianj.api.dal.service.AlbianEntityMetadata;
import org.albianj.api.dal.service.IAlbianStorageParserService;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class PersistenceQueryScope extends FreePersistenceQueryScope implements IPersistenceQueryScope {


    protected void perExecute(RdrJob job)  {
        String sessionId = job.getId();
        PersistenceNamedParameter.parseSql(job.getCommand());
        RStgAttr rsa = job.getStorage();
        IAlbianStorageParserService asps = ServRouter
            .getService(job.getId(), IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        IDBP dbp = asps.getDatabasePool(sessionId, rsa);
        Connection conn = dbp.getConnection(sessionId, rsa, true);

        if (conn == null) {
            throw new AblThrowable(
                "could't get connection from " + job.getStorage().getStgAttr().getName());
        }

        job.setConnection(conn);
        job.setDatabasePool(dbp);
        PCmd cmd = job.getCommand();
        PreparedStatement statement = null;
        try {
            statement = job.getConnection().prepareStatement(cmd.getCommandText());
        } catch (SQLException e) {
            ServRouter.logAndThrowAgain(sessionId,  LogLevel.Error,e,
                    "get the statement is fail." );
        }
        Map<Integer, String> map = cmd.getParameterMapper();
        if (!SetUtil.isNullOrEmpty(map)) {
            for (int i = 1; i <= map.size(); i++) {
                String paraName = map.get(i);
                SqlPara para = cmd.getParameters().get(paraName);
                try {
                    if (null == para.getValue()) {

                        statement.setNull(i, para.getSqlType());
                    } else {
                        statement.setObject(i, DbValueFormatter.toSqlValue(para.getValue()), para.getSqlType());
                    }
                } catch (SQLException e) {
                    ServRouter.logAndThrowAgain(sessionId,LogLevel.Error,e,
                        "set the sql paras is error.para name: {} ,para value: {}" ,
                            para.getName() , RstConv.sqlValueToString(para.getSqlType(), para.getValue()));
                }
            }
        }
        job.setStatement(statement);
    }

    protected void executing(RdrJob job)  {
        String text = job.getCommand().getCommandText();
        Map<String, SqlPara> map = job.getCommand().getParameters();
        RStgAttr st = job.getStorage();
        String sessionId = job.getId();

        ResultSet result = null;
        try {
            ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Info,
                    "job:{} Storage:{},database:{},SqlText:{},paras:{}.",
                   job.getId(), st.getStgAttr().getName(),
                st.getDatabase(), text, SetConv.toString(map));
            long begin1 = System.currentTimeMillis();
            result = ((PreparedStatement)job.getStatement()).executeQuery();

            if (!StringsUtil.isNullOrEmptyOrAllSpace(sessionId) && sessionId.endsWith("_SPX_LOG")) {
                long end1 = System.currentTimeMillis();
                ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Info,
                        "SpxLog job:{} execute query use times:{}.", job.getId(),end1 - begin1);
            }
        } catch (SQLException e) {
            ServRouter.logAndThrowAgain(sessionId,LogLevel.Error,e,
                    "execute the reader job:{} is fail." ,job.getId());
        } finally {
            try {
                if(!job.getConnection().getAutoCommit()) {
                    job.getConnection().commit();
                }
            } catch (Exception e) {
                ServRouter.logAndThrowAgain(sessionId,LogLevel.Error,e,
                        "commit the reader job:{} is fail." ,job.getId());
            }
        }
        job.setResult(result);
    }

    protected <T extends IAblObj> List<T> executed(Class<T> cls, RdrJob job)
            {
        long begin1 = System.currentTimeMillis();
        String sessionId = job.getId();
        List<T> list = executed(cls, job.getId(), job.getResult());
        if (!StringsUtil.isNullOrEmptyOrAllSpace(sessionId) && sessionId.endsWith("_SPX_LOG")) {
            long end1 = System.currentTimeMillis();
            ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Error,
                    "SpxLog executed query and make data result use times:{}.",
                    end1 - begin1);
        }
        String text = job.getCommand().getCommandText();
        Map<String, SqlPara> map = job.getCommand().getParameters();
        RStgAttr st = job.getStorage();
        ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Error,
                "Storage:{},database:{},SqlText:{},paras:{}.return count:{}",
                st.getStgAttr().getName(), st.getDatabase(), text, SetConv.toString(map),
            SetUtil.isNullOrEmpty(list) ? "NULL" : String.valueOf(list.size()));
        return list;
    }

    protected void unloadExecute(RdrJob job)   {
        String sessionId = job.getId();
        RStgAttr rsa = job.getStorage();
        IDBP dbp = job.getDatabasePool();
        dbp.returnConnection(sessionId, rsa.getStgAttr().getName(), rsa.getDatabase(), job.getConnection(),
            job.getStatement(), job.getResult());
    }

    protected ResultSet executing(String sessionId, CmdOpt cmdType, Statement statement)
            {
        try {
            if (CmdOpt.Text == cmdType) {
                return ((PreparedStatement)statement).executeQuery();
            }
            return ((CallableStatement)statement).executeQuery();
        } catch (SQLException e) {
            ServRouter.logAndThrowAgain(sessionId,LogLevel.Error,e,
                    "execute the reader job fail");
        }

        return null;
    }

    protected <T extends IAblObj> List<T> executed(Class<T> cli, String sessionId, ResultSet result)
            {
        String inter = cli.getName();

                AblEntityAttr objAttr = AlbianEntityMetadata.getEntityMetadata(inter);
        String className = objAttr.getType();
        Map<String, AblEntityFieldAttr> member = objAttr.getFields();

        Class<?> cls = null;
        try {
            cls = AlbianClassLoader.getInstance().loadClass(className);
        } catch (ClassNotFoundException e) {
            ServRouter.logAndThrowAgain(sessionId,LogLevel.Error,e,
                    "class:{} is not found.",className);
        }
        List<T> list = new Vector<T>();
        try {
            while (result.next()) {
                try {
                    @SuppressWarnings("unchecked") T obj = (T)cls.newInstance();
                    for (AblEntityFieldAttr fAttr : member.values()) {
                        if (!fAttr.isSave()) {
                            continue;
                        }

                        Instant curr = RstConv.getDateTime(result,fAttr.getPropertyName());
                        if (null != curr) {
                            Object rc = RstConv.toBoxValue(fAttr.getEntityField().getType(), curr);
                            fAttr.getEntityField().set(obj, rc);
                            obj.setOldAlbianObject(fAttr.getPropertyName(), rc);
                        } else {
                            Object v = result.getObject(fAttr.getPropertyName());
                            if (null != v) {
                                Object rc = RstConv.toBoxValue(fAttr.getEntityField().getType(), v);
                                fAttr.getEntityField().set(obj, rc);
                                obj.setOldAlbianObject(fAttr.getPropertyName(), rc);
                            }
                        }

                    }
                    obj.setIsAlbianNew(false);
                    list.add(obj);
                } catch (Exception e) {
                    ServRouter.logAndThrowAgain(sessionId,LogLevel.Error,e,
                            "create object from class::{} is fail.",className);
                }
            }
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(sessionId,LogLevel.Error,e,
                    "loop the result from database for class:{} is error.",
                     className);
        }

        return list;
    }

    @Override
    protected Object executed(String sessionId, RdrJob job)  {
        Object v = null;
        ResultSet result = job.getResult();
        try {
            if (result.next()) {
                v = result.getObject("COUNT");
                String text = job.getCommand().getCommandText();
                Map<String, SqlPara> map = job.getCommand().getParameters();
                RStgAttr st = job.getStorage();
                ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Error,
                        "Storage:{},database:{},SqlText:{},paras:{}.return COUNT(1) :{}",
                    st.getStgAttr().getName(), st.getDatabase(), text, SetConv.toString(map),
                    String.valueOf(v));
            }
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(sessionId,LogLevel.Error,e,
                    "get pagesize is null."
            );
        }

        return v;
    }
}