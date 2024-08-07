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
package org.albianj.api.kernel.service;

import org.albianj.ServRouter;
import org.albianj.api.kernel.anno.proxy.AlbianProxyIgnoreRant;
import org.albianj.api.kernel.attr.AlbianServiceLifetime;
import org.albianj.api.kernel.attr.GlobalSettings;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.common.utils.StringsUtil;

import java.io.File;

/**

 *
 * @author Seapeak
 */
//@AlbianKernel
public abstract class FreeAblServ implements IAblServ {

    boolean enableProxy = false;
    IAblServ service = null;
    private AlbianServiceLifetime state = AlbianServiceLifetime.Normal;

    @AlbianProxyIgnoreRant(ignore = true)
    public AlbianServiceLifetime getAlbianServiceState() {
        // TODO Auto-generated method stub
        return this.state;
    }

    @AlbianProxyIgnoreRant(ignore = true)
    public void beforeLoad()   {
        // TODO Auto-generated method stub
        this.state = AlbianServiceLifetime.BeforeLoading;
    }

    @AlbianProxyIgnoreRant(ignore = true)
    public void loading()  {
        // TODO Auto-generated method stub
        this.state = AlbianServiceLifetime.Loading;
    }

    @AlbianProxyIgnoreRant(ignore = true)
    public void afterLoading()   {
        // TODO Auto-generated method stub
        this.state = AlbianServiceLifetime.Running;
    }

    @AlbianProxyIgnoreRant(ignore = true)
    public void beforeUnload()   {
        // TODO Auto-generated method stub
        this.state = AlbianServiceLifetime.BeforeUnloading;

    }

    @AlbianProxyIgnoreRant(ignore = true)
    public void unload()   {
        // TODO Auto-generated method stub
        this.state = AlbianServiceLifetime.Unloading;
    }

    @AlbianProxyIgnoreRant(ignore = true)
    public void afterUnload()   {
        // TODO Auto-generated method stub
        this.state = AlbianServiceLifetime.Unloaded;
    }

    @AlbianProxyIgnoreRant(ignore = true)
    public void init()  {
        // TODO Auto-generated method stub

    }

    @AlbianProxyIgnoreRant(ignore = true)
    public boolean enableProxy() {
        return enableProxy;
    }

    @AlbianProxyIgnoreRant(ignore = true)
    public IAblServ getRealService() {
        return null == service ? this : service;
    }

    @AlbianProxyIgnoreRant(ignore = true)
    public void setRealService(IAblServ service) {
        if (null != service) {
            this.service = service;
            enableProxy = true;
        } else {
            enableProxy = false;
        }
        return;
    }

    @Override
    @AlbianProxyIgnoreRant(ignore = true)
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    @AlbianProxyIgnoreRant(ignore = true)
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    @AlbianProxyIgnoreRant(ignore = true)
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    @AlbianProxyIgnoreRant(ignore = true)
    public String toString() {
        return super.toString();
    }


    /**
     * 判断配置文件是否存在，如果存在就返回配置文件路径
     * 优先根据配置文件目录判断，如果不存在，直接使用filename，就认为filename就是文件地址
     *
     * @param filename
     * @return
     */
    @AlbianProxyIgnoreRant(ignore = true)
    protected String findConfigFile(String filename,boolean isMustExist)  {
        try {
            File f = new File(filename);
            if (f.exists()) {
                return filename;
            }
            String folder = GlobalSettings.getInst().getConfigurtionFolder();
            String tmpName = null;
            if (folder.endsWith(File.separator)) {
                tmpName = folder + filename;
            } else {
                tmpName = folder + File.separator + filename;
            }
            f = new File(tmpName);
            if (f.exists()) {
                return f.getAbsolutePath();
            }

            if (isMustExist) {
                ServRouter.log(ServRouter.__StartupSessionId, LogLevel.Warn,
                        "not found the config filename:{}", filename);
            }
            return null;
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,  LogLevel.Error,e,
                    "found the config filename:{} broken", filename);
        }
        return filename;
    }

    public String getServiceName() {
        return this.getClass().getSimpleName();
    }
}
