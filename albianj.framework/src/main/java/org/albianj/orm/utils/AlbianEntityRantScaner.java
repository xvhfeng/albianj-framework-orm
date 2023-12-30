package org.albianj.orm.utils;

import org.albianj.common.utils.CheckUtil;
import org.albianj.common.utils.ReflectUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.kit.logger.LogLevel;
import org.albianj.kernel.kit.logger.LogTarget;
import org.albianj.kernel.kit.service.AlbianServiceRouter;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.loader.AlbianClassScanner;
import org.albianj.loader.IAlbianClassExcavator;
import org.albianj.loader.IAlbianClassFilter;
import org.albianj.orm.anno.AlbianObjectDataFieldRant;
import org.albianj.orm.anno.AlbianObjectDataRouterRant;
import org.albianj.orm.anno.AlbianObjectDataRoutersRant;
import org.albianj.orm.anno.AlbianObjectRant;
import org.albianj.orm.attr.AlbianEntityFieldAttribute;
import org.albianj.orm.attr.AlbianObjectAttribute;
import org.albianj.orm.attr.DataRouterAttribute;
import org.albianj.orm.attr.DataRoutersAttribute;
import org.albianj.orm.bks.AlbianEntityMetadata;
import org.albianj.orm.impl.router.AlbianDataRouterParserService;
import org.albianj.orm.impl.storage.AlbianStorageParserService;
import org.albianj.orm.kit.object.IAlbianObject;
import org.albianj.orm.kit.object.IAlbianObjectDataRouter;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Types;
import java.util.*;

public class AlbianEntityRantScaner {

    public static HashMap<String, Object> scanPackage(final String pkgName) throws Throwable {
        return AlbianClassScanner.filter(AlbianClassLoader.getInstance(),
                pkgName,

                new IAlbianClassFilter() {
                    @Override
                    public boolean verify(Class<?> cls) {
                        //must flag with anno and extends IAlbianObject
                        // extends interface is compatibling the last version
                        return cls.isAnnotationPresent(AlbianObjectRant.class)
                                && IAlbianObject.class.isAssignableFrom(cls)
                                && !cls.isInterface()
                                && !Modifier.isAbstract(cls.getModifiers());
                    }
                },

                new IAlbianClassExcavator() {
                    @Override
                    public Object finder(Class<?> clzz)  {
                        AlbianObjectAttribute objAttr = null;
                        AlbianObjectRant or = clzz.getAnnotation(AlbianObjectRant.class);
                        if (null == or.Interface()) {
                            return null;
                        }

                        Class<?> itfClzz = or.Interface();
                        String sItf = itfClzz.getName();

                        if (AlbianEntityMetadata.exist(sItf)) {
                            objAttr = AlbianEntityMetadata.getEntityMetadata(sItf);
                        } else {
                            objAttr = new AlbianObjectAttribute();
                            objAttr.setType(clzz.getName());
                            objAttr.setInterfaceName(sItf);
                            AlbianEntityMetadata.put(sItf, objAttr);

                        }

                        objAttr.setImplClzz(clzz);

                        Map<String, AlbianEntityFieldAttribute> fields = scanFields(clzz);
                        if (!CheckUtil.isNullOrEmpty(fields)) {
                            objAttr.setFields(fields);
                        }

                        DataRouterAttribute defaultRouting = makeDefaultDataRouter(clzz);
                        objAttr.setDefaultRouting(defaultRouting);


                        AlbianObjectDataRoutersRant drr = or.DataRouters();
                        DataRoutersAttribute pkgDataRouterAttr = scanRouters(clzz, drr);
                        //set data router
                        if (null != pkgDataRouterAttr) {
                            DataRoutersAttribute cfgDataRouterAttr = objAttr.getDataRouters();
                            if (null == cfgDataRouterAttr) { // not exist data router from drouter.xml
                                objAttr.setDataRouters(pkgDataRouterAttr);
                            } else {
                                Map<String, DataRouterAttribute> cfgWRouter = cfgDataRouterAttr.getWriterRoutings();
                                Map<String, DataRouterAttribute> cfgRRouter = cfgDataRouterAttr.getReaderRoutings();
                                Map<String, DataRouterAttribute> pkgWRouter = pkgDataRouterAttr.getWriterRoutings();
                                Map<String, DataRouterAttribute> pkgRRouter = pkgDataRouterAttr.getReaderRoutings();
                                if (null != pkgRRouter) {
                                    if (null != cfgRRouter) {
                                        //exist pkg datarouter and cfg datarouter,merger them base cfg datarouter
                                        pkgRRouter.putAll(cfgRRouter);
                                    }
                                    //if not exist cfg drouter or memgered drouter,set to total drouter
                                    cfgDataRouterAttr.setReaderRoutings(pkgRRouter);

                                }

                                if (null != pkgWRouter) {
                                    if (null != cfgWRouter) {
                                        pkgWRouter.putAll(cfgWRouter);
                                    }
                                    cfgDataRouterAttr.setWriterRoutings(pkgRRouter);
                                }
                            }
                        }
                        return objAttr;
                    }
                });
    }

