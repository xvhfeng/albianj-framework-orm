package org.albianj.dal.impl.db.localize.mysql;

import org.albianj.AblThrowable;
import org.albianj.dal.api.db.CmdOpt;
import org.albianj.dal.api.db.PCmd;
import org.albianj.dal.api.db.SqlPara;
import org.albianj.dal.api.object.AblEntityAttr;
import org.albianj.dal.api.object.AblEntityFieldAttr;
import org.albianj.dal.api.object.IAblObj;
import org.albianj.dal.impl.db.IDMLCmd;
import org.albianj.dal.impl.db.PersistenceNamedParameter;
import org.albianj.dal.impl.db.SqlField;

import java.util.HashMap;
import java.util.Map;


/**

 insert into camera_info(
 zone1Id,zone1Name,zone2Id,zone2Name,zone3Id,zone3Name,zone4Id,zone4Name,
 cameraId
 )VALUES
 <foreach collection ="list" item="cameraInfo" index= "index" separator =",">
 (
 #{cameraInfo.zone1Id}, #{cameraInfo.zone1Name}, #{cameraInfo.zone2Id},
 #{cameraInfo.zone2Name}, #{cameraInfo.zone3Id}, #{cameraInfo.zone3Name},
 #{cameraInfo.zone4Id}, #{cameraInfo.zone4Name},
 #{cameraInfo.cameraId},
 )
 </foreach>
 ON DUPLICATE KEY UPDATE
 zone1Id = VALUES(zone1Id),zone1Name = VALUES(zone1Name),zone2Id = VALUES(zone2Id),
 zone2Name = VALUES(zone2Name),zone3Id = VALUES(zone3Id),zone3Name = VALUES(zone3Name),
 zone4Id = VALUES(zone4Id),zone4Name = VALUES(zone4Name),
 cameraId = VALUES(cameraId)


 */
public class UpsertMysqlCommandAdapter implements IDMLCmd {

    public static Map<String, SqlPara> makeIstOrUpdCommand(String sessionId, int dbStyle, String tableName,
                                                           AblEntityAttr objAttr, Map<String, Object> sqlParaVals,
                                                           StringBuilder sqlText)   {
        StringBuilder cols = new StringBuilder();
        StringBuilder paras = new StringBuilder();
        StringBuilder upd = new StringBuilder();

        sqlText.append("INSERT INTO ").append(SqlField.nonKeywords(dbStyle,tableName));

        Map<String, AblEntityFieldAttr> fieldsAttr = objAttr.getFields();

        Map<String, SqlPara> sqlParas = new HashMap<String, SqlPara>();
        for (Map.Entry<String, AblEntityFieldAttr> entry : fieldsAttr
                .entrySet()) {
            AblEntityFieldAttr member = entry.getValue();

            if (member.isAutoGenKey()) {
                continue;
            }
            Object v = sqlParaVals.get(member.getPropertyName());
            if (!member.isSave() || null == v)
                continue;

            SqlPara para = new SqlPara();
            para.setName(member.getPropertyName());
            para.setSqlFieldName(member.getSqlFieldName());
            para.setSqlType(member.getDatabaseType());
            para.setValue(v);
            sqlParas.put(String.format("#%1$s#", member.getSqlFieldName()),
                    para);

            cols.append(SqlField.nonKeywords(dbStyle,member.getSqlFieldName()));

            upd.append(SqlField.nonKeywords(dbStyle,member.getSqlFieldName()))
                    .append("=VALUES").append("(").append(SqlField.nonKeywords(dbStyle,member.getSqlFieldName())).append("),");

            cols.append(",");
            paras.append("#").append(member.getSqlFieldName()).append("# ,");
        }

        if (!cols.isEmpty()) {
            cols.deleteCharAt(cols.length() - 1);
        }
        if (!paras.isEmpty()) {
            paras.deleteCharAt(paras.length() - 1);
        }
        if (!upd.isEmpty()) {
            upd.deleteCharAt(upd.length() - 1);
        }
        sqlText.append(" (").append(cols).append(") ").append("VALUES (")
                .append(paras).append(") ");

        sqlText.append(" ON DUPLICATE KEY UPDATE ").append(upd);

        return sqlParas;
    }

    public PCmd buildPstCmd(String sessionId, int dbStyle, String tableName, IAblObj object,
                            AblEntityAttr objAttr, Map<String, Object> mapValue, boolean rbkOnError)   {
        if (!object.getIsAlbianNew()) {
            throw new AblThrowable(
                    "the loaded albianj object can not be insert.please new the object from database first.");
        }

        PCmd cmd = new PCmd();
        StringBuilder sqlText = new StringBuilder();

        Map<String, SqlPara> sqlParas = makeIstOrUpdCommand(sessionId, dbStyle, tableName,
                objAttr, mapValue, sqlText);

        cmd.setCommandText(sqlText.toString());
        cmd.setCommandType(CmdOpt.Text);
        cmd.setParameters(sqlParas);

        PersistenceNamedParameter.parseSql(cmd);
        return cmd;
    }
}
