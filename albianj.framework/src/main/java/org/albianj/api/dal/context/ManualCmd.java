package org.albianj.api.dal.context;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.api.dal.db.SqlParameter;
import org.albianj.api.dal.db.CmdOpt;

import java.util.Map;

/**
 * Created by xuhaifeng on 17/8/31.
 */
@Data
@NoArgsConstructor
public class ManualCommand  {
    private String commandText;
    private CmdOpt cmdType = CmdOpt.Text;
    private Map<String, SqlParameter> commandParameters = null;
}
