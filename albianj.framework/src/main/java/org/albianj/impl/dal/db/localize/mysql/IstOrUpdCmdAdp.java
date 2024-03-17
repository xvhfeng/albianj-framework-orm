package org.albianj.impl.dal.db.localize.mysql;

import org.albianj.api.dal.db.PCmd;
import org.albianj.api.dal.object.AblEntityAttr;
import org.albianj.api.dal.object.IAblObj;
import org.albianj.impl.dal.db.IDMLCmd;

import java.util.Map;

/**
 * 不需要AlbianObject isNew状态支持的insert or update功能
 * 但是需要区分是否需要更新null字段
 */
public class IstOrUpdCmdAdp implements IDMLCmd {
    @Override
    public PCmd buildPstCmd(String sessionId, int dbStyle, String tableName, IAblObj object, AblEntityAttr objAttr, Map<String, Object> mapValue, boolean rbkOnError) {
        //            String sqlTxt = "INSERT INTO keyword(radical, show_text, katakana, speech_id, show_times, speech_1, speech_2, speech_next) VALUES(?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE show_times= show_times+VALUES(show_times) ";
        return null;
    }
}
