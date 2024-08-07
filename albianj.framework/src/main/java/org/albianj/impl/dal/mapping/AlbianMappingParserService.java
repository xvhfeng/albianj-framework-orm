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
package org.albianj.impl.dal.mapping;

import org.albianj.AblThrowable;
import org.albianj.ServRouter;
import org.albianj.api.dal.object.*;
import org.albianj.common.utils.ReflectUtil;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.common.utils.XmlUtil;
import org.albianj.impl.dal.rant.AlbianEntityRantScaner;
import org.albianj.impl.dal.routing.AlbianDataRouterParserService;
import org.albianj.impl.dal.storage.AlbianStorageParserService;
import org.albianj.impl.dal.toolkit.SqlTypeConv;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.api.kernel.anno.serv.AblServRant;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.api.dal.service.AlbianEntityMetadata;
import org.albianj.api.dal.service.IAlbianMappingParserService;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;

@AblServRant(Id = IAlbianMappingParserService.Name, Interface = IAlbianMappingParserService.class)
public class AlbianMappingParserService extends FreeAlbianMappingParserService {

    private static final String memberTagName = "Members/Member";

    private static void parserEntityFields(String type, @SuppressWarnings("rawtypes") List nodes,
                                           Map<String, AblEntityFieldAttr> map){
        for (Object node : nodes) {
            parserEntityField(type, (Element) node, map);
        }
    }

    private static void parserEntityField(String type, Element elt, Map<String, AblEntityFieldAttr> map) {
        String name = XmlUtil.getAttributeValue(elt, "Name");
        if (StringsUtil.isNullEmptyTrimmed(name)) {
            throw new AblThrowable(
                "the persisten node name is null or empty.type:" + type + ",node xml:" + elt.asXML());
        }
        AblEntityFieldAttr fieldAttr = map.get(name.toLowerCase());
        if (null == fieldAttr) {
            throw new AblThrowable("the field: " + name + "is not found in the :" + type);
        }

        String fieldName = XmlUtil.getAttributeValue(elt, "FieldName");
        String allowNull = XmlUtil.getAttributeValue(elt, "AllowNull");
        String length = XmlUtil.getAttributeValue(elt, "Length");
        String primaryKey = XmlUtil.getAttributeValue(elt, "PrimaryKey");
        String dbType = XmlUtil.getAttributeValue(elt, "DbType");
        String isSave = XmlUtil.getAttributeValue(elt, "IsSave");

        if (!StringsUtil.isNullEmptyTrimmed(fieldName)) {
            fieldAttr.setSqlFieldName(fieldName);
        }
        if (!StringsUtil.isNullEmptyTrimmed(allowNull)) {
            fieldAttr.setAllowNull( Boolean.parseBoolean(allowNull));
        }
        if (!StringsUtil.isNullEmptyTrimmed(length)) {
            fieldAttr.setLength( Integer.parseInt(length));
        }
        if (!StringsUtil.isNullEmptyTrimmed(primaryKey)) {
            fieldAttr.setPrimaryKey( Boolean.parseBoolean(primaryKey));
        }
        if (!StringsUtil.isNullEmptyTrimmed(dbType)) {
            fieldAttr.setDatabaseType(SqlTypeConv.toSqlType(dbType));
        }
        if (!StringsUtil.isNullEmptyTrimmed(isSave)) {
            fieldAttr.setSave( Boolean.parseBoolean(isSave));
        }
    }

