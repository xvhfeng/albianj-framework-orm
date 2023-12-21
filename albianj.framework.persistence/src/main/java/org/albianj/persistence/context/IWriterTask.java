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
package org.albianj.persistence.context;

import org.albianj.persistence.db.IDataBasePool;
import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.db.localize.IDBClientSection;
import org.albianj.persistence.object.IRunningStorageAttribute;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * 写事务的任务
 * 相对于job来说，task是job的细分
 * 对于albianj来说，每个task对应一个storage，task是albianj写操作的最小单位
 *
 * @author seapeak
 */
public interface IWriterTask {
    /**
     * 得到task所对应操作的storage
     *
     * @return
     */
    public IRunningStorageAttribute getStorage();

    /**
     * 设置task所对应操作的storage
     *
     * @param storage
     */
    public void setStorage(IRunningStorageAttribute storage);

    /**
     * 得到task所对应的命令
     *
     * @return
     */
    public List<IPersistenceCommand> getCommands();

    /**
     * 设置task所对应的命令
     *
     * @param commands
     */
    public void setCommands(List<IPersistenceCommand> commands);

    /**
     * 得到task对应storage的链接
     *
     * @return
     */
    public Connection getConnection();

    /**
     * 设置task对应的storage的链接
     *
     * @param connection
     */
    public void setConnection(Connection connection);

    /**
     * 得到task对应的sql命令
     *
     * @return
     */
    public Map<String,PersistenceStatement> getStatements();

    /**
     * 设置task对应的sql命令
     *
     * @param statements
     */
    public void setStatements(Map<String,PersistenceStatement> statements);

    /**
     * task是否已经提交
     *
     * @return
     */
    public boolean getIsCommited();

    /**
     * 设置task是否已经提交
     *
     * @param iscommited
     */
    public void setIsCommited(boolean iscommited);

    /**
     * 获取albianj“补偿事务”的sql语句
     *
     * @return
     */
    public List<Statement> getRollbackStatements();

    /**
     * 设置albanj的“补偿事务”的sql语句
     *
     * @param statements
     */
    public void setRollbackStatements(List<Statement> statements);


    public List<IPersistenceCommand> getRollbackCommands();

    public void setRollbackCommands(List<IPersistenceCommand> rollbackCmds);

    public boolean getCompensating();

    public void setCompensating(boolean compensating);

    public IDataBasePool getDatabasePool();

    public void setDatabasePool(IDataBasePool pool);

    /**
     * 是否开启批量提交
     */
    public boolean isBatchSubmit();
    public void setBatchSubmit(boolean isBatchSubmit);

    /**
     * 开启batch提交时候的命令
     */
    public Statement getBatchStmt();
    public void setBatchStmt(Statement stmt);

    public List<String> getBatchSqlText();
    public void setBatchSqlText(List<String> sqlTexts);

    public IDBClientSection getClientSection();
    public void setClientSection(IDBClientSection dbClient);


//    /*
//        开发者指定storage名称
//        该值出现的情况下，不需要走路由获取
//     */
//    String getStorageAliasName();
//
//    void setStorageAliasName(String storageAliasName);
//
//    /*
//     开发者指定的实体对应的数据库表名
//     一般和storageAliasName一起指定
//     当tableAliasName不存在并且不走路由的情况下，使用实体类名作为默认值
//     */
//    String getTableAliasName();
//
//    void setTableAliasName();
}
