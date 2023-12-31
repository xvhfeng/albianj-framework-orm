package org.albianj.kernel.bkt;

import org.albianj.kernel.attr.GlobalSettings;

public class GlobalSettingsBkt {
    private static GlobalSettings self;

    public static GlobalSettings getSelf() {
        return self;
    }

    public static void setSelf(GlobalSettings self) {
        GlobalSettingsBkt.self = self;
    }

}
