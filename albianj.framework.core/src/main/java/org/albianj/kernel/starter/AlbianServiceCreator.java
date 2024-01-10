package org.albianj.kernel.starter;

import ognl.Ognl;
import org.albianj.common.utils.CheckUtil;
import org.albianj.common.utils.ReflectUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.attr.MethodAttr;
import org.albianj.kernel.attr.ServiceAttr;
import org.albianj.kernel.attr.ServiceFieldAttr;
import org.albianj.kernel.attr.GlobalSettings;
import org.albianj.kernel.attr.opt.AblFieldSetStageOpt;
import org.albianj.kernel.attr.opt.AblVarTypeOpt;
import org.albianj.kernel.bkt.ServiceAttrsBkt;
import org.albianj.kernel.bkt.GlobalSettingsBkt;
import org.albianj.kernel.impl.aspect.ServAspectProxy;
import org.albianj.kernel.itf.builtin.logger.LogLevel;
import org.albianj.kernel.itf.builtin.logger.LogTarget;
import org.albianj.kernel.itf.service.IAlbianService;
import org.albianj.kernel.ServRouter;

import java.util.Map;

public class AlbianServiceCreator {

    public static Object newService(Object sessionId,Class<?> clzz) {
        String servId = clzz.getName();
        return newService(sessionId, servId);
    }

    public static Object newService(Object sessionId,String servId){
        GlobalSettings settings = GlobalSettingsBkt.getSelf();
        ServRouter.throwIfFalse(ServiceAttrsBkt.exist(servId),
                StringsUtil.nonIdxFormat("Serv:{} attribute is not exist.",servId));
        ServiceAttr serviceAttr =  ServiceAttrsBkt.get(servId);
        return newService(settings,serviceAttr, ServiceAttrsBkt.getReadOnlySelf());
    }

    public static Object newService(GlobalSettings settings, ServiceAttr selfServAttr,
                                    Map<String, ServiceAttr> servAttrs)  {
        String id = selfServAttr.getId();
        Class<?> implClzz = selfServAttr.getSelfClass();
        Class<?> itfClzz = selfServAttr.getRootItfClass();
        Object rtnServ = null;
        try {
            ServRouter.throwIfFalse(itfClzz.isAssignableFrom(implClzz),
                    StringsUtil.nonIdxFormat("servid:{} itf-class:{} is not assign from impl-class:{}",
                    id,itfClzz.getName(),implClzz.getName()));

            Object serv = implClzz.getDeclaredConstructor().newInstance();

            setServiceFields(settings,serv, selfServAttr, AblFieldSetStageOpt.AfterNew, servAttrs);
            MethodAttr initFnAttr = selfServAttr.getInitFnAttr();
            if(null != initFnAttr) {
                initFnAttr.getSelf().invoke(serv,null);
            }
            setServiceFields(settings,serv, selfServAttr, AblFieldSetStageOpt.AfterInit, servAttrs);

            if (CheckUtil.isNullOrEmpty(selfServAttr.getAspectAttrs())) {
                rtnServ = serv;
            } else {
                ServAspectProxy proxy = new ServAspectProxy();
                Object serviceProxy =  proxy.newInstance(settings.getBatchId(),settings,selfServAttr, serv);
                rtnServ = serviceProxy;
            }
        } catch (Exception e) {
            ServRouter.throwAgain(StringsUtil.nonIdxFormat("NewInstance Service:{} is fail",id),e);
        }
        return rtnServ;
    }

