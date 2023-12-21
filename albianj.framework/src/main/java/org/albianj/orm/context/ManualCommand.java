package org.albianj.orm.context;

import org.albianj.orm.db.ISqlParameter;
import org.albianj.orm.db.PersistenceCommandType;

import java.util.Map;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public class ManualCommand implements IManualCommand {

    private String cmdText;
    private PersistenceCommandType cmdType = PersistenceCommandType.Text;
    private Map<String, ISqlParameter> cmdParameters = null;

    @Override
    public String getCommandText() {
        return cmdText;
    }

    @Override
    public void setCommandText(String sqlText) {
        this.cmdText = sqlText;
    }

    @Override
    public PersistenceCommandType getCmdType() {
        return this.cmdType;
    }

    @Override
    public void setCmdType(PersistenceCommandType cmdType) {
        this.cmdType = cmdType;
    }

    @Override
    public Map<String, ISqlParameter> getCommandParameters() {
        return this.cmdParameters;
    }

    @Override
    public void setCommandParameters(Map<String, ISqlParameter> paras) {
        this.cmdParameters = paras;
    }
}
