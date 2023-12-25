package org.albianj.kernel.starter;

import org.albianj.common.utils.KeyValuePair;
import org.albianj.common.utils.CheckUtil;
import org.albianj.kernel.AlbianRuntimeException;
import org.albianj.kernel.attr.AlbianBuiltinServiceAttr;
import org.albianj.kernel.attr.AlbianServiceAttr;
import org.albianj.kernel.bkt.AlbianBuiltinServicesBkt;
import org.albianj.kernel.bkt.ServiceBkt;
import org.albianj.kernel.impl.service.AlbianServiceRantParser;
import org.albianj.kernel.kit.logger.LogLevel;
import org.albianj.kernel.kit.logger.LogTarget;
import org.albianj.kernel.kit.service.AlbianServiceRouter;
import org.albianj.kernel.kit.service.IAlbianService;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.loader.GlobalSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlbianBuiltinServiceLoader {

    private LinkedHashMap<String, AlbianBuiltinServiceAttr> bltServ = null;
    private Map<String, AlbianServiceAttr> bltSrvAttrs = null;

    public AlbianBuiltinServiceLoader() {

        bltServ = new LinkedHashMap<>();
        bltSrvAttrs = new LinkedHashMap<>();

        bltServ.put(AlbianBuiltinServicesBkt.AlbianKernelServicePair[0],
                new AlbianBuiltinServiceAttr(AlbianBuiltinServicesBkt.AlbianKernelServicePair[0],
                        AlbianBuiltinServicesBkt.AlbianKernelServicePair[1], true));
        bltServ.put(AlbianBuiltinServicesBkt.AlbianLogicIdServicePair[0],
                new AlbianBuiltinServiceAttr(AlbianBuiltinServicesBkt.AlbianLogicIdServicePair[0],
                        AlbianBuiltinServicesBkt.AlbianLogicIdServicePair[1], true));

        bltServ.put(AlbianBuiltinServicesBkt.AlbianSecurityServicePair[0],
                new AlbianBuiltinServiceAttr(AlbianBuiltinServicesBkt.AlbianSecurityServicePair[0],
                        AlbianBuiltinServicesBkt.AlbianSecurityServicePair[1], true));

        // persistence
        bltServ.put(AlbianBuiltinServicesBkt.AlbianStorageServicePair[0],
                new AlbianBuiltinServiceAttr(AlbianBuiltinServicesBkt.AlbianStorageServicePair[0],
                        AlbianBuiltinServicesBkt.AlbianStorageServicePair[1], true));
        bltServ.put(AlbianBuiltinServicesBkt.AlbianMappingServicePair[0],
                new AlbianBuiltinServiceAttr(AlbianBuiltinServicesBkt.AlbianMappingServicePair[0],
                        AlbianBuiltinServicesBkt.AlbianMappingServicePair[1], true));
        bltServ.put(AlbianBuiltinServicesBkt.AlbianDataRouterServicePair[0],
                new AlbianBuiltinServiceAttr(AlbianBuiltinServicesBkt.AlbianDataRouterServicePair[0],
                        AlbianBuiltinServicesBkt.AlbianDataRouterServicePair[1], false));
        bltServ.put(AlbianBuiltinServicesBkt.AlbianPersistenceServicePair[0],
                new AlbianBuiltinServiceAttr(AlbianBuiltinServicesBkt.AlbianPersistenceServicePair[0],
                        AlbianBuiltinServicesBkt.AlbianPersistenceServicePair[1], true));
        bltServ.put(AlbianBuiltinServicesBkt.AlbianDataAccessServicePair[0],
                new AlbianBuiltinServiceAttr(AlbianBuiltinServicesBkt.AlbianDataAccessServicePair[0],
                        AlbianBuiltinServicesBkt.AlbianDataAccessServicePair[1], false));


        // load service.xml
        bltServ.put(AlbianBuiltinServicesBkt.AlbianServiceParserPair[0],
                new AlbianBuiltinServiceAttr(AlbianBuiltinServicesBkt.AlbianServiceParserPair[0],
                        AlbianBuiltinServicesBkt.AlbianServiceParserPair[1], true));
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
                    IAlbianService service = AlbianServiceLoader.makeupService(settings,attr,bltServMap);
                    ServiceBkt.addService(id, service);
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
    public KeyValuePair<String, AlbianServiceAttr> sacnService(AlbianBuiltinServiceAttr servAttr) {
            String id = servAttr.getId();
            String sImplClzz = servAttr.getImplClzz();
            KeyValuePair kvp = null;
            try {
                Class<?> implClzz = AlbianClassLoader.getInstance().loadClass(sImplClzz);
                AlbianServiceAttr attr = AlbianServiceRantParser.scanAlbianService(implClzz);
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
        KeyValuePair<String, AlbianServiceAttr>  logServAttr =  sacnService(new AlbianBuiltinServiceAttr(AlbianBuiltinServicesBkt.AlbianLoggerServicePair[0],
                                                                    AlbianBuiltinServicesBkt.AlbianLoggerServicePair[1],true));
        IAlbianService service = AlbianServiceLoader.makeupService(settings,logServAttr.getValue(),null);
        ServiceBkt.addService(logServAttr.getKey(), service);
    }

}
