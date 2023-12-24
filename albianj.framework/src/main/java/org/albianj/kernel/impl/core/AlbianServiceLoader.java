package org.albianj.kernel.impl.core;

import ognl.Ognl;
import org.albianj.common.utils.CheckUtil;
import org.albianj.common.utils.ReflectUtil;
import org.albianj.kernel.impl.aop.AlbianServiceAopProxy;
import org.albianj.kernel.impl.service.AlbianServiceAttribute;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.kernel.logger.LogTarget;
import org.albianj.kernel.service.AlbianServiceFieldAttribute;
import org.albianj.kernel.service.AlbianServiceFieldSetterLifetime;
import org.albianj.kernel.service.AlbianServiceRouter;
import org.albianj.kernel.service.IAlbianService;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.loader.GlobalSettings;

import java.util.Map;

public class AlbianServiceLoader {

    private static String sessionId = "AlbianServiceLoader";

    public static IAlbianService makeupService(GlobalSettings settings, AlbianServiceAttribute serviceAttr,
                                               Map<String, AlbianServiceAttribute> servAttrs)  {
        String sImplClzz = serviceAttr.getType();
        String id = serviceAttr.getId();
        IAlbianService rtnService = null;

        String sInterface = serviceAttr.getItfClzzName();
        try {
            Class<?> cla = AlbianClassLoader.getInstance().loadClass(sImplClzz);
            if (null == cla) {
                AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                        "load impl class : {}  is null for service : {} " ,sImplClzz, id);
            }

            if (!IAlbianService.class.isAssignableFrom(cla)) {
                AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                    "Service :{}  class:{}  is not extends IAlbianServise",id,sImplClzz);
            }

            Class<?> itf = null;
            if (!CheckUtil.isNullOrEmptyOrAllSpace(sInterface)) {
                itf = AlbianClassLoader.getInstance().loadClass(sInterface);
                if (!itf.isAssignableFrom(cla)) {
                    AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                            "Service :{}  class: {}  is not extends is not impl from itf : {} ",
                            id,sImplClzz,sInterface);
                }

                if (!IAlbianService.class.isAssignableFrom(itf)) {
                    AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                            "Service :{}  itf: {}  is not extends IAlbianSercvice ",
                            id,sInterface);
                }
            }

            IAlbianService service = (IAlbianService) cla.getDeclaredConstructor().newInstance();
            service.setSettings(settings);
            setServiceFields(service, serviceAttr, AlbianServiceFieldSetterLifetime.AfterNew, servAttrs);
            service.beforeLoad();
            setServiceFields(service, serviceAttr, AlbianServiceFieldSetterLifetime.BeforeLoading, servAttrs);
            service.loading();
            setServiceFields(service, serviceAttr, AlbianServiceFieldSetterLifetime.AfterLoading, servAttrs);
            service.afterLoading();
            if (CheckUtil.isNullOrEmpty(serviceAttr.getAopAttributes())) {
                rtnService = service;
            } else {
                AlbianServiceAopProxy proxy = new AlbianServiceAopProxy();
                IAlbianService serviceProxy = (IAlbianService) proxy.newInstance(sessionId,service, serviceAttr.getAopAttributes());
                serviceProxy.setRealService(service);
                serviceProxy.beforeLoad();
                serviceProxy.loading();
                serviceProxy.afterLoading();
                rtnService = serviceProxy;
            }
        } catch (Exception e) {
            AlbianServiceRouter.logAndThrowAgain(sessionId, LogTarget.Running, LogLevel.Error,e,
                    "load and init service:{} with class:{} is fail.",id,sImplClzz);
        }
        return rtnService;
    }

    public static void setServiceFields(IAlbianService serv, AlbianServiceAttribute servAttr, AlbianServiceFieldSetterLifetime lifetime, Map<String, AlbianServiceAttribute> servAttrs)  {
        if(CheckUtil.isNullOrEmpty(servAttr.getServiceFields())) {
            return;
        }
        for (AlbianServiceFieldAttribute fAttr : servAttr.getServiceFields().values()) {
            if (lifetime != fAttr.getSetterLifetime() || fAttr.isReady()) { //when in the lifecycle
                continue;
            }
            if (!fAttr.getType().toLowerCase().equals("ref")) { // not set ref
                try {
                    Object o = ReflectUtil.toRealObject(fAttr.getType(), fAttr.getValue());
                    fAttr.getField().set(serv, o);
                    fAttr.setReady(true);
                } catch (Exception e) {
                    AlbianServiceRouter.logAndThrowAgain(sessionId, LogTarget.Running, LogLevel.Error,e,
                            "set field {}.{} = {} is fail.",servAttr.getId(),fAttr.getName(),fAttr.getValue());
                }
                continue;
            }

            String value = fAttr.getValue();
            Object realObject = null;
            int indexof = value.indexOf(".");
            if (-1 == indexof) { // real ref service
                realObject = AlbianServiceRouter.getService(sessionId,IAlbianService.class, value, false);
                if (!fAttr.isAllowNull() && null == realObject) {
                    AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                    "not found ref service:{} to set field:{} in service:{}",
                    value,fAttr.getName(),servAttr.getId());
                    //continue;
                }

                if (null != realObject) {
                    try {
                        fAttr.getField().set(serv, realObject);
                        fAttr.setReady(true);
                    } catch (Exception e) {
                        AlbianServiceRouter.logAndThrowAgain(sessionId, LogTarget.Running, LogLevel.Error,e,
                        "set field {}.{} = {} is fail.the field type is ref.",
                        servAttr.getId(),fAttr.getName(),fAttr.getValue());
                    }
                }
                continue;
            }

            String refServiceId = value.substring(0, indexof);
            String exp = value.substring(indexof + 1);
            IAlbianService refService =
                AlbianServiceRouter.getService(sessionId,IAlbianService.class, refServiceId, false);

            if (!fAttr.isAllowNull() && null == refService) {
                AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                "{}.{} = {}.{} is fail.not found ref srvice:{}",
                servAttr.getId(),fAttr.getName(),refServiceId,exp,exp);
            }

            if (null != refService) {
                AlbianServiceAttribute sAttr = servAttrs.get(refServiceId);
                Object refRealObj = sAttr.getServiceClass().cast(refService);//must get service full type sign
                try {
                    realObject = Ognl.getValue(exp, refRealObj);// get read value from full-sgin ref service
                } catch (Exception e) {
                    AlbianServiceRouter.logAndThrowAgain(sessionId, LogTarget.Running, LogLevel.Error,e,
                            "{}.{} = {}.{} is fail.not found exp {} in ref srvice:{}",
                            servAttr.getId(),fAttr.getName(),refServiceId,exp,exp,refServiceId);
                }
                if (null == realObject && !fAttr.isAllowNull()) {
                    AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                            "{}.{} = {}.{} is fail.not found  ref srvice:{}",
                            servAttr.getId(),fAttr.getName(),refServiceId,exp,exp);
                }
                if (null != realObject) {
                    try {
                        fAttr.getField().set(serv, realObject);
                        fAttr.setReady(true);
                    } catch (Exception e) {
                        AlbianServiceRouter.logAndThrowAgain(sessionId, LogTarget.Running, LogLevel.Error,e,
                                "{}.{} = {}.{} is fail.",
                                servAttr.getId(),fAttr.getName(),refServiceId,exp);
                    }
                }
            }
        }
    }
}
