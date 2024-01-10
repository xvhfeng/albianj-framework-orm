package org.albianj.kernel.bkt;

import org.albianj.common.apj.util.LangUtil;
import org.albianj.kernel.ServRouter;
import org.albianj.kernel.attr.PackageAttr;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataRouterPkgsBkt {
    private static ConcurrentHashMap<String, PackageAttr> bkt = new ConcurrentHashMap<>();

    public  static boolean exist(String key) {
        ServRouter.throwIaxIfFalse(LangUtil.isEmpty(key),"Service Id is nullOrEmpty");
        return bkt.containsKey(key);
    }

    public  static boolean notExist(String key) {
        ServRouter.throwIaxIfFalse(LangUtil.isEmpty(key),"Service Id is nullOrEmpty");
        return !bkt.containsKey(key);
    }

    public static PackageAttr get(String key){
        ServRouter.throwIaxIfFalse(LangUtil.isEmpty(key),"Service Id is nullOrEmpty");
        return bkt.get(key);
    }

    public  static void insert(String key, PackageAttr value) {
        ServRouter.throwIaxIfFalse(LangUtil.isEmpty(key),"Service Id is nullOrEmpty");
        bkt.put(key, value);
    }

    public  static void remove(String key) {
        ServRouter.throwIaxIfFalse(LangUtil.isEmpty(key),"Service Id is nullOrEmpty");
        bkt.remove(key);
    }

    public  static Map<String, PackageAttr> getReadOnlySelf() {
        return bkt;
    }

    public  static void clear() {
        bkt.clear();
    }
}
