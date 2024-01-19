package org.albianj.orm.itf.service;

import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.itf.builtin.logger.LogLevel;
import org.albianj.kernel.itf.builtin.logger.LogTarget;
import org.albianj.kernel.ServRouter;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.orm.bks.AlbianEntityMetadata;
import org.albianj.orm.attr.AlbianObjectAttribute;
import org.albianj.orm.itf.object.IAlbianObject;

/**
 * Created by xuhaifeng on 17/3/14.
 */
public class AlbianObjectCreator {

    public static IAlbianObject newInstance(Object sessionId, String itf)  {
        AlbianObjectAttribute attr = AlbianEntityMetadata.getEntityMetadata(itf);
        if (null == attr) {
            ServRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                    "can not found interface:{} attribute,please lookup persistence config.",itf);
        }
        String className = attr.getType();
        if (StringsUtil.isNullOrEmptyOrAllSpace(className)) {
            ServRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                    " can not found impl-class for interface:{},please lookup persistence config.", itf);
        }
        Class<?> cls = null;
        try {
            Class<?> itfs = AlbianClassLoader.getInstance().loadClass(itf);
            if (!itfs.isInterface()) {
                ServRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                        "{} in not a interface.", itf);
            }
            if (!IAlbianObject.class.isAssignableFrom(itfs)) {
                ServRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                        "{} in not a interface.", itf);
            }
            cls = AlbianClassLoader.getInstance().loadClass(className);
            if (null == cls) {
                ServRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                        "class:{} is not found.", className);

            }
            if (!IAlbianObject.class.isAssignableFrom(cls)) {
                ServRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                        "class:{} is not extends from IAlbianObject.", className);
            }
            if (!itfs.isAssignableFrom(cls)) {
                ServRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                        "class:{} is not extends from interface:{}.", className, itf);
            }
            IAlbianObject obj = (IAlbianObject)cls.newInstance();
            return obj;
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(sessionId, LogTarget.Running, LogLevel.Error,e,
            "class: {] is not fount.",className);
        }

        return null;
    }

    public static IAlbianObject newInstance(Object sessionId, Class<? extends IAlbianObject> clazz)  {
        return newInstance(sessionId, clazz.getName());
    }
}
