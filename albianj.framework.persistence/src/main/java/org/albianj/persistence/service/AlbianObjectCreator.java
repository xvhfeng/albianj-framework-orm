package org.albianj.persistence.service;

import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.LogLevel;
import org.albianj.logger.LogTarget;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

/**
 * Created by xuhaifeng on 17/3/14.
 */
public class AlbianObjectCreator {

    public static IAlbianObject newInstance(Object sessionId, String itf)  {
        IAlbianObjectAttribute attr = AlbianEntityMetadata.getEntityMetadata(itf);
        if (null == attr) {
            AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                    "can not found interface:{} attribute,please lookup persistence config.",itf);
        }
        String className = attr.getType();
        if (Validate.isNullOrEmptyOrAllSpace(className)) {
            AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                    " can not found impl-class for interface:{},please lookup persistence config.", itf);
        }
        Class<?> cls = null;
        try {
            Class<?> itfs = AlbianClassLoader.getInstance().loadClass(itf);
            if (!itfs.isInterface()) {
                AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                        "{} in not a interface.", itf);
            }
            if (!IAlbianObject.class.isAssignableFrom(itfs)) {
                AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                        "{} in not a interface.", itf);
            }
            cls = AlbianClassLoader.getInstance().loadClass(className);
            if (null == cls) {
                AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                        "class:{} is not found.", className);

            }
            if (!IAlbianObject.class.isAssignableFrom(cls)) {
                AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                        "class:{} is not extends from IAlbianObject.", className);
            }
            if (!itfs.isAssignableFrom(cls)) {
                AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                        "class:{} is not extends from interface:{}.", className, itf);
            }
            IAlbianObject obj = (IAlbianObject)cls.newInstance();
            return obj;
        } catch (Exception e) {
            AlbianServiceRouter.logAndThrowAgain(sessionId, LogTarget.Running, LogLevel.Error,e,
            "class: {] is not fount.",className);
        }

        return null;
    }

    public static IAlbianObject newInstance(Object sessionId, Class<? extends IAlbianObject> clazz)  {
        return newInstance(sessionId, clazz.getName());
    }
}
