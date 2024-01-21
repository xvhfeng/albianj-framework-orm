/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
package org.albianj.kernel.servs.impl;

import org.albianj.anno.AblApplicationAnno;
import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.ServRouter;
import org.albianj.kernel.anno.AblArgumentAnno;
import org.albianj.kernel.anno.AblServAnno;
import org.albianj.kernel.anno.AblServFieldAnno;
import org.albianj.kernel.anno.AblServInitAnno;
import org.albianj.kernel.attr.GlobalSettings;
import org.albianj.kernel.attr.config.KernelConfigAttr;
import org.albianj.kernel.attr.opt.AblFieldSetWhenOpt;
import org.albianj.kernel.attr.opt.AblVarModeOpt;
import org.albianj.kernel.attr.opt.AblVarTypeOpt;
import org.albianj.kernel.itf.builtin.logger.LogLevel;
import org.albianj.kernel.itf.builtin.logger.LogTarget;
import org.albianj.kernel.servs.IConfigServ;
import org.albianj.kernel.servs.IKernelAttrInjector;
import org.albianj.kernel.servs.IKernelStarter;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;

import java.util.List;

@AblServAnno
public class KernelStarter implements IKernelStarter {

    @AblServFieldAnno(SetWhen = AblFieldSetWhenOpt.AfterNew)
    IConfigServ cfServ;

    @AblServFieldAnno(SetWhen = AblFieldSetWhenOpt.AfterNew,Type = AblVarTypeOpt.Data,
            Mode = AblVarModeOpt.Static, Value="org.albianj.kernel.bkt.GlobalSettingsBkt@getSelf" )
    GlobalSettings settings;

    @AblServFieldAnno(SetWhen = AblFieldSetWhenOpt.AfterNew)
    IKernelAttrInjector injector;

    @AblServInitAnno(Args = {
            @AblArgumentAnno(Name="settings",Type = AblVarTypeOpt.Data,
                    Mode = AblVarModeOpt.Static, Value="org.albianj.kernel.bkt.GlobalSettingsBkt@getSelf"),
            @AblArgumentAnno(Name="simpleFileName",Value="abl"),
    })
    @Override
    public void loadConf( GlobalSettings settings,String simpleFileName) {

        KernelConfigAttr attrAnno = parserAnno(settings);

        String configFilename = cfServ.decideConfigFilename(settings, simpleFileName);
        KernelConfigAttr attrFile = parserConfig(configFilename);

        KernelConfigAttr attrInjector = null;
        /**
         * 外部注射值进入
         * 只有这个配置是有特殊性的，别的都不会存在这种需求
         * 因为有一些像key一样的值是有保密需求的
         */
        if(null != injector){
            attrInjector = injector.execute();
        }
       KernelConfigAttr attrEnv =  parserConfigFromSysConfig();

        KernelConfigAttr total = new KernelConfigAttr();
        if(null != attrInjector) {
            cfServ.mergeFieldsTo(KernelConfigAttr.class,total, attrEnv, attrInjector);
        }
        cfServ.mergeFieldsTo(KernelConfigAttr.class,total, total, attrFile);
        cfServ.mergeFieldsTo(KernelConfigAttr.class,total, total, attrAnno);
        settings.setKernelConfigAttr(total);
    }

    /**
     *  系统变量的值只会在abl的这个配置项中存在
     *  因为该项有一定的保密性质，有一些需要系统管理员来处理
     */
    private KernelConfigAttr parserConfigFromSysConfig(){
        KernelConfigAttr  attr = new KernelConfigAttr();
        SystemConfiguration sysConfig = new SystemConfiguration();
        String mkey = sysConfig.getString("abl.machinekey");
        String mid = sysConfig.getString("abl.machineId");
        String appname = sysConfig.getString("abl.appname");
        if(StringsUtil.existValue(mkey)) {
            attr.setMachineKey(mkey);
        }
        if(StringsUtil.existValue(mid)){
            attr.setMachineId(mid);
        }

        if(StringsUtil.existValue(appname)){
            attr.setAppName(appname);
        }
        return attr;
    }


    private KernelConfigAttr parserConfig(String configFilename){
        if (StringsUtil.isNullOrEmptyOrAllSpace(configFilename)) {
            ServRouter.logBuilder(settings.getBatchId(), LogTarget.Running, LogLevel.Warn)
                    .format("no abl config file,but not affect startup,running...")
                    .done();
            return null;
        }

        ServRouter.logBuilder(settings.getBatchId(), LogTarget.Running, LogLevel.Info)
                .format("abl config:{} is exist,parser it.", configFilename)
                .done();

        CompositeConfiguration config =  cfServ.neatConfigurtion(configFilename);
        if(null == config){
            ServRouter.logBuilder(settings.getBatchId(),LogTarget.Running,LogLevel.Warn)
                    .format("no config file -> {} for neatting",configFilename)
                    .done();
            return null;
        }

        KernelConfigAttr  attr = new KernelConfigAttr();

        String mkey = config.getString("machinekey[@value]",config.getString("machinekey"));
        String mid = config.getString("machineId[@value]",config.getString("machineId"));
        String appname = config.getString("appname[@value]",config.getString("appname"));

        if(StringsUtil.existValue(mkey)) {
            attr.setMachineKey(mkey);
        }
        if(StringsUtil.existValue(mid)){
            attr.setMachineId(mid);
        }

        if(StringsUtil.existValue(appname)){
            attr.setAppName(appname);
        }
        return attr;
    }

    /**
     *
     * 修改abl的特性，用于将值配置入数据库的情况
     * @param machinekey
     * @param machineId
     * @param appName
     */
    @Override
    public void modifyKernelAttr(String machinekey, String machineId, String appName){
        if(StringsUtil.existValue(machinekey)) {
            settings.getKernelConfigAttr().setMachineKey(machinekey);
        }
        if(StringsUtil.existValue(machineId)){
            settings.getKernelConfigAttr().setMachineId(machineId);
        }

        if(StringsUtil.existValue(appName)){
            settings.getKernelConfigAttr().setAppName(appName);
        }
    }

    private KernelConfigAttr parserAnno(GlobalSettings settings) {
        List<AblApplicationAnno> appAnnos = cfServ.listApplicationAnno(settings.getMainClass(), AblApplicationAnno.class);
        KernelConfigAttr attr = new KernelConfigAttr();
        appAnnos.forEach(appAnno -> {
            if (StringsUtil.existValue(appAnno.MachineId())) {
                attr.setMachineId(appAnno.MachineId());
            }
            if (StringsUtil.existValue(appAnno.MachineKey())) {
                attr.setMachineKey(appAnno.MachineKey());
            }
            if (StringsUtil.existValue(appAnno.Name())) {
                attr.setAppName(appAnno.Name());
            }
        });

        return attr;
    }
}
