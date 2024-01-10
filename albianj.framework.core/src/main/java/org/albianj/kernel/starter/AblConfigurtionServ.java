package org.albianj.kernel.starter;

import org.albianj.anno.AblConfigurationAnno;
import org.albianj.anno.AblDataRouterScannerAnno;
import org.albianj.anno.AblMappingScannerAnno;
import org.albianj.anno.AblServiceScannerAnno;
import org.albianj.common.utils.CheckUtil;
import org.albianj.kernel.ServRouter;
import org.albianj.kernel.attr.PackageAttr;

import java.util.Arrays;
import java.util.Map;

/**
 * 对于配置文件的优先级
 * 优先使用config class配置的
 * 逻辑在于：通常情况下，大部分的人（或者说在没有特殊的情况下），自觉会驱使我们去main-class上配置，因为简单方便
 *          如果还需要单独建立一个config class，那么肯定是有特殊的情况
 * 所有的配置都遵循这个规则
 */
public class AblConfigurtionServ {
    String[] servFilenameDefs = {
            "@config:service.xml",
            "@config:service.yaml",
            "@classpath:service.xml",
            "@classpath:service.yaml",
            "@config:serv.xml",
            "@config:serv.yaml",
            "@classpath:serv.xml",
            "@classpath:serv.yaml",
    };


    public void pickAnnoAndMerge(Class<?> mainClzz) {

        Package mainPkg = mainClzz.getPackage();

        AblConfigurationAnno cfAnnoMain =  mainClzz.getAnnotation(AblConfigurationAnno.class);
        AblServiceScannerAnno servAnnoMain =  mainClzz.getAnnotation(AblServiceScannerAnno.class);
        AblMappingScannerAnno mappingAnnoMain =  mainClzz.getAnnotation(AblMappingScannerAnno.class);
        AblDataRouterScannerAnno drAnnoMain =  mainClzz.getAnnotation(AblDataRouterScannerAnno.class);

        AblServiceScannerAnno servRantCf =  null;
        AblMappingScannerAnno mappingRantCf =  null;
        AblDataRouterScannerAnno drRantCf =  null;

        if(null != cfAnnoMain) {
            Class<?> cfClzz = cfAnnoMain.Class();
            servRantCf =  cfClzz.getAnnotation(AblServiceScannerAnno.class);
            mappingRantCf =  cfClzz.getAnnotation(AblMappingScannerAnno.class);
            drRantCf =  cfClzz.getAnnotation(AblDataRouterScannerAnno.class);
        }

        String[] servPkgMain = servAnnoMain.Packages();
        String[] mappingPkgMain = mappingAnnoMain.Packages();
        String[] drPkgMain = drAnnoMain.Packages();

        String[] servPkgCf = servRantCf.Packages();
        String[] mappingPkgCf = mappingRantCf.Packages();
        String[] drPkgCf = drRantCf.Packages();

        String servFilename = decideServCfFilename(servAnnoMain,servRantCf);
        String mappingFilename = decideMappingCfFilename(mappingAnnoMain,mappingRantCf);
        String drFilename = decideDataRouterCfFilename(drAnnoMain,drRantCf);

        String[] servFilenameDefs = {
                "@config:service.xml",
                "@config:service.yaml",
                "@classpath:service.xml",
                "@classpath:service.yaml",
                "@config:serv.xml",
                "@config:serv.yaml",
                "@classpath:serv.xml",
                "@classpath:serv.yaml",
        };
        if(CheckUtil.isNullOrEmptyOrAllSpace(servFilename)) {

        }


    }

    private static void mergeAndBankPkgs(String[] pkgsMain, String[] pkgsCf,
                                         String[] PkgsCff, Class<?> mainClzz,
                                         Map<String,PackageAttr> bank) {
        Arrays.stream(pkgsMain).forEach( e -> {
            bank.putIfAbsent(e,new PackageAttr(e,false));
        });

        Arrays.stream(pkgsCf).forEach( e -> {
            bank.putIfAbsent(e,new PackageAttr(e,false));
        });

        Arrays.stream(PkgsCff).forEach( e -> {
            bank.putIfAbsent(e,new PackageAttr(e,false));
        });

        String pkgMain = mainClzz.getPackage().getName();
        bank.putIfAbsent(pkgMain,new PackageAttr(pkgMain,false));
    }

    private static String decideServCfFilename(AblServiceScannerAnno servAnnoMain,
                                               AblServiceScannerAnno servRantCf){

        String filenameMain = null;
        String filenameCf = null;
        if(null != servAnnoMain) {
            filenameMain = servAnnoMain.FileName();
        }
        if(null != servRantCf ) {
            filenameCf = servRantCf.FileName();
        }

        if(!CheckUtil.isNullOrEmptyOrAllSpace(filenameCf)) {
            return filenameCf;
        }

        return filenameMain;
    }

    private static String decideMappingCfFilename(AblMappingScannerAnno servAnnoMain,
                                                  AblMappingScannerAnno servRantCf){

        String filenameMain = null;
        String filenameCf = null;
        if(null != servAnnoMain) {
            filenameMain = servAnnoMain.FileName();
        }
        if(null != servRantCf ) {
            filenameCf = servRantCf.FileName();
        }

        if(!CheckUtil.isNullOrEmptyOrAllSpace(filenameCf)) {
            return filenameCf;
        }

        return filenameMain;
    }

    private static String decideDataRouterCfFilename(AblDataRouterScannerAnno servAnnoMain,
                                                     AblDataRouterScannerAnno servRantCf){

        String filenameMain = null;
        String filenameCf = null;
        if(null != servAnnoMain) {
            filenameMain = servAnnoMain.FileName();
        }
        if(null != servRantCf ) {
            filenameCf = servRantCf.FileName();
        }

        if(!CheckUtil.isNullOrEmptyOrAllSpace(filenameCf)) {
            return filenameCf;
        }

        return filenameMain;
    }

    private static String decideAspectCfFilename(){
        ServRouter.throwAgain("this function is not impl.");
        return null;
    }
}
