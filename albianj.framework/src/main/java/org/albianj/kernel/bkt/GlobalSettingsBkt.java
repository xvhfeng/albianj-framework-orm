package org.albianj.kernel.bkt;

import lombok.Getter;
import lombok.Setter;
import org.albianj.kernel.attr.GlobalSettings;

public class GlobalSettingsBkt {

    @Getter
    @Setter
    private static GlobalSettings self;

//    public static void setSelf(GlobalSettings self) {
//        GlobalSettingsBkt.self = self;
//    }

}