    private static DataRoutersAttribute scanRouters(Class<?> clzz, AlbianObjectDataRoutersRant drr)  {
        if (null == drr.EntitiyClass()) {
            return null;
        }

        Class<?> clazz = drr.EntitiyClass();

        if (!IAlbianObjectDataRouter.class.isAssignableFrom(clazz)) {
            // datarouter not impl IAlbianObjectDataRouter
            return null;
        }

        DataRoutersAttribute drsAttr = new DataRoutersAttribute();
        IAlbianObjectDataRouter dr = null;
        try {
            dr = (IAlbianObjectDataRouter) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
           AlbianServiceRouter.logAndThrowAgain(AlbianServiceRouter.__StartupSessionId, LogTarget.Running, LogLevel.Error,e,
                   "scanRouters is IllegalAccessException error ");
        }
        drsAttr.setDataRouter(dr);
        drsAttr.setReaderRouterEnable(drr.ReaderRoutersEnable());
        drsAttr.setWriterRouterEnable(drr.WriterRoutersEnable());

        Map<String, DataRouterAttribute> rMap = scanRouter(clzz, drr.ReaderRouters());
        drsAttr.setReaderRoutings(rMap);

        Map<String, DataRouterAttribute> wMap = scanRouter(clzz, drr.WriterRouters());
        drsAttr.setWriterRoutings(wMap);
        return drsAttr;

    }

    private static Map<String, DataRouterAttribute> scanRouter(Class<?> clzz, AlbianObjectDataRouterRant[] rrs) {
        Map<String, DataRouterAttribute> map = new HashMap<>();
        for (AlbianObjectDataRouterRant odrr : rrs) {
            if (odrr.Enable()) {
                DataRouterAttribute dra = new DataRouterAttribute();
                dra.setEnable(true);
                dra.setName(odrr.Name());
                dra.setStorageName(odrr.StorageName());

                if (!CheckUtil.isNullOrEmptyOrAllSpace(odrr.TableOwner())) {
                    dra.setOwner(odrr.TableOwner());
                }
                if (!CheckUtil.isNullOrEmptyOrAllSpace(odrr.TableName())) {
                    dra.setTableName(odrr.TableName());
                } else {
                    dra.setTableName(clzz.getSimpleName());
                }
                map.put(dra.getName(), dra);

            }
        }
        return map;
    }


    public static Map<String, AlbianEntityFieldAttribute> scanFields(Class<?> clzz) {

        Class tempClass = clzz;
        List<Field> fields = new ArrayList<>() ;
        while (tempClass !=null && !tempClass.getName().toLowerCase().equals("java.lang.object") ) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }

