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
package org.albianj.kernel.impl.core;

import org.albianj.kernel.AlbianRuntimeException;
import org.albianj.kernel.impl.service.FreeAlbianServiceParser;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.kernel.logger.LogTarget;
import org.albianj.kernel.service.*;
import org.albianj.loader.GlobalSettings;
import org.albianj.loader.IAlbianTransmitterService;

import java.util.*;

/**
 * @author Seapeak
 */
public class AlbianTransmitterService implements IAlbianTransmitterService {

    private static AlbianState state = AlbianState.Normal;
    private static Date startDateTime;
    private static String serialId;

    public String getServiceName() {
        return Name;
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#getStartDateTime()
     */
    @Override
    public Date getStartDateTime() {
        return startDateTime;
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#getSerialId()
     */
    @Override
    public String getSerialId() {
        return serialId;
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#getLifeState()
     */
    public AlbianState getLifeState() {
        return state;
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#start(java.lang.String)
     */
    @Override
    public void start(GlobalSettings settings)  {
        this.settings =  settings;
        lunch(this.settings);
    }

    private GlobalSettings settings;

//    @Override
//    public GlobalSettings getGlobalSettings() {
//        return this.settings;
//    }

    public void lunch(GlobalSettings settings)  {

        // first load logger
        // 必须开始第一件事情就是起logger service，以保证后续日志可以被记录
        AlbianBuiltinServiceLoader bltSevLoader = new AlbianBuiltinServiceLoader();
        bltSevLoader.loadLoggerService(settings);

        // 从这里以后，就可以正常使用log了，前面的logger自行处理，但是一般不需要任何的处理

        // do load builtin service
        bltSevLoader.loadServices(AlbianServiceRouter.__StartupSessionId,settings);
        Map<String, IAlbianServiceAttribute> bltSrvAttrs = bltSevLoader.getBltSrvAttrs();

        //do load bussiness service
        Map<String, IAlbianServiceAttribute> bnsSrvAttrs =
            (Map<String, IAlbianServiceAttribute>) ServiceAttributeMap.get(FreeAlbianServiceParser.ALBIANJSERVICEKEY);

        Map<String, IAlbianServiceAttribute> mapAttr = new HashMap<>();
        if (bnsSrvAttrs != null) {
            mapAttr.putAll(bnsSrvAttrs); // copy it for field setter
        }
        for (String bltServKey : bltSrvAttrs.keySet()) { // remove builtin service in service.xml
            if (mapAttr.containsKey(bltServKey)) {
                mapAttr.remove(bltServKey);
            }
        }

        Map<String, IAlbianServiceAttribute> failMap = new LinkedHashMap<String, IAlbianServiceAttribute>();
        int lastFailSize = 0;
        int currentFailSize = 0;
        Exception e = null;
        while (true) {
            lastFailSize = currentFailSize;
            currentFailSize = 0;
            String sType = null;
            String id = null;
            String sInterface = null;
            for (Map.Entry<String, IAlbianServiceAttribute> entry : mapAttr.entrySet())
                try {
                    IAlbianServiceAttribute serviceAttr = entry.getValue();
                    IAlbianService service = AlbianServiceLoader.makeupService(settings,serviceAttr, mapAttr);
                    ServiceContainer.addService(serviceAttr.getId(), service);
                } catch (Exception exc) {
                    e = exc;
                    currentFailSize++;
                    failMap.put(entry.getKey(), entry.getValue());
                    AlbianServiceRouter.logAndThrowAgain(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,exc,
                            "Kernel is error.load and init service:{} with class:{} is fail.",
                            id,sType);
                }
            if (0 == currentFailSize) {
                // if open the distributed mode,
                // please contact to manager machine to logout the system.
                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,
                        "load service is success,then set field in the services!");
                break;// load service successen
            }

            if (lastFailSize == currentFailSize) {
                // startup the service fail in this times,
                // so throw the exception and stop the albianj engine
                state = AlbianState.Unloading;
                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,
                        "startup slbianJ engine is fail ,maybe cross refernce");
                if (null != e) {
                    StringBuilder errBuilder = new StringBuilder();
                    for (Map.Entry<String, IAlbianServiceAttribute> entry : failMap.entrySet()) {
                        errBuilder.append(entry.getKey()).append(",");
                    }

                    AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,
                            "startup the service :{} is fail .", errBuilder.toString());
                }
                ServiceContainer.clear();
                state = AlbianState.Unloaded;
                throw new AlbianRuntimeException(e);
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
        //set field in service
        //        if (!setServiceFields(bltSrvAttrs)) {
        //            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService.AlbianRunningLoggerName,
        //                    IAlbianLoggerService2.InnerThreadName,
        //                    AlbianLoggerLevel.Error,
        //                    " set field in the services is fail!startup albianj is fail.");
        //            throw new AlbianRuntimeException("startup albianj is fail.");
        //
        //        }
        state = AlbianState.Running;
        AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,
                "set fieds in the service over .Startup albianJ is success!");
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#requestHandlerContext()
     */
    @Override
    public String requestHandlerContext() {
        if (AlbianState.Running != state) {
            return "Albian is not ready,Please wait a minute or contact administrators!";
        }
        return "";
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
                AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Info,e,
                        "ubload albianj framework is fail!");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#makeEnvironment()
     */
//    @Override
//    public void makeEnvironment() {
//        String system = System.getProperty("os.name");
//        if (system.toLowerCase().contains("windows"))// start with '/'
//        {
//            KernelSetting.setSystem(KernelSetting.Windows);
//        } else {
//            KernelSetting.setSystem(KernelSetting.Linux);
//        }
//    }

}
