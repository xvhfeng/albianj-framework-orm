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
package org.albianj.impl.kernel.service;

import org.albianj.AblThrowable;
import org.albianj.ServRouter;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.common.utils.XmlUtil;
import org.albianj.api.kernel.attr.AlbianServiceAopAttribute;
import org.albianj.api.kernel.anno.proxy.AlbianProxyIgnoreRant;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.api.kernel.attr.AlbianServiceAttribute;
import org.albianj.api.kernel.attr.AlbianServiceFieldAttribute;
import org.albianj.api.kernel.attr.ServiceAttributeMap;
import org.albianj.api.kernel.service.parser.FreeAlbianParserService;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class FreeAlbianServiceParser extends FreeAlbianParserService {

    public final static String ALBIANJSERVICEKEY = "@$#&ALBIANJ_ALL_SERVICE&#$@";
    private final static String tagName = "Services/Service";
    private final static String pkgTagName = "Services/Packages/Package";
    private String file = "service.xml";

    @AlbianProxyIgnoreRant(ignore = true)
    public String getConfigFileName() {
        return file;
    }

    @AlbianProxyIgnoreRant(ignore = true)
    public void init()  {

        Map<String, AlbianServiceAttribute> map = new LinkedHashMap<>();
        try {
            String filename = findConfigFile(file);
            parserFile(map, filename);
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,  LogLevel.Error,e,
                    "loading the service.xml is error." );
        }

        if (0 == map.size()) {
            ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Warn,
                    "The albian services is empty." );
            return;
        }
        ServiceAttributeMap.insert(ALBIANJSERVICEKEY, map);
        return;
    }

    private void parserFile(Map<String, AlbianServiceAttribute> map, String filename)  {
        Document doc = null;
        try {
            String realFilename = findConfigFile(filename);
            //判断fname如果是空，则直接返回不需要进行加载操作。
            if(StringUtils.isBlank(realFilename)){
//                logger.error("loading the service.xml is error. service.xml is not exist");
                return;
            }
            doc = XmlUtil.load(realFilename);
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,  LogLevel.Error,e,
                    "loading the service.xml is error." );
        }
        if (null == doc) {
            throw new AblThrowable("loading the service.xml is error. the file is null.");
        }
        @SuppressWarnings("rawtypes")
        List nodes = XmlUtil.selectNodes(doc, "Services/IncludeSet/Include");
        if (!SetUtil.isNullOrEmpty(nodes)) {
            for (Object node : nodes) {
                Element elt = XmlUtil.toElement(node);
                String path = XmlUtil.getAttributeValue(elt, "Filename");
                if (StringsUtil.isNullOrEmptyOrAllSpace(path)) continue;
                parserFile(map, path);
            }
        }

        //parser pkg in service.xml
        HashMap<String, Object> pkgMetedataMap = new HashMap<>();
        List pkgNodes = XmlUtil.selectNodes(doc, pkgTagName);
        if (!SetUtil.isNullOrEmpty(pkgNodes)) {
            for (Object node : pkgNodes) {
                Element elt = XmlUtil.toElement(node);
                String enable = XmlUtil.getAttributeValue(elt, "Enable");
                String pkg = XmlUtil.getAttributeValue(elt, "Path");

                if (!StringsUtil.isNullOrEmptyOrAllSpace(enable)) {
                    boolean b = Boolean.parseBoolean(enable);
                    if (!b) {
//                        logger.warn("Path:{} in the Package enable is false,so not load it.",
//                            Validate.isNullOrEmptyOrAllSpace(pkg) ? "NoPath" : pkg);
                        ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Warn,
                                "Path:{} in the Package enable is false,so not load it.",
                                StringsUtil.isNullOrEmptyOrAllSpace(pkg) ? "NoPath" : pkg);

                        continue;// not load pkg
                    }
                }

                if (StringsUtil.isNullOrEmptyOrAllSpace(pkg)) {
                    throw new AblThrowable(
                        "loading the service.xml is error. 'Path' attribute in  Package config-item is null or empty.");
                } else {
                    try {
                        //notice:all pkgmap key is service's type,not service's id
                        //change it when merger
                        HashMap<String, Object> pkgMap = AlbianServiceRantParser.scanPackage(pkg);
                        if (null != pkgMap) {
                            pkgMetedataMap.putAll(pkgMap);//merger the metedata
                        }
                    } catch (Throwable e) {
                        ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,  LogLevel.Error,e,
                                "loading the service.xml is error. Path :{}s in Package is fail.",pkg );
                    }
                }
            }
        }

        Map<String, AlbianServiceAttribute> attrMap = new HashMap<>();
        List serviceNodes = XmlUtil.selectNodes(doc, "Services/Service");
        if (!SetUtil.isNullOrEmpty(serviceNodes)) {
            parserServices(attrMap, tagName, serviceNodes);
        }

        mergerServiceAttributes(map, attrMap, pkgMetedataMap);
        return;
    }

    /*
     * merger the map from annotation and map form service.xml
     * if config-item in annotation as the same as in service.xml then use the service.xml
     */
    private void mergerServiceAttributes(Map<String, AlbianServiceAttribute> totalMap,
                                         Map<String, AlbianServiceAttribute> attrMap,
                                         Map<String, Object> pkgMap) {
        if(null == attrMap || 0 == attrMap.size()) {
            return;
        }

        for (Map.Entry<String, AlbianServiceAttribute> entry : attrMap.entrySet()) {
            AlbianServiceAttribute asa = entry.getValue();
            AlbianServiceAttribute asaPkg = null;

            if(null != pkgMap) {
                if (pkgMap.containsKey(asa.getId())) {
                    asaPkg = (AlbianServiceAttribute) pkgMap.get(asa.getId());
                    pkgMap.remove(asa.getId());
                } else {
                    // because reuse，so key must type
                    // and change type to id when merger
                    if (pkgMap.containsKey(asa.getType())) {
                        asaPkg = (AlbianServiceAttribute) pkgMap.get(asa.getType());
                        pkgMap.remove(asa.getType());
                    }
                }
            }

            if (!asa.isEnable()) { // not load this service
                continue;
            }

            if (null == asaPkg) {
                totalMap.put(asa.getId(), asa);
                continue;
            }

            Map<String, AlbianServiceFieldAttribute> asaFieldAttr = asa.getServiceFields();
            Map<String, AlbianServiceFieldAttribute> pkgFieldAttr = asaPkg.getServiceFields();

            if (SetUtil.isNullOrEmpty(asaFieldAttr)) {
                asa.setServiceFields(pkgFieldAttr);
            } else {
                if (!SetUtil.isNullOrEmpty(pkgFieldAttr)) {
                    // merger field attribute
                    // base on service.xml and merger field from pkg
                    // if exist in service.xml not merger field from pkg
                    for (Map.Entry<String, AlbianServiceFieldAttribute> fe : pkgFieldAttr.entrySet()) {
                        if (!asaFieldAttr.containsKey(fe.getKey())) {
                            asaFieldAttr.put(fe.getKey(), fe.getValue());
                        }
                    }
                }
            }

            Map<String, AlbianServiceAopAttribute> asaAopAttr = asa.getAopAttributes();
            Map<String, AlbianServiceAopAttribute> pkgAopAttr = asaPkg.getAopAttributes();
            if (SetUtil.isNullOrEmpty(asaAopAttr)) {
                asa.setAopAttributes(pkgAopAttr);
            } else {
                if (!SetUtil.isNullOrEmpty(pkgAopAttr)) {
                    // merger field attribute
                    // base on service.xml and merger field from pkg
                    // if exist in service.xml not merger field from pkg
                    for (Map.Entry<String, AlbianServiceAopAttribute> fe : pkgAopAttr.entrySet()) {
                        if (!asaAopAttr.containsKey(fe.getKey())) {
                            asaAopAttr.put(fe.getKey(), fe.getValue());
                        }
                    }
                }
            }
        }

        if(null != pkgMap) {
            // add in pkg but not in attr
            // and change the key from type to id
            for (Object val : pkgMap.values()) {
                AlbianServiceAttribute asa = (AlbianServiceAttribute) val;
                totalMap.put(asa.getId(), asa);
            }
        }
    }

    protected abstract void parserServices(Map<String, AlbianServiceAttribute> map,
                                           String tagName,
                                           @SuppressWarnings("rawtypes") List nodes) ;

    protected abstract AlbianServiceAttribute parserService(String name, Element node);
}
