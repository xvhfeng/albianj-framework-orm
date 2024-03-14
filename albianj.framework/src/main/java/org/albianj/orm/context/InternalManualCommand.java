package org.albianj.orm.context;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.orm.db.ISqlParameter;
import org.albianj.orm.db.PersistenceCommandType;

import java.util.Map;

/**
 * Created by xuhaifeng on 17/8/31.
 */
@Data
@NoArgsConstructor
public class InternalManualCommand  {

    private String sqlText;
    private PersistenceCommandType cmdType = PersistenceCommandType.Text;
    private Map<Integer, String> parameterMapper = null;
    private Map<String, ISqlParameter> commandParameters = null;

}
