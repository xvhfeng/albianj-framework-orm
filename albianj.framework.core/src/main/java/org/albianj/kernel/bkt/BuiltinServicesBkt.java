package org.albianj.kernel.bkt;

public class BuiltinServicesBkt {

    public final static String AlbianLoggerServiceName = "AlbianLoggerService";
    public final static String AlbianKernelServiceName = "AlbianKernelService";

    public final static String AlbianSecurityServiceName = "AlbianSecurityService";
    // persistence
    public final static String AlbianStorageServiceName = "AlbianStorageService";
    public final static String AlbianMappingServiceName = "AlbianMappingService";
    public final static String AlbianDataRouterServiceName = "AlbianDataRouterService";
    public final static String AlbianPersistenceServiceName = "AlbianPersistenceService";
    public final static String AlbianDataAccessServiceName = "AlbianDataAccessService";

    // load service.xml
    public final static String AlbianServiceParserName = "AlbianServiceParserService";


    public final static String[] AlbianLoggerServicePair = {AlbianLoggerServiceName, "org.albianj.logger.impl.AlbianLoggerService"};
//    public final static String[] AlbianLoggerService2Pair = {AlbianLoggerService2Name, "org.albianj.logger.impl.AlbianLoggerService2"};
    public final static String[] AlbianKernelServicePair = {AlbianKernelServiceName, "org.albianj.kernel.impl.AlbianKernelParserService"};
    public final static String[] AlbianSecurityServicePair = {AlbianSecurityServiceName, "org.albianj.security.impl.AlbianSecurityService"};
    // persistence
    public final static String[] AlbianStorageServicePair = {AlbianStorageServiceName, "org.albianj.persistence.impl.storage.AlbianStorageParserService"};
    public final static String[] AlbianMappingServicePair = {AlbianMappingServiceName, "org.albianj.persistence.impl.mapping.AlbianMappingParserService"};
    public final static String[] AlbianDataRouterServicePair = {AlbianDataRouterServiceName, "org.albianj.persistence.impl.routing.AlbianDataRouterParserService"};
    public final static String[] AlbianPersistenceServicePair = {AlbianPersistenceServiceName, "org.albianj.persistence.impl.service.AlbianPersistenceService"};
    public final static String[] AlbianDataAccessServicePair = {AlbianDataAccessServiceName, "org.albianj.persistence.impl.service.AlbianDataAccessService"};


    // load service.xml
    public final static String[] AlbianServiceParserPair = {AlbianServiceParserName, "org.albianj.service.impl.AlbianServiceParser"};
}
