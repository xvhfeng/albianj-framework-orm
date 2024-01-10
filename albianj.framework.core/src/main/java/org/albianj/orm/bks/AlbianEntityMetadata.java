package org.albianj.orm.bks;


import org.albianj.orm.attr.AlbianObjectAttribute;

import java.util.HashMap;
import java.util.Map;

public final class AlbianEntityMetadata {
    private static Map<String, Object> entityMetadata = new HashMap<>();
    private static Map<String, String> type2itf = new HashMap<>();

    public static AlbianObjectAttribute getEntityMetadata(String itf) {
        return (AlbianObjectAttribute) entityMetadata.get(itf);
    }

    public static AlbianObjectAttribute getEntityMetadata(Class<?> itfClzz) {
        return (AlbianObjectAttribute) entityMetadata.get(itfClzz.getName());
    }

    public static boolean exist(String itf) {
        return entityMetadata.containsKey(itf);
    }

    public static boolean exist(Class<?> itfClzz) {
        return entityMetadata.containsKey(itfClzz.getName());
    }

    public static void put(String itf, AlbianObjectAttribute attr) {
        type2itf.put(attr.getType(), itf);
        entityMetadata.put(itf, attr);
    }

    public static void put(Class<?> itf, AlbianObjectAttribute attr) {
        put(itf.getName(), attr);
    }

    public static void putAll(Map<String, Object> map) {
        //can not use putAll
        for (Object entry : map.values()) {
            AlbianObjectAttribute objAttr = (AlbianObjectAttribute) entry;
            put(objAttr.getInterfaceName(), objAttr);
        }
    }

    public static AlbianObjectAttribute getEntityMetadataByType(String type) {
        return (AlbianObjectAttribute) entityMetadata.get(type2Interface(type));
    }

    public static AlbianObjectAttribute getEntityMetadataByType(Class<?> implClzz) {
        return getEntityMetadataByType(implClzz.getName());
    }


    public static String makeFieldsKey(String propertyName) {
        return propertyName.toLowerCase();
    }

    public static String type2Interface(String type) {
        return type2itf.get(type);
    }
}