    public static void setServiceFields(GlobalSettings settings,
                                        Object serv, ServiceAttr servAttr,
                                        AblFieldSetStageOpt lifetime,
                                        Map<String, ServiceAttr> servAttrs)  {
        /**
         * 对于service字段的注入，目前只支持service（使用ref和ervid）的注入和 builtinTpeOpt支持的类型直接量注入
         * 暂不考虑对于实体的注入，
         * 如需注入一个实体（java bean）请在init anno的方法中自行解决
         */
        if(CheckUtil.isNullOrEmpty(servAttr.getFieldAttrs())) {
            return;
        }
        for (ServiceFieldAttr fAttr : servAttr.getFieldAttrs().values()) {
            if (lifetime != fAttr.getSetterLifetime() || fAttr.isReady()) { //when in the lifecycle
                continue;
            }
            // 属性值是直接量
            if (AblVarTypeOpt.Service != fAttr.getTypeOpt()) { // not set ref
                try {
                    Object o = ReflectUtil.toRealObject(fAttr.getFieldType().getName(), fAttr.getValue());
                    fAttr.getField().set(serv, o);
                    fAttr.setReady(true);
                } catch (Exception e) {
                    ServRouter.logAndThrowAgain(settings.getBatchId(), LogTarget.Running, LogLevel.Error,e,
                            "set field {}.{} = {} is fail.",servAttr.getId(),fAttr.getName(),fAttr.getValue());
                }
                continue;
            }

            String value = fAttr.getValue();
            Object realObject = null;
            int indexof = value.indexOf(".");
            // 直接是一个serv
            if (-1 == indexof) { // real ref service
                realObject = ServRouter.getService(settings.getBatchId(),IAlbianService.class, value, false);
                if (!fAttr.isAllowNull() && null == realObject) {
                    ServRouter.logAndThrowNew(settings.getBatchId(), LogTarget.Running, LogLevel.Error,
                    "not found ref service:{} to set field:{} in service:{}",
                    value,fAttr.getName(),servAttr.getId());
                    //continue;
                }

                if (null != realObject) {
                    try {
                        fAttr.getField().set(serv, realObject);
                        fAttr.setReady(true);
                    } catch (Exception e) {
                        ServRouter.logAndThrowAgain(settings.getBatchId(), LogTarget.Running, LogLevel.Error,e,
                        "set field {}.{} = {} is fail.the field type is ref.",
                        servAttr.getId(),fAttr.getName(),fAttr.getValue());
                    }
                }
                continue;
            }

            // 是一个serv中的某个属性值
            /*
                因为serv是一个单例的对象，所以这个有必要吗？
             */
            String refServiceId = value.substring(0, indexof);
            String exp = value.substring(indexof + 1);
            IAlbianService refService =
                ServRouter.getService(settings.getBatchId(),IAlbianService.class, refServiceId, false);

            if (!fAttr.isAllowNull() && null == refService) {
                ServRouter.logAndThrowNew(settings.getBatchId(), LogTarget.Running, LogLevel.Error,
                "{}.{} = {}.{} is fail.not found ref srvice:{}",
                servAttr.getId(),fAttr.getName(),refServiceId,exp,exp);
            }

            if(null == refService) {
                continue;
            }

            ServiceAttr sAttr = servAttrs.get(refServiceId);
            Object refRealObj = sAttr.getSelfClass().cast(refService);//must get service full type sign
            try {
                realObject = Ognl.getValue(exp, refRealObj);// get read value from full-sgin ref service
            } catch (Exception e) {
                ServRouter.logAndThrowAgain(settings.getBatchId(), LogTarget.Running, LogLevel.Error,e,
                        "{}.{} = {}.{} is fail.not found exp {} in ref srvice:{}",
                        servAttr.getId(),fAttr.getName(),refServiceId,exp,exp,refServiceId);
            }
            if (null == realObject && !fAttr.isAllowNull()) {
                ServRouter.logAndThrowNew(settings.getBatchId(), LogTarget.Running, LogLevel.Error,
                        "{}.{} = {}.{} is fail.not found  ref srvice:{}",
                        servAttr.getId(),fAttr.getName(),refServiceId,exp,exp);
            }
            if (null != realObject) {
                try {
                    fAttr.getField().set(serv, realObject);
                    fAttr.setReady(true);
                } catch (Exception e) {
                    ServRouter.logAndThrowAgain(settings.getBatchId(), LogTarget.Running, LogLevel.Error,e,
                            "{}.{} = {}.{} is fail.",
                            servAttr.getId(),fAttr.getName(),refServiceId,exp);
                }
            }
        }
    }
}
