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
package org.albianj.orm.impl.mapping;

import org.albianj.common.utils.CheckUtil;
import org.albianj.common.utils.ReflectUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.common.utils.XmlUtil;
import org.albianj.AlbianRuntimeException;
import org.albianj.kernel.kit.builtin.logger.LogLevel;
import org.albianj.kernel.kit.builtin.logger.LogTarget;
import org.albianj.kernel.anno.AlbianServRant;
import org.albianj.kernel.kit.service.AlbianServiceRouter;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.orm.attr.AlbianEntityFieldAttribute;
import org.albianj.orm.attr.AlbianObjectAttribute;
import org.albianj.orm.attr.DataRouterAttribute;
import org.albianj.orm.attr.MemberAttribute;
import org.albianj.orm.utils.AlbianEntityRantScaner;
import org.albianj.orm.impl.router.AlbianDataRouterParserService;
import org.albianj.orm.impl.storage.AlbianStorageParserService;
import org.albianj.orm.utils.Convert;
import org.albianj.orm.attr.AlbianObjectMemberAttribute;
import org.albianj.orm.kit.object.IAlbianObject;
import org.albianj.orm.bks.AlbianEntityMetadata;
import org.albianj.orm.kit.service.IAlbianMappingParserService;
import org.dom4j.Element;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AlbianServRant(Id = IAlbianMappingParserService.Name, Interface = IAlbianMappingParserService.class)
public class AlbianMappingParserService extends FreeAlbianMappingParserService {

    private static final String cacheTagName = "Cache";
    private static final String memberTagName = "Members/Member";

    private static void parserEntityFields(String type, @SuppressWarnings("rawtypes") List nodes,
                                           Map<String, AlbianEntityFieldAttribute> map)   {
        for (Object node : nodes) {
            parserEntityField(type, (Element) node, map);
        }
    }

//    private static ICacheAttribute parserAlbianObjectCache(Node node) {
//        String enable = XmlParser.getAttributeValue(node, "Enable");
//        String lifeTime = XmlParser.getAttributeValue(node, "LifeTime");
//        String name = XmlParser.getAttributeValue(node, "Name");
//        ICacheAttribute cache = new CacheAttribute();
//        cache.setEnable(Validate.isNullOrEmptyOrAllSpace(enable) ? true : new Boolean(enable));
//        cache.setLifeTime(Validate.isNullOrEmptyOrAllSpace(lifeTime) ? 300 : new Integer(lifeTime));
//        cache.setName(Validate.isNullOrEmptyOrAllSpace(name) ? "Default" : name);
//        return cache;
//    }

    private static void parserEntityField(String type, Element elt, Map<String, AlbianEntityFieldAttribute> map)
             {
        String name = XmlUtil.getAttributeValue(elt, "Name");
        if (CheckUtil.isNullOrEmpty(name)) {
            throw new AlbianRuntimeException(
                "the persisten node name is null or empty.type:" + type + ",node xml:" + elt.asXML());
        }
                 AlbianEntityFieldAttribute fieldAttr = map.get(name.toLowerCase());
//        IMemberAttribute member = (IMemberAttribute) map.get(name.toLowerCase());
        if (null == fieldAttr) {
            throw new AlbianRuntimeException("the field: " + name + "is not found in the :" + type);
        }

        String fieldName = XmlUtil.getAttributeValue(elt, "FieldName");
        String allowNull = XmlUtil.getAttributeValue(elt, "AllowNull");
        String length = XmlUtil.getAttributeValue(elt, "Length");
        String primaryKey = XmlUtil.getAttributeValue(elt, "PrimaryKey");
        String dbType = XmlUtil.getAttributeValue(elt, "DbType");
        String isSave = XmlUtil.getAttributeValue(elt, "IsSave");

        if (!CheckUtil.isNullOrEmpty(fieldName)) {
            fieldAttr.setSqlFieldName(fieldName);
        }
        if (!CheckUtil.isNullOrEmpty(allowNull)) {
            fieldAttr.setAllowNull( Boolean.parseBoolean(allowNull));
        }
        if (!CheckUtil.isNullOrEmpty(length)) {
            fieldAttr.setLength( Integer.parseInt(length));
        }
        if (!CheckUtil.isNullOrEmpty(primaryKey)) {
            fieldAttr.setPrimaryKey( Boolean.parseBoolean(primaryKey));
        }
        if (!CheckUtil.isNullOrEmpty(dbType)) {
            fieldAttr.setDatabaseType(Convert.toSqlType(dbType));
        }
        if (!CheckUtil.isNullOrEmpty(isSave)) {
            fieldAttr.setSave( Boolean.parseBoolean(isSave));
        }
//        if(Validate.isNullOrEmptyOrAllSpace(varField)){
//            member.setVarField(StringHelper.lowercasingFirstLetter(name));
//        } else {
//            member.setVarField(varField);
//        }
    }

