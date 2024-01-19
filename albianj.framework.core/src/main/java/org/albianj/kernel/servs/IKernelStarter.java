package org.albianj.kernel.servs;

import org.albianj.kernel.attr.GlobalSettings;

public interface IKernelStarter {
    void loadConf( GlobalSettings settings,String simpleFileName);

    void modifyKernelAttr(String machinekey, String machineId, String appName);
}
