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
package org.albianj.orm.impl.scope;


import org.albianj.kernel.kit.builtin.logger.LogLevel;
import org.albianj.kernel.kit.builtin.logger.LogTarget;
import org.albianj.kernel.kit.service.AlbianServiceRouter;
import org.albianj.orm.kit.context.ICompensateNotify;
import org.albianj.orm.ctx.WriterJobLifeTime;
import org.albianj.orm.ctx.WriterJob;

public abstract class FreeTransactionScope implements ITransactionScope {


    public boolean execute(WriterJob writerJob)  {
        boolean isSuccess = true;
        boolean isAutoRollbackSuccess = true;
        boolean isManualRollbackSuccess = true;
        StringBuilder sbMsg = new StringBuilder();
        try {
            writerJob.setWriterJobLifeTime(WriterJobLifeTime.NoStarted);
            this.preExecute(writerJob);
            writerJob.setWriterJobLifeTime(WriterJobLifeTime.Opened);
            this.executeHandler(writerJob);
            writerJob.setWriterJobLifeTime(WriterJobLifeTime.Runned);
            this.commit(writerJob);
            writerJob.setWriterJobLifeTime(WriterJobLifeTime.Commited);
        } catch (Exception e) {
            isSuccess = false;
            sbMsg.append("Execute job is error.Job lifetime is:").append(writerJob.getWriterJobLifeTime())
                .append(",exception msg:").append(e.getMessage()).append(",Current task:")
                .append(writerJob.getCurrentStorage()).append(",job id:").append(writerJob.getId());
            AlbianServiceRouter.log(writerJob.getId(), LogTarget.Running, LogLevel.Error,e,sbMsg.toString());
            try {
                switch (writerJob.getWriterJobLifeTime()) {
                    case Opened:
                    case Opening: {
                        break;
                    }
                    case Running:
                    case Runned:
                    case Commiting:
                    case Commited: {
                        // commited then manua rollback the data by albian
                        // and it can not keep the data consistency
                        writerJob.setWriterJobLifeTime(WriterJobLifeTime.AutoRollbacking);
                        try {
                            this.exceptionHandler(writerJob);
                        } catch (Exception exc) {
                            isAutoRollbackSuccess = false;
                            AlbianServiceRouter.log(writerJob.getId(),LogTarget.Running,LogLevel.Error,exc,
                                    "auto rollback  the job {} is fail.",writerJob.getId());
                        }
                        if (writerJob.isNeedManualRollback()) {
                            writerJob.setWriterJobLifeTime(WriterJobLifeTime.ManualRollbacking);
                            try {
                                isManualRollbackSuccess = this.exceptionManualRollback(writerJob);
                            } catch (Exception exc) {
                                isManualRollbackSuccess = false;
                                AlbianServiceRouter.log(writerJob.getId(),LogTarget.Running,LogLevel.Error,exc,
                                        "manual rollback  the job {} is fail.",writerJob.getId());
                            }
                        }

                        writerJob.setWriterJobLifeTime(WriterJobLifeTime.Rollbacked);
                        break;
                    }
                    default:
                        break;
                }

            } catch (Exception exc) {
                AlbianServiceRouter.log(writerJob.getId(),LogTarget.Running,LogLevel.Error,exc,
                        " rollback  the job {} is fail.",writerJob.getId());
            }

            try {
                if (!isManualRollbackSuccess) {
                    if (writerJob.isNeedManualRollback()) {
                        ICompensateNotify callback = null;
                        callback = writerJob.getCompensateCallback();

                        if (null == callback) {
                            callback = CompensateNotify.getInstance();
                        }
                        callback.send(isAutoRollbackSuccess, isManualRollbackSuccess, writerJob);
                    }
                }
            } catch (Exception exc) {
                AlbianServiceRouter.log(writerJob.getId(),LogTarget.Running,LogLevel.Error,exc,
                        " Execute the compensate callback of job {} is fail.",writerJob.getId());

            }

        } finally {
            try {
                unLoadExecute(writerJob);
            } catch (Exception exc) {
                AlbianServiceRouter.log(writerJob.getId(),LogTarget.Running,LogLevel.Error,exc,
                        " unload job {} is fail.",writerJob.getId());
            }
            if (null != writerJob.getNotifyCallback()) {
                try {

                    writerJob.getNotifyCallback().notice(isSuccess, sbMsg.toString(),
                            writerJob.getNotifyCallback());
                } catch (Exception exc) {
                    AlbianServiceRouter.log(writerJob.getId(),LogTarget.Running,LogLevel.Error,exc,
                            " Execute the notice of job {} is fail.",writerJob.getId());
                }
            }
            writerJob.setCurrentStorage(null);
        }

        return isSuccess;
    }

    protected abstract void preExecute(WriterJob writerJob) ;

    protected abstract void executeHandler(WriterJob writerJob) ;

    protected abstract void commit(WriterJob writerJob) ;

    protected abstract void exceptionHandler(WriterJob writerJob) ;

    protected abstract void unLoadExecute(WriterJob writerJob) ;

    protected abstract boolean exceptionManualRollback(WriterJob writerJob) ;
}
