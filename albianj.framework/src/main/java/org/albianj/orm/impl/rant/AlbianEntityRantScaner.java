package org.albianj.orm.impl.rant;

import org.albianj.kernel.common.utils.SetUtil;
import org.albianj.kernel.common.utils.ReflectUtil;
import org.albianj.kernel.common.utils.StringsUtil;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.kernel.ServRouter;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.loader.AlbianClassScanner;
import org.albianj.loader.IAlbianClassExcavator;
import org.albianj.loader.IAlbianClassFilter;
import org.albianj.orm.impl.object.AlbianEntityFieldAttribute;
import org.albianj.orm.impl.object.AlbianObjectAttribute;
import org.albianj.orm.impl.object.DataRouterAttribute;
import org.albianj.orm.impl.routing.AlbianDataRouterParserService;
import org.albianj.orm.impl.storage.AlbianStorageParserService;
import org.albianj.orm.impl.toolkit.Convert;
import org.albianj.orm.object.*;
import org.albianj.orm.object.rants.AlbianObjectDataFieldRant;
import org.albianj.orm.object.rants.AlbianObjectDataRouterRant;
import org.albianj.orm.object.rants.AlbianObjectDataRoutersRant;
import org.albianj.orm.object.rants.AlbianObjectRant;
import org.albianj.orm.service.AlbianEntityMetadata;

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
                        IAlbianObjectAttribute objAttr = null;
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
                            objAttr.setInterface(sItf);
                            AlbianEntityMetadata.put(sItf, objAttr);

                        }

                        objAttr.setImplClzz(clzz);

                        Map<String, IAlbianEntityFieldAttribute> fields = scanFields(clzz);
                        if (!SetUtil.isNullOrEmpty(fields)) {
                            objAttr.setFields(fields);
                        }

                        IDataRouterAttribute defaultRouting = makeDefaultDataRouter(clzz);
                        objAttr.setDefaultRouting(defaultRouting);


                        AlbianObjectDataRoutersRant drr = or.DataRouters();
                        IDataRoutersAttribute pkgDataRouterAttr = scanRouters(clzz, drr);
                        //set data router
                        if (null != pkgDataRouterAttr) {
                            IDataRoutersAttribute cfgDataRouterAttr = objAttr.getDataRouters();
                            if (null == cfgDataRouterAttr) { // not exist data router from drouter.xml
                                objAttr.setDataRouters(pkgDataRouterAttr);
                            } else {
                                Map<String, IDataRouterAttribute> cfgWRouter = cfgDataRouterAttr.getWriterRouters();
                                Map<String, IDataRouterAttribute> cfgRRouter = cfgDataRouterAttr.getReaderRouters();
                                Map<String, IDataRouterAttribute> pkgWRouter = pkgDataRouterAttr.getWriterRouters();
                                Map<String, IDataRouterAttribute> pkgRRouter = pkgDataRouterAttr.getReaderRouters();
                                if (null != pkgRRouter) {
                                    if (null != cfgRRouter) {
                                        //exist pkg datarouter and cfg datarouter,merger them base cfg datarouter
                                        pkgRRouter.putAll(cfgRRouter);
                                    }
                                    //if not exist cfg drouter or memgered drouter,set to total drouter
                                    cfgDataRouterAttr.setReaderRouters(pkgRRouter);

                                }

                                if (null != pkgWRouter) {
                                    if (null != cfgWRouter) {
                                        pkgWRouter.putAll(cfgWRouter);
                                    }
                                    cfgDataRouterAttr.setWriterRouters(pkgRRouter);
                                }
                            }
                        }
                        return objAttr;
                    }
                });
    }

    private static IDataRoutersAttribute scanRouters(Class<?> clzz, AlbianObjectDataRoutersRant drr)  {
        if (null == drr.DataRouter()) {
            return null;
        }

        Class<?> clazz = drr.DataRouter();

        if (!IAlbianObjectDataRouter.class.isAssignableFrom(clazz)) {
            // datarouter not impl IAlbianObjectDataRouter
            return null;
        }

        IDataRoutersAttribute drsAttr = new DataRoutersAttribute();
        IAlbianObjectDataRouter dr = null;
        try {
            dr = (IAlbianObjectDataRouter) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
           ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,  LogLevel.Error,e,
                   "scanRouters is IllegalAccessException error ");
        }
        drsAttr.setDataRouter(dr);
        drsAttr.setReaderRouterEnable(drr.ReaderRoutersEnable());
        drsAttr.setWriterRouterEnable(drr.WriterRoutersEnable());

        Map<String, IDataRouterAttribute> rMap = scanRouter(clzz, drr.ReaderRouters());
        drsAttr.setReaderRouters(rMap);

        Map<String, IDataRouterAttribute> wMap = scanRouter(clzz, drr.WriterRouters());
        drsAttr.setWriterRouters(wMap);
        return drsAttr;

    }

    private static Map<String, IDataRouterAttribute> scanRouter(Class<?> clzz, AlbianObjectDataRouterRant[] rrs) {
        Map<String, IDataRouterAttribute> map = new HashMap<>();
        for (AlbianObjectDataRouterRant odrr : rrs) {
            if (odrr.Enable()) {
                IDataRouterAttribute dra = new DataRouterAttribute();
                dra.setEnable(true);
                dra.setName(odrr.Name());
                dra.setStorageName(odrr.StorageName());

                if (!StringsUtil.isNullOrEmptyOrAllSpace(odrr.TableOwner())) {
                    dra.setOwner(odrr.TableOwner());
                }
                if (!StringsUtil.isNullOrEmptyOrAllSpace(odrr.TableName())) {
                    dra.setTableName(odrr.TableName());
                } else {
                    dra.setTableName(clzz.getSimpleName());
                }
                map.put(dra.getName(), dra);

            }
        }
        return map;
    }


    public static Map<String, IAlbianEntityFieldAttribute> scanFields(Class<?> clzz) {

        Class tempClass = clzz;
        List<Field> fields = new ArrayList<>() ;
        while (tempClass !=null && !tempClass.getName().toLowerCase().equals("java.lang.object") ) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }

        Map<String, IAlbianEntityFieldAttribute> fieldsAttrs = new HashMap<>();
        for (Field f : fields) {
            IAlbianEntityFieldAttribute fAttr = null;
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
                if (StringsUtil.isNullOrEmptyOrAllSpace(fr.PropertyName())) {
                    propertyName = FieldConvert.fieldName2PropertyName(f.getName());
                    fAttr.setPropertyName(propertyName);
                } else {
                    propertyName = StringsUtil.lowercasingFirstLetter(fr.PropertyName());
                    fAttr.setPropertyName(propertyName);
                }


                if (StringsUtil.isNullOrEmptyOrAllSpace(fr.FieldName())) {
                    fAttr.setSqlFieldName(StringsUtil.uppercasingFirstLetter(propertyName));
                } else {
                    fAttr.setSqlFieldName(fr.FieldName());
                }


                fAttr.setAllowNull(fr.IsAllowNull());
                if (Types.OTHER == fr.DbType()) {
                    fAttr.setDatabaseType(Convert.toSqlType(f.getType()));
                } else {
                    fAttr.setDatabaseType(fr.DbType());
                }
                fAttr.setIsSave(fr.IsSave());
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


    private static IDataRouterAttribute makeDefaultDataRouter(Class<?> implClzz) {
        IDataRouterAttribute defaultRouting = new DataRouterAttribute();
        defaultRouting.setName(AlbianDataRouterParserService.DEFAULT_ROUTING_NAME);
        defaultRouting.setOwner("dbo");
        defaultRouting.setStorageName(AlbianStorageParserService.DEFAULT_STORAGE_NAME);
        defaultRouting.setTableName(implClzz.getSimpleName());
        return defaultRouting;
    }


}


