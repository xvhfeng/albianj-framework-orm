package org.albianj.impl.dal.rant;

import org.albianj.ServRouter;
import org.albianj.api.dal.object.*;
import org.albianj.common.utils.ReflectUtil;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.impl.dal.routing.AlbianDataRouterParserService;
import org.albianj.impl.dal.storage.AlbianStorageParserService;
import org.albianj.impl.dal.toolkit.SqlTypeConv;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.loader.AlbianClassScanner;
import org.albianj.loader.IAlbianClassExcavator;
import org.albianj.loader.IAlbianClassFilter;
import org.albianj.api.dal.object.rants.AblEntityFieldRant;
import org.albianj.api.dal.object.rants.AblDrRant;
import org.albianj.api.dal.object.rants.AblDrsRant;
import org.albianj.api.dal.object.rants.AblObjRant;
import org.albianj.api.dal.service.AlbianEntityMetadata;

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
                        return cls.isAnnotationPresent(AblObjRant.class)
                                && IAblObj.class.isAssignableFrom(cls)
                                && !cls.isInterface()
                                && !Modifier.isAbstract(cls.getModifiers());
                    }
                },

                new IAlbianClassExcavator() {
                    @Override
                    public Object found(Class<?> clzz)  {
                        String implClzzName = clzz.getName();
                        AblEntityAttr objAttr = null;
                        AblObjRant or = clzz.getAnnotation(AblObjRant.class);
                        if (AlbianEntityMetadata.exist(implClzzName)) {
                            objAttr = AlbianEntityMetadata.getEntityMetadata(implClzzName);
                        } else {
                            objAttr = new AblEntityAttr();
                            objAttr.setType(clzz.getName());
                            AlbianEntityMetadata.put(implClzzName, objAttr);
                        }

                        objAttr.setImplClzz(clzz);

                        Map<String, AblEntityFieldAttr> fields = scanFields(clzz);
                        if (!SetUtil.isNullOrEmpty(fields)) {
                            objAttr.setFields(fields);
                        }

                        objAttr.setSqlFieldUseUnderline(or.SqlFieldUseUnderline());
                        objAttr.setTableNameUseUnderline(or.TableNameUseUnderline());

                        DrAttr defaultRouting = makeDefaultDataRouter(clzz);
                        objAttr.setDefaultRouting(defaultRouting);

//                        clzz.getAnnotation(AlbianObjectDataRoutersRant.class);
                        AblDrsRant drr =  clzz.getAnnotation(AblDrsRant.class);
                        DrsAttr pkgDataRouterAttr = scanRouters(clzz, drr);
                        //set data router
                        if (null != pkgDataRouterAttr) {
                            DrsAttr cfgDataRouterAttr = objAttr.getDataRouters();
                            if (null == cfgDataRouterAttr) { // not exist data router from drouter.xml
                                objAttr.setDataRouters(pkgDataRouterAttr);
                            } else {
                                Map<String, DrAttr> cfgWRouter = cfgDataRouterAttr.getWriterRouters();
                                Map<String, DrAttr> cfgRRouter = cfgDataRouterAttr.getReaderRouters();
                                Map<String, DrAttr> pkgWRouter = pkgDataRouterAttr.getWriterRouters();
                                Map<String, DrAttr> pkgRRouter = pkgDataRouterAttr.getReaderRouters();
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

    private static DrsAttr scanRouters(Class<?> clzz, AblDrsRant drr)  {
        if (null == drr) {
            return null;
        }

        Class<?> clazz = drr.DataRouter();

        if (clazz == null  || !IAblDr.class.isAssignableFrom(clazz)) {
            // datarouter not impl IAlbianObjectDataRouter
            return null;
        }

        DrsAttr drsAttr = new DrsAttr();
        IAblDr dr = null;
        try {
            dr = (IAblDr) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
           ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,  LogLevel.Error,e,
                   "scanRouters is IllegalAccessException error ");
        }
        drsAttr.setDataRouter(dr);
        drsAttr.setReaderRouterEnable(drr.ReaderRoutersEnable());
        drsAttr.setWriterRouterEnable(drr.WriterRoutersEnable());

        Map<String, DrAttr> rMap = scanRouter(clzz, drr.ReaderRouters());
        drsAttr.setReaderRouters(rMap);

        Map<String, DrAttr> wMap = scanRouter(clzz, drr.WriterRouters());
        drsAttr.setWriterRouters(wMap);
        return drsAttr;

    }

    private static Map<String, DrAttr> scanRouter(Class<?> clzz, AblDrRant[] rrs) {
        Map<String, DrAttr> map = new HashMap<>();
        for (AblDrRant odrr : rrs) {
            if (odrr.Enable()) {
                DrAttr dra = new DrAttr();
                dra.setEnable(true);
                dra.setName(odrr.Name());
                dra.setStorageName(odrr.StorageName());

                if (!StringsUtil.isNullEmptyTrimmed(odrr.TableOwner())) {
                    dra.setOwner(odrr.TableOwner());
                }
                if (!StringsUtil.isNullEmptyTrimmed(odrr.TableName())) {
                    dra.setTableName(odrr.TableName());
                } else {
                    dra.setTableName(clzz.getSimpleName());
                }
                map.put(dra.getName(), dra);

            }
        }
        return map;
    }


    public static Map<String, AblEntityFieldAttr> scanFields(Class<?> clzz) {

        Class tempClass = clzz;
        List<Field> fields = new ArrayList<>() ;
        while (tempClass !=null && !tempClass.getName().toLowerCase().equals("java.lang.object") ) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }

        Map<String, AblEntityFieldAttr> fieldsAttrs = new HashMap<>();
        for (Field f : fields) {
            AblEntityFieldAttr fAttr = null;
            if (f.isAnnotationPresent(AblEntityFieldRant.class)) {
                fAttr = new AblEntityFieldAttr();
                AblEntityFieldRant fr = f.getAnnotation(AblEntityFieldRant.class);
                if (fr.Ignore()) {
                    continue;
                }
                fAttr.setName(f.getName());
                f.setAccessible(true);
                fAttr.setEntityField(f);
                String propertyName = null;
                if (StringsUtil.isNullEmptyTrimmed(fr.PropertyName())) {
                    propertyName = FieldConvert.fieldName2PropertyName(f.getName());
                    fAttr.setPropertyName(propertyName);
                } else {
                    propertyName = StringsUtil.lowercasingFirstLetter(fr.PropertyName());
                    fAttr.setPropertyName(propertyName);
                }


                if (StringsUtil.isNullEmptyTrimmed(fr.FieldName())) {
                    fAttr.setSqlFieldName(StringsUtil.uppercasingFirstLetter(propertyName));
                } else {
                    fAttr.setSqlFieldName(fr.FieldName());
                }


                fAttr.setAllowNull(fr.IsAllowNull());
                if (Types.OTHER == fr.DbType()) {
                    fAttr.setDatabaseType(SqlTypeConv.toSqlType(f.getType()));
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
                    ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,e,
                            "class:{},field:{} cat not find getter/setter by name:{}.",
                            clzz.getName(),f.getName(),propertyName);
                }

            } else {
                fAttr = new AblEntityFieldAttr();
                f.setAccessible(true);
                fAttr.setName(f.getName());
                String propertyName = FieldConvert.fieldName2PropertyName(f.getName());
                fAttr.setPropertyName(propertyName);
                fAttr.setSqlFieldName(StringsUtil.uppercasingFirstLetter(propertyName));
                fAttr.setDatabaseType(SqlTypeConv.toSqlType(f.getType()));
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
                    ServRouter.log(ServRouter.__StartupSessionId,LogLevel.Error,e,
                            "class:{},field:{} cat not find getter/setter by name:{}.",
                            clzz.getName(),f.getName(),propertyName);
                }
            }
            AlbianEntityMetadata.putGetterLinkFieldAttr(clzz,fAttr);
            fieldsAttrs.put(fAttr.getPropertyName().toLowerCase(), fAttr);
        }
        return fieldsAttrs.isEmpty() ? null : fieldsAttrs;
    }


    private static DrAttr makeDefaultDataRouter(Class<?> implClzz) {
        DrAttr defaultRouting = new DrAttr();
        defaultRouting.setName(AlbianDataRouterParserService.DEFAULT_ROUTING_NAME);
        defaultRouting.setOwner("dbo");
        defaultRouting.setStorageName(AlbianStorageParserService.DEFAULT_STORAGE_NAME);
        defaultRouting.setTableName(implClzz.getSimpleName());
        return defaultRouting;
    }


}


