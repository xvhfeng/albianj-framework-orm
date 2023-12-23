package org.albianj.kernel.impl.core;

import org.albianj.common.argument.KeyValuePair;
import org.albianj.common.utils.CheckUtil;
import org.albianj.kernel.AlbianRuntimeException;
import org.albianj.kernel.impl.service.AlbianServiceRantParser;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.kernel.logger.LogTarget;
import org.albianj.kernel.service.*;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.loader.GlobalSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlbianBuiltinServiceLoader {

    private LinkedHashMap<String, AlbianBuiltinServiceAttribute> bltServ = null;
    private Map<String, IAlbianServiceAttribute> bltSrvAttrs = null;

    public AlbianBuiltinServiceLoader() {

        bltServ = new LinkedHashMap<>();
        bltSrvAttrs = new LinkedHashMap<>();
        // kernel
//        bltServ.put(AlbianBuiltinServiceNamePair.AlbianLoggerServicePair[0],
//                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianLoggerServicePair[0],
//                        AlbianBuiltinServiceNamePair.AlbianLoggerServicePair[1], true));
//        bltServ.put(AlbianBuiltinServiceNamePair.AlbianLoggerService2Pair[0],
//                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianLoggerService2Pair[0],
//                        AlbianBuiltinServiceNamePair.AlbianLoggerService2Pair[1], true));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianKernelServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianKernelServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianKernelServicePair[1], true));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianLogicIdServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianLogicIdServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianLogicIdServicePair[1], true));
//        bltServ.put(AlbianBuiltinServiceNamePair.AlbianThreadPoolServicePair[0],
//                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianThreadPoolServicePair[0],
//                        AlbianBuiltinServiceNamePair.AlbianThreadPoolServicePair[1], true));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianSecurityServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianSecurityServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianSecurityServicePair[1], true));

        // persistence
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianStorageServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianStorageServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianStorageServicePair[1], true));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianMappingServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianMappingServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianMappingServicePair[1], true));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianDataRouterServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianDataRouterServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianDataRouterServicePair[1], false));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianPersistenceServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianPersistenceServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianPersistenceServicePair[1], true));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianDataAccessServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianDataAccessServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianDataAccessServicePair[1], false));

        //pplog monitor
