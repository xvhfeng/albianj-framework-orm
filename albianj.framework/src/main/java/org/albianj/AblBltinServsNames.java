package org.albianj;

import org.albianj.impl.dal.service.AlbianDataAccessService;
import org.albianj.impl.kernel.core.AlbianKernelParserService;
import org.albianj.impl.kernel.logger.AlbianLoggerService;
import org.albianj.impl.kernel.security.AblSecuritySecurityServ;
import org.albianj.impl.kernel.service.AlbianServiceParser;
import org.albianj.impl.dal.mapping.AlbianMappingParserService;
import org.albianj.impl.dal.routing.AlbianDataRouterParserService;
import org.albianj.impl.dal.service.AlbianPersistenceService;
import org.albianj.impl.dal.storage.AlbianStorageParserService;

public class AblBltinServsNames {

    public final static String AlbianLoggerServiceName = "AlbianLoggerService";
    public final static String AlbianKernelServiceName = "AlbianKernelService";
    public final static String AlbianSecurityServiceName = "AlbianSecurityService";

    // persistence
    public final static String AlbianStorageServiceName = "AlbianStorageService";
    public final static String AlbianMappingServiceName = "AlbianMappingService";
    public final static String AlbianDataRouterServiceName = "AlbianDataRouterService";
    public final static String AlbianPersistenceServiceName = "AlbianPersistenceService";
    public final static String AlbianDataAccessServiceName = "AlbianDataAccessService";

    // web mvf framework
    public final static String AlbianMvcConfigurtionServiceName = "AlbianMvcConfigurtionService";
    public final static String AlbianFileUploadServiceName = "AlbianFileUploadService";
    public final static String AlbianResourceServiceName = "AlbianResourceService";
    public final static String AlbianTemplateServiceName = "AlbianTemplateService";
    public final static String AlbianBrushingServiceName = "AlbianBrushingService";

    // load service.xml
    public final static String AlbianServiceParserName = "AlbianServiceParserService";

    public final static String[] AlbianLoggerServicePair = {AlbianLoggerServiceName, AlbianLoggerService.class.getName()};
    public final static String[] AlbianKernelServicePair = {AlbianKernelServiceName, AlbianKernelParserService.class.getName()};
    public final static String[] AlbianSecurityServicePair = {AlbianSecurityServiceName, AblSecuritySecurityServ.class.getName()};
    // persistence
    public final static String[] AlbianStorageServicePair = {AlbianStorageServiceName, AlbianStorageParserService.class.getName()};
    public final static String[] AlbianMappingServicePair = {AlbianMappingServiceName, AlbianMappingParserService.class.getName()};
    public final static String[] AlbianDataRouterServicePair = {AlbianDataRouterServiceName, AlbianDataRouterParserService.class.getName()};
    public final static String[] AlbianPersistenceServicePair = {AlbianPersistenceServiceName, AlbianPersistenceService.class.getName()};
    public final static String[] AlbianDataAccessServicePair = {AlbianDataAccessServiceName, AlbianDataAccessService.class.getName()};

    // web mvf framework
    public final static String[] AlbianMvcConfigurtionServicePair = {AlbianMvcConfigurtionServiceName, "org.albianj.mvc.config.impl.AlbianMVCConfigurtionService"};
    public final static String[] AlbianFileUploadServicePair = {AlbianFileUploadServiceName, "org.albianj.mvc.service.impl.AlbianFileUploadService"};
    public final static String[] AlbianResourceServicePair = {AlbianResourceServiceName, "org.albianj.mvc.service.impl.AlbianResourceService"};
    public final static String[] AlbianTemplateServicePair = {AlbianTemplateServiceName, "org.albianj.mvc.service.impl.AlbianBeetlTemplateService"};
    public final static String[] AlbianBrushingServicePair = {AlbianBrushingServiceName, "org.albianj.mvc.service.impl.AlbianBrushingService"};

    // load service.xml
    public final static String[] AlbianServiceParserPair = {AlbianServiceParserName, AlbianServiceParser.class.getName()};
}
