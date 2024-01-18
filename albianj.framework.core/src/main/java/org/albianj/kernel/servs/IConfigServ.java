package org.albianj.kernel.servs;

import org.albianj.kernel.attr.GlobalSettings;

public interface IConfigServ {
    String decideConfigFilename(GlobalSettings settings, String filenameWithoutSuffix);
}
