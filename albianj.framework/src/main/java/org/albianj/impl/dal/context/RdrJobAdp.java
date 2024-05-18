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
package org.albianj.impl.dal.context;

import org.albianj.AblThrowable;
import org.albianj.ServRouter;
import org.albianj.api.dal.object.*;
import org.albianj.common.utils.StringsUtil;
import org.albianj.common.values.RefArg;
import org.albianj.api.dal.db.SqlPara;
import org.albianj.impl.dal.db.SqlField;
import org.albianj.impl.dal.toolkit.SqlTypeConv;
import org.albianj.api.dal.service.IAlbianStorageParserService;

import java.util.LinkedList;
import java.util.Map;

public class RdrJobAdp extends FreeRdrJobAdp implements IRdrJobAdp {

    protected StgAttr makeReaderToStorageCtx(String sessionId, AblEntityAttr objAttr, boolean isExact,
                                             String storageAlias, String tableAlias, String drouterAlias, Map<String, IFltCdt> hashWheres,
                                             Map<String, OdrBy> hashOrderbys, RefArg<String> dbName, RefArg<String> tableName) {
        StgAttr stgAttr = null;
        IAlbianStorageParserService asps = ServRouter
            .getService(sessionId,IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        if (StringsUtil.isNullEmptyTrimmed(drouterAlias)) { // not exist fix-drouterAlias
            if (StringsUtil.isNullEmptyTrimmed(storageAlias)) { // use drouer callback
                DrsAttr drsAttr = objAttr.getDataRouters();
                IAblDr drouter = drsAttr.getDataRouter();
                if (isExact) {
                    DrAttr drAttr =
                        drouter.mappingExactReaderRouting(drsAttr.getWriterRouters(), hashWheres, hashOrderbys);
                    String storageName = drouter.mappingExactReaderRoutingStorage(drAttr, hashWheres, hashOrderbys);
                    stgAttr = asps.getStorageAttribute(storageName);
                    dbName.setValue(drouter.mappingExactReaderRoutingDatabase(stgAttr, hashWheres, hashOrderbys));
                    tableName.setValue(drouter.mappingExactReaderTable(drAttr, hashWheres, hashOrderbys));
                } else {
                    DrAttr drAttr =
                        drouter.mappingReaderRouting(drsAttr.getReaderRouters(), hashWheres, hashOrderbys);
                    String storageName = drouter.mappingReaderRoutingStorage(drAttr, hashWheres, hashOrderbys);
                    stgAttr = asps.getStorageAttribute(storageName);
                    dbName.setValue(drouter.mappingReaderRoutingDatabase(stgAttr, hashWheres, hashOrderbys));
                    tableName.setValue(drouter.mappingReaderTable(drAttr, hashWheres, hashOrderbys));
                }
            } else { // fix storageAlias then use it and table is class simplename default
                stgAttr = asps.getStorageAttribute(storageAlias);
                dbName.setValue(stgAttr.getDatabase());
                tableName.setValue(
                    StringsUtil.isNullEmptyTrimmed(tableAlias) ? objAttr.getImplClzz().getSimpleName() : tableAlias);
            }
        } else { // do fix drouter
            DrsAttr drsAttr = objAttr.getDataRouters();
            IAblDr drouter = drsAttr.getDataRouter();
            DrAttr drAttr = drsAttr.getReaderRouters().get(drouterAlias);
            String storageName = drAttr.getStorageName();
            stgAttr = asps.getStorageAttribute(storageName);
            dbName.setValue(drouter.mappingReaderRoutingDatabase(stgAttr, hashWheres, hashOrderbys));
            tableName.setValue(drouter.mappingReaderTable(drAttr, hashWheres, hashOrderbys));
        }
        return stgAttr;
    }

    protected StringBuilder makeSltCmdCols(String sessionId, AblEntityAttr objAttr, int dbStyle) {
        StringBuilder sbCols = new StringBuilder();
        for (String key : objAttr.getFields().keySet()) {
            AblEntityFieldAttr member = objAttr.getFields().get(key);
            if (null == member) {
                throw new AblThrowable(
                    "albian-object:" + objAttr.getType() + " member:" + key + " is not found.");
            }
            if (!member.isSave())
                continue;
            if (member.getSqlFieldName().equals(member.getPropertyName())) {
                sbCols.append(SqlField.nonKeywords(dbStyle,member.getSqlFieldName())).append(",");
//                if (DBOpt.MySql == dbStyle) {
//                    sbCols.append("`").append(member.getSqlFieldName()).append("`").append(",");
//                } else {
//                    sbCols.append("[").append(member.getSqlFieldName()).append("]").append(",");
//                }
            } else {
                sbCols.append(SqlField.nonKeywords(dbStyle,member.getSqlFieldName())).append(" AS ")
                        .append(SqlField.nonKeywords(dbStyle,member.getPropertyName())).append(",");
//                if (DBOpt.MySql == dbStyle) {
//                    sbCols.append("`").append(member.getSqlFieldName()).append("`").append(" AS ").append("`")
//                        .append(member.getPropertyName()).append("`").append(",");
//                } else {
//                    sbCols.append("[").append(member.getSqlFieldName()).append("]").append(" AS ").append("[")
//                        .append(member.getPropertyName()).append("]").append(",");
//                }
            }
        }
        if (0 != sbCols.length())
            sbCols.deleteCharAt(sbCols.length() - 1);
        return sbCols;
    }

    protected StringBuilder makeSltCmdCount(int dbStyle) {
        StringBuilder sbCols = new StringBuilder();
        sbCols.append(" COUNT(1) ").append("AS ").append(SqlField.nonKeywords(dbStyle,"COUNT")).append(" ");
//        if (DBOpt.MySql == dbStyle) {
//            sbCols.append(" COUNT(1) ").append(" AS ").append(" `COUNT` ");
//        } else {
//            sbCols.append(" COUNT(1) ").append(" AS ").append(" [COUNT] ");
//        }
        return sbCols;
    }

    protected StringBuilder makeSltCmdOdrs(String sessionId, AblEntityAttr objAttr,
                                           LinkedList<OdrBy> orderbys, int dbStyle) {
        StringBuilder sbOrderby = new StringBuilder();
        if (null != orderbys) {
            for (OdrBy orderby : orderbys) {
                AblEntityFieldAttr member = objAttr.getFields().get(orderby.getFieldName().toLowerCase());
                if (null == member) {
                    throw new AblThrowable(
                        "albian-object:" + objAttr.getType() + " member:" + orderby.getFieldName() + " is not found.");
                }
                sbOrderby.append(SqlField.nonKeywords(dbStyle,member.getSqlFieldName()));

//                if (DBOpt.MySql == dbStyle) {
//                    sbOrderby.append("`").append(member.getSqlFieldName()).append("`");
//                } else {
//                    sbOrderby.append("[").append(member.getSqlFieldName()).append("]");
//                }
                sbOrderby.append(orderby.getSOpt().getWord()).append(",");
            }
        }
        if (0 != sbOrderby.length())
            sbOrderby.deleteCharAt(sbOrderby.length() - 1);
        return sbOrderby;
    }

    protected StringBuilder makeSltCmdTxt(String sessionId,int sbStyle, StringBuilder sbCols, String tableName, StringBuilder sbWhere,
        StringBuilder sbOrderby, int start, int step, String idxName) {
        StringBuilder sbCmdTxt = new StringBuilder();
        sbCmdTxt.append("SELECT ").append(sbCols).append(" FROM ");
        sbCmdTxt.append(SqlField.nonKeywords(sbStyle,tableName));
//        if (DBOpt.MySql == sbStyle) {
//            sbCmdTxt.append("`").append(tableName).append("`");
//        } else {
//            sbCmdTxt.append("[").append(tableName).append("]");
//        }
        if (DBOpt.MySql == sbStyle && !StringsUtil.isNotEmptyTrimmed(idxName)) { //按照木木的要求，增加强行执行索引行为，只为mysql使用
            sbCmdTxt.append(" FORCE INDEX (").append(idxName).append(") ");
        }
        if (!StringsUtil.isNullEmptyTrimmed(sbWhere.toString())) {
            sbCmdTxt.append(" WHERE ").append(sbWhere);
        }
        if (!sbOrderby.isEmpty()) {
            sbCmdTxt.append(" ORDER BY ").append(sbOrderby);
        }
        if (0 <= start && 0 < step) {
            sbCmdTxt.append(" LIMIT ").append(start).append(", ").append(step);
        }
        if (0 > start && 0 < step) {
            sbCmdTxt.append(" LIMIT ").append(step);
        }
        return sbCmdTxt;
    }

    protected StringBuilder makeSltCmdWhrs(String sessionId, AblEntityAttr objAttr, int dbStyle,
                                           String implType, LinkedList<IFltCdt> wheres, Map<String, SqlPara> paras) {
        StringBuilder sbWhrs = new StringBuilder();
        if (null != wheres) {
            for (IFltCdt where : wheres) {
                if (where.isAddition())
                    continue;
                AblEntityFieldAttr member = objAttr.getFields().get(where.getFieldName().toLowerCase());

                if (null == member) {
                    throw new AblThrowable(
                        "albian-object:" + implType + " member:" + where.getFieldName() + " is not found.");
                }

                sbWhrs.append(where.getBoolOpt().getWord()).append(where.isBeginSub() ? "(" : " ");
//                if (DBOpt.MySql == dbStyle) {
//                    sbWhrs.append("`").append(member.getSqlFieldName()).append("`");
//                } else {
//                    sbWhrs.append("[").append(member.getSqlFieldName()).append("]");
//                }
                sbWhrs.append(SqlField.nonKeywords(dbStyle,member.getSqlFieldName()));
                sbWhrs.append(where.getOperatorOpt().getWord()).append("#").append(
                    StringsUtil.isNullEmptyTrimmed(where.getAliasName()) ? member.getSqlFieldName() :
                        where.getAliasName())
                        .append("#")
                        .append(where.isCloseSub() ? ")" : "");

                SqlPara para = new SqlPara();
                para.setName(member.getSqlFieldName());
                para.setSqlFieldName(member.getSqlFieldName());
                if (null == where.getFieldClass()) {
                    para.setSqlType(member.getDatabaseType());
                } else {
                    para.setSqlType(SqlTypeConv.toSqlType(where.getFieldClass()));
                }
                para.setValue(where.getValue());
                paras.put(String.format("#%1$s#",
                    StringsUtil.isNullEmptyTrimmed(where.getAliasName()) ? member.getSqlFieldName() :
                        where.getAliasName()), para);
            }
        }
        return sbWhrs;
    }
}