    private static void parserAlbianObjectMember(String type, Element elt, Map<String, AblEntityFieldAttr> map) {
        String name = XmlUtil.getAttributeValue(elt, "Name");
        if (StringsUtil.isNullEmptyTrimmed(name)) {
            throw new AblThrowable(
                "the persisten node name is null or empty.type:" + type + ",node xml:" + elt.asXML() + ".");
        }
        AblEntityFieldAttr member = (AblEntityFieldAttr) map.get(name.toLowerCase());
        if (null == member) {
            throw new AblThrowable("the field:" + name + " is not found in the " + type + ".");
        }

        String fieldName = XmlUtil.getAttributeValue(elt, "FieldName");
        String allowNull = XmlUtil.getAttributeValue(elt, "AllowNull");
        String length = XmlUtil.getAttributeValue(elt, "Length");
        String primaryKey = XmlUtil.getAttributeValue(elt, "PrimaryKey");
        String dbType = XmlUtil.getAttributeValue(elt, "DbType");
        String isSave = XmlUtil.getAttributeValue(elt, "IsSave");
        String varField = XmlUtil.getAttributeValue(elt, "VarField");
        String autoGenKey = XmlUtil.getAttributeValue(elt, "AutoGenKey");
        if (!StringsUtil.isNullEmptyTrimmed(fieldName)) {
            member.setSqlFieldName(fieldName);
        }
        if (!StringsUtil.isNullEmptyTrimmed(allowNull)) {
            member.setAllowNull(Boolean.parseBoolean(allowNull));
        }
        if (!StringsUtil.isNullEmptyTrimmed(length)) {
            member.setLength( Integer.parseInt(length));
        }
        if (!StringsUtil.isNullEmptyTrimmed(primaryKey)) {
            member.setPrimaryKey( Boolean.parseBoolean(primaryKey));
        }
        if (!StringsUtil.isNullEmptyTrimmed(dbType)) {
            member.setDatabaseType(SqlTypeConv.toSqlType(dbType));
        }
        if (!StringsUtil.isNullEmptyTrimmed(isSave)) {
            member.setSave( Boolean.parseBoolean(isSave));
        }
        if (StringsUtil.isNullEmptyTrimmed(varField)) {
            member.setVarField(StringsUtil.lowercasingFirstLetter(name));
        } else {
            member.setVarField(varField);
        }
        if (!StringsUtil.isNullEmptyTrimmed(autoGenKey)) {
            member.setAutoGenKey( Boolean.parseBoolean(autoGenKey));
        }
    }




    public String getServiceName() {
        return Name;
    }

    @Override
    protected void parserAlbianObjects(@SuppressWarnings("rawtypes") List nodes)  {
        if (SetUtil.isEmpty(nodes)) {
            throw new IllegalArgumentException("nodes");
        }
        String inter = null;
        for (Object node : nodes) {
            Element ele = (Element) node;
            try {
                parserAlbianObject(ele);
            } catch (Exception e) {
                ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,LogLevel.Error,e,
                "parser persisten node is fail,xml:{}" ,ele.asXML());
            }
        }

    }

    protected void parserAlbianObject(Element node)  {
        String type = XmlUtil.getAttributeValue(node, "Type");
        if (StringsUtil.isNullEmptyTrimmed(type)) {
            throw new AblThrowable("The AlbianObject's type is empty in persistence.xml");
        }

        String inter = XmlUtil.getAttributeValue(node, "Interface");


        AblEntityAttr pkgEntityAttr = null;
        if (AlbianEntityMetadata.exist(type)) {
            pkgEntityAttr = AlbianEntityMetadata.getEntityMetadata(type);
            pkgEntityAttr.setType(type);
        } else {
            pkgEntityAttr = new AblEntityAttr();
            pkgEntityAttr.setItf(inter);
            pkgEntityAttr.setType(type);
            AlbianEntityMetadata.put(type, pkgEntityAttr);
        }

        Class<?> implClzz = null;
        try {
            implClzz = AlbianClassLoader.getInstance().loadClass(type);

            if (!IAblObj.class.isAssignableFrom(implClzz)) {
                throw new AblThrowable(
                    "the albian-object class:" + type + " is not implements from interface: IAlbianObject.");
            }
        } catch (ClassNotFoundException e1) {
            ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,LogLevel.Error,e1,
                    "the type:{} is not found",type);
        }

        pkgEntityAttr.setImplClzz(implClzz);

        DrAttr defaultRouting = new DrAttr();
        defaultRouting.setName(AlbianDataRouterParserService.DEFAULT_ROUTING_NAME);
        defaultRouting.setOwner("dbo");
        defaultRouting.setStorageName(AlbianStorageParserService.DEFAULT_STORAGE_NAME);
        String csn = null;
        try {
            csn = ReflectUtil.getClassSimpleName(AlbianClassLoader.getInstance(), type);
        } catch (ClassNotFoundException e) {
            ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,LogLevel.Error,e,

                    "the type:{} is not found",type);
        }
        if (null != csn) {
            defaultRouting.setTableName(csn);
        }

        Map<String, AblEntityFieldAttr> entityFieldAttr = null;
        if (SetUtil.isEmpty(pkgEntityAttr.getFields())) {
            entityFieldAttr = AlbianEntityRantScaner.scanFields(implClzz);
            pkgEntityAttr.setFields(entityFieldAttr);
        } else {
            entityFieldAttr = pkgEntityAttr.getFields();
        }
        @SuppressWarnings("rawtypes")
        List nodes = node.selectNodes(memberTagName);
        if (!SetUtil.isEmpty(nodes)) {
            parserEntityFields(type, nodes, entityFieldAttr);
        }

        pkgEntityAttr.setDefaultRouting(defaultRouting);
        return;
    }

    //    private static AblEntityFieldAttr reflexAlbianObjectMember(String type, PropertyDescriptor propertyDescriptor) {
