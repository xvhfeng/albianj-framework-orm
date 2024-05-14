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
package org.albianj.dal.impl.db;

import org.albianj.AblThrowable;
import org.albianj.dal.api.db.PCmd;
import org.albianj.dal.api.db.SqlPara;
import org.albianj.dal.api.object.AblEntityFieldAttr;
import org.albianj.dal.api.object.AblEntityAttr;
import org.albianj.dal.api.db.CmdOpt;
import org.albianj.dal.api.object.IAblObj;

import org.albianj.dal.api.object.DBOpt;

import java.util.HashMap;
import java.util.Map;

public class ModifyCommandAdapter implements IDMLCmd {

/*
            String sqlTxt = "INSERT INTO
            keyword(radical, show_text, katakana, speech_id, show_times,
             speech_1, speech_2, speech_next)
             VALUES(?,?,?,?,?,?,?,?)
             ON DUPLICATE KEY UPDATE show_times= show_times+VALUES(show_times) ";

 */
    public PCmd buildPstCmd(String sessionId, int dbStyle, String tableName, IAblObj object,
                            AblEntityAttr objAttr, Map<String, Object> mapValue, boolean rbkOnError)
              {
        if (object.getIsAlbianNew()) {
            throw new AblThrowable(
                "the new albianj object can not be update.please load the object from database first.");
        }

                  PCmd cmd = new PCmd();
        StringBuilder text = new StringBuilder();
        StringBuilder cols = new StringBuilder();
        StringBuilder where = new StringBuilder();

        StringBuilder rollbackText = null;
        StringBuilder rollbackCols = null;
        StringBuilder rollbackWhere = null;
        if (rbkOnError) {
            rollbackText = new StringBuilder();
            rollbackCols = new StringBuilder();
            rollbackWhere = new StringBuilder();
        }

        text.append("UPDATE ").append(SqlField.nonKeywords(dbStyle,tableName));
        if (rbkOnError) {
            rollbackText.append("UPDATE ").append(SqlField.nonKeywords(dbStyle,tableName));
        }
//        switch (dbStyle) {
//            case DBOpt.SqlServer :{
//                text.append("[").append(tableName).append("]");
//                if (rbkOnError) {
//                    rollbackText.append("[").append(tableName).append("]");
//                }
//                break;
//            }
//            case DBOpt.RedShift:
//            case DBOpt.PgSql: {
//                text.append("\"").append(tableName).append("\"");
//                if (rbkOnError) {
//                    rollbackText.append("\"").append(tableName).append("\"");
//                }
//                break;
//            }
//            case DBOpt.MySql:
//            default:{
//                text.append("`").append(tableName).append("`");
//                if (rbkOnError) {
//                    rollbackText.append("`").append(tableName).append("`");
//                }
//            }
//        }

        Map<String, AblEntityFieldAttr> fieldsAttr = objAttr.getFields();
        Map<String, SqlPara> sqlParas = new HashMap<String, SqlPara>();
        Map<String, SqlPara> rollbackParas = new HashMap<String, SqlPara>();
        for (Map.Entry<String, AblEntityFieldAttr> entry : fieldsAttr.entrySet()) {
            AblEntityFieldAttr member = entry.getValue();
            if (!member.isSave())
                continue;
            String name = member.getPropertyName();
            Object newValue = mapValue.get(name);
            Object oldValue = null;

            if (member.isPrimaryKey()) {
                where.append(" AND ").append(SqlField.nonKeywords(dbStyle,member.getSqlFieldName()));
                if (rbkOnError) {
                    rollbackWhere.append(" AND ").append(SqlField.nonKeywords(dbStyle,member.getSqlFieldName()));
                }
//                if (DBOpt.MySql == dbStyle) {
//                    where.append("`").append(member.getSqlFieldName()).append("`");
//                    if (rbkOnError) {
//                        rollbackWhere.append("`").append(member.getSqlFieldName()).append("`");
//                    }
//                } else {
//                    where.append("[").append(member.getSqlFieldName()).append("]");
//                    if (rbkOnError) {
//                        rollbackWhere.append("[").append(member.getSqlFieldName()).append("]");
//                    }
//                }
                where.append(" = ").append("#").append(member.getSqlFieldName()).append("# ");
                if (rbkOnError) {
                    rollbackWhere.append(" = ").append("#").append(member.getSqlFieldName()).append("# ");
                }
            } else {
                // cols
                oldValue = object.getOldAlbianObject(name);
                if ((null == newValue && null == oldValue)) {
                    continue;
                }
                if (null != newValue && newValue.equals(oldValue)) {
                    continue;
                }
                if (null != oldValue && oldValue.equals(newValue)) {
                    continue;
                }

                cols.append(SqlField.nonKeywords(dbStyle,member.getSqlFieldName()));
                if(rbkOnError){
                    rollbackCols.append(SqlField.nonKeywords(dbStyle,member.getSqlFieldName()));
                }
//                if (DBOpt.MySql == dbStyle) {
//                    cols.append("`").append(member.getSqlFieldName()).append("`");
//                    if (rbkOnError) {
//                        rollbackCols.append("`").append(member.getSqlFieldName()).append("`");
//                    }
//                } else {
//                    cols.append("[").append(member.getSqlFieldName()).append("]");
//                    if (rbkOnError) {
//                        rollbackCols.append("[").append(member.getSqlFieldName()).append("]");
//                    }
//                }
                cols.append(" = ").append("#").append(member.getSqlFieldName()).append("# ,");
                if (rbkOnError) {
                    rollbackCols.append(" = ").append("#").append(member.getSqlFieldName()).append("# ,");
                }
            }
            SqlPara para = new SqlPara();
            para.setName(name);
            para.setSqlFieldName(member.getSqlFieldName());
            para.setSqlType(member.getDatabaseType());
            para.setValue(newValue);
            sqlParas.put(String.format("#%1$s#", member.getSqlFieldName()), para);

            if (rbkOnError) {
                SqlPara rollbackPara = new SqlPara();
                rollbackPara.setName(name);
                rollbackPara.setSqlFieldName(member.getSqlFieldName());
                rollbackPara.setSqlType(member.getDatabaseType());
                rollbackPara.setValue(oldValue);
                rollbackParas.put(String.format("#%1$s#", member.getSqlFieldName()), rollbackPara);
            }
        }

        if (cols.isEmpty())
            return null;// no the upload operator
        if (!cols.isEmpty()) {
            cols.deleteCharAt(cols.length() - 1);
            if (rbkOnError) {
                rollbackCols.deleteCharAt(cols.length() - 1);
            }
        }

        if (where.isEmpty()) {
            throw new AblThrowable(
                "the new albianj object can not be update .there is not PrimaryKey in the object.");
        }


        text.append(" SET ").append(cols).append(" WHERE 1=1 ").append(where);
        if (rbkOnError) {
            rollbackText.append(" SET ").append(rollbackCols).append(" WHERE 1=1 ").append(rollbackWhere);
        }

        cmd.setCommandText(text.toString());
        cmd.setCommandType(CmdOpt.Text);
        cmd.setParameters(sqlParas);

        if (rbkOnError) {
            cmd.setRollbackCommandText(rollbackText.toString());
            cmd.setRollbackCommandType(CmdOpt.Text);
            cmd.setRollbackParameters(rollbackParas);
        }

        PersistenceNamedParameter.parseSql(cmd);
        return cmd;
    }

}
