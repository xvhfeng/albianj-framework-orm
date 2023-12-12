package org.albianj.kernel.impl;

import ognl.Ognl;
import org.albianj.aop.impl.AlbianServiceAopProxy;
import org.albianj.except.AlbianRuntimeException;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.reflection.AlbianTypeConvert;
import org.albianj.service.*;
import org.albianj.verify.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AlbianServiceLoader {

    private static final Logger logger = LoggerFactory.getLogger(AlbianServiceLoader.class);
    private static String sessionId = "AlbianServiceLoader";

    public static IAlbianService makeupService(IAlbianServiceAttribute serviceAttr,
        Map<String, IAlbianServiceAttribute> servAttrs) {
        String sImplClzz = serviceAttr.getType();
        String id = serviceAttr.getId();
        IAlbianService rtnService = null;

        String sInterface = serviceAttr.getInterface();
        try {
            Class<?> cla = AlbianClassLoader.getInstance().loadClass(sImplClzz);
            if (null == cla) {
                throw new AlbianRuntimeException("load impl class :" + sImplClzz + " is null for service :" + id);
            }

            if (!IAlbianService.class.isAssignableFrom(cla)) {
                throw new AlbianRuntimeException(
                    "Service :" + id + "class:" + sImplClzz + "is not extends IAlbianServise");
            }

            Class<?> itf = null;
            if (!Validate.isNullOrEmptyOrAllSpace(sInterface)) {
                itf = AlbianClassLoader.getInstance().loadClass(sInterface);
                if (!itf.isAssignableFrom(cla)) {
                    throw new AlbianRuntimeException(
                        "Service :" + id + "class:" + sImplClzz + "is not impl from itf :" + sInterface);
                }

                if (!IAlbianService.class.isAssignableFrom(itf)) {
                    throw new AlbianRuntimeException(
                        "Service :" + id + "itf：" + sInterface + "is not extends IAlbianSercvice.");
                }
            }

            IAlbianService service = (IAlbianService) cla.newInstance();
            setServiceFields(service, serviceAttr, AlbianServiceFieldSetterLifetime.AfterNew, servAttrs);
            service.beforeLoad();
            setServiceFields(service, serviceAttr, AlbianServiceFieldSetterLifetime.BeforeLoading, servAttrs);
            service.loading();
            setServiceFields(service, serviceAttr, AlbianServiceFieldSetterLifetime.AfterLoading, servAttrs);
            service.afterLoading();
            if (Validate.isNullOrEmpty(serviceAttr.getAopAttributes())) {
                rtnService = service;
            } else {
                AlbianServiceAopProxy proxy = new AlbianServiceAopProxy();
                IAlbianService serviceProxy = (IAlbianService) proxy.newInstance(service, serviceAttr.getAopAttributes());
                serviceProxy.setRealService(service);
                serviceProxy.beforeLoad();
                serviceProxy.loading();
                serviceProxy.afterLoading();
                rtnService = serviceProxy;
            }
        } catch (Exception e) {
            throw new AlbianRuntimeException("load and init service:" + id + "with class:" + sImplClzz + " is fail.",
                e);
        }
        return rtnService;
    }

    public static void setServiceFields(IAlbianService serv, IAlbianServiceAttribute servAttr, AlbianServiceFieldSetterLifetime lifetime, Map<String, IAlbianServiceAttribute> servAttrs) {
        if(Validate.isNullOrEmpty(servAttr.getServiceFields())) {
            return;
        }
        for (IAlbianServiceFieldAttribute fAttr : servAttr.getServiceFields().values()) {
            if (lifetime != fAttr.getSetterLifetime() || fAttr.isReady()) { //when in the lifecycle
                continue;
            }
            if (!fAttr.getType().toLowerCase().equals("ref")) { // not set ref
                try {
                    Object o = AlbianTypeConvert.toRealObject(fAttr.getType(), fAttr.getValue());
                    fAttr.getField().set(serv, o);
                    fAttr.setReady(true);
                } catch (Exception e) {
                    throw new AlbianRuntimeException(
                        "set field" + servAttr.getId()+"." + fAttr.getName() + " =" + fAttr.getValue() + "is fail.", e);
                }
                continue;
            }

            String value = fAttr.getValue();
            Object realObject = null;
            int indexof = value.indexOf(".");
            if (-1 == indexof) { // real ref service
                realObject = AlbianServiceRouter.getSingletonService(IAlbianService.class, value, false);
                if (!fAttr.getAllowNull() && null == realObject) {
                    throw new AlbianRuntimeException(
                        "not fund ref Service :" + value + "to set field :" + fAttr.getName() + "in servise:" + servAttr
                            .getId());
                    //continue;
                }

                if (null != realObject) {
                    try {
                        fAttr.getField().set(serv, realObject);
                        fAttr.setReady(true);
                    } catch (Exception e) {
                        throw new AlbianRuntimeException(
                            "set field" + servAttr.getId()+"." + fAttr.getName() + " = " + fAttr.getValue()
                                + " is fail.the field type is ref.", e);
                    }
                }
                continue;
            }

            String refServiceId = value.substring(0, indexof);
            String exp = value.substring(indexof + 1);
            IAlbianService refService =
                AlbianServiceRouter.getSingletonService(IAlbianService.class, refServiceId, false);

            if (!fAttr.getAllowNull() && null == refService) {
                throw new AlbianRuntimeException(
                    servAttr.getId() +"."+ fAttr.getName() + "=" + refServiceId +"."+ exp + "is fail . not found ref Service :"
                        + exp);
                //continue;
            }

            if (null != refService) {
                IAlbianServiceAttribute sAttr = servAttrs.get(refServiceId);
                Object refRealObj = sAttr.getServiceClass().cast(refService);//must get service full type sign
                try {
                    realObject = Ognl.getValue(exp, refRealObj);// get read value from full-sgin ref service
                } catch (Exception e) {
                    throw new AlbianRuntimeException( servAttr.getId()+"."+fAttr.getName()+" ="+refServiceId+"."
                        +exp+ " is fail. not found exp ->"+exp+"in ref service ->"+refServiceId+". ",e);
                    //continue;
                }
                if (null == realObject && !fAttr.getAllowNull()) {
                    throw new AlbianRuntimeException(servAttr.getId()+"." + fAttr.getName() + "= " + refServiceId +"."+ exp
                        + "is fail ,not found ref service :" + exp);
                    //continue;
                }
                if (null != realObject) {
                    try {
                        fAttr.getField().set(serv, realObject);
                        fAttr.setReady(true);
                    } catch (Exception e) {
                        throw new AlbianRuntimeException(
                            servAttr.getId()+"." + fAttr.getName() + " = " + refServiceId +"."+ exp + " is fail.", e);
                    }
                }
            }
        }
    }
}
