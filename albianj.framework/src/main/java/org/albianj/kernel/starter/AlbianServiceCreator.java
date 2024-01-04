package org.albianj.kernel.starter;

import ognl.Ognl;
import org.albianj.common.utils.CheckUtil;
import org.albianj.common.utils.ReflectUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.attr.AlbianMethodAttr;
import org.albianj.kernel.attr.opt.AlbianVarTypeOpt;
import org.albianj.kernel.impl.aspect.AlbianServAspectProxy;
import org.albianj.kernel.attr.AlbianServiceAttr;
import org.albianj.kernel.itf.builtin.logger.LogLevel;
import org.albianj.kernel.itf.builtin.logger.LogTarget;
import org.albianj.kernel.attr.AlbianServiceFieldAttr;
import org.albianj.kernel.attr.opt.AlbianServFieldSetStageOpt;
import org.albianj.kernel.itf.service.AlbianServRouter;
import org.albianj.kernel.itf.service.IAlbianService;
import org.albianj.kernel.attr.GlobalSettings;

import java.util.Map;

public class AlbianServiceCreator {

    public static Object newService(GlobalSettings settings, AlbianServiceAttr serviceAttr,
                                    Map<String, AlbianServiceAttr> servAttrs)  {
        String id = serviceAttr.getId();
        Class<?> implClzz = serviceAttr.getSelfClass();
        Class<?> itfClzz = serviceAttr.getRootItfClass();
        Object rtnServ = null;
        try {
            AlbianServRouter.throwIfFalse(itfClzz.isAssignableFrom(implClzz),
                    StringsUtil.nonIdxFormat("servid:{} itf-class:{} is not assign from impl-class:{}",
                    id,itfClzz.getName(),implClzz.getName()));

            Object serv = implClzz.getDeclaredConstructor().newInstance();

            setServiceFields(settings,serv, serviceAttr, AlbianServFieldSetStageOpt.AfterNew, servAttrs);
            AlbianMethodAttr initFnAttr = serviceAttr.getInitFnAttr();
            if(null != initFnAttr) {
                initFnAttr.getSelf().invoke(serv,null);
            }
            setServiceFields(settings,serv, serviceAttr, AlbianServFieldSetStageOpt.AfterInit, servAttrs);

            if (CheckUtil.isNullOrEmpty(serviceAttr.getAspectAttrs())) {
                rtnServ = serv;
            } else {
                AlbianServAspectProxy proxy = new AlbianServAspectProxy();
                Object serviceProxy =  proxy.newInstance(settings.getBatchId(),settings,serviceAttr, serv);
                rtnServ = serviceProxy;
            }
        } catch (Exception e) {
            AlbianServRouter.throwAgain(StringsUtil.nonIdxFormat("NewInstance Service:{} is fail",id),e);
        }
        return rtnServ;
    }

    public static void setServiceFields(GlobalSettings settings,
                                        Object serv, AlbianServiceAttr servAttr,
                                        AlbianServFieldSetStageOpt lifetime,
                                        Map<String, AlbianServiceAttr> servAttrs)  {
        /**
         * 对于service字段的注入，目前只支持service（使用ref和ervid）的注入和 builtinTpeOpt支持的类型直接量注入
         * 暂不考虑对于实体的注入，
         * 如需注入一个实体（java bean）请在init anno的方法中自行解决
         */
        if(CheckUtil.isNullOrEmpty(servAttr.getFieldAttrs())) {
            return;
        }
        for (AlbianServiceFieldAttr fAttr : servAttr.getFieldAttrs().values()) {
            if (lifetime != fAttr.getSetterLifetime() || fAttr.isReady()) { //when in the lifecycle
                continue;
            }
            // 属性值是直接量
            if (AlbianVarTypeOpt.Ref != fAttr.getTypeOpt()) { // not set ref
                try {
                    Object o = ReflectUtil.toRealObject(fAttr.getFieldType().getName(), fAttr.getValue());
                    fAttr.getField().set(serv, o);
                    fAttr.setReady(true);
                } catch (Exception e) {
                    AlbianServRouter.logAndThrowAgain(settings.getBatchId(), LogTarget.Running, LogLevel.Error,e,
                            "set field {}.{} = {} is fail.",servAttr.getId(),fAttr.getName(),fAttr.getValue());
                }
                continue;
            }

            String value = fAttr.getValue();
            Object realObject = null;
            int indexof = value.indexOf(".");
            // 直接是一个serv
            if (-1 == indexof) { // real ref service
                realObject = AlbianServRouter.getService(settings.getBatchId(),IAlbianService.class, value, false);
                if (!fAttr.isAllowNull() && null == realObject) {
                    AlbianServRouter.logAndThrowNew(settings.getBatchId(), LogTarget.Running, LogLevel.Error,
                    "not found ref service:{} to set field:{} in service:{}",
                    value,fAttr.getName(),servAttr.getId());
                    //continue;
                }

                if (null != realObject) {
                    try {
                        fAttr.getField().set(serv, realObject);
                        fAttr.setReady(true);
                    } catch (Exception e) {
                        AlbianServRouter.logAndThrowAgain(settings.getBatchId(), LogTarget.Running, LogLevel.Error,e,
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
                AlbianServRouter.getService(settings.getBatchId(),IAlbianService.class, refServiceId, false);

            if (!fAttr.isAllowNull() && null == refService) {
                AlbianServRouter.logAndThrowNew(settings.getBatchId(), LogTarget.Running, LogLevel.Error,
                "{}.{} = {}.{} is fail.not found ref srvice:{}",
                servAttr.getId(),fAttr.getName(),refServiceId,exp,exp);
            }

            if(null == refService) {
                continue;
            }

            AlbianServiceAttr sAttr = servAttrs.get(refServiceId);
            Object refRealObj = sAttr.getSelfClass().cast(refService);//must get service full type sign
            try {
                realObject = Ognl.getValue(exp, refRealObj);// get read value from full-sgin ref service
            } catch (Exception e) {
                AlbianServRouter.logAndThrowAgain(settings.getBatchId(), LogTarget.Running, LogLevel.Error,e,
                        "{}.{} = {}.{} is fail.not found exp {} in ref srvice:{}",
                        servAttr.getId(),fAttr.getName(),refServiceId,exp,exp,refServiceId);
            }
            if (null == realObject && !fAttr.isAllowNull()) {
                AlbianServRouter.logAndThrowNew(settings.getBatchId(), LogTarget.Running, LogLevel.Error,
                        "{}.{} = {}.{} is fail.not found  ref srvice:{}",
                        servAttr.getId(),fAttr.getName(),refServiceId,exp,exp);
            }
            if (null != realObject) {
                try {
                    fAttr.getField().set(serv, realObject);
                    fAttr.setReady(true);
                } catch (Exception e) {
                    AlbianServRouter.logAndThrowAgain(settings.getBatchId(), LogTarget.Running, LogLevel.Error,e,
                            "{}.{} = {}.{} is fail.",
                            servAttr.getId(),fAttr.getName(),refServiceId,exp);
                }
            }
        }
    }
}