//        bltServ.put(AlbianBuiltinServiceNamePair.YuewenPPLogPair[0],
//                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.YuewenPPLogPair[0],
//                        AlbianBuiltinServiceNamePair.YuewenPPLogPair[1], false));
//        // web mvf framework
//        bltServ.put(AlbianBuiltinServiceNamePair.AlbianMvcConfigurtionServicePair[0],
//                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianMvcConfigurtionServicePair[0],
//                        AlbianBuiltinServiceNamePair.AlbianMvcConfigurtionServicePair[1], false));
//        bltServ.put(AlbianBuiltinServiceNamePair.AlbianFileUploadServicePair[0],
//                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianFileUploadServicePair[0],
//                        AlbianBuiltinServiceNamePair.AlbianFileUploadServicePair[1], false));
//        bltServ.put(AlbianBuiltinServiceNamePair.AlbianResourceServicePair[0],
//                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianResourceServicePair[0],
//                        AlbianBuiltinServiceNamePair.AlbianResourceServicePair[1], false));
//        bltServ.put(AlbianBuiltinServiceNamePair.AlbianTemplateServicePair[0],
//                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianTemplateServicePair[0],
//                        AlbianBuiltinServiceNamePair.AlbianTemplateServicePair[1], false));
//        bltServ.put(AlbianBuiltinServiceNamePair.AlbianBrushingServicePair[0],
//                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianBrushingServicePair[0],
//                        AlbianBuiltinServiceNamePair.AlbianBrushingServicePair[1], false));

        // load service.xml
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianServiceParserPair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianServiceParserPair[0],
                        AlbianBuiltinServiceNamePair.AlbianServiceParserPair[1], true));
    }

    public void loadServices(String sessionId, GlobalSettings settings)  {

        Map<String,IAlbianServiceAttribute> bltServMap = sacnService(sessionId);
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
                    IAlbianServiceAttribute attr = bltServMap.get(id);
                    IAlbianService service = AlbianServiceLoader.makeupService(settings,attr,bltServMap);
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
                    AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Warn,
                            "loader plugin service：{} is error",sbFailPluginServiceBiref);
                } //can not return,check required service
                if (requiredServiceFail) {
                    //required service can not load fail.
                    AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                            new AlbianRuntimeException("loader required service is fail.sbFailReqServiceBiref:" + sbFailReqServiceBiref),
                            "loader plugin ReService：{} is error",sbFailReqServiceBiref);
                }
                return;
            }
        }
    }

    public Map<String, IAlbianServiceAttribute> getBltSrvAttrs() {
        return this.bltSrvAttrs;
    }

    public Map<String,IAlbianServiceAttribute> sacnService(String sessionId)  {
        bltSrvAttrs = new LinkedHashMap<>();
        for (AlbianBuiltinServiceAttribute bltSerAttr : this.bltServ.values()) {
//            String id = bltSerAttr.getId();
//            String sImplClzz = bltSerAttr.getImplClzz();
            try {
//                Class<?> implClzz = AlbianClassLoader.getInstance().loadClass(sImplClzz);
//                IAlbianServiceAttribute attr = AlbianServiceRantParser.scanAlbianService(implClzz);
//                bltSrvAttrs.put(id,attr);
                KeyValuePair<String,IAlbianServiceAttribute> kvp = sacnService(bltSerAttr);
                bltSrvAttrs.put(kvp.getKey(),kvp.getValue());
            }catch (Exception e){
                if(bltSerAttr.isRequired()) {
                    AlbianServiceRouter.logAndThrowNew(sessionId, LogTarget.Running, LogLevel.Error,
                            new AlbianRuntimeException("loader builtin  service:"+bltSerAttr.getId()+" is fail."),
                            "BuiltinServiceLoader loader builtin  service:{} is fail. but it is must load.",bltSerAttr.getId());
                } else {
                    AlbianServiceRouter.log(sessionId, LogTarget.Running, LogLevel.Warn,
                            new AlbianRuntimeException("loader builtin  service:"+bltSerAttr.getId()+" is fail."),
                            "BuiltinServiceLoader loader builtin  service:{} is fail. but it is not must load.",bltSerAttr.getId());
                }
            }
        }
        if(CheckUtil.isNullOrEmpty(bltSrvAttrs))  return null;
        return bltSrvAttrs;
    }

    /**
     * 解析单个service attribute
     * @param servAttr
     * @return
     * @throws ClassNotFoundException
     */
    public KeyValuePair<String,IAlbianServiceAttribute> sacnService(AlbianBuiltinServiceAttribute servAttr) {
            String id = servAttr.getId();
            String sImplClzz = servAttr.getImplClzz();
            KeyValuePair kvp = null;
            try {
                Class<?> implClzz = AlbianClassLoader.getInstance().loadClass(sImplClzz);
                IAlbianServiceAttribute attr = AlbianServiceRantParser.scanAlbianService(implClzz);
                kvp = new KeyValuePair(id,attr);
            }catch (Throwable e){
                throw new AlbianRuntimeException(e);
            }

            return kvp;
    }

    /**
     * 单独建立日志service，必须在startup方法的第一条就调用，以便后续启动的时候日志可以记录
     * @throws ClassNotFoundException
     */
    public void loadLoggerService(GlobalSettings settings)  {
        KeyValuePair<String,IAlbianServiceAttribute>  logServAttr =  sacnService(new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianLoggerServicePair[0],
                                                                    AlbianBuiltinServiceNamePair.AlbianLoggerServicePair[1],true));
        IAlbianService service = AlbianServiceLoader.makeupService(settings,logServAttr.getValue(),null);
        ServiceContainer.addService(logServAttr.getKey(), service);
    }

}
