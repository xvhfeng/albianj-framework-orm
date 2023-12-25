package org.albianj.orm.ctx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Statement;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersistenceStatement {
    private boolean isBatch = false;
    private String cmdText;
    private Statement statement;
}
