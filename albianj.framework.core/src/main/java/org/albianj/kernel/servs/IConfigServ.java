package org.albianj.kernel.servs;

import org.albianj.kernel.attr.GlobalSettings;
import org.apache.commons.configuration2.CompositeConfiguration;

public interface IConfigServ {
    String decideConfigFilename(GlobalSettings settings, String filenameWithoutSuffix);

    CompositeConfiguration neatConfigurtion(String configFilename);
}
