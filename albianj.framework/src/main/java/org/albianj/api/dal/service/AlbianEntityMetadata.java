package org.albianj.api.dal.service;

import org.albianj.api.dal.object.AblEntityAttr;

import java.util.HashMap;
import java.util.Map;

public final class AlbianEntityMetadata {
    private static Map<String, Object> entityMetadata = new HashMap<>();
//    private static Map<String, String> type2itf = new HashMap<>();

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
//        type2itf.put(attr.getType(), itf);
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

//    public static AlbianObjectAttribute getEntityMetadataByType(String type) {
//        return (AlbianObjectAttribute) entityMetadata.get(type2Interface(type));
//    }
//
//    public static AlbianObjectAttribute getEntityMetadataByType(Class<?> implClzz) {
//        return getEntityMetadataByType(implClzz.getName());
//    }


    public static String makeFieldsKey(String propertyName) {
        return propertyName.toLowerCase();
    }

//    public static String type2Interface(String type) {
//        return type2itf.get(type);
//    }
}
