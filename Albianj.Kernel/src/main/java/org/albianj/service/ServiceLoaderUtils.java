package org.albianj.service;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * project : com.yuewen.nrzx.albianj
 *
 * @ccversion 解决动态加载Service - liyuqi 2019-07-01 17:06</br>
 */
public class ServiceLoaderUtils {

    public static <T> T findService(Class<T> clazz) {
        java.util.ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        Iterator<T> services = serviceLoader.iterator();
        T service = null;
        if (services.hasNext()) {
            service = services.next();
        }
        return service;
    }

    public static <T> Iterator<T> listService(Class<T> clazz) {
        java.util.ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        Iterator<T> services = serviceLoader.iterator();
        return services;
    }
}
