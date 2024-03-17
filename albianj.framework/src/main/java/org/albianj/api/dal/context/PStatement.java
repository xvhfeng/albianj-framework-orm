package org.albianj.api.dal.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Statement;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PStatement {
    private boolean batch = false;
    private String cmdText;
    private Statement statement;
}
