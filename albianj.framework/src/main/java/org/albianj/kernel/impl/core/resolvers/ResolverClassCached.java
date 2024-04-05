package org.albianj.kernel.impl.core.resolvers;

import org.albianj.scanner.ClassAttr;
import org.albianj.scanner.IResolverAttr;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.spi.CurrencyNameProvider;

/**
 * 缓存已经解析过的class的Attr
 * 这样的好处是只需要解析一次即可，第二次碰到的时候直接拿即可
 */
public class ResolverClassCached {
    private static Map<String, IResolverAttr> cached = new ConcurrentHashMap<>();

    public static void putIfAbsent(String clzzFullName,IResolverAttr ca){
        cached.putIfAbsent(clzzFullName,ca);
    }

    public static IResolverAttr get(String clzzFullName){
        return cached.get(clzzFullName);
    }

    public static long size(){
        return cached.size();
    }
}