    private static void parserAlbianObjectMembers(String type, @SuppressWarnings("rawtypes") List nodes,
                                                  Map<String, MemberAttribute> map)   {
        for (Object node : nodes) {
            parserAlbianObjectMember(type, (Element) node, map);
        }
    }

    private static void parserAlbianObjectMember(String type, Element elt, Map<String, MemberAttribute> map)
              {
        String name = XmlUtil.getAttributeValue(elt, "Name");
        if (CheckUtil.isNullOrEmpty(name)) {
            throw new AlbianRuntimeException(
                "the persisten node name is null or empty.type:" + type + ",node xml:" + elt.asXML() + ".");
        }
                  MemberAttribute member = (MemberAttribute) map.get(name.toLowerCase());
        if (null == member) {
            throw new AlbianRuntimeException("the field:" + name + " is not found in the " + type + ".");
        }

        String fieldName = XmlUtil.getAttributeValue(elt, "FieldName");
        String allowNull = XmlUtil.getAttributeValue(elt, "AllowNull");
        String length = XmlUtil.getAttributeValue(elt, "Length");
        String primaryKey = XmlUtil.getAttributeValue(elt, "PrimaryKey");
        String dbType = XmlUtil.getAttributeValue(elt, "DbType");
        String isSave = XmlUtil.getAttributeValue(elt, "IsSave");
        String varField = XmlUtil.getAttributeValue(elt, "VarField");
        String autoGenKey = XmlUtil.getAttributeValue(elt, "AutoGenKey");
        if (!CheckUtil.isNullOrEmpty(fieldName)) {
            member.setSqlFieldName(fieldName);
        }
        if (!CheckUtil.isNullOrEmpty(allowNull)) {
            member.setAllowNull(Boolean.parseBoolean(allowNull));
        }
        if (!CheckUtil.isNullOrEmpty(length)) {
            member.setLength( Integer.parseInt(length));
        }
        if (!CheckUtil.isNullOrEmpty(primaryKey)) {
            member.setPrimaryKey( Boolean.parseBoolean(primaryKey));
        }
        if (!CheckUtil.isNullOrEmpty(dbType)) {
            member.setDatabaseType(Convert.toSqlType(dbType));
        }
        if (!CheckUtil.isNullOrEmpty(isSave)) {
            member.setSave( Boolean.parseBoolean(isSave));
        }
        if (CheckUtil.isNullOrEmptyOrAllSpace(varField)) {
            member.setVarField(StringsUtil.lowercasingFirstLetter(name));
        } else {
            member.setVarField(varField);
        }
        if (!CheckUtil.isNullOrEmptyOrAllSpace(autoGenKey)) {
            member.setAutoGenKey( Boolean.parseBoolean(autoGenKey));
        }
    }

