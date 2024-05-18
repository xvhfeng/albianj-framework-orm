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

import org.albianj.AblBltinServsNames;
import org.albianj.ServRouter;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.common.utils.XmlUtil;
import org.albianj.api.kernel.attr.AlbianServiceAopAttribute;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.api.kernel.attr.AlbianServiceAttribute;
import org.albianj.api.kernel.attr.AlbianServiceFieldAttribute;
import org.albianj.api.kernel.anno.serv.AblServRant;
import org.albianj.api.kernel.service.parser.IAlbianParserService;
import org.albianj.loader.AlbianClassLoader;
import org.dom4j.Element;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AblServRant(Id = AblBltinServsNames.AlbianServiceParserName, Interface = IAlbianParserService.class)
public class AlbianServiceParser extends FreeAlbianServiceParser {

    private final static String ID_ATTRBUITE_NAME = "Id";
    private final static String TYPE_ATTRBUITE_NAME = "Type";

    public String getServiceName() {
        return AblBltinServsNames.AlbianServiceParserName;
    }

    @Override
    protected void parserServices(Map<String, AlbianServiceAttribute> map,
                                  String tagName, @SuppressWarnings("rawtypes") List nodes)  {
        if (SetUtil.isNullOrEmpty(nodes)) {
            ServRouter.logAndThrowNew(ServRouter.__StartupSessionId,  LogLevel.Error,
                    "parser the nodes named {} for service is null or empty.",tagName);
        }
        String name = null;
        for (Object node : nodes) {
            Element elt = XmlUtil.toElement(node);
            name = null == name ? "tagName" : name;
            AlbianServiceAttribute serviceAttr = parserService(name, elt);
            if (null == serviceAttr) {
                ServRouter.logAndThrowNew(ServRouter.__StartupSessionId,LogLevel.Error,
                        "Tags {} as xml {} not lookup service.",tagName,elt.asXML());
            }
            name = serviceAttr.getId();
            map.put(name, serviceAttr);
        }
        return;
    }

    @Override
    protected AlbianServiceAttribute parserService(String name, Element elt)  {
        if (null == elt) {
            ServRouter.logAndThrowNew(ServRouter.__StartupSessionId,LogLevel.Error,
                    "Parser service {} fail.",name);
        }
        AlbianServiceAttribute serviceAttr = new AlbianServiceAttribute();
        String id = XmlUtil.getAttributeValue(elt, ID_ATTRBUITE_NAME);
        String sitf = XmlUtil.getAttributeValue(elt, "Interface");
        if (!StringsUtil.isNullOrEmptyOrAllSpace(sitf)) {
            serviceAttr.setItf(sitf);
        }
        if(StringsUtil.isNullOrEmptyOrAllSpace(id) && StringsUtil.isNullOrEmptyOrAllSpace(sitf)) {
            ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Warn,
                    "parser service node id and interface are bose null or empty ,the node next id:{}", name);
            return null;
        }

