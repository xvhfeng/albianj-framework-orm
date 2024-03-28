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
package org.albianj.dal.impl.context;

import org.albianj.AblThrowable;
import org.albianj.dal.api.object.*;
import org.albianj.dal.api.object.filter.ExprOpt;
import org.albianj.common.utils.StringsUtil;
import org.albianj.dal.api.db.SqlPara;
import org.albianj.dal.impl.toolkit.SqlTypeConv;
import org.albianj.dal.api.object.filter.IChaExpr;
import org.albianj.dal.api.object.filter.IFltExpr;
import org.albianj.dal.api.service.AlbianEntityMetadata;

import java.util.List;
import java.util.Map;

public class ChaExprPrs {

    public static void toFilterConditionMap(IChaExpr f, Map<String, IFltCdt> map) {
        if (null == f) return;
        List<IChaExpr> ces = f.getChainExpression();
        if (null == ces || ces.isEmpty())
            return;
        for (IChaExpr ce : ces) {
            if (ExprOpt.FilterGroup == ce.getExprOpt()) {
                toFilterConditionMap(ce, map);
            } else {
                IFltExpr fe = (IFltExpr) ce;
                map.put(decideFieldName(fe),new FltCdt(fe));
            }
        }
    }

    public static String decideFieldName(IFltExpr expr){
        String fieldName = null;
        if(StringsUtil.isNullEmptyTrimmed(expr.getFieldName())) {
            fieldName = AlbianEntityMetadata.getFieldNameByGetter(expr.getGetter());
            expr.setFieldName(fieldName);
        }
        if(!StringsUtil.isNullEmptyTrimmed(expr.getAliasName())) {
            return expr.getAliasName();
        }
        return expr.getFieldName();
    }

    public static void toFilterConditionArray(IChaExpr f, List<IFltCdt> list) {
        if (null == f) return;
        List<IChaExpr> ces = f.getChainExpression();
        if (null == ces || ces.isEmpty())
            return;
        for (IChaExpr ce : ces) {
            if (ExprOpt.FilterGroup == ce.getExprOpt()) {
                toFilterConditionArray(ce, list);
            } else {
                IFltExpr fe = (IFltExpr) ce;
                list.add(new FltCdt(fe));
            }
        }
    }

    public static void toConditionText(String sessionId, Class<?> cls, AblEntityAttr albianObject,
                                       StgAttr storage, IChaExpr f, StringBuilder sb, Map<String, SqlPara> paras)
              {
        if (null == f) return;
        List<IChaExpr> ces = f.getChainExpression();
        if (null == ces || 0 == ces.size())
            return;
        for (IChaExpr ce : ces) {
            if (ExprOpt.FilterGroup == ce.getExprOpt()) {
                if (null == ce.getChainExpression() || ce.getChainExpression().isEmpty()) {
                    continue;
                }

                if (BOpt.Normal != ce.getBoolOpt()) {
                    sb.append(" ").append(ce.getBoolOpt().getWord());
                }
                sb.append(" (");
                toConditionText(sessionId, cls, albianObject, storage, ce, sb, paras);
                sb.append(" )");
            } else {
                IFltExpr fe = (IFltExpr) ce;

                if (fe.isAddition())
                    continue;

                if (fe.isAutoId()) {
                    sb.append(" 1 = 1 ");
                    continue;
                }

                String className = cls.getName();
                AblEntityFieldAttr fieldAttr = albianObject.getFields().get(AlbianEntityMetadata.makeFieldsKey(fe.getFieldName().toLowerCase()));

                if (null == fieldAttr) {
                    throw new AblThrowable(
                        "PersistenceService is error. albian-object:" + className + " member:" + fe.getFieldName()
                            + " is not found.");
                }

                if (null != ce.getBoolOpt()) {
                    sb.append(ce.getBoolOpt().getWord());
                }

                if (DBOpt.MySql == storage.getDatabaseStyle()) {
                    sb.append(" `").append(fieldAttr.getSqlFieldName()).append("`");
                } else {
                    sb.append(" [").append(fieldAttr.getSqlFieldName()).append("]");
                }

                if(fe.getOperatorOpt() == OOpt.in) {
                   Object[] inParas = (Object[]) fe.getValue();
                    sb.append(fe.getOperatorOpt().getWord()).append(" ( ");
                    for(int i = 0 ; i <  inParas.length;i++){
                        String sqlFieldName =StringsUtil.nonIdxFmt("{}_{}",
                        StringsUtil.isNullEmptyTrimmed(fe.getAliasName()) ? fieldAttr.getSqlFieldName() : fe.getAliasName() , i);
                        sb.append(" #").append(sqlFieldName).append("#,");

                        SqlPara para = new SqlPara();
                        para.setName(fieldAttr.getSqlFieldName());
                        para.setSqlFieldName(sqlFieldName);
                        if (null == fe.getFieldClass()) {
                            para.setSqlType(fieldAttr.getDatabaseType());
                        } else {
                            para.setSqlType(SqlTypeConv.toSqlType(fe.getFieldClass()));
                        }
                        para.setValue(inParas[i]);
                        paras.put(String.format("#%1$s#", sqlFieldName), para);

                    }

                    if(',' == sb.charAt(sb.length() - 1)) {
                        sb.deleteCharAt(sb.length() - 1);
                    }

                    sb.append(" ) ");
                } else {
                    sb.append(fe.getOperatorOpt().getWord()).append("#")
                            .append(StringsUtil.isNullEmptyTrimmed(fe.getAliasName()) ? fieldAttr.getSqlFieldName()
                                    : fe.getAliasName())
                            .append("# ");

                    SqlPara para = new SqlPara();
                    para.setName(fieldAttr.getSqlFieldName());
                    para.setSqlFieldName(fieldAttr.getSqlFieldName());
                    if (null == fe.getFieldClass()) {
                        para.setSqlType(fieldAttr.getDatabaseType());
                    } else {
                        para.setSqlType(SqlTypeConv.toSqlType(fe.getFieldClass()));
                    }
                    if(fe.getOperatorOpt() == OOpt.in) {
                        para.setArray(true);
                    }
                    para.setValue(fe.getValue());
                    paras.put(String.format("#%1$s#", StringsUtil.isNullEmptyTrimmed(fe.getAliasName())
                            ? fieldAttr.getSqlFieldName() : fe.getAliasName()), para);
                    }
            }
        }
    }

}