    private static AlbianEntityFieldAttribute reflexAlbianObjectMember(String type, PropertyDescriptor propertyDescriptor) {
        Method mr = propertyDescriptor.getReadMethod();
        Method mw = propertyDescriptor.getWriteMethod();
        if (null == mr || null == mw) {
//            logger.error("property::{} of type::{} is not exist readerMethod or write Method.",
//                propertyDescriptor.getName(), type);
            AlbianServiceRouter.log(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Warn,"property::{} of type::{} is not exist readerMethod or write Method.",
                    propertyDescriptor.getName(), type
                    );
            return null;
        }
        AlbianObjectMemberAttribute attr = null;
        if (mr.isAnnotationPresent(AlbianObjectMemberAttribute.class))
            attr = mr.getAnnotation(AlbianObjectMemberAttribute.class);
        if (mw.isAnnotationPresent(AlbianObjectMemberAttribute.class))
            attr = mw.getAnnotation(AlbianObjectMemberAttribute.class);

        if (attr.Ignore()) return null;

        AlbianEntityFieldAttribute member = new AlbianEntityFieldAttribute();
        if (null != attr) {
            member.setName(propertyDescriptor.getName());

            if (CheckUtil.isNullOrEmptyOrAllSpace(attr.FieldName())) {
                member.setSqlFieldName(propertyDescriptor.getName());
            } else {
                member.setSqlFieldName(attr.FieldName());
            }
            member.setAllowNull(attr.IsAllowNull());
            if (0 == attr.DbType()) {
                member.setDatabaseType(Convert.toSqlType(propertyDescriptor.getPropertyType()));
            } else {
                member.setDatabaseType(attr.DbType());
            }
            member.setSave(attr.IsSave());
            member.setLength(attr.Length());
            member.setPrimaryKey(attr.IsPrimaryKey());
            return member;
        }

        if ("isAlbianNew".equals(propertyDescriptor.getName())) {
            member.setSave(false);
            member.setName(propertyDescriptor.getName());
            return member;
        }
        member.setAllowNull(true);
        member.setDatabaseType(Convert.toSqlType(propertyDescriptor.getPropertyType()));
        member.setSqlFieldName(propertyDescriptor.getName());
        member.setSave(true);
        member.setLength(-1);
        member.setPrimaryKey(false);
        member.setName(propertyDescriptor.getName());
        return member;
    }


    //unuseful

    public String getServiceName() {
        return Name;
    }

    @Override
    protected void parserAlbianObjects(@SuppressWarnings("rawtypes") List nodes)  {
        if (CheckUtil.isNullOrEmpty(nodes)) {
            throw new IllegalArgumentException("nodes");
        }
        String inter = null;
        for (Object node : nodes) {
//            IAlbianObjectAttribute albianObjectAttribute = null;
            Element ele = (Element) node;
            try {
                parserAlbianObject(ele);
            } catch (Exception e) {
                AlbianServiceRouter.logAndThrowAgain(AlbianServiceRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e,
                "parser persisten node is fail,xml:{}" ,ele.asXML());
            }
//            if (null == albianObjectAttribute) {
//                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
//                        IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,null, AlbianModuleType.AlbianPersistence,
//                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
//                        "parser persisten node is fail,the node attribute is null,xml:%s", ele.asXML());
//            }
//            inter = albianObjectAttribute.getInterface();

//            addAlbianObjectAttribute(inter, albianObjectAttribute);
        }

    }

