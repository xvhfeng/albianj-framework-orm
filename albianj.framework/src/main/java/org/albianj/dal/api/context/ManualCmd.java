package org.albianj.dal.api.context;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.dal.api.db.SqlPara;
import org.albianj.dal.api.db.CmdOpt;

import java.util.Map;

/**
 * Created by xuhaifeng on 17/8/31.
 */
@Data
@NoArgsConstructor
public class ManualCmd {
    private String commandText;
    private CmdOpt cmdType = CmdOpt.Text;
    private Map<String, SqlPara> commandParameters = null;
}
