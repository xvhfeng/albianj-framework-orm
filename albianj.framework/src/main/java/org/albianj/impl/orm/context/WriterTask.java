/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
package org.albianj.impl.orm.context;


import org.albianj.orm.context.IWriterTask;
import org.albianj.orm.context.PersistenceStatement;
import org.albianj.orm.db.IDataBasePool;
import org.albianj.orm.db.IPersistenceCommand;
import org.albianj.orm.db.localize.IDBClientSection;
import org.albianj.orm.object.IRunningStorageAttribute;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class WriterTask implements IWriterTask {
    List<IPersistenceCommand> rbkCmds = null;
    boolean compensating = true;
    private IRunningStorageAttribute storage = null;
    private List<IPersistenceCommand> commands = null;
    private Connection connection = null;
    private Map<String, PersistenceStatement> statements = null;
    private List<Statement> rollbackStatements = null;
    private boolean iscommited = false;
    private IDataBasePool pool = null;
    private boolean isBatchSubmit = false;
    private Statement batchStmt = null;
    private IDBClientSection dbClientSection =null;
    private List<String> sqlTexts;

    public IRunningStorageAttribute getStorage() {
        // TODO Auto-generated method stub
        return this.storage;
    }

    public void setStorage(IRunningStorageAttribute storage) {
        // TODO Auto-generated method stub
        this.storage = storage;
    }

    public List<IPersistenceCommand> getCommands() {
        // TODO Auto-generated method stub
        return this.commands;
    }

    public void setCommands(List<IPersistenceCommand> commands) {
        // TODO Auto-generated method stub
        this.commands = commands;
    }

    public Connection getConnection() {
        // TODO Auto-generated method stub
        return this.connection;
    }

    public void setConnection(Connection connection) {
        // TODO Auto-generated method stub
        this.connection = connection;
    }

    public Map<String,PersistenceStatement> getStatements() {
        // TODO Auto-generated method stub
        return this.statements;
    }

    public void setStatements(Map<String,PersistenceStatement> statements) {
        // TODO Auto-generated method stub
        this.statements = statements;
    }

    @Override
    public boolean getIsCommited() {
        // TODO Auto-generated method stub
        return this.iscommited;
    }

    @Override
    public void setIsCommited(boolean iscommited) {
        // TODO Auto-generated method stub
        this.iscommited = iscommited;
    }

    @Override
    public List<Statement> getRollbackStatements() {
        // TODO Auto-generated method stub
        return this.rollbackStatements;
    }

    @Override
    public void setRollbackStatements(List<Statement> statements) {
        // TODO Auto-generated method stub
        this.rollbackStatements = statements;
    }

    public List<IPersistenceCommand> getRollbackCommands() {
        return this.rbkCmds;
    }

    public void setRollbackCommands(List<IPersistenceCommand> rollbackCmds) {
        this.rbkCmds = rollbackCmds;
    }

    public boolean getCompensating() {
        return this.compensating;
    }

    public void setCompensating(boolean compensating) {
        this.compensating = compensating;
    }

    public IDataBasePool getDatabasePool() {
        return this.pool;
    }

    public void setDatabasePool(IDataBasePool pool) {
        this.pool = pool;
    }

    @Override
    public boolean isBatchSubmit() {
        return this.isBatchSubmit;
    }

    @Override
    public void setBatchSubmit(boolean isBatchSubmit) {
        this.isBatchSubmit = isBatchSubmit;
    }

    @Override
    public Statement getBatchStmt() {
        return this.batchStmt;
    }

    @Override
    public void setBatchStmt(Statement stmt) {
        this.batchStmt = stmt;
    }

    public IDBClientSection getClientSection() {
        return dbClientSection;
    }
    public void setClientSection(IDBClientSection dbClient){
        this.dbClientSection = dbClient;
    }

    public List<String> getBatchSqlText(){
        return this.sqlTexts;
    }
    public void setBatchSqlText(List<String> sqlTexts){
        this.sqlTexts = sqlTexts;
    }

}
