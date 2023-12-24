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
package org.albianj.orm.impl.db;

import org.albianj.common.utils.CheckUtil;
import org.albianj.kernel.AlbianRuntimeException;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.kernel.logger.LogTarget;
import org.albianj.kernel.service.AlbianServiceRouter;
import org.albianj.orm.context.PersistenceStatement;
import org.albianj.orm.context.WriterJobLifeTime;
import org.albianj.orm.db.IDataBasePool;
import org.albianj.orm.db.localize.IDBClientSection;
import org.albianj.orm.impl.context.WriterJob;
import org.albianj.orm.impl.context.WriterTask;
import org.albianj.orm.impl.object.StorageAttribute;
import org.albianj.orm.impl.toolkit.ListConvert;
import org.albianj.orm.object.RunningStorageAttribute;
import org.albianj.orm.service.IAlbianStorageParserService;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class PersistenceTransactionClusterScope extends FreePersistenceTransactionClusterScope
    implements IPersistenceTransactionClusterScope {

    protected void preExecute(WriterJob writerJob)  {
        writerJob.setWriterJobLifeTime(WriterJobLifeTime.Opening);
        Map<String, WriterTask> tasks = writerJob.getWriterTasks();
        if (CheckUtil.isNullOrEmpty(tasks)) {
            throw new AlbianRuntimeException("the task for the job is null or empty.");
        }

        for (Map.Entry<String, WriterTask> task : tasks.entrySet()) {
            writerJob.setCurrentStorage(task.getKey());
            WriterTask t = task.getValue();
            RunningStorageAttribute rsa = t.getStorageSAttr();
            StorageAttribute storage = rsa.getStorageAttribute();
            IDBClientSection dbClientSection = t.getDbClientSection();
            if (null == storage) {
                throw new AlbianRuntimeException("The storage for task is null.");
            }
            try {
                IAlbianStorageParserService asps = AlbianServiceRouter.getService(writerJob.getId(),IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
                IDataBasePool dbp = asps.getDatabasePool(writerJob.getId(), rsa);
                t.setPool(dbp);
                t.setConnection(asps.getConnection(writerJob.getId(), dbp, rsa,false));
            } catch (Exception e) {
                AlbianServiceRouter.logAndThrowAgain(writerJob.getId(), LogTarget.Running, LogLevel.Error,e,
                        "get the connect to storage:{} is error.",
                        storage.getName());
            }
            List<PersistenceCommand> cmds = t.getCommands();
            if (CheckUtil.isNullOrEmpty(cmds)) {
                throw new AlbianRuntimeException("The commands for task is empty or null");
            }

            Map<String, PersistenceStatement> psMap = new LinkedHashMap<>();
            try {
                for (PersistenceCommand cmd : cmds) {
                    String cmdTxt = cmd.getCommandText();
                    if(psMap.containsKey(cmdTxt)) {
                        PersistenceStatement ps =  psMap.get(cmdTxt);
                        PreparedStatement psDb = ((PreparedStatement) ps.getStatement());
                        /**
                         * 第一次设置PreparedStatement的时候都不是batch的，所以isbatch == false
                         * 但第二次发现有执行语句一样的时候启用batch操作，那么得先把第一次的参数设置为batch，然后再设置第二次的
                         * 并且后续一直会再设置完sql参数的时候直接就addBatch，所以只有第二次的时候需要设置一下
                         */
                        if(!ps.isBatch()) {
                            psDb.addBatch();
                        }
                        Map<Integer, String> map = cmd.getParameterMapper();
                        if (CheckUtil.isNullOrEmpty(map)) {
                            continue;
                        } else {
                            for (int i = 1; i <= map.size(); i++) {
                                String paraName = map.get(i);
                                SqlParameter para = cmd.getParameters().get(paraName);
                                if (null == para.getValue()) {
                                    psDb.setNull(i, para.getSqlType());
                                } else {
                                    psDb.setObject(i, para.getValue(),
                                            para.getSqlType());
                                }
                            }
                        }
                        psDb.addBatch();
                        ps.setBatch(true);
                    } else {
                        PreparedStatement prepareStatement = t
                                .getConnection().prepareStatement(cmdTxt);
                        Map<Integer, String> map = cmd.getParameterMapper();
                        if (CheckUtil.isNullOrEmpty(map)) {
                            continue;
                        } else {
                            for (int i = 1; i <= map.size(); i++) {
                                String paraName = map.get(i);
                                SqlParameter para = cmd.getParameters().get(paraName);
                                if (null == para.getValue()) {
                                    prepareStatement.setNull(i, para.getSqlType());
                                } else {
                                    prepareStatement.setObject(i, para.getValue(),
                                            para.getSqlType());
                                }
                            }
                        }
                        PersistenceStatement ps = new PersistenceStatement(false,cmdTxt,prepareStatement);
                        psMap.put(cmdTxt,ps);
                    }
                }

                t.setStatements(psMap);
            } catch (SQLException e) {
                AlbianServiceRouter.logAndThrowAgain(writerJob.getId(),LogTarget.Running,LogLevel.Error,e,
                        "make sql command for task is empty or null");
            }

//            if(false) { // open batch submit from jdk
//                try {
//                    t.setBatchSubmit(true);
//                    Connection conn = t.getConnection();
//                    Statement batchStmt = conn.createStatement();
//                    List<String> sqlTexts = new ArrayList<>();
//                    String sqlText = null;
//                    for (IPersistenceCommand cmd : cmds) {
//                        StringBuilder sb = new StringBuilder();
//                        sqlText = cmd.getCommandText();
//                        Map<Integer, String> map = cmd.getParameterMapper();
//                        if (Validate.isNullOrEmpty(map)) {
//                            continue;
//                        } else {
//                            String regex = "\\?";
//                            Pattern pattern = Pattern.compile(regex);
//                            Matcher matcher = pattern.matcher(sqlText);
//                            int i = 1;
//                            while (matcher.find()){
//                                String paraName = map.get(i);
//                                ISqlParameter para = cmd.getParameters().get(paraName);
//                                String sqlvalue = dbClientSection.toSqlValue(para.getSqlType(),para.getValue(), storage.getCharset());
//                                String replacement = java.util.regex.Matcher.quoteReplacement(sqlvalue);
//                                matcher.appendReplacement(sb,replacement);
//                                i++;
//                            }
//                            sb.append(" )");//add last )
////                            String regex = "\\?";
////                            Pattern pattern = Pattern.compile(regex);
////
////                            Matcher matcher = pattern.matcher(sqlText);
////                            for(int i = 0;i < matcher.groupCount(); i++)
////                            {
////                                String paraName = map.get(i);
////                                ISqlParameter para = cmd.getParameters().get(paraName);
////                                sqlText = matcher.replaceFirst(dbClientSection.toSqlValue(para.getSqlType(),para.getValue(), null));
////                            }
//
////                            for (int i = 1; i <= map.size(); i++) {
////                                String paraName = map.get(i);
////                                ISqlParameter para = cmd.getParameters().get(paraName);
////                                sqlText = sqlText.replaceFirst("\\?",dbClientSection.toSqlValue(para.getSqlType(),para.getValue(), null));
////                            }
//                        }
//                        batchStmt.addBatch(sb.toString());
//                        sqlTexts.add(sb.toString());
//                    }
//                    t.setBatchStmt(batchStmt);
//                    t.setBatchSqlText(sqlTexts);
//                } catch (SQLException e) {
//                    throw new AlbianRuntimeException("make sql command for task is empty or null", e);
//                }
//            } else {
//                List<Statement> statements = new Vector<Statement>();
//                try {
//                    for (IPersistenceCommand cmd : cmds) {
//                        PreparedStatement prepareStatement = t
//                                .getConnection().prepareStatement(cmd.getCommandText());
//                        Map<Integer, String> map = cmd.getParameterMapper();
//                        if (Validate.isNullOrEmpty(map)) {
//                            continue;
//                        } else {
//                            for (int i = 1; i <= map.size(); i++) {
//                                String paraName = map.get(i);
//                                ISqlParameter para = cmd.getParameters().get(paraName);
//                                if (null == para.getValue()) {
//                                    prepareStatement.setNull(i, para.getSqlType());
//                                } else {
//                                    prepareStatement.setObject(i, para.getValue(),
//                                            para.getSqlType());
//                                }
//                            }
//                        }
//                        statements.add(prepareStatement);
//                    }
//                } catch (SQLException e) {
//                    throw new AlbianRuntimeException("make sql command for task is empty or null", e);
//                }
//                t.setStatements(statements);
//            }
        }
    }

    protected void executeHandler(WriterJob writerJob)  {
        writerJob.setWriterJobLifeTime(WriterJobLifeTime.Running);
        Map<String, WriterTask> tasks = writerJob.getWriterTasks();
        if (CheckUtil.isNullOrEmpty(tasks)) {
            throw new AlbianRuntimeException("The task is null or empty.");
        }


        for (Map.Entry<String, WriterTask> task : tasks.entrySet()) {
            WriterTask t = task.getValue();
            writerJob.setCurrentStorage(task.getKey());

            List<PersistenceCommand> cmds = t.getCommands();
            AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,
                    "then execute sql command but may be use batchupdate when commands(size >= 2) are the same.");
            for (int i = 0; i < cmds.size(); i++) {
                PersistenceCommand cmd = cmds.get(i);
                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,
                        "executeHandler storage:{},sqltext:{},parars:{}.",
                        task.getKey(), cmd.getCommandText(), ListConvert.toString(cmd.getParameters()));
            }

            Map<String,PersistenceStatement> psMap = t.getStatements();
            for (Map.Entry<String,PersistenceStatement> kv : psMap.entrySet()) {
                PersistenceStatement ps =  kv.getValue();
                try {
                    if (ps.isBatch()) {
                        int[] ret = ps.getStatement().executeBatch();
                    } else {
                        ((PreparedStatement) ps.getStatement()).executeUpdate();
                    }
                }  catch (SQLException e) {
                RunningStorageAttribute rsa = t.getStorageSAttr();
                    AlbianServiceRouter.logAndThrowAgain(writerJob.getId(),LogTarget.Running,LogLevel.Error,e,
                            "commit to storage:{}  database: {} is fail.",
                            rsa.getStorageAttribute().getName() ,rsa.getDatabase() );
                }
            }




//
//            if(t.isBatchSubmit()) {
//                List<String> sqlTexts = t.getBatchSqlText();
//                Statement batchStmt = t.getBatchStmt();
//                for(String sqlText : sqlTexts){
//                    logger.info("executeHandler storage:{},sqltext:{}.", task.getKey(), sqlText);
//                }
//                try {
//                    int[] rtn = batchStmt.executeBatch();
//                    batchStmt.clearBatch();
//                } catch (SQLException e) {
//                    RunningStorageAttribute rsa = t.getStorage();
//                    throw new AlbianRuntimeException(
//                            "execute to storage:" + rsa.getStorageAttribute().getName() + " dtabase:" + rsa.getDatabase()
//                                    + " is fail.", e);
//                }
//            } else {
//                List<Statement> statements = t.getStatements();
//                List<IPersistenceCommand> cmds = t.getCommands();
//                for (int i = 0; i < statements.size(); i++) {
//                    try {
//                        IPersistenceCommand cmd = cmds.get(i);
//                        logger.info("executeHandler storage:{},sqltext:{},parars:{}.", task.getKey(), cmd.getCommandText(),
//                                ListConvert.toString(cmd.getParameters()));
//                        ((PreparedStatement)statements.get(i)).executeUpdate();
//                    } catch (SQLException e) {
//                        RunningStorageAttribute rsa = t.getStorage();
//                        throw new AlbianRuntimeException(
//                                "execute to storage:" + rsa.getStorageAttribute().getName() + " dtabase:" + rsa.getDatabase()
//                                        + " is fail.", e);
//                    }
//                }
//            }
        }
    }

    protected void commit(WriterJob writerJob)  {
        writerJob.setWriterJobLifeTime(WriterJobLifeTime.Commiting);
        Map<String, WriterTask> tasks = writerJob.getWriterTasks();
        if (CheckUtil.isNullOrEmpty(tasks)) {
            throw new AlbianRuntimeException("The task is null or empty.");
        }
        for (Map.Entry<String, WriterTask> task : tasks.entrySet()) {
            WriterTask t = task.getValue();
            try {
                writerJob.setCurrentStorage(task.getKey());
                t.getConnection().commit();
                t.setBatchSubmit(true);
                writerJob.setNeedManualRollback(true);
            } catch (SQLException e) {
                RunningStorageAttribute rsa = t.getStorageSAttr();
                AlbianServiceRouter.logAndThrowAgain(writerJob.getId(),LogTarget.Running,LogLevel.Error,e,
                    "commit to storage:{}  database: {} is fail.",
                        rsa.getStorageAttribute().getName() ,rsa.getDatabase() );
            }
        }
    }

    protected void exceptionHandler(WriterJob writerJob)  {
        boolean isThrow = false;
        Map<String, WriterTask> tasks = writerJob.getWriterTasks();
        if (CheckUtil.isNullOrEmpty(tasks)) {
            throw new AlbianRuntimeException("The task is null or empty.");
        }
        for (Map.Entry<String, WriterTask> task : tasks.entrySet()) {
            WriterTask t = task.getValue();
            try {
                if (!t.isCommited()) {
                    t.getConnection().rollback();
                }
            } catch (Exception e) {
                RunningStorageAttribute rsa = t.getStorageSAttr();
                AlbianServiceRouter.log(writerJob.getId(), LogTarget.Running, LogLevel.Error,e,
                        "rollback to storage:{} database:{} is error.",
                        rsa.getStorageAttribute().getName(), rsa.getDatabase());
                isThrow = true;
            }
        }
        if (isThrow)
            throw new AlbianRuntimeException("DataService is error.");
    }

    protected boolean exceptionManualRollback(WriterJob writerJob)  {
        try {
            manualRollbackPreExecute(writerJob);
            manualRollbackExecuteHandler(writerJob);
            manualRollbackCommit(writerJob);
            return true;
        } catch (Exception e) {
            AlbianServiceRouter.log(writerJob.getId(), LogTarget.Running, LogLevel.Error,e,
                    "manual rollback is fail.");
            return false;
        }
    }


    protected void unLoadExecute(WriterJob writerJob)  {
        boolean isThrow = false;
        Map<String, WriterTask> tasks = writerJob.getWriterTasks();
        if (CheckUtil.isNullOrEmpty(tasks)) {
            throw new AlbianRuntimeException("The task is null or empty.");
        }
        WriterTask t = null;
        for (Map.Entry<String, WriterTask> task : tasks.entrySet()) {
            try {
                t = task.getValue();
                Map<String,PersistenceStatement> psMap =  t.getStatements();
                List<Statement> sts = new ArrayList<>();
                for (PersistenceStatement ps: psMap.values()) {
                    sts.add(ps.getStatement());
                }
                RunningStorageAttribute rsa = t.getStorageSAttr();
                IDataBasePool dbp = t.getPool();
                dbp.returnConnection(writerJob.getId(), rsa.getStorageAttribute().getName(), rsa.getDatabase(),
                        t.getConnection(), sts);
            }catch (Exception e){
                isThrow = true;
                RunningStorageAttribute rsa = t.getStorageSAttr();
                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,e,
                        "close the connect to storage:{} database:{} is fail.",
                        rsa.getStorageAttribute().getName(), rsa.getDatabase());
            } finally {
                t = null;
            }
        }
    }


    private void manualRollbackPreExecute(WriterJob writerJob)  {
        Map<String, WriterTask> tasks = writerJob.getWriterTasks();
        if (CheckUtil.isNullOrEmpty(tasks)) {
            throw new AlbianRuntimeException("the task for the job is null or empty when manual rollbacking.");
        }

        for (Map.Entry<String, WriterTask> task : tasks.entrySet()) {
            WriterTask t = task.getValue();
            if (!t.isCommited()) continue;// not commit then use auto rollback

            List<PersistenceCommand> cmds = t.getCommands();
            if (CheckUtil.isNullOrEmpty(cmds)) {
                throw new AlbianRuntimeException("The commands for task is empty or null when manual rollbacking.");
            }
            List<Statement> statements = new Vector<Statement>();
            List<PersistenceCommand> rbkCmds = new Vector<>();
            try {
                for (PersistenceCommand cmd : cmds) {
                    if (!cmd.isCompensating()) continue;
                    PreparedStatement prepareStatement = t
                            .getConnection().prepareStatement(cmd.getRollbackCommandText());
                    Map<Integer, String> map = cmd.getRollbackParameterMapper();
                    if (CheckUtil.isNullOrEmpty(map)) {
                        continue;
                    } else {
                        for (int i = 1; i <= map.size(); i++) {
                            String paraName = map.get(i);
                            SqlParameter para = cmd.getRollbackParameters().get(paraName);
                            if (null == para.getValue()) {
                                prepareStatement.setNull(i, para.getSqlType());
                            } else {
                                prepareStatement.setObject(i, para.getValue(),
                                        para.getSqlType());
                            }
                        }
                    }
                    statements.add(prepareStatement);
                    rbkCmds.add(cmd);
                }
            } catch (SQLException e) {
                AlbianServiceRouter.logAndThrowAgain(writerJob.getId(), LogTarget.Running, LogLevel.Info,e,
                    "make sql command for task is empty or null when maunal rollbacking.");
            }
            if (!CheckUtil.isNullOrEmpty(statements)) {
                t.setRollbackStatements(statements);
                t.setRbkCmds(rbkCmds);
                t.setCompensating(true);
            }
        }
    }

    private void manualRollbackExecuteHandler(WriterJob writerJob)  {
        Map<String, WriterTask> tasks = writerJob.getWriterTasks();
        if (CheckUtil.isNullOrEmpty(tasks)) {
            throw new AlbianRuntimeException("The task is null or empty.");
        }

        for (Map.Entry<String, WriterTask> task : tasks.entrySet()) {
            WriterTask t = task.getValue();
            if (!t.isCommited()) continue;
            if (!t.isCompensating()) continue;

            List<Statement> statements = t.getRollbackStatements();
            List<PersistenceCommand> cmds = t.getRbkCmds();
            if (CheckUtil.isNullOrEmpty(statements)) continue;
            ;
            for (int i = 0; i < statements.size(); i++) {
                try {
                    PersistenceCommand cmd = cmds.get(i);
                    AlbianServiceRouter.log(writerJob.getId(), LogTarget.Running, LogLevel.Info,
                            "manual-rollback job,storage:{},sqltext:{},parars:{}.", task.getKey(),
                        cmd.getRollbackCommandText(), ListConvert.toString(cmd.getRollbackParameters()));
                    ((PreparedStatement)statements.get(i)).executeUpdate();
                } catch (SQLException e) {
                    RunningStorageAttribute rsa = t.getStorageSAttr();
                    AlbianServiceRouter.logAndThrowAgain(writerJob.getId(), LogTarget.Running, LogLevel.Info,e,
                        "execute to storage:{} database:{} is error when manual rollbacking.",
                            rsa.getStorageAttribute().getName(), rsa.getDatabase());
                }
            }
        }
    }

    private void manualRollbackCommit(WriterJob writerJob)  {
        Map<String, WriterTask> tasks = writerJob.getWriterTasks();
        if (CheckUtil.isNullOrEmpty(tasks)) {
            throw new RuntimeException("The task is null or empty.");
        }
        for (Map.Entry<String, WriterTask> task : tasks.entrySet()) {
            WriterTask t = task.getValue();
            if (!t.isCommited()) continue;
            try {
                if (t.isCompensating()) {
                    t.getConnection().commit();
                }
            } catch (SQLException e) {
                RunningStorageAttribute rsa = t.getStorageSAttr();
                AlbianServiceRouter.logAndThrowAgain(writerJob.getId(), LogTarget.Running, LogLevel.Info,e,
                        "commit to storage:{} database:{} is error when manual rollbacking.",
                        rsa.getStorageAttribute().getName(), rsa.getDatabase());
            }
        }
    }

}
