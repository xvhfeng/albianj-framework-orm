package org.albianj.kernel.servs.impl;

import org.albianj.kernel.anno.AblArgumentAnno;
import org.albianj.kernel.anno.AblServAnno;
import org.albianj.kernel.anno.AblServFieldAnno;
import org.albianj.kernel.anno.AblServInitAnno;
import org.albianj.kernel.attr.GlobalSettings;
import org.albianj.kernel.attr.opt.AblFieldSetWhenOpt;
import org.albianj.kernel.attr.opt.AblVarModeOpt;
import org.albianj.kernel.attr.opt.AblVarTypeOpt;
import org.albianj.kernel.servs.IConfigServ;
import org.albianj.kernel.servs.IServiceStarter;

@AblServAnno
public class SerivceStarter implements IServiceStarter {
    @AblServFieldAnno(SetWhen = AblFieldSetWhenOpt.AfterNew)
    IConfigServ cfServ;

    @AblServFieldAnno(SetWhen = AblFieldSetWhenOpt.AfterNew,Type = AblVarTypeOpt.Data,
            Mode = AblVarModeOpt.Static, Value="org.albianj.kernel.bkt.GlobalSettingsBkt@getSelf" )
    GlobalSettings settings;



    @AblServInitAnno(Args = {
            @AblArgumentAnno(Name="settings",Type = AblVarTypeOpt.Data,
                    Mode = AblVarModeOpt.Static, Value="org.albianj.kernel.bkt.GlobalSettingsBkt@getSelf"),
            @AblArgumentAnno(Name="simpleFileName",Value="abl"),
    })
    public void loadConf(GlobalSettings settings,String simpleFileName) {

    }



}
