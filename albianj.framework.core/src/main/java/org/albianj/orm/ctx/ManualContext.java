package org.albianj.orm.ctx;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.orm.attr.RunningStorageAttribute;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
/**
 * Created by xuhaifeng on 17/8/31.
 */
public class ManualContext  {

    private String sessionId;
    private List<ManualCommand> cmds;
    private List<InternalManualCommand> internalCmds;
    private Connection conn;
    private List<Statement> statements;
    private String storageName;
    private RunningStorageAttribute runStgAttr;
    private String dbName;
    private List<Integer> rcs;
    private WriterJobLifeTime lifeTime = WriterJobLifeTime.Normal;
}