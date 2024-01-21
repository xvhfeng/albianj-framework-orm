package org.albianj.kernel.servs.impl;

import org.albianj.anno.AblConfigurationAnno;
import org.albianj.common.utils.NullValue;
import org.albianj.common.utils.ReflectUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.ServRouter;
import org.albianj.kernel.anno.AblAttrFieldAnno;
import org.albianj.kernel.anno.AblAttrFieldIgnoreAnno;
import org.albianj.kernel.anno.AblServAnno;
import org.albianj.kernel.attr.GlobalSettings;
import org.albianj.kernel.servs.IConfigServ;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@AblServAnno
public class ConfigServ implements IConfigServ {

    private static String[] supportedSuffix = {
            ".xml",
            ".properties",
            ".yaml",
            ".prop",
            ".yam",

    };

    @Override
    public String decideConfigFilename(GlobalSettings settings, String filenameWithoutSuffix) {
        String configPath = settings.getConfigPath();
        String filename = null;
        for (String suffix : supportedSuffix) {
            String fname = (StringsUtil.nonIdxFormat("{}{}{}{}", configPath, File.separator, filenameWithoutSuffix, suffix));
            if (new File(fname).exists()) {
                filename = fname;
                break;
            }
        }
        return filename;
    }

    /**
     * 统一规整配置文件
     *
     * @param configFilename 配置文件全路径
     * @return
     */
    @Override
    public CompositeConfiguration neatConfigurtion(String configFilename) {
        Configurations configs = new Configurations();
        CompositeConfiguration config = new CompositeConfiguration();
        try {
            // 根据文件扩展名选择解析器
            if (configFilename.endsWith(".xml")) {
                Configuration xmlConfig = configs.xml(configFilename);
                config.addConfiguration(xmlConfig);
            } else if (configFilename.endsWith(".yaml") || configFilename.endsWith(".yml")) {
                YAMLConfiguration yamlConfig = new YAMLConfiguration();
                yamlConfig.read(new FileInputStream(configFilename));
                config.addConfiguration(yamlConfig);
            } else if (configFilename.endsWith(".properties") || configFilename.endsWith(".prop")) {
                Configuration propConfig = configs.properties(configFilename);
                config.addConfiguration(propConfig);
            } else {
                ServRouter.throwIax("configFilename", configFilename);
            }

            return config;
        } catch (Throwable e) {
            ServRouter.throwAgain(StringsUtil.nonIdxFormat("error by neat config file -> {}", configFilename), e);
        }
        return null;
    }

//    public AblApplicationAnno decideApplicationAnno(Class<?> mainClzz){
//        return decideApplicationAnno(mainClzz,AblApplicationAnno.class);
//    }

    @Override
    public <T extends Annotation> T decideApplicationAnno(Class<?> mainClzz, Class<T> annoClzz) {
        T mainAnno = null;
        AblConfigurationAnno cfAnno = mainClzz.getAnnotation(AblConfigurationAnno.class);
        if (null != cfAnno) {
            Class<?> cfClazz = cfAnno.Class();
            if (NullValue.class != cfClazz) {
                mainAnno = cfClazz.getAnnotation(annoClzz);
            }
        }

        if (null != mainAnno) {
            return mainAnno;
        }

        mainAnno = mainClzz.getAnnotation(annoClzz);
        return mainAnno;
    }

    /**
     * 得到所有可能存在的anno的列表
     * 在list中，排序越前（index越小）的，优先级越低
     *
     * @param mainClzz
     * @param annoClzz
     * @param <T>
     * @return
     */
    @Override
    public <T extends Annotation> List<T> listApplicationAnno(Class<?> mainClzz, Class<T> annoClzz) {
        List<T> list = new ArrayList<>();

        T mainAnno = mainClzz.getAnnotation(annoClzz);
        if (null != mainAnno) {
            list.add(mainAnno);
        }

        AblConfigurationAnno cfAnno = mainClzz.getAnnotation(AblConfigurationAnno.class);
        if (null != cfAnno) {
            Class<?> cfClazz = cfAnno.Class();
            if (NullValue.class != cfClazz) {
                T appAnnoFromConfig = cfClazz.getAnnotation(annoClzz);
                if (null != appAnnoFromConfig) {
                    list.add(appAnnoFromConfig);
                }

            }
        }

        return list;
    }

