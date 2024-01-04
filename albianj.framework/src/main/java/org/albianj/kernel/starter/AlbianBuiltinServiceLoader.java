package org.albianj.kernel.starter;

import org.albianj.common.utils.KeyValuePair;
import org.albianj.common.utils.CheckUtil;
import org.albianj.AlbianRuntimeException;
import org.albianj.kernel.attr.AlbianBuiltinServiceAttr;
import org.albianj.kernel.attr.AlbianServiceAttr;
import org.albianj.kernel.bkt.AlbianServicesBkt;
import org.albianj.kernel.impl.service.AlbianServiceRantParser;
import org.albianj.kernel.itf.builtin.logger.LogLevel;
import org.albianj.kernel.itf.builtin.logger.LogTarget;
import org.albianj.kernel.itf.service.AlbianServRouter;
import org.albianj.kernel.itf.service.IAlbianService;
import org.albianj.kernel.attr.GlobalSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlbianBuiltinServiceLoader {

    private Map<String, AlbianServiceAttr> bltSrvAttrs = null;

    public AlbianBuiltinServiceLoader() {
    }

    public void loadServices(String sessionId, GlobalSettings settings)  {

        Map<String, AlbianServiceAttr> bltServMap = sacnService(sessionId);
        String id = null;
        String sImplClzz = null;
        int failCount = 0;
        int lastFailCount = 0;
        boolean requiredServiceFail = false;
        boolean pluginServiceFail = false;
        StringBuilder sbFailReqServiceBiref = new StringBuilder();
        StringBuilder sbFailPluginServiceBiref = new StringBuilder();
        while (true) {
            failCount = 0;
            requiredServiceFail = false;
            pluginServiceFail = false;
            if (0 != sbFailReqServiceBiref.length()) {
                sbFailReqServiceBiref.delete(0, sbFailReqServiceBiref.length() - 1);
            }
            if (0 != sbFailPluginServiceBiref.length()) {
                sbFailPluginServiceBiref.delete(0, sbFailPluginServiceBiref.length() - 1);
            }

            for (AlbianBuiltinServiceAttr bltSerAttr : this.bltServ.values()) {
                if (bltSerAttr.isLoadOK()) continue;
                id = bltSerAttr.getId();
                try {
                    AlbianServiceAttr attr = bltServMap.get(id);
                    IAlbianService service = AlbianServiceCreator.newService(settings,attr,bltServMap);
                    AlbianServicesBkt.addService(id, service);
                    bltSerAttr.setLoadOK(true);
                } catch (Exception e) {
                    bltSerAttr.setLoadOK(false);
                    if (bltSerAttr.isRequired()) {
                        sbFailReqServiceBiref.append(" ReServiceId : ").append(bltSerAttr.getId())
                                .append(" ImplClass : ").append(bltSerAttr.getImplClzz()).append(";");
                        requiredServiceFail = true;
                    } else {
                        sbFailPluginServiceBiref.append(" noReServiceId -> ").append(bltSerAttr.getId())
                                .append(" ImplClass -> ").append(bltSerAttr.getImplClzz());
                        pluginServiceFail = true;
                    }
                    failCount++;
                }
            }
            // load all services success
            if (0 == failCount) {
                break;
            }

            if (lastFailCount != failCount) {
                lastFailCount = failCount; // load next
            } else {
                // the last fail service count is the same as this time
                // means no new service was loaded.

                if (pluginServiceFail) {
                    // plugin service load fail is not throw exception
                    AlbianServRouter.log(sessionId, LogTarget.Running, LogLevel.Warn,
                            "loader plugin service：{} is error",sbFailPluginServiceBiref);
                } //can not return,check required service
                if (requiredServiceFail) {
                    //required service can not load fail.
                    AlbianServRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                            new AlbianRuntimeException("loader required service is fail.sbFailReqServiceBiref:" + sbFailReqServiceBiref),
                            "loader plugin ReService：{} is error",sbFailReqServiceBiref);
                }
                return;
            }
        }
    }

    public Map<String, AlbianServiceAttr> getBltSrvAttrs() {
        return this.bltSrvAttrs;
    }

    public Map<String, AlbianServiceAttr> sacnService(String sessionId)  {
        bltSrvAttrs = new LinkedHashMap<>();
        for (AlbianBuiltinServiceAttr bltSerAttr : this.bltServ.values()) {
//            String id = bltSerAttr.getId();
//            String sImplClzz = bltSerAttr.getImplClzz();
            try {
//                Class<?> implClzz = AlbianClassLoader.getInstance().loadClass(sImplClzz);
//                IAlbianServiceAttribute attr = AlbianServiceRantParser.scanAlbianService(implClzz);
//                bltSrvAttrs.put(id,attr);
                KeyValuePair<String, AlbianServiceAttr> kvp = sacnService(bltSerAttr);
                bltSrvAttrs.put(kvp.getKey(),kvp.getValue());
            }catch (Exception e){
                if(bltSerAttr.isRequired()) {
                    AlbianServRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                            new AlbianRuntimeException("loader builtin  service:"+bltSerAttr.getId()+" is fail."),
                            "BuiltinServiceLoader loader builtin  service:{} is fail. but it is must load.",bltSerAttr.getId());
                } else {
                    AlbianServRouter.log(sessionId, LogTarget.Running, LogLevel.Warn,
                            new AlbianRuntimeException("loader builtin  service:"+bltSerAttr.getId()+" is fail."),
                            "BuiltinServiceLoader loader builtin  service:{} is fail. but it is not must load.",bltSerAttr.getId());
                }
            }
        }
        if(CheckUtil.isNullOrEmpty(bltSrvAttrs))  return null;
        return bltSrvAttrs;
    }

//    /**
//     * 解析单个service attribute
//     * @param servAttr
//     * @return
//     * @throws ClassNotFoundException
//     */
//    public KeyValuePair<String, AlbianServiceAttr> sacnService(String sessionId,AlbianBuiltinServiceAttr servAttr) {
//            String id = servAttr.getId();
//            String sImplClzz = servAttr.getImplClzz();
//            KeyValuePair kvp = null;
//            try {
//                Class<?> implClzz = AlbianClassLoader.getInstance().loadClass(sImplClzz);
//                AlbianServiceAttr attr = AlbianServiceRantParser.scanAlbianService(sessionId,implClzz);
//                kvp = new KeyValuePair(id,attr);
//            }catch (Throwable e){
//                throw new AlbianRuntimeException(e);
//            }
//
//            return kvp;
//    }

    public KeyValuePair<String, AlbianServiceAttr> sacnService(String sessionId,ClassLoader loader,String servImplClassName) {
        KeyValuePair kvp = null;
        try {
            Class<?> implClzz = loader.loadClass(servImplClassName);
            AlbianServiceAttr attr = AlbianServiceRantParser.scanAlbianService(sessionId,implClzz);
            kvp = new KeyValuePair(attr.getId(),attr);
        }catch (Throwable e){
            AlbianServRouter.throwAgain(e);
        }

        return kvp;
    }

    /**
     * 单独建立日志service，必须在startup方法的第一条就调用，以便后续启动的时候日志可以记录
     * @throws ClassNotFoundException
     */
    public void loadLoggerService(GlobalSettings settings)  {
        KeyValuePair<String, AlbianServiceAttr>  logServAttr =  sacnService(settings.getBatchId(),settings.getClassLoader(),
                "org.albianj.kernel.impl.builtin.AlbianLoggerService");
        Object service = AlbianServiceCreator.newService(settings,logServAttr.getValue(),null);
        AlbianServicesBkt.addService(logServAttr.getKey(), service);
    }

}
