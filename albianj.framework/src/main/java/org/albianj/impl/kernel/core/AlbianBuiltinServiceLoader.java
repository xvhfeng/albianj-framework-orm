package org.albianj.impl.kernel.core;

import org.albianj.AblThrowable;
import org.albianj.AblBltinServsNames;
import org.albianj.impl.kernel.service.AlbianServiceAttribute;
import org.albianj.impl.kernel.service.AlbianServiceRantParser;
import org.albianj.common.values.KeyValuePair;
import org.albianj.common.utils.SetUtil;
import org.albianj.ServRouter;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.kernel.service.*;
import org.albianj.loader.AlbianClassLoader;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlbianBuiltinServiceLoader {

    private LinkedHashMap<String, AlbianBuiltinServiceAttribute> bltServ = null;
    private Map<String, AlbianServiceAttribute> bltSrvAttrs = null;

    public AlbianBuiltinServiceLoader() {

        bltServ = new LinkedHashMap<>();
        bltSrvAttrs = new LinkedHashMap<>();
        bltServ.put(AblBltinServsNames.AlbianKernelServicePair[0],
                new AlbianBuiltinServiceAttribute(AblBltinServsNames.AlbianKernelServicePair[0],
                        AblBltinServsNames.AlbianKernelServicePair[1], true));
        bltServ.put(AblBltinServsNames.AlbianSecurityServicePair[0],
                new AlbianBuiltinServiceAttribute(AblBltinServsNames.AlbianSecurityServicePair[0],
                        AblBltinServsNames.AlbianSecurityServicePair[1], true));

        // persistence
        bltServ.put(AblBltinServsNames.AlbianStorageServicePair[0],
                new AlbianBuiltinServiceAttribute(AblBltinServsNames.AlbianStorageServicePair[0],
                        AblBltinServsNames.AlbianStorageServicePair[1], true));
        bltServ.put(AblBltinServsNames.AlbianMappingServicePair[0],
                new AlbianBuiltinServiceAttribute(AblBltinServsNames.AlbianMappingServicePair[0],
                        AblBltinServsNames.AlbianMappingServicePair[1], true));
        bltServ.put(AblBltinServsNames.AlbianDataRouterServicePair[0],
                new AlbianBuiltinServiceAttribute(AblBltinServsNames.AlbianDataRouterServicePair[0],
                        AblBltinServsNames.AlbianDataRouterServicePair[1], false));
        bltServ.put(AblBltinServsNames.AlbianPersistenceServicePair[0],
                new AlbianBuiltinServiceAttribute(AblBltinServsNames.AlbianPersistenceServicePair[0],
                        AblBltinServsNames.AlbianPersistenceServicePair[1], true));
        bltServ.put(AblBltinServsNames.AlbianDataAccessServicePair[0],
                new AlbianBuiltinServiceAttribute(AblBltinServsNames.AlbianDataAccessServicePair[0],
                        AblBltinServsNames.AlbianDataAccessServicePair[1], false));

        // load service.xml
        bltServ.put(AblBltinServsNames.AlbianServiceParserPair[0],
                new AlbianBuiltinServiceAttribute(AblBltinServsNames.AlbianServiceParserPair[0],
                        AblBltinServsNames.AlbianServiceParserPair[1], true));
    }

    public void loadServices(String sessionId)  {

        Map<String,AlbianServiceAttribute> bltServMap = sacnService(sessionId);
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

            for (AlbianBuiltinServiceAttribute bltSerAttr : this.bltServ.values()) {
                if (bltSerAttr.isLoadOK()) continue;
                id = bltSerAttr.getId();
                try {
                    AlbianServiceAttribute attr = bltServMap.get(id);
                    IAlbianService service = AlbianServiceLoader.makeupService(attr,bltServMap);
                    ServiceContainer.addService(id, service);
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
                    ServRouter.log(sessionId,  LogLevel.Warn,
                            "loader plugin service：{} is error",sbFailPluginServiceBiref);
                } //can not return,check required service
                if (requiredServiceFail) {
                    //required service can not load fail.
                    ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                            new AblThrowable("loader required service is fail.sbFailReqServiceBiref:" + sbFailReqServiceBiref),
                            "loader plugin ReService：{} is error",sbFailReqServiceBiref);
                }
                return;
            }
        }
    }

    public Map<String, AlbianServiceAttribute> getBltSrvAttrs() {
        return this.bltSrvAttrs;
    }

    public Map<String,AlbianServiceAttribute> sacnService(String sessionId)  {
        bltSrvAttrs = new LinkedHashMap<>();
        for (AlbianBuiltinServiceAttribute bltSerAttr : this.bltServ.values()) {
//            String id = bltSerAttr.getId();
//            String sImplClzz = bltSerAttr.getImplClzz();
            try {
//                Class<?> implClzz = AlbianClassLoader.getInstance().loadClass(sImplClzz);
//                IAlbianServiceAttribute attr = AlbianServiceRantParser.scanAlbianService(implClzz);
//                bltSrvAttrs.put(id,attr);
                KeyValuePair<String,AlbianServiceAttribute> kvp = sacnService(bltSerAttr);
                bltSrvAttrs.put(kvp.getKey(),kvp.getValue());
            }catch (Exception e){
                if(bltSerAttr.isRequired()) {
                    ServRouter.logAndThrowNew(sessionId,  LogLevel.Error,
                            new AblThrowable("loader builtin  service:"+bltSerAttr.getId()+" is fail."),
                            "BuiltinServiceLoader loader builtin  service:{} is fail. but it is must load.",bltSerAttr.getId());
                } else {
                    ServRouter.log(sessionId,  LogLevel.Warn,
                            new AblThrowable("loader builtin  service:"+bltSerAttr.getId()+" is fail."),
                            "BuiltinServiceLoader loader builtin  service:{} is fail. but it is not must load.",bltSerAttr.getId());
                }
            }
        }
        if(SetUtil.isNullOrEmpty(bltSrvAttrs))  return null;
        return bltSrvAttrs;
    }

    /**
     * 解析单个service attribute
     * @param servAttr
     * @return
     * @throws ClassNotFoundException
     */
    public KeyValuePair<String,AlbianServiceAttribute> sacnService(AlbianBuiltinServiceAttribute servAttr) {
            String id = servAttr.getId();
            String sImplClzz = servAttr.getImplClzz();
            KeyValuePair kvp = null;
            try {
                Class<?> implClzz = AlbianClassLoader.getInstance().loadClass(sImplClzz);
                AlbianServiceAttribute attr = AlbianServiceRantParser.scanAlbianService(implClzz);
                kvp = new KeyValuePair(id,attr);
            }catch (Throwable e){
                throw new AblThrowable(e);
            }

            return kvp;
    }

    /**
     * 单独建立日志service，必须在startup方法的第一条就调用，以便后续启动的时候日志可以记录
     * @throws ClassNotFoundException
     */
    public void loadLoggerService()  {
        KeyValuePair<String,AlbianServiceAttribute>  logServAttr =  sacnService(new AlbianBuiltinServiceAttribute(AblBltinServsNames.AlbianLoggerServicePair[0],
                                                                    AblBltinServsNames.AlbianLoggerServicePair[1],true));
        IAlbianService service = AlbianServiceLoader.makeupService(logServAttr.getValue(),null);
        ServiceContainer.addService(logServAttr.getKey(), service);
    }

}