    protected void parserAlbianObject(Element node)  {
        String type = XmlUtil.getAttributeValue(node, "Type");
        if (CheckUtil.isNullOrEmptyOrAllSpace(type)) {
            throw new AlbianRuntimeException("The AlbianObject's type is empty in persistence.xml");
            //return;
        }

        String inter = XmlUtil.getAttributeValue(node, "Interface");
        if (CheckUtil.isNullOrEmptyOrAllSpace(inter)) {
            throw new AlbianRuntimeException("The AlbianObject's type->:" + type + " is empty in persistence.xml");
            //return;
        }

        AlbianObjectAttribute pkgEntityAttr = null;
        if (AlbianEntityMetadata.exist(inter)) {
            pkgEntityAttr = AlbianEntityMetadata.getEntityMetadata(inter);
            pkgEntityAttr.setType(type);
        } else {
            pkgEntityAttr = new AlbianObjectAttribute();
            pkgEntityAttr.setInterfaceName(inter);
            pkgEntityAttr.setType(type);
            AlbianEntityMetadata.put(inter, pkgEntityAttr);
        }

        Class<?> implClzz = null;
        try {
            implClzz = AlbianClassLoader.getInstance().loadClass(type);
            Class<?> itf = AlbianClassLoader.getInstance().loadClass(inter);
            if (!itf.isAssignableFrom(implClzz)) {
                throw new AlbianRuntimeException(
                    "the albian-object class:" + type + " is not implements from interface:" + inter + ".");
            }

            if (!IAlbianObject.class.isAssignableFrom(implClzz)) {
                throw new AlbianRuntimeException(
                    "the albian-object class:" + type + " is not implements from interface: IAlbianObject.");
            }

            if (!IAlbianObject.class.isAssignableFrom(itf)) {
                throw new AlbianRuntimeException(
                    "the albian-object interface:" + inter + " is not implements from interface: IAlbianObject.");
            }


        } catch (ClassNotFoundException e1) {
            AlbianServiceRouter.logAndThrowAgain(AlbianServiceRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e1,
                    "the type:{} is not found",type);
        }

        pkgEntityAttr.setImplClzz(implClzz);

        //        addAlbianObjectClassToInterface(type, inter);
        //        Map<String, IMemberAttribute> map = reflexAlbianObjectMembers(type);
        //        Node cachedNode = node.selectSingleNode(cacheTagName);
        //        ICacheAttribute cached;
        //        if (null == cachedNode) {
        //            cached = new CacheAttribute();
        //            cached.setEnable(false);
        //            cached.setLifeTime(300);
        //        } else {
        //            cached = parserAlbianObjectCache(cachedNode);
        //        }

        DataRouterAttribute defaultRouting = new DataRouterAttribute();
        defaultRouting.setName(AlbianDataRouterParserService.DEFAULT_ROUTING_NAME);
        defaultRouting.setOwner("dbo");
        defaultRouting.setStorageName(AlbianStorageParserService.DEFAULT_STORAGE_NAME);
        String csn = null;
        try {
            csn = ReflectUtil.getClassSimpleName(AlbianClassLoader.getInstance(), type);
        } catch (ClassNotFoundException e) {
            AlbianServiceRouter.logAndThrowAgain(AlbianServiceRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e,

                    "the type:{} is not found",type);
        }
        if (null != csn) {
            defaultRouting.setTableName(csn);
        }

//        IAlbianObjectAttribute albianObjectAttribute = new AlbianObjectAttribute();
//        Node tnode = node.selectSingleNode("Transaction");
//        if (null != tnode) {
//            String sCompensating = XmlParser.getAttributeValue(node, "Compensating");
//            if (!Validate.isNullOrEmptyOrAllSpace(sCompensating)) {
//                pkgEntityAttr.setCompensating(new Boolean(sCompensating));
//            }
//        }

        Map<String, AlbianEntityFieldAttribute> entityFieldAttr = null;
        if (CheckUtil.isNullOrEmpty(pkgEntityAttr.getFields())) {
            entityFieldAttr = AlbianEntityRantScaner.scanFields(implClzz);
            pkgEntityAttr.setFields(entityFieldAttr);
        } else {
            entityFieldAttr = pkgEntityAttr.getFields();
        }
        @SuppressWarnings("rawtypes")
        List nodes = node.selectNodes(memberTagName);
        if (!CheckUtil.isNullOrEmpty(nodes)) {
//            parserAlbianObjectMembers(type, nodes, map);
            parserEntityFields(type, nodes, entityFieldAttr);
        }

//        pkgEntityAttr.setCache(cached);
//        pkgEntityAttr.setMembers(map);
        pkgEntityAttr.setDefaultRouting(defaultRouting);
        return;
    }

    private Map<String, AlbianEntityFieldAttribute> reflexAlbianObjectMembers(String type)  {
        Map<String, AlbianEntityFieldAttribute> map = new LinkedHashMap<String, AlbianEntityFieldAttribute>();
        PropertyDescriptor[] propertyDesc = null;
        try {
            propertyDesc = ReflectUtil.getBeanPropertyDescriptor(AlbianClassLoader.getInstance(), type);
        } catch (Exception e) {
            AlbianServiceRouter.logAndThrowAgain(AlbianServiceRouter.__StartupSessionId,LogTarget.Running,LogLevel.Error,e,

                    "the type:{} is not found",type);
        }
        if (null == propertyDesc) {
            throw new AlbianRuntimeException("the type:" + type + " is not found");
        }
//        addAlbianObjectPropertyDescriptor(type, propertyDesc);
        for (PropertyDescriptor p : propertyDesc) {
            AlbianEntityFieldAttribute member = reflexAlbianObjectMember(type, p);
            if (null == member) {
                throw new AlbianRuntimeException(String.format("reflx albianobject:%s is fail.", type));
            }
            map.put(member.getVarField(), member);
        }
        return map;
    }

}
