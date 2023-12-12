package org.albianj.persistence.service;

import org.albianj.loader.AlbianClassLoader;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.verify.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xuhaifeng on 17/3/14.
 */
public class AlbianObjectCreator {

    private static final Logger logger = LoggerFactory.getLogger(AlbianObjectCreator.class);

    public static IAlbianObject newInstance(String sessionId, String itf) {
        IAlbianObjectAttribute attr = AlbianEntityMetadata.getEntityMetadata(itf);
//            IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
//            IAlbianObjectAttribute attr = amps.getAlbianObjectAttribute(itf);
        if (null == attr) {
            logger.error("can not found interface:{} attribute,please lookup persistence config.",itf);
            throw new AlbianDataServiceException("no found interface attribute.");
        }
        String className = attr.getType();
        if (Validate.isNullOrEmptyOrAllSpace(className)) {
            logger.error(" can not found impl-class for interface:{},please lookup persistence config.", itf);
            throw new AlbianDataServiceException("no implements class.");
        }
        Class<?> cls = null;
        try {
            Class<?> itfs = AlbianClassLoader.getInstance().loadClass(itf);
            if (!itfs.isInterface()) {
                logger.error("{} in not a interface.", itf);
                throw new AlbianDataServiceException("no found interface.");
            }
            if (!IAlbianObject.class.isAssignableFrom(itfs)) {
                logger.error( "{} in not a interface.", itf);
                throw new AlbianDataServiceException("interface inherit error.");
            }
            cls = AlbianClassLoader.getInstance().loadClass(className);
            if (null == cls) {
                logger.error("class:{} is not found.", className);
                throw new AlbianDataServiceException("not found class.");

            }
            if (!IAlbianObject.class.isAssignableFrom(cls)) {
                logger.error("class:{} is not extends from IAlbianObject.", className);
                throw new AlbianDataServiceException("class inherit error.");
            }
            if (!itfs.isAssignableFrom(cls)) {
                logger.error("class:{} is not extends from interface:{}.", className, itf);
                throw new AlbianDataServiceException("class inherit error.");
            }
            IAlbianObject obj = (IAlbianObject)cls.newInstance();
            return obj;
        } catch (Exception e) {
            throw new AlbianDataServiceException("class:" + className + "is not found.", e);
        }

        //return null;
    }

    public static IAlbianObject newInstance(String sessionId, Class<? extends IAlbianObject> clazz) {
        return newInstance(sessionId, clazz.getName());
    }
}
