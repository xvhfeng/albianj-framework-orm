package org.albianj.api.dal.service;

import org.albianj.ServRouter;
import org.albianj.common.utils.StringsUtil;
import org.albianj.api.dal.object.AblEntityAttr;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.api.dal.object.IAblObj;

/**
 * Created by xuhaifeng on 17/3/14.
 */
public class AlbianObjectCreator {

    public static IAblObj newInstance(Object sessionId, String typeClzzName)  {
        AblEntityAttr attr = AlbianEntityMetadata.getEntityMetadata(typeClzzName);
//        if (null == attr) {
//            ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
//                    "can not found interface:{} attribute,please lookup persistence config.",itf);
//        }
        String className = attr.getType();
        if (StringsUtil.isNullOrEmptyOrAllSpace(className)) {
            ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                    " can not found impl-class for interface:{},please lookup persistence config.", className);
        }
        Class<?> cls = null;
        try {
//            Class<?> itfs = AlbianClassLoader.getInstance().loadClass(itf);
//            if (!itfs.isInterface()) {
//                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
//                        "{} in not a interface.", itf);
//            }
//            if (!IAlbianObject.class.isAssignableFrom(itfs)) {
//                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
//                        "{} in not a interface.", itf);
//            }
            cls = AlbianClassLoader.getInstance().loadClass(className);
            if (null == cls) {
                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                        "class:{} is not found.", className);

            }
            if (!IAblObj.class.isAssignableFrom(cls)) {
                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                        "class:{} is not extends from IAlbianObject.", className);
            }
//            if (!itfs.isAssignableFrom(cls)) {
//                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
//                        "class:{} is not extends from interface:{}.", className, itf);
//            }
            IAblObj obj = (IAblObj)cls.newInstance();
            return obj;
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(sessionId,  LogLevel.Error,e,
            "class: {] is not fount.",className);
        }

        return null;
    }

    public static IAblObj newInstance(Object sessionId, Class<? extends IAblObj> typeClzz)  {
        return newInstance(sessionId, typeClzz.getName());
    }
}