        serviceAttr.setId(StringsUtil.isNullOrEmptyOrAllSpace(id) ? sitf : id);
        String type = XmlUtil.getAttributeValue(elt, TYPE_ATTRBUITE_NAME);
        if (StringsUtil.isNullOrEmptyOrAllSpace(type)) {
            ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Warn,
                    "The type for service :{} is null or empty .", serviceAttr.getId());
            return null;
        }
        serviceAttr.setType(type);

        Class clzz = null;
        try {
            clzz = AlbianClassLoader.getInstance().loadClass(type);

        } catch (Exception e) {
            ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,LogLevel.Error,e,
                    "service {} by type {} is not loaded.",id,type);
        }

        serviceAttr.setServiceClass(clzz);

        String enable = XmlUtil.getAttributeValue(elt, "Enable");
        if (!StringsUtil.isNullOrEmptyOrAllSpace(enable)) {
            serviceAttr.setEnable(Boolean.parseBoolean(enable));
        }

        List nodes = elt.selectNodes("Properties/Property");
        if (!SetUtil.isNullOrEmpty(nodes)) {
            Map<String, AlbianServiceFieldAttribute> ps = parserAlbianServiceFieldsAttribute(clzz, id, nodes);
            if (!SetUtil.isNullOrEmpty(ps)) {
                serviceAttr.setServiceFields(ps);
            }

        }

        List aopNodes = elt.selectNodes("Aop/Aspect");
        if (!SetUtil.isNullOrEmpty(aopNodes)) {
            Map<String, AlbianServiceAopAttribute> aas = parserAlbianServiceAopAttribute(id, aopNodes);
            if (!SetUtil.isNullOrEmpty(aas)) {
                serviceAttr.setAopAttributes(aas);
            }

        }

        return serviceAttr;
    }

    protected Map<String, AlbianServiceFieldAttribute> parserAlbianServiceFieldsAttribute(Class<?> clzz, String id, List nodes)  {
        Map<String, AlbianServiceFieldAttribute> pas = new HashMap<>();
        for (Object node : nodes) {
            AlbianServiceFieldAttribute pa = parserAlbianServiceFieldAttribute(clzz, id, (Element) node);
            pas.put(pa.getName(), pa);
        }
        return pas;
    }

    protected AlbianServiceFieldAttribute parserAlbianServiceFieldAttribute(Class<?> clzz, String id, Element e)  {
        String name = XmlUtil.getAttributeValue(e, "Name");
        AlbianServiceFieldAttribute pa = new AlbianServiceFieldAttribute();
        if (StringsUtil.isNullOrEmptyOrAllSpace(name)) {
            ServRouter.logAndThrowNew(ServRouter.__StartupSessionId,LogLevel.Error,
                    " name of service {} is null or empty.",id);

        }
        pa.setName(name);
        String type = XmlUtil.getAttributeValue(e, "Type");
        if (StringsUtil.isNullOrEmptyOrAllSpace(type)) {
            ServRouter.logAndThrowNew(ServRouter.__StartupSessionId,LogLevel.Error,
                    " type of service {} is null or empty.",id);
        }
        pa.setType(type);
        String value = XmlUtil.getAttributeValue(e, "Value");
        if (!StringsUtil.isNullOrEmptyOrAllSpace(name)) {
            pa.setValue(value);
        }

        String allowNull = XmlUtil.getAttributeValue(e, "AllowNull");
        if (!StringsUtil.isNullOrEmptyOrAllSpace(allowNull)) {
            pa.setAllowNull(Boolean.parseBoolean(allowNull));
        }

        try {
            Field f = clzz.getDeclaredField(name);
            f.setAccessible(true);
            pa.setField(f);
        } catch (NoSuchFieldException exc) {
            ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,LogLevel.Error,exc,
                    " Field {} of service {} is not exist.",name,id);
        }

        return pa;

    }

    protected Map<String, AlbianServiceAopAttribute> parserAlbianServiceAopAttribute(String id, List nodes)  {
        Map<String, AlbianServiceAopAttribute> aas = new HashMap<>();
        for (Object node : nodes) {
            AlbianServiceAopAttribute pa = parserAlbianServiceAopAttribute(id, (Element) node);
            aas.put(pa.getProxyName(), pa);
        }
        return aas;
    }

    protected AlbianServiceAopAttribute parserAlbianServiceAopAttribute(String id, Element e)  {


        AlbianServiceAopAttribute aa = new AlbianServiceAopAttribute();
        String beginWith = XmlUtil.getAttributeValue(e, "BeginWith");
        if (!StringsUtil.isNullOrEmptyOrAllSpace(beginWith)) {
            aa.setBeginWith(beginWith);
        }

        String notBeginWith = XmlUtil.getAttributeValue(e, "NotBeginWith");
        if (!StringsUtil.isNullOrEmptyOrAllSpace(notBeginWith)) {
            aa.setNotBeginWith(notBeginWith);
        }

        String endWith = XmlUtil.getAttributeValue(e, "EndWith");
        if (!StringsUtil.isNullOrEmptyOrAllSpace(endWith)) {
            aa.setEndWith(endWith);
        }

        String notEndWith = XmlUtil.getAttributeValue(e, "NotEndWith");
        if (!StringsUtil.isNullOrEmptyOrAllSpace(notEndWith)) {
            aa.setNotEndWith(notEndWith);
        }

        String contain = XmlUtil.getAttributeValue(e, "Contain");
        if (!StringsUtil.isNullOrEmptyOrAllSpace(contain)) {
            aa.setContain(contain);
        }

        String notContain = XmlUtil.getAttributeValue(e, "NotContain");
        if (!StringsUtil.isNullOrEmptyOrAllSpace(notContain)) {
            aa.setNotContain(notContain);
        }

        String fullname = XmlUtil.getAttributeValue(e, "FullName");
        if (!StringsUtil.isNullOrEmptyOrAllSpace(fullname)) {
            aa.setFullName(fullname);
        }

        String sIsAll = XmlUtil.getAttributeValue(e, "IsAll");
        if (!StringsUtil.isNullOrEmptyOrAllSpace(sIsAll)) {
            aa.setAll(Boolean.parseBoolean(sIsAll));
        }

        String proxy = XmlUtil.getAttributeValue(e, "Proxy");
        if (StringsUtil.isNullOrEmptyOrAllSpace(proxy)) {
            ServRouter.logAndThrowNew(ServRouter.__StartupSessionId,LogLevel.Error,
                    " Aop proxy of  service {} is null or empty.",id);
        }
        aa.setServiceName(proxy);


        String proxyName = XmlUtil.getAttributeValue(e, "ProxyName");
        if (StringsUtil.isNullOrEmptyOrAllSpace(proxyName)) {
            ServRouter.logAndThrowNew(ServRouter.__StartupSessionId,LogLevel.Error,
                    " Aop proxyName of  service {} is null or empty.",id);
        }
        aa.setProxyName(proxyName);


        return aa;

    }

}
