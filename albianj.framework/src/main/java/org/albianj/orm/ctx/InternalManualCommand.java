package org.albianj.orm.ctx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.orm.itf.db.PersistenceCommandType;
import org.albianj.orm.itf.db.SqlParameter;

import java.util.Map;

/**
 * Created by xuhaifeng on 17/8/31.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InternalManualCommand {

    private String sqlText;
    private PersistenceCommandType cmdType = PersistenceCommandType.Text;
    private Map<Integer, String> parameterMapper = null;
    private Map<String, SqlParameter> commandParameters = null;
}
