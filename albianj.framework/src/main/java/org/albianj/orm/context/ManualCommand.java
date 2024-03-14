package org.albianj.orm.context;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.impl.orm.db.SqlParameter;
import org.albianj.orm.db.PersistenceCommandType;

import java.util.Map;

/**
 * Created by xuhaifeng on 17/8/31.
 */
@Data
@NoArgsConstructor
public class ManualCommand  {
    private String commandText;
    private PersistenceCommandType cmdType = PersistenceCommandType.Text;
    private Map<String, SqlParameter> commandParameters = null;
}
