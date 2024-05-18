package org.albianj.api.dal.object;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StgTempAttr {
    private String name = null;
    private int databaseStyle = DBOpt.MySql;
    private String user = null;
    private String password = null;
    private boolean pooling = true;
    private int minSize = 5;
    private int maxSize = 10;
    private int timeout = 30;
    private String charset = "utf8";
    private boolean transactional = true;
    private String server = null;
    private int port = 3306;
    private int transactionLevel = 0;
    private String options = null;
    private int idelTime = 180;
    private String dbps = DBPOpt.SpxDBCP.name();
    private int waitTimeWhenGetMs = 2;
    private int lifeCycleTime = 3600000;
    private int waitInFreePoolMs = 120000;
    private int maxRemedyConnectionCount = 50;
    private int cleanupTimestampMs = 30000;
    private int maxRequestTimeMs = 60000;
    private String urlParaments;
}
