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
package org.albianj.orm.impl.context;

import org.albianj.common.argument.RefArg;
import org.albianj.kernel.AlbianRuntimeException;
import org.albianj.orm.db.IPersistenceCommand;
import org.albianj.orm.db.ISqlParameter;
import org.albianj.orm.db.PersistenceCommandType;
import org.albianj.orm.impl.db.PersistenceCommand;
import org.albianj.orm.impl.toolkit.ListConvert;
import org.albianj.orm.object.*;
import org.albianj.orm.object.filter.IChainExpression;
import org.albianj.orm.service.AlbianEntityMetadata;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class FreeReaderJobAdapter implements IReaderJobAdapter {

    protected abstract StringBuilder makeSltCmdTxt(String sessionId,int sbStyle, StringBuilder sbCols, String tableName,
                                                   StringBuilder sbWhere, StringBuilder sbOrderby,
                                                   int start, int step, String idxName);

    protected abstract StringBuilder makeSltCmdOdrs(String sessionId, IAlbianObjectAttribute objAttr,
                                                    LinkedList<IOrderByCondition> orderbys, int dbStyle);

    protected abstract StringBuilder makeSltCmdCols(String sessionId, IAlbianObjectAttribute objAttr, int dbStyle);

    protected abstract IStorageAttribute makeReaderToStorageCtx(String sessionId, IAlbianObjectAttribute objAttr,
                                                                boolean isExact,
                                                                String storageAlias,
                                                                String tableAlias,
                                                                String drouterAlias,
                                                                Map<String, IFilterCondition> hashWheres,
                                                                Map<String, IOrderByCondition> hashOrderbys,
                                                                RefArg<String> dbName,
                                                                RefArg<String> tableName
    );

    protected abstract StringBuilder makeSltCmdWhrs(String sessionId, IAlbianObjectAttribute objAttr,
                                                    int dbStyle, String implType,
                                                    LinkedList<IFilterCondition> wheres,
                                                    Map<String, ISqlParameter> paras);

    protected abstract StringBuilder makeSltCmdCount(int dbStyle);

    @Deprecated
    public ReaderJob buildReaderJob(String sessionId, Class<?> itf, boolean isExact, String drouterAlias,
                                     int start, int step, LinkedList<IFilterCondition> wheres,
                                     LinkedList<IOrderByCondition> orderbys, String idxName)  {
        ReaderJob job = new ReaderJob(sessionId);
        IAlbianObjectAttribute objAttr = AlbianEntityMetadata.getEntityMetadata(itf);
        if (null == objAttr) {
            throw new AlbianRuntimeException(
                "albian-object:" + itf.getName() + " attribute is not found.maybe not exist mapping.");
        }

        Class<?> implClzz = objAttr.getImplClzz();
        Map<String, IOrderByCondition> hashOrderbys = ListConvert
                .toLinkedHashMap(orderbys);

        Map<String, IFilterCondition> hashWheres = ListConvert
                .toLinkedHashMap(wheres);

        RefArg<String> dbName = new RefArg<>();
        RefArg<String> tableName = new RefArg<>();

        IStorageAttribute stgAttr = makeReaderToStorageCtx(sessionId,objAttr, isExact, null, null, drouterAlias,
                hashWheres, hashOrderbys, dbName, tableName);

        StringBuilder sbCols = makeSltCmdCols(sessionId, objAttr, stgAttr.getDatabaseStyle());

        Map<String, ISqlParameter> paras = new HashMap<>();
        StringBuilder sbWhrs = makeSltCmdWhrs(sessionId, objAttr, stgAttr.getDatabaseStyle(),
                objAttr.getType(), wheres, paras);

        StringBuilder sbOdrs = makeSltCmdOdrs(sessionId, objAttr, orderbys, stgAttr.getDatabaseStyle());

        StringBuilder sbCmdTxt = makeSltCmdTxt(sessionId,stgAttr.getDatabaseStyle(), sbCols, tableName.getValue(),
                sbWhrs, sbOdrs, start, step, idxName);

        IPersistenceCommand cmd = new PersistenceCommand();
        cmd.setCommandText(sbCmdTxt.toString());
        cmd.setParameters(paras);
        cmd.setCommandType(PersistenceCommandType.Text);
        job.setCommand(cmd);
        job.setStorageAttr(new RunningStorageAttribute(stgAttr, dbName.getValue()));
        return job;
    }

    @Deprecated
    public ReaderJob buildReaderJob(String sessionId, Class<?> itf, boolean isExact, String drouterAlias,
                                     LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys,
                                     String idxName)   {

        ReaderJob job = new ReaderJob(sessionId);
        IAlbianObjectAttribute objAttr = AlbianEntityMetadata.getEntityMetadata(itf);
        if (null == objAttr) {
            throw new AlbianRuntimeException(
                "albian-object:" + itf.getName() + " attribute is not found.maybe not exist mapping.");
        }

        Class<?> implClzz = objAttr.getImplClzz();
        Map<String, IOrderByCondition> hashOrderbys = ListConvert
                .toLinkedHashMap(orderbys);

        Map<String, IFilterCondition> hashWheres = ListConvert
                .toLinkedHashMap(wheres);

        RefArg<String> dbName = new RefArg<>();
        RefArg<String> tableName = new RefArg<>();

        IStorageAttribute stgAttr = makeReaderToStorageCtx(sessionId,objAttr, isExact, null, null, drouterAlias,
                hashWheres, hashOrderbys, dbName, tableName);

        StringBuilder sbCols = makeSltCmdCount(stgAttr.getDatabaseStyle());

        Map<String, ISqlParameter> paras = new HashMap<>();
        StringBuilder sbWhrs = makeSltCmdWhrs(sessionId, objAttr, stgAttr.getDatabaseStyle(),
                objAttr.getType(), wheres, paras);

        StringBuilder sbOdrs = makeSltCmdOdrs(sessionId, objAttr, orderbys, stgAttr.getDatabaseStyle());

        StringBuilder sbCmdTxt = makeSltCmdTxt(sessionId,stgAttr.getDatabaseStyle(), sbCols, tableName.getValue(),
                sbWhrs, sbOdrs, -1, -1, idxName);

        IPersistenceCommand cmd = new PersistenceCommand();
        cmd.setCommandText(sbCmdTxt.toString());
        cmd.setParameters(paras);
        cmd.setCommandType(PersistenceCommandType.Text);
        job.setCommand(cmd);
        job.setStorageAttr(new RunningStorageAttribute(stgAttr, dbName.getValue()));
        return job;
    }

    public ReaderJob buildReaderJob(String sessionId, Class<?> itf, boolean isExact,
                                     String storageAlias, String tableAlias, String drouterAlias,
                                     int start, int step, IChainExpression f,
                                     LinkedList<IOrderByCondition> orderbys, String idxName)   {

        ReaderJob job = new ReaderJob(sessionId);
        IAlbianObjectAttribute objAttr = AlbianEntityMetadata.getEntityMetadata(itf);
        if (null == objAttr) {
            throw new AlbianRuntimeException(
                "albian-object:" + itf.getName() + " attribute is not found.maybe not exist mapping.");
        }

        Class<?> implClzz = objAttr.getImplClzz();
        Map<String, IOrderByCondition> hashOrderbys = ListConvert
                .toLinkedHashMap(orderbys);

        Map<String, IFilterCondition> hashWheres = new HashMap<>();
        ChainExpressionParser.toFilterConditionMap(f, hashWheres);

        RefArg<String> dbName = new RefArg<>();
        RefArg<String> tableName = new RefArg<>();

        IStorageAttribute stgAttr = makeReaderToStorageCtx(sessionId,objAttr, isExact, storageAlias, tableAlias, drouterAlias,
                hashWheres, hashOrderbys, dbName, tableName);

        Map<String, ISqlParameter> paras = new HashMap<>();

        StringBuilder sbCols = makeSltCmdCols(sessionId, objAttr, stgAttr.getDatabaseStyle());

        StringBuilder sbWhere = new StringBuilder();
        ChainExpressionParser.toConditionText(sessionId, implClzz, objAttr, stgAttr, f, sbWhere, paras);
        StringBuilder sbOdrs = makeSltCmdOdrs(sessionId, objAttr, orderbys, stgAttr.getDatabaseStyle());

        StringBuilder sbCmdTxt = makeSltCmdTxt(sessionId,stgAttr.getDatabaseStyle(), sbCols, tableName.getValue(),
                sbWhere, sbOdrs, start, step, idxName);

        IPersistenceCommand cmd = new PersistenceCommand();
        cmd.setCommandText(sbCmdTxt.toString());
        cmd.setParameters(paras);
        cmd.setCommandType(PersistenceCommandType.Text);
        job.setCommand(cmd);
        job.setStorageAttr(new RunningStorageAttribute(stgAttr, dbName.getValue()));
        return job;
    }


    public ReaderJob buildReaderJob(String sessionId, Class<?> itf, boolean isExact, String storageAlias, String tableAlias, String drouterAlias,
                                     IChainExpression f,
                                     LinkedList<IOrderByCondition> orderbys, String idxName)   {
        ReaderJob job = new ReaderJob(sessionId);
        IAlbianObjectAttribute objAttr = AlbianEntityMetadata.getEntityMetadata(itf);
        if (null == objAttr) {
            throw new AlbianRuntimeException(
                "albian-object:" + itf.getName() + " attribute is not found.maybe not exist mapping.");
        }

        Class<?> implClzz = objAttr.getImplClzz();
        Map<String, IOrderByCondition> hashOrderbys = ListConvert
                .toLinkedHashMap(orderbys);

        Map<String, IFilterCondition> hashWheres = new HashMap<>();
        ChainExpressionParser.toFilterConditionMap(f, hashWheres);

        RefArg<String> dbName = new RefArg<>();
        RefArg<String> tableName = new RefArg<>();

        IStorageAttribute stgAttr = makeReaderToStorageCtx(sessionId,objAttr, isExact, storageAlias, tableAlias, drouterAlias,
                hashWheres, hashOrderbys, dbName, tableName);

        Map<String, ISqlParameter> paras = new HashMap<>();

        StringBuilder sbCols = makeSltCmdCount(stgAttr.getDatabaseStyle());

        StringBuilder sbWhere = new StringBuilder();
        ChainExpressionParser.toConditionText(sessionId, implClzz, objAttr, stgAttr, f, sbWhere, paras);
        StringBuilder sbOdrs = makeSltCmdOdrs(sessionId, objAttr, orderbys, stgAttr.getDatabaseStyle());

        StringBuilder sbCmdTxt = makeSltCmdTxt(sessionId,stgAttr.getDatabaseStyle(), sbCols, tableName.getValue(),
                sbWhere, sbOdrs, -1, -1, idxName);

        IPersistenceCommand cmd = new PersistenceCommand();
        cmd.setCommandText(sbCmdTxt.toString());
        cmd.setParameters(paras);
        cmd.setCommandType(PersistenceCommandType.Text);
        job.setCommand(cmd);
        job.setStorageAttr(new RunningStorageAttribute(stgAttr, dbName.getValue()));
        return job;
    }

    public ReaderJob buildReaderJob(String sessionId, Class<?> cls, IRunningStorageAttribute storage,
                                     PersistenceCommandType cmdType, String text, Map<String, ISqlParameter> paras)   {
        ReaderJob job = new ReaderJob(sessionId);
        IPersistenceCommand cmd = new PersistenceCommand();
        cmd.setCommandText(text);
        cmd.setParameters(paras);
        cmd.setCommandType(cmdType);
        job.setCommand(cmd);
        job.setStorageAttr(storage);
        return job;
    }
}
