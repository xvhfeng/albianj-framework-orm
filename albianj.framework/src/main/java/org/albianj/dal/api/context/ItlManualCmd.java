package org.albianj.dal.api.context;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.albianj.dal.api.db.CmdOpt;
import org.albianj.dal.api.db.SqlPara;

import java.util.Map;

/**
 * Created by xuhaifeng on 17/8/31.
 */
@Data
@NoArgsConstructor
public class ItlManualCmd {

    private String sqlText;
    private CmdOpt cmdType = CmdOpt.Text;
    private Map<Integer, String> parameterMapper = null;
    private Map<String, SqlPara> commandParameters = null;

}