    /**
     * 合并通属于一个class的两个对象的各字段的值
     * 一般内部使用在合并解析Anno，配置文件等后形成的attrs
     * 注意：
     *  已经规避了为null的情况
     *  不过当clzz没有field的时候，直接返回high对象，否则将会返回一个新的对象
     *
     * @param clzz
     * @param high
     * @param low
     * @return
     * @param <T>
     */
    @Override
    public <T> T mergeFieldsToNew(Class<? extends T> clzz, T high, T low) {
        Map<String,Field> fields = ReflectUtil.getAllFields(clzz);
        if(fields.isEmpty()) {
            return high;
        }

        T merge = null;
        try {
            for (Map.Entry<String,Field> kv : fields.entrySet()) {
                Field field = kv.getValue();

                if(field.isAnnotationPresent(AblAttrFieldIgnoreAnno.class)) {
                    continue;
                }

                if(!field.isAccessible()) {
                    field.setAccessible(true);
                }

                AblAttrFieldAnno fieldAnno = field.getAnnotation(AblAttrFieldAnno.class);

                Object highVal = null;
                if(null != high) {
                    highVal = field.get(high);
                }
                Object lowVal = null;
                if(null != low) {
                    lowVal = field.get(low);
                }

                Class<?> fieldClass = field.getType();
                boolean isCollection = fieldClass.isAssignableFrom(Collection.class);
                boolean isMap = fieldClass.isAssignableFrom(Map.class);

                if(isCollection) {
                    Collection<?> mergedCollection = mergeCollectionsToNew(fieldClass, (Collection<?>) highVal, (Collection<?>) lowVal);
                    field.set(merge, mergedCollection);
                } else if(isMap) {
                    Map<?, ?> mergedMap = mergeMapsToNew(fieldClass,(Map<?, ?>) highVal, (Map<?, ?>) lowVal);
                    field.set(merge, mergedMap);
                } else {
                    /**
                     * 必须所有的值的默认值为null,否则合并不太好判断
                     * 就算是使用默认值，也因为类型的未知问题比较难搞
                     */
                    if(null != lowVal && !lowVal.equals(fieldAnno.DefaultValue())) {
                        field.set(merge, lowVal);
                    }
                    if(null != highVal && !fieldAnno.DefaultValue().equals(highVal)) {
                        field.set(merge, highVal);
                    }
                }
            }
        } catch (Throwable t) {
            ServRouter.throwAgain(t);
        }
        return merge;
    }

    @Override
    public <T> T mergeFieldsTo(Class<? extends T> clzz, T merge, T high, T low) {
        Map<String,Field> fields = ReflectUtil.getAllFields(clzz);
        if(fields.isEmpty()) {
            return merge;
        }

        try {
            for (Map.Entry<String,Field> kv : fields.entrySet()) {
                Field field = kv.getValue();

                if(field.isAnnotationPresent(AblAttrFieldIgnoreAnno.class)) {
                    continue;
                }

                if(!field.isAccessible()) {
                    field.setAccessible(true);
                }

                AblAttrFieldAnno fieldAnno = field.getAnnotation(AblAttrFieldAnno.class);

                Object highVal = null;
                if(null != high) {
                    highVal = field.get(high);
                }
                Object lowVal = null;
                if(null != low) {
                    lowVal = field.get(low);
                }

                Class<?> fieldClass = field.getType();
                boolean isCollection = fieldClass.isAssignableFrom(Collection.class);
                boolean isMap = fieldClass.isAssignableFrom(Map.class);

                if(isCollection) {
                    Collection<?> mergedCollection = mergeCollectionsToNew(fieldClass, (Collection<?>) highVal, (Collection<?>) lowVal);
                    field.set(merge, mergedCollection);
                } else if(isMap) {
                    Map<?, ?> mergedMap = mergeMapsToNew(fieldClass,(Map<?, ?>) highVal, (Map<?, ?>) lowVal);
                    field.set(merge, mergedMap);
                } else {
                    /**
                     * 必须所有的值的默认值为null,否则合并不太好判断
                     * 就算是使用默认值，也因为类型的未知问题比较难搞
                     */
                    if(null != lowVal && !lowVal.equals(fieldAnno.DefaultValue())) {
                        field.set(merge, lowVal);
                    }
                    if(null != highVal && !fieldAnno.DefaultValue().equals(highVal)) {
                        field.set(merge, highVal);
                    }
                }
            }
        } catch (Throwable t) {
            ServRouter.throwAgain(t);
        }
        return merge;
    }

    private  Collection mergeCollectionsToNew(Class<?> clzz,Collection high, Collection low) {
        // 实现根据实际情况合并两个集合的逻辑，这里简单示范直接合并
        try {
            Collection mergedCollection = (Collection) clzz.getConstructor().newInstance();
            if(null != low) {
                mergedCollection.addAll(low);
            }
            if(null != high) {
                mergedCollection.addAll(high);
            }
            return mergedCollection;
        }catch (Throwable t){
            ServRouter.throwAgain(t);
        }
        return null;
    }

    private Map mergeMapsToNew(Class<?> clzz,Map high, Map low) {
        // 实现根据实际情况合并两个Map的逻辑，这里简单示范直接合并
        try {
            Map mergedMap = (Map) clzz.getConstructor().newInstance();
            if(null != low) {
                mergedMap.putAll(low);
            }
            if(null != high) {
                mergedMap.putAll(high);
            }
            return mergedMap;
        }catch (Throwable t){
            ServRouter.throwAgain(t);
        }
        return null;
    }
}



