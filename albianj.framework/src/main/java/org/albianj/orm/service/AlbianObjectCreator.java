package org.albianj.orm.service;

import org.albianj.kernel.common.utils.CheckUtil;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.kernel.ServRouter;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.orm.object.IAlbianObject;
import org.albianj.orm.object.IAlbianObjectAttribute;

/**
 * Created by xuhaifeng on 17/3/14.
 */
public class AlbianObjectCreator {

    public static IAlbianObject newInstance(Object sessionId, String itf)  {
        IAlbianObjectAttribute attr = AlbianEntityMetadata.getEntityMetadata(itf);
        if (null == attr) {
            ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                    "can not found interface:{} attribute,please lookup persistence config.",itf);
        }
        String className = attr.getType();
        if (CheckUtil.isNullOrEmptyOrAllSpace(className)) {
            ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                    " can not found impl-class for interface:{},please lookup persistence config.", itf);
        }
        Class<?> cls = null;
        try {
            Class<?> itfs = AlbianClassLoader.getInstance().loadClass(itf);
            if (!itfs.isInterface()) {
                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                        "{} in not a interface.", itf);
            }
            if (!IAlbianObject.class.isAssignableFrom(itfs)) {
                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                        "{} in not a interface.", itf);
            }
            cls = AlbianClassLoader.getInstance().loadClass(className);
            if (null == cls) {
                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                        "class:{} is not found.", className);

            }
            if (!IAlbianObject.class.isAssignableFrom(cls)) {
                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                        "class:{} is not extends from IAlbianObject.", className);
            }
            if (!itfs.isAssignableFrom(cls)) {
                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                        "class:{} is not extends from interface:{}.", className, itf);
            }
            IAlbianObject obj = (IAlbianObject)cls.newInstance();
            return obj;
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(sessionId,  LogLevel.Error,e,
            "class: {] is not fount.",className);
        }

        return null;
    }

    public static IAlbianObject newInstance(Object sessionId, Class<? extends IAlbianObject> clazz)  {
        return newInstance(sessionId, clazz.getName());
    }
}
