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
package org.albianj.loader;

import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AblApp {
    private static final String AlbianStarter = "org.albianj.impl.kernel.core.AlbianTransmitterService";

    private static String lookupLoggerConfigFile(String configPath){
        String[] filenames = {
                "log4j2.properties",
                "log4j2.xml",
                "log4j2.yaml",
        };

        for (String filename : filenames) {
            String logConfigFile = null;
            if (configPath.endsWith(File.separator)) {
                logConfigFile = configPath + filename;
            } else {
                logConfigFile = configPath + File.separator + filename;
            }
            File f =  new File(logConfigFile);
            if(f.exists()){
                return f.getAbsolutePath();
            }
        }
        return null;
    }

    public static boolean run(Class<?> mainClass,String configurtionFolder) {

        String cfPath = configurtionFolder;
        if(null == configurtionFolder ||  configurtionFolder.trim().isEmpty()) {
            cfPath =  mainClass.getClassLoader().getResource(".").getPath();
        }

        String cfFileName = lookupLoggerConfigFile(cfPath);
        LoggerContext context = null;
        if (null != cfFileName) {
            try {
//                System.setProperty("log4j2.configurationFile", cfFileName);
//                Configurator.initialize(null, cfFileName);

                File file = new File(cfFileName);

                 context = (LoggerContext) LogManager.getContext(false);
                context.setConfigLocation(file.toURI());

            }catch (Throwable t){
//                logger.error("set configurationFile fail.",t);
                throw new RuntimeException(t);
            }
        }

        Logger logger = LoggerFactory.getLogger(AblApp.class);

        logger.info("The sun rises in the east and the lighthouse is no more!");
        logger.info(AlbianAsciiArt.Images);
        logger.info("Albianj startup,main class:" + mainClass.getName() + ",configurtion folder: " + cfPath);
        logger.info("Albianj found log4j configuration file:" + null == cfFileName || cfFileName.isEmpty() ? "EMPTY": cfFileName);

        try {
            logger.info("load albianj start class:{}",AlbianStarter);
            Class<?> clss = AlbianClassLoader.getInstance().loadClass(AlbianStarter);
            IAlbianTransmitterService abs = (IAlbianTransmitterService) clss.newInstance();
            abs.start(mainClass,cfPath);
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            logger.error("AlbianBootService start is error ",e);
            return false;
        }
        return true;
    }

    public static void daemonRun(Class<?> mainClass,String configPath,String...paras) {
        if(!IAblCommandLine.class.isAssignableFrom(mainClass)) {
            throw new RuntimeException(mainClass.getName() + " must assignable from IAlbianCommandLine ");
        }
        run(mainClass,configPath);
        try {
            IAblCommandLine cmdFunc = (IAblCommandLine) mainClass.getConstructor().newInstance();
            cmdFunc.run(paras);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}