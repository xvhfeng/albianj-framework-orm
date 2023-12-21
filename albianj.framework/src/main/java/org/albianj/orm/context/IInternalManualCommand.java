package org.albianj.orm.context;

import org.albianj.orm.db.ISqlParameter;
import org.albianj.orm.db.PersistenceCommandType;

import java.util.Map;

/**
 * 内部的手动执行command类
 */
public interface IInternalManualCommand {

    PersistenceCommandType getCmdType();

    void setCmdType(PersistenceCommandType cmdType);

    /**
     * 命令的执行参数
     *
     * @return
     */
    public Map<Integer, String> getParameterMapper();

    /**
     * 命令的执行参数
     *
     * @param parameterMapper
     */
    public void setParameterMapper(Map<Integer, String> parameterMapper);

    /*
     *   经过正则表达式过滤后的可执行sql
     */
    String getSqlText();

    /**
     * 经过正则表达式过滤后的可执行sql
     *
     * @param sql
     */
    void setSqlText(String sql);

    Map<String, ISqlParameter> getCommandParameters();

    void setCommandParameters(Map<String, ISqlParameter> paras);

}
