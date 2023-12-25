package org.albianj.orm.ctx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.orm.kit.db.PersistenceCommandType;
import org.albianj.orm.kit.db.SqlParameter;

import java.util.Map;

/**
 * Created by xuhaifeng on 17/8/31.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ManualCommand {
    private String cmdText;
    private PersistenceCommandType cmdType = PersistenceCommandType.Text;
    private Map<String, SqlParameter> cmdParameters = null;

}
