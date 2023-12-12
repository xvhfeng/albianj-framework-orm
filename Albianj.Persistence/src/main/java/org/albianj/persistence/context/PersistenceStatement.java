package org.albianj.persistence.context;

import java.sql.Statement;

public class PersistenceStatement {
    private boolean isBatch = false;

    private String cmdText;

    public boolean isBatch() {
        return isBatch;
    }

    public void setBatch(boolean batch) {
        isBatch = batch;
    }

    public String getCmdText() {
        return cmdText;
    }

    public void setCmdText(String cmdText) {
        this.cmdText = cmdText;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    private Statement statement;

    public PersistenceStatement(boolean isBatch, String cmdText, Statement statement) {
        this.isBatch = isBatch;
        this.cmdText = cmdText;
        this.statement = statement;
    }

    public PersistenceStatement() {
    }
}
