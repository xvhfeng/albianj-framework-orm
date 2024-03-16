package org.albianj.dal.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Statement;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersistenceStatement {
    private boolean batch = false;
    private String cmdText;
    private Statement statement;
}
