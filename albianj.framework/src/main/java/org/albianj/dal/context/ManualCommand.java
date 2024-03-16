package org.albianj.dal.context;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.dal.db.SqlParameter;
import org.albianj.dal.db.CommandOpt;

import java.util.Map;

/**
 * Created by xuhaifeng on 17/8/31.
 */
@Data
@NoArgsConstructor
public class ManualCommand  {
    private String commandText;
    private CommandOpt cmdType = CommandOpt.Text;
    private Map<String, SqlParameter> commandParameters = null;
}
