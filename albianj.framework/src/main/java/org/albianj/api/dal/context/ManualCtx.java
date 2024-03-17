package org.albianj.api.dal.context;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.api.dal.object.RStgAttr;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * Created by xuhaifeng on 17/8/31.
 */
@Data
@NoArgsConstructor
public class ManualContext {

    private String sessionId;
    private List<ManualCommand> commands;
    private List<InternalManualCommand> internalCommands;
    private Connection connection;
    private List<Statement> statements;
    private String storageName;
    private RStgAttr RStgAttr;
    private String databaseName;
    private List<Integer> results;
    private WrtJobLfcOpt lifeTime = WrtJobLfcOpt.Normal;
}
