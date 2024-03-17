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
package org.albianj.impl.kernel.core;

import org.albianj.AblThrowable;
import org.albianj.ServRouter;
import org.albianj.api.kernel.attr.AlbianServiceAttribute;
import org.albianj.impl.kernel.service.FreeAlbianServiceParser;
import org.albianj.api.kernel.attr.ApplicationSettings;
import org.albianj.api.kernel.attr.GlobalSettings;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.api.kernel.service.IAlbianService;
import org.albianj.api.kernel.attr.ServiceAttributeMap;
import org.albianj.api.kernel.attr.ServiceContainer;
import org.albianj.loader.IAlbianTransmitterService;

import java.util.*;

/**
 * @author Seapeak
 */
public class AlbianTransmitterService implements IAlbianTransmitterService {

    public String getServiceName() {
        return Name;
    }

    @Override
    public void start(Class<?> mainClzz,String configurtionFolder)  {

        GlobalSettings globalSettings = new GlobalSettings(mainClzz,configurtionFolder);
        ApplicationSettings.setGlobalSettings(globalSettings);

        // first load logger
        // 必须开始第一件事情就是起logger service，以保证后续日志可以被记录
        AlbianBuiltinServiceLoader bltSevLoader = new AlbianBuiltinServiceLoader();
        bltSevLoader.loadLoggerService();
        ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Info,"Logger Service startup normal.");
        // 从这里以后，就可以正常使用log了，前面的logger自行处理，但是一般不需要任何的处理

        // do load builtin service
        bltSevLoader.loadServices(ServRouter.__StartupSessionId);
        Map<String, AlbianServiceAttribute> bltSrvAttrs = bltSevLoader.getBltSrvAttrs();

        //do load bussiness service
        Map<String, AlbianServiceAttribute> bnsSrvAttrs =
            (Map<String, AlbianServiceAttribute>) ServiceAttributeMap.get(FreeAlbianServiceParser.ALBIANJSERVICEKEY);

        Map<String, AlbianServiceAttribute> mapAttr = new HashMap<>();
        if (bnsSrvAttrs != null) {
            mapAttr.putAll(bnsSrvAttrs); // copy it for field setter
        }
        for (String bltServKey : bltSrvAttrs.keySet()) { // remove builtin service in service.xml
            if (mapAttr.containsKey(bltServKey)) {
                mapAttr.remove(bltServKey);
            }
        }

        Map<String, AlbianServiceAttribute> failMap = new LinkedHashMap<String, AlbianServiceAttribute>();
        int lastFailSize = 0;
        int currentFailSize = 0;
        Exception e = null;
        while (true) {
            lastFailSize = currentFailSize;
            currentFailSize = 0;
            String sType = null;
            String id = null;
            String sInterface = null;
            for (Map.Entry<String, AlbianServiceAttribute> entry : mapAttr.entrySet())
                try {
                    AlbianServiceAttribute serviceAttr = entry.getValue();
                    IAlbianService service = AlbianServiceLoader.makeupService(serviceAttr, mapAttr);
                    ServiceContainer.addService(serviceAttr.getId(), service);
                } catch (Exception exc) {
                    e = exc;
                    currentFailSize++;
                    failMap.put(entry.getKey(), entry.getValue());
                    ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,  LogLevel.Error,exc,
                            "Kernel is error.load and init service:{} with class:{} is fail.",
                            id,sType);
                }
            if (0 == currentFailSize) {
                // if open the distributed mode,
                // please contact to manager machine to logout the system.
                ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Info,
                        "load service is success,then set field in the services!");
                break;// load service successen
            }

            if (lastFailSize == currentFailSize) {
                // startup the service fail in this times,
                // so throw the exception and stop the albianj engine
                ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Error,
                        "startup slbianJ engine is fail ,maybe cross refernce");
                if (null != e) {
                    StringBuilder errBuilder = new StringBuilder();
                    for (Map.Entry<String, AlbianServiceAttribute> entry : failMap.entrySet()) {
                        errBuilder.append(entry.getKey()).append(",");
                    }

                    ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Error,
                            "startup the service :{} is fail .", errBuilder.toString());
                }
                ServiceContainer.clear();
                throw new AblThrowable(e);
            } else {
                mapAttr.clear();
                mapAttr.putAll(failMap);
                failMap.clear();
            }
        }

        // merger kernel service and bussines service
        // then update the all service attribute
        if (bnsSrvAttrs != null) {
            bltSrvAttrs.putAll(bnsSrvAttrs);
        }
        ServiceAttributeMap.insert(FreeAlbianServiceParser.ALBIANJSERVICEKEY, bltSrvAttrs);
        ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Info,
                "set fieds in the service over .Startup albianJ is success!");
    }



    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#unload()
     */
    @Override
    public void unload()  {
        Set<String> keys = ServiceContainer.getAllServiceNames();
        for (String key : keys) {
            try {
                IAlbianService service = ServiceContainer.getService(key);
                service.beforeUnload();
                service.unload();
                service.afterUnload();
            } catch (Throwable e) {
                ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Info,e,
                        "ubload albianj framework is fail!");
            }
        }
    }
}