//        Method mr = propertyDescriptor.getReadMethod();
//        Method mw = propertyDescriptor.getWriteMethod();
//        if (null == mr || null == mw) {
//            ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Warn,"property::{} of type::{} is not exist readerMethod or write Method.",
//                    propertyDescriptor.getName(), type
//                    );
//            return null;
//        }
//        AlbianObjectMemberRant attr = null;
//        if (mr.isAnnotationPresent(AlbianObjectMemberRant.class))
//            attr = mr.getAnnotation(AlbianObjectMemberRant.class);
//        if (mw.isAnnotationPresent(AlbianObjectMemberRant.class))
//            attr = mw.getAnnotation(AlbianObjectMemberRant.class);
//
//        if (attr.Ignore()) return null;

//        AblEntityFieldAttr member = new AblEntityFieldAttr();
//        if (null != attr) {
//            member.setName(propertyDescriptor.getName());
//
//            if (StringsUtil.isNullEmptyTrimmed(attr.FieldName())) {
//                member.setSqlFieldName(propertyDescriptor.getName());
//            } else {
//                member.setSqlFieldName(attr.FieldName());
//            }
//            member.setAllowNull(attr.IsAllowNull());
//            if (0 == attr.DbType()) {
//                member.setDatabaseType(SqlTypeConv.toSqlType(propertyDescriptor.getPropertyType()));
//            } else {
//                member.setDatabaseType(attr.DbType());
//            }
//            member.setSave(attr.IsSave());
//            member.setLength(attr.Length());
//            member.setPrimaryKey(attr.IsPrimaryKey());
//            return member;
//        }
//
//        if ("isAlbianNew".equals(propertyDescriptor.getName())) {
//            member.setSave(false);
//            member.setName(propertyDescriptor.getName());
//            return member;
//        }
//        member.setAllowNull(true);
//        member.setDatabaseType(SqlTypeConv.toSqlType(propertyDescriptor.getPropertyType()));
//        member.setSqlFieldName(propertyDescriptor.getName());
//        member.setSave(true);
//        member.setLength(-1);
//        member.setPrimaryKey(false);
//        member.setName(propertyDescriptor.getName());
//        return member;
//    }

//    private Map<String, AblEntityFieldAttr> reflexAlbianObjectMembers(String type)  {
//        Map<String, AblEntityFieldAttr> map = new LinkedHashMap<String, AblEntityFieldAttr>();
//        PropertyDescriptor[] propertyDesc = null;
//        try {
//            propertyDesc = ReflectUtil.getBeanPropertyDescriptor(AlbianClassLoader.getInstance(), type);
//        } catch (Exception e) {
//            ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,LogLevel.Error,e,
//
//                    "the type:{} is not found",type);
//        }
//        if (null == propertyDesc) {
//            throw new AblThrowable("the type:" + type + " is not found");
//        }
//        for (PropertyDescriptor p : propertyDesc) {
//            AblEntityFieldAttr member = reflexAlbianObjectMember(type, p);
//            if (null == member) {
//                throw new AblThrowable(String.format("reflx albianobject:%s is fail.", type));
//            }
//            map.put(member.getVarField(), member);
//        }
//        return map;
//    }

}
