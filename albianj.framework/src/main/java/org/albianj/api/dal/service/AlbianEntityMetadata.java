package org.albianj.api.dal.service;

import org.albianj.api.dal.object.AlbianObjectAttribute;

import java.util.HashMap;
import java.util.Map;

public final class AlbianEntityMetadata {
    private static Map<String, Object> entityMetadata = new HashMap<>();
//    private static Map<String, String> type2itf = new HashMap<>();

    public static AlbianObjectAttribute getEntityMetadata(String implClzzName) {
        return (AlbianObjectAttribute) entityMetadata.get(implClzzName);
    }

    public static AlbianObjectAttribute getEntityMetadata(Class<?> implClzz) {
        return (AlbianObjectAttribute) entityMetadata.get(implClzz.getName());
    }

    public static boolean exist(String implClzzName) {
        return entityMetadata.containsKey(implClzzName);
    }

    public static boolean exist(Class<?> implClzz) {
        return entityMetadata.containsKey(implClzz.getName());
    }

    public static void put(String implClzzName, AlbianObjectAttribute attr) {
//        type2itf.put(attr.getType(), itf);
        entityMetadata.put(implClzzName, attr);
    }

    public static void put(Class<?> implClzz, AlbianObjectAttribute attr) {
        put(implClzz.getName(), attr);
    }

    public static void putAll(Map<String, Object> map) {
        //can not use putAll
        for (Object entry : map.values()) {
            AlbianObjectAttribute objAttr = (AlbianObjectAttribute) entry;
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
