package org.albianj.kernel.servs;

import org.albianj.kernel.attr.GlobalSettings;
import org.apache.commons.configuration2.CompositeConfiguration;

import java.lang.annotation.Annotation;
import java.util.List;

public interface IConfigServ {
    String decideConfigFilename(GlobalSettings settings, String filenameWithoutSuffix);

    CompositeConfiguration neatConfigurtion(String configFilename);

    <T extends Annotation> T decideApplicationAnno(Class<?> mainClzz, Class<T> annoClzz);

    <T extends Annotation> List<T> listApplicationAnno(Class<?> mainClzz, Class<T> annoClzz);

    <T> T mergeFieldsToNew(Class<? extends T> clzz, T highLevel, T lowLevel);

    <T> T mergeFieldsTo(Class<? extends T> clzz, T merge, T high, T low);
}