        Map<String, AlbianEntityFieldAttribute> fieldsAttrs = new HashMap<>();
        for (Field f : fields) {
            AlbianEntityFieldAttribute fAttr = null;
            if (f.isAnnotationPresent(AlbianObjectDataFieldRant.class)) {
                fAttr = new AlbianEntityFieldAttribute();
                AlbianObjectDataFieldRant fr = f.getAnnotation(AlbianObjectDataFieldRant.class);
                if (fr.Ignore()) {
                    continue;
                }
                fAttr.setName(f.getName());
                f.setAccessible(true);
                fAttr.setEntityField(f);
                String propertyName = null;
                if (CheckUtil.isNullOrEmptyOrAllSpace(fr.PropertyName())) {
                    propertyName = FieldConvert.fieldName2PropertyName(f.getName());
                    fAttr.setPropertyName(propertyName);
                } else {
                    propertyName = StringsUtil.lowercasingFirstLetter(fr.PropertyName());
                    fAttr.setPropertyName(propertyName);
                }


                if (CheckUtil.isNullOrEmptyOrAllSpace(fr.DbFieldName())) {
                    fAttr.setSqlFieldName(StringsUtil.uppercasingFirstLetter(propertyName));
                } else {
                    fAttr.setSqlFieldName(fr.DbFieldName());
                }


                fAttr.setAllowNull(fr.IsAllowNull());
                if (Types.OTHER == fr.DbType()) {
                    fAttr.setDatabaseType(Convert.toSqlType(f.getType()));
                } else {
                    fAttr.setDatabaseType(fr.DbType());
                }
                fAttr.setSave(fr.IsSave());
                fAttr.setLength(fr.Length());
                fAttr.setPrimaryKey(fr.IsPrimaryKey());
                fAttr.setAutoGenKey(fr.IsAutoGenKey());
                try {
                    PropertyDescriptor pd = ReflectUtil.getBeanPropertyDescriptor(clzz, propertyName);
                    if (null != pd) {
                        if (null != pd.getReadMethod()) {
                            fAttr.setPropertyGetter(pd.getReadMethod());
                        }
                        if (null != pd.getWriteMethod()) {
                            fAttr.setPropertySetter(pd.getWriteMethod());
                        }
                    }
                } catch (ClassNotFoundException | IntrospectionException e) {
                }

            } else {
                fAttr = new AlbianEntityFieldAttribute();
                f.setAccessible(true);
                fAttr.setName(f.getName());
                String propertyName = FieldConvert.fieldName2PropertyName(f.getName());
                fAttr.setPropertyName(propertyName);
                fAttr.setSqlFieldName(StringsUtil.uppercasingFirstLetter(propertyName));
                fAttr.setDatabaseType(Convert.toSqlType(f.getType()));
                fAttr.setEntityField(f);
                try {
                    PropertyDescriptor pd = ReflectUtil.getBeanPropertyDescriptor(clzz, propertyName);
                    if (null != pd) {
                        if (null != pd.getReadMethod()) {
                            fAttr.setPropertyGetter(pd.getReadMethod());
                        }
                        if (null != pd.getWriteMethod()) {
                            fAttr.setPropertySetter(pd.getWriteMethod());
                        }
                    }
                } catch (ClassNotFoundException | IntrospectionException e) {
                }
            }
            fieldsAttrs.put(fAttr.getPropertyName().toLowerCase(), fAttr);
        }
        return 0 == fieldsAttrs.size() ? null : fieldsAttrs;
    }


    private static DataRouterAttribute makeDefaultDataRouter(Class<?> implClzz) {
        DataRouterAttribute defaultRouting = new DataRouterAttribute();
        defaultRouting.setName(AlbianDataRouterParserService.DEFAULT_ROUTING_NAME);
        defaultRouting.setOwner("dbo");
        defaultRouting.setStorageName(AlbianStorageParserService.DEFAULT_STORAGE_NAME);
        defaultRouting.setTableName(implClzz.getSimpleName());
        return defaultRouting;
    }


}

