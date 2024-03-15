package org.albianj.dal.context;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.albianj.dal.db.SqlParameter;
import org.albianj.dal.db.PersistenceCommandType;

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
    private Map<String, SqlParameter> commandParameters = null;

}
