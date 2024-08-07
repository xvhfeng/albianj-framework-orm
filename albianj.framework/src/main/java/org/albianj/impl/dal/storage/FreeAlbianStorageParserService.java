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
package org.albianj.impl.dal.storage;

import org.albianj.AblThrowable;
import org.albianj.ServRouter;
import org.albianj.api.dal.object.StgTempAttr;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.common.utils.XmlUtil;
import org.albianj.api.dal.object.StgAttr;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.api.kernel.service.parser.FreeAlbianParserService;
import org.albianj.api.dal.object.DBOpt;
import org.albianj.api.dal.object.RStgAttr;
import org.albianj.api.dal.service.IAlbianStorageParserService;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FreeAlbianStorageParserService extends FreeAlbianParserService
    implements IAlbianStorageParserService {

    private final static String tagName = "Storages/Storage";
    private final static String templateTagName = "Storages/Templates/Template";
    private String file = "storage.xml";
    private HashMap<String, StgAttr> cached = null;

    public static String generateConnectionUrl(
            RStgAttr rsa) {
        if (null == rsa) {
            ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Warn,
                    "The argument storageAttribute is null.");
            return null;
        }

        StgAttr stgAttr = rsa.getStgAttr();
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:");
        // String url =
        // "jdbc:mysql://localhost/baseinfo?useUnicode=true&characterEncoding=8859_1";
        switch (stgAttr.getDatabaseStyle()) {
            case (DBOpt.Oracle): {
                sb.append("oracle:thin:@").append(stgAttr.getServer());
                if (0 != stgAttr.getPort()) {
                    sb.append(":").append(stgAttr.getPort());
                }
                sb.append(":").append(rsa.getDatabase());
                if(!StringsUtil.isNullEmptyTrimmed(rsa.getStgAttr().getUrlParaments())){
                    sb.append("?").append(rsa.getStgAttr().getUrlParaments());
                }
                break;
            }
            case (DBOpt.SqlServer): {
                sb.append("microsoft:sqlserver://").append(
                        stgAttr.getServer());
                if (0 != stgAttr.getPort()) {
                    sb.append(":").append(stgAttr.getPort());
                }
                sb.append(";").append(rsa.getDatabase());
                if(!StringsUtil.isNullEmptyTrimmed(rsa.getStgAttr().getUrlParaments())){
                    sb.append("?").append(rsa.getStgAttr().getUrlParaments());
                }
                break;
            }
            case (DBOpt.RedShift) : {
                sb.append("redshift://").append(
                        stgAttr.getServer());
                if (0 != stgAttr.getPort()) {
                    sb.append(":").append(stgAttr.getPort());
                }
                sb.append("/").append(rsa.getDatabase());
                if(!StringsUtil.isNullEmptyTrimmed(rsa.getStgAttr().getUrlParaments())){
                    sb.append("?").append(rsa.getStgAttr().getUrlParaments());
                }
                break;
            }
            case (DBOpt.MySql):
            default: {
                sb.append("mysql://").append(stgAttr.getServer());
                if (0 != stgAttr.getPort()) {
                    sb.append(":").append(stgAttr.getPort());
                }
                sb.append("/").append(rsa.getDatabase());
                sb.append("?useUnicode=true");
                if (null != stgAttr.getCharset()) {
                    sb.append("&characterEncoding=").append(
                            stgAttr.getCharset());
                }
                int timeout = rsa.getStgAttr().getTimeout();
                if (0 < timeout) {
                    sb.append("&connectTimeout=").append(timeout * 1000).append("&socketTimeout=").append(timeout * 1000);
                }
                sb.append("&autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull&maxReconnect=3&autoReconnectForPools=true&rewriteBatchedStatements=true&useSSL=true&serverTimezone=CTT");
                if(!StringsUtil.isNullEmptyTrimmed(rsa.getStgAttr().getUrlParaments())){
                    sb.append("&").append(rsa.getStgAttr().getUrlParaments());
                }
                break;
//                sb.append("&autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull");
            }
        }
        return sb.toString();
    }

    public void setConfigFileName(String fileName) {
        this.file = fileName;
    }

    @Override
    public void init()  {
        Document doc = null;
        cached = new HashMap<String, StgAttr>();
        try {
            parserFile(file);
        } catch (Throwable e) {
            ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,LogLevel.Error,e,
                    "loading the storage.xml is error." );
        }
        return;
    }

    private void parserFile(String filename)  {
        Document doc = null;
        cached = new HashMap<String, StgAttr>();
        try {
            String fname = findConfigFile(filename,true);
            doc = XmlUtil.load(fname);
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,LogLevel.Error,e,
                    "loading the storage.xml is error.");
        }
        if (null == doc) {
            throw new AblThrowable("loading the storage.xml is error.");
        }

        @SuppressWarnings("rawtypes")
        List nodes = XmlUtil.selectNodes(doc, "Storages/IncludeSet/Include");
        if (!SetUtil.isEmpty(nodes)) {
            for (Object node : nodes) {
                Element elt = XmlUtil.toElement(node);
                String path = XmlUtil.getAttributeValue(elt, "Filename");
                if (StringsUtil.isNullEmptyTrimmed(path)) continue;
                parserFile(path);
            }
        }

        Map<String,StgTempAttr> maps = new HashMap<>();
        List tpltNodes = XmlUtil.selectNodes(doc,templateTagName);
        if(!SetUtil.isEmpty(tpltNodes)){
            for(Object ele : tpltNodes){
                StgTempAttr stgTempAttr =  parserStgTemp((Element) ele);
                maps.putIfAbsent(stgTempAttr.getName(), stgTempAttr);
            }
        }

        @SuppressWarnings("rawtypes")
        List objNodes = XmlUtil.selectNodes(doc, tagName);
        if (SetUtil.isEmpty(objNodes)) {
            throw new AblThrowable("parser the node tags:" + tagName
                + " in the storage.xml is error. the node of the tags is null or empty.");
        }
        parserStorages(objNodes,maps);
        return;
    }

    protected abstract void parserStorages(
            @SuppressWarnings("rawtypes") List nodes, Map<String,StgTempAttr> maps);

    protected abstract StgAttr parserStorage(Element node, Map<String,StgTempAttr> maps);

    protected abstract StgTempAttr parserStgTemp(Element node);

    public void addStorageAttribute(String name, StgAttr sa) {
        cached.put(name, sa);
    }

    public StgAttr getStorageAttribute(String name) {
        return cached.get(name);
    }
}
