package org.albianj.impl.kernel.core;

import ognl.Ognl;
import org.albianj.ServRouter;
import org.albianj.common.utils.ReflectUtil;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.impl.kernel.aop.AlbianServiceAopProxy;
import org.albianj.api.kernel.attr.AlbianServiceAttribute;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.api.kernel.attr.AlbianServiceFieldAttribute;
import org.albianj.api.kernel.anno.serv.AblServFieldSetterOpt;
import org.albianj.api.kernel.service.IAblServ;
import org.albianj.loader.AlbianClassLoader;

import java.util.Map;

public class AlbianServiceLoader {

    private static String sessionId = "AlbianServiceLoader";

    public static IAblServ makeupService(AlbianServiceAttribute serviceAttr,
                                         Map<String, AlbianServiceAttribute> servAttrs)  {
        String sImplClzz = serviceAttr.getType();
        String id = serviceAttr.getId();
        IAblServ rtnService = null;

        String sInterface = serviceAttr.getItf();
        try {
            Class<?> cla = AlbianClassLoader.getInstance().loadClass(sImplClzz);
            if (null == cla) {
                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                        "load impl class : {}  is null for service : {} " ,sImplClzz, id);
            }

            if (!IAblServ.class.isAssignableFrom(cla)) {
                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                    "Service :{}  class:{}  is not extends IAlbianServise",id,sImplClzz);
            }

            Class<?> itf = null;
            if (!StringsUtil.isNullEmptyTrimmed(sInterface)) {
                itf = AlbianClassLoader.getInstance().loadClass(sInterface);
                if (!itf.isAssignableFrom(cla)) {
                    ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                            "Service :{}  class: {}  is not extends is not impl from itf : {} ",
                            id,sImplClzz,sInterface);
                }

                if (!IAblServ.class.isAssignableFrom(itf)) {
                    ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                            "Service :{}  itf: {}  is not extends IAlbianSercvice ",
                            id,sInterface);
                }
            }

            IAblServ service = (IAblServ) cla.newInstance();
            setServiceFields(service, serviceAttr, AblServFieldSetterOpt.AfterNew, servAttrs);
            service.beforeLoad();
            setServiceFields(service, serviceAttr, AblServFieldSetterOpt.BeforeLoading, servAttrs);
            service.loading();
            setServiceFields(service, serviceAttr, AblServFieldSetterOpt.AfterLoading, servAttrs);
            service.afterLoading();
            if (SetUtil.isEmpty(serviceAttr.getAopAttributes())) {
                rtnService = service;
            } else {
                AlbianServiceAopProxy proxy = new AlbianServiceAopProxy();
                IAblServ serviceProxy = (IAblServ) proxy.newInstance(sessionId,service, serviceAttr.getAopAttributes());
                serviceProxy.setRealService(service);
                serviceProxy.beforeLoad();
                serviceProxy.loading();
                serviceProxy.afterLoading();
                rtnService = serviceProxy;
            }
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(sessionId,  LogLevel.Error,e,
                    "load and init service:{} with class:{} is fail.",id,sImplClzz);
        }
        return rtnService;
    }

    public static void setServiceFields(IAblServ serv, AlbianServiceAttribute servAttr, AblServFieldSetterOpt lifetime, Map<String, AlbianServiceAttribute> servAttrs)  {
        if(SetUtil.isEmpty(servAttr.getServiceFields())) {
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
                    ServRouter.logAndThrowAgain(sessionId,  LogLevel.Error,e,
                            "set field {}.{} = {} is fail.",servAttr.getId(),fAttr.getName(),fAttr.getValue());
                }
                continue;
            }

            String value = fAttr.getValue();
            Object realObject = null;
            int indexof = value.indexOf(".");
            if (-1 == indexof) { // real ref service
                realObject = ServRouter.getService(sessionId, IAblServ.class, value, false);
                if (!fAttr.isAllowNull() && null == realObject) {
                    ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                    "not found ref service:{} to set field:{} in service:{}",
                    value,fAttr.getName(),servAttr.getId());
                    //continue;
                }

                if (null != realObject) {
                    try {
                        fAttr.getField().set(serv, realObject);
                        fAttr.setReady(true);
                    } catch (Exception e) {
                        ServRouter.logAndThrowAgain(sessionId,  LogLevel.Error,e,
                        "set field {}.{} = {} is fail.the field type is ref.",
                        servAttr.getId(),fAttr.getName(),fAttr.getValue());
                    }
                }
                continue;
            }

            String refServiceId = value.substring(0, indexof);
            String exp = value.substring(indexof + 1);
            IAblServ refService =
                ServRouter.getService(sessionId, IAblServ.class, refServiceId, false);

            if (!fAttr.isAllowNull() && null == refService) {
                ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                "{}.{} = {}.{} is fail.not found ref srvice:{}",
                servAttr.getId(),fAttr.getName(),refServiceId,exp,exp);
            }

            if (null != refService) {
                AlbianServiceAttribute sAttr = servAttrs.get(refServiceId);
                Object refRealObj = sAttr.getServiceClass().cast(refService);//must get service full type sign
                try {
                    realObject = Ognl.getValue(exp, refRealObj);// get read value from full-sgin ref service
                } catch (Exception e) {
                    ServRouter.logAndThrowAgain(sessionId,  LogLevel.Error,e,
                            "{}.{} = {}.{} is fail.not found exp {} in ref srvice:{}",
                            servAttr.getId(),fAttr.getName(),refServiceId,exp,exp,refServiceId);
                }
                if (null == realObject && !fAttr.isAllowNull()) {
                    ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                            "{}.{} = {}.{} is fail.not found  ref srvice:{}",
                            servAttr.getId(),fAttr.getName(),refServiceId,exp,exp);
                }
                if (null != realObject) {
                    try {
                        fAttr.getField().set(serv, realObject);
                        fAttr.setReady(true);
                    } catch (Exception e) {
                        ServRouter.logAndThrowAgain(sessionId,  LogLevel.Error,e,
                                "{}.{} = {}.{} is fail.",
                                servAttr.getId(),fAttr.getName(),refServiceId,exp);
                    }
                }
            }
        }
    }
}
