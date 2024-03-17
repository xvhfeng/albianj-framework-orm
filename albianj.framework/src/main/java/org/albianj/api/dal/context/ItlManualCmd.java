package org.albianj.api.dal.context;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.albianj.api.dal.db.SqlPara;
import org.albianj.api.dal.db.CmdOpt;

import java.util.Map;

/**
 * Created by xuhaifeng on 17/8/31.
 */
@Data
@NoArgsConstructor
public class InternalManualCommand  {

    private String sqlText;
    private CmdOpt cmdType = CmdOpt.Text;
    private Map<Integer, String> parameterMapper = null;
    private Map<String, SqlPara> commandParameters = null;

}
