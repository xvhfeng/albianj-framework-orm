package org.albianj.api.dal.service;

import org.albianj.api.dal.object.AblEntityAttr;
import org.albianj.api.dal.object.AblEntityFieldAttr;
import org.albianj.common.mybp.LambdaUtils;
import org.albianj.common.mybp.support.LambdaMeta;
import org.albianj.common.mybp.support.SFunction;
import org.albianj.common.utils.StringsUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class AlbianEntityMetadata {
    private static Map<String, Object> entityMetadata = new HashMap<>();
    private static Map<String, AblEntityFieldAttr> getterLinkFieldAttrMetadata = new HashMap<>();

    public static AblEntityAttr getEntityMetadata(String implClzzName) {
        return (AblEntityAttr) entityMetadata.get(implClzzName);
    }

    public static AblEntityAttr getEntityMetadata(Class<?> implClzz) {
        return (AblEntityAttr) entityMetadata.get(implClzz.getName());
    }

    public static boolean exist(String implClzzName) {
        return entityMetadata.containsKey(implClzzName);
    }

    public static boolean exist(Class<?> implClzz) {
        return entityMetadata.containsKey(implClzz.getName());
    }

    public static void put(String implClzzName, AblEntityAttr attr) {
        entityMetadata.put(implClzzName, attr);
    }

    public static void put(Class<?> implClzz, AblEntityAttr attr) {
        put(implClzz.getName(), attr);
    }

    public static void putAll(Map<String, Object> map) {
        //can not use putAll
        for (Object entry : map.values()) {
            AblEntityAttr objAttr = (AblEntityAttr) entry;
            put(objAttr.getItf(), objAttr);
        }
    }

    public static String makeFieldsKey(String propertyName) {
        return propertyName.toLowerCase();
    }

    private  static String buildGetterFullName(Class<?> clzz,String getterName) {
        return StringsUtil.nonIdxFmt("{}.{}",clzz.getName(),getterName);
    }

    public static <T,R> AblEntityFieldAttr findFieldAttrByGetter(SFunction<T,R> getter){
        LambdaMeta meta = LambdaUtils.extract(getter);
        String getterFullName = buildGetterFullName(meta.getInstantiatedClass(),meta.getImplMethodName());
        return getterLinkFieldAttrMetadata.get(getterFullName);
    }

    public static void putGetterLinkFieldAttr(Class<?> clzz,AblEntityFieldAttr fieldAttr){
        String getterFullName = buildGetterFullName(clzz,fieldAttr.getPropertyGetter().getName());
        getterLinkFieldAttrMetadata.put(getterFullName,fieldAttr);
    }

    public static <T,R> String getFieldNameByGetter(SFunction<T,R> getter) {
        AblEntityFieldAttr fieldAttr = findFieldAttrByGetter(getter);
        if(null == fieldAttr) return null;
        return fieldAttr.getName();
    }
}
