/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
package org.albianj.common.utils;

import org.albianj.ServRouter;
import org.albianj.common.langs.UtilClassLoader;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ReflectUtil {

    public static BeanInfo getBeanInfo(ClassLoader cl, String className)
            throws ClassNotFoundException, IntrospectionException {
        Class cls = cl.loadClass(className);
        BeanInfo info = Introspector.getBeanInfo(cls, Object.class);
        return info;
    }

    public static PropertyDescriptor[] getBeanPropertyDescriptor(ClassLoader cl, String className)
            throws ClassNotFoundException, IntrospectionException {
        BeanInfo beanInfo;
        beanInfo = getBeanInfo(cl, className);
        return beanInfo.getPropertyDescriptors();
    }

    public static PropertyDescriptor getBeanPropertyDescriptor(Class<?> clzz, String propertyName)
            throws ClassNotFoundException, IntrospectionException {
        String pName = StringsUtil.lowercasingFirstLetter(propertyName);
        PropertyDescriptor pd = new PropertyDescriptor(pName, clzz);
        return pd;
    }


    public static String getClassSimpleName(ClassLoader cl, String className)
            throws ClassNotFoundException {
        Class cls = cl.loadClass(className);
        return cls.getSimpleName();
    }

    public static String getClassName(Class<?> cls) {
        return cls.getName();
    }

    public static String getSimpleName(Class<?> cls) {
        return cls.getSimpleName();
    }

    public static <T> T newInstance(Class<T> cls, Class<?>[] parameterTypes, Object[] initArgs)
            throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Constructor<T> cons = null;
        if (null == parameterTypes || 0 == parameterTypes.length) {
            return cls.newInstance();
        }
        cons = cls.getConstructor(parameterTypes);
        T instance = cons.newInstance(initArgs);
        return instance;
    }

    /**
     * Extracts the enum constant of the specified enum class with the
     * specified name. The name must match exactly an identifier used
     * to declare an enum constant in the given class.
     *
     * @param clazz the {@code Class} object of the enum type from which
     *              to return a constant.
     * @param name  the name of the constant to return.
     * @return the enum constant of the specified enum type with the
     * specified name.
     * @throws IllegalArgumentException if the specified enum type has
     *                                  no constant with the specified name, or the specified
     *                                  class object does not represent an enum type.
     * @see {@link Enum#valueOf(Class, String)}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object getEnumConstant(Class<?> clazz, String name) {
        if (clazz == null || name == null || name.isEmpty()) {
            return null;
        }
        return Enum.valueOf((Class<Enum>) clazz, name);
    }

    /**
     * Returns a {@code Class} object that identifies the
     * declared class as a return type for the method represented by the given
     * {@code String name} parameter inside the invoked {@code Class<?> clazz} parameter.
     *
     * @param clazz the {@code Class} object whose declared methods to be
     *              checked for the wanted method name.
     * @param name  the method name as {@code String} to be
     *              compared with {@link Method#getName()}
     * @return the {@code Class} object representing the return type of the given method name.
     * @see {@link Class#getDeclaredMethods()}
     * @see {@link Method#getReturnType()}
     */
    public static Class<?> getMethodReturnType(Class<?> clazz, String name) {
        if (clazz == null || name == null || name.isEmpty()) {
            return null;
        }

        name = name.toLowerCase();
        Class<?> returnType = null;

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(name)) {
                returnType = method.getReturnType();
                break;
            }
        }

        return returnType;
    }

    /**
     * Returns a {@code Class} object that identifies the
     * declared class for the field represented by the given {@code String name} parameter inside
     * the invoked {@code Class<?> clazz} parameter.
     *
     * @param clazz the {@code Class} object whose declared fields to be
     *              checked for a certain field.
     * @param name  the field name as {@code String} to be
     *              compared with {@link Field#getName()}
     * @return the {@code Class} object representing the type of given field name.
     * @see {@link Class#getDeclaredFields()}
     * @see {@link Field#getType()}
     */
    public static Class<?> getFieldClass(Class<?> clazz, String name) {
        if (clazz == null || name == null || name.isEmpty()) {
            return null;
        }

        Class<?> propertyClass = null;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equalsIgnoreCase(name)) {
                propertyClass = field.getType();
                break;
            }
        }

        return propertyClass;
    }

    /**
     * Checks whether a {@code Constructor} object with no parameter types is specified
     * by the invoked {@code Class} object or not.
     *
     * @param clazz the {@code Class} object whose constructors are checked.
     * @return {@code true} if a {@code Constructor} object with no parameter types is specified.
     * @throws SecurityException If a security manager, <i>s</i> is present and any of the
     *                           following conditions is met:
     *                           <ul>
     *                           <li> invocation of
     *                           {@link SecurityManager
     *                           s.checkMemberAccess(this, Member.PUBLIC)} denies
     *                           access to the constructor
     *
     *                           <li> the caller's class loader is not the same as or an
     *                           ancestor of the class loader for the current class and
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           s.checkPackageAccess()} denies access to the package
     *                           of this class
     *                           </ul>
     * @see {@link Class#getConstructor(Class...)}
     */
    public static boolean hasDefaultConstructor(Class<?> clazz) throws SecurityException {
        Class<?>[] empty = {};
        try {
            clazz.getConstructor(empty);
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns an array of {@code Type} objects representing the actual type
     * arguments to this object.
     * If the returned value is null, then this object represents a non-parameterized
     * object.
     *
     * @param object the {@code object} whose type arguments are needed.
     * @return an array of {@code Type} objects representing the actual type
     * arguments to this object.
     * @see {@link Class#getGenericSuperclass()}
     * @see {@link ParameterizedType#getActualTypeArguments()}
     */
    public static Type[] getParameterizedTypes(Object object) {
        Type superclassType = object.getClass().getGenericSuperclass();
        if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
            return null;
        }

        return ((ParameterizedType) superclassType).getActualTypeArguments();
    }

    public static Type[] getParameterizedTypes(Class<?> clazz) {
        Type superclassType = clazz.getGenericSuperclass();
        if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
            return null;
        }

        return ((ParameterizedType) superclassType).getActualTypeArguments();
    }

    public static Class getGenericClass(ParameterizedType parameterizedType, int i) {
        Object genericClass = parameterizedType.getActualTypeArguments()[i];
        if (genericClass instanceof ParameterizedType) { // 处理多级泛型
            return (Class) ((ParameterizedType) genericClass).getRawType();
        } else if (genericClass instanceof GenericArrayType) { // 处理数组泛型
            return (Class) ((GenericArrayType) genericClass).getGenericComponentType();
        } else if (genericClass instanceof TypeVariable) { // 处理泛型擦拭对象
            return (Class) getClass(((TypeVariable) genericClass).getBounds()[0], 0);
        } else {
            return (Class) genericClass;
        }
    }

    public static Class getClass(Type type, int i) {
        if (type instanceof ParameterizedType) { // 处理泛型类型
            return getGenericClass((ParameterizedType) type, i);
        } else if (type instanceof TypeVariable) {
            return (Class) getClass(((TypeVariable) type).getBounds()[0], 0); // 处理泛型擦拭对象
        } else {// class本身也是type，强制转型
            return (Class) type;
        }
    }


    public static Object toRealObject(String type, String o) throws ParseException {

        if ("java.lang.string".equalsIgnoreCase(type)
                || "string".equalsIgnoreCase(type)) {
            return o;
        } else if (
                "java.math.bigdecimal".equalsIgnoreCase(type)
                        || "bigdecimal".equalsIgnoreCase(type)) {
            BigDecimal bd = new BigDecimal(o.toString());
            return bd;
        } else if ("java.lang.boolean".equalsIgnoreCase(type)
                || "boolean".equalsIgnoreCase(type)) {
            return Boolean.parseBoolean(o.toString());
        } else if ("java.lang.integer".equalsIgnoreCase(type)
                || "int".equalsIgnoreCase(type)) {
            return Integer.parseInt(o.toString());
        } else if ("java.lang.long".equalsIgnoreCase(type)
                || "long".equalsIgnoreCase(type)) {
            return Long.parseLong(o.toString());
        } else if (
                "java.math.biginteger".equalsIgnoreCase(type)
                        || "biginteger".equalsIgnoreCase(type)) {
            BigInteger bi = new BigInteger(o.toString());
            return bi;
        } else if ("java.lang.float".equalsIgnoreCase(type)
                || "float".equalsIgnoreCase(type)) {
            return Float.parseFloat(o.toString());
        } else if ("java.lang.double".equalsIgnoreCase(type)
                || "double".equalsIgnoreCase(type)) {
            return Double.parseDouble(o.toString());
        } else if ("java.sql.time".equalsIgnoreCase(type)) {
            Date d = null;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        DateTimeUtil.CHINESE_SIMPLE_FORMAT);
                d = dateFormat.parse(o.toString());
            } catch (Exception e) {
                d = null;
            }
            if (null == d) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            DateTimeUtil.CHINESE_FORMAT);
                    d = dateFormat.parse(o.toString());
                } catch (Exception e) {
                    throw e;
                }
            }
            ;
            return new java.sql.Date(d.getTime());
        } else if ("java.util.date".equalsIgnoreCase(type)) {
            Date d = null;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        DateTimeUtil.CHINESE_SIMPLE_FORMAT);
                d = dateFormat.parse(o.toString());
            } catch (Exception e) {
                d = null;
            }
            if (null == d) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            DateTimeUtil.CHINESE_FORMAT);
                    d = dateFormat.parse(o.toString());
                } catch (Exception e) {
                    throw e;
                }
            }
            return d;
        } else if ("java.text.simpledateformat".equalsIgnoreCase(type)) {
            return o;
        } else {
            return o;
        }
    }


    public static final Class<?>[] MAIN_PARM_TYPES = new Class[] {String[].class};


    public static Object invokestaticN(Class<?> class_, String name, Object[] args) {
        return invokeN(class_, name, null, args);
    }

    public static Object invoke(Class<?> class_, Object target, String name, Object arg1, Object arg2) {
        return invokeN(class_, name, target, new Object[] { arg1, arg2 });
    }

    public static Object invoke(Class<?> class_, Object target, String name, Object arg1, Object arg2, Object arg3) {
        return invokeN(class_, name, target, new Object[] { arg1, arg2, arg3 });
    }


    public static Object invokeN(Class<?> class_, String name, Object target, Object[] args) {
        Method meth = getMatchingMethod(class_, name, args);
        try {
            return meth.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.toString());
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof Error) throw (Error)t;
            if (t instanceof RuntimeException) throw (RuntimeException)t;
            t.printStackTrace();
            throw new RuntimeException(t.toString());
        }
    }


    public static Method getMatchingMethod(Class<?> class_, String name, Object[] args) {
        Method[] meths = class_.getMethods();
        for (Method meth : meths) {
            if (meth.getName().equals(name) && isCompatible(meth, args)) {
                return meth;
            }
        }
        return null;
    }

    private static boolean isCompatible(Method meth, Object[] args) {
        // ignore methods with overloading other than lengths
        return meth.getParameterTypes().length == args.length;
    }




    public static Object getStaticField(Class<?> class_, String name) {
        try {
            return class_.getField(name).get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("unimplemented");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("unimplemented");
        }
    }

    public static void runMainInSameVM(
            String classpath,
            String className,
            String[] args)
            throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, ClassNotFoundException {
        ServRouter.throwIaxIfNull(className, "class name");
        if (StringsUtil.isEmpty(classpath)) {
            Class<?> mainClass = Class.forName(className);
            runMainInSameVM(mainClass, args);
            return;
        }
        ArrayList<File> dirs = new ArrayList<>();
        ArrayList<File> libs = new ArrayList<>();
        ArrayList<URL> urls = new ArrayList<>();
        String[] entries = LangUtil.splitClasspath(classpath);
        for (String entry : entries) {
            URL url = makeURL(entry);
            if (null != url) {
                urls.add(url);
            }
            File file = new File(entry);
// tolerate bad entries b/c bootclasspath sometimes has them
//            if (!file.canRead()) {
//                throw new IllegalArgumentException("cannot read " + file);
//            }
            if (FileUtil.isZipFile(file)) {
                libs.add(file);
            } else if (file.isDirectory()) {
                dirs.add(file);
            } else {
                // not URL, zip, or dir - unsure what to do
            }
        }
        File[] dirRa = dirs.toArray(new File[0]);
        File[] libRa = libs.toArray(new File[0]);
        URL[] urlRa = urls.toArray(new URL[0]);
        runMainInSameVM(urlRa, libRa, dirRa, className, args);
    }

    public static void runMainInSameVM(
            URL[] urls,
            File[] libs,
            File[] dirs,
            String className,
            String[] args)
            throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, ClassNotFoundException {
        ServRouter.throwIaxIfNull(className, "class name");
        ServRouter.throwIaxIfNotAssignable(libs, File.class, "jars");
        ServRouter.throwIaxIfNotAssignable(dirs, File.class, "dirs");
        URL[] libUrls = FileUtil.getFileURLs(libs);
        if (!SetUtil.isNullOrEmpty(libUrls)) {
            if (!SetUtil.isNullOrEmpty(urls)) {
                URL[] temp = new URL[libUrls.length + urls.length];
                System.arraycopy(urls, 0, temp, 0, urls.length);
                System.arraycopy(urls, 0, temp, libUrls.length, urls.length);
                urls = temp;
            } else {
                urls = libUrls;
            }
        }
        UtilClassLoader loader = new UtilClassLoader(urls, dirs);
        Class<?> targetClass = null;
        try {
            targetClass = loader.loadClass(className);
        } catch (ClassNotFoundException e) {
            String s = "unable to load class " + className
                    + " using class loader " + loader;
            throw new ClassNotFoundException(s);
        }
        Method main = targetClass.getMethod("main", MAIN_PARM_TYPES);
        main.invoke(null, new Object[] { args });
    }

    public static void runMainInSameVM(Class<?> mainClass, String[] args) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        ServRouter.throwIaxIfNull(mainClass, "main class");
        Method main = mainClass.getMethod("main", MAIN_PARM_TYPES);
        main.invoke(null, new Object[] { args });
    }

    /** @return URL if the input is valid as such */
    private static URL makeURL(String s) {
        try {
            return new URL(s);
        } catch (Throwable t) {
            return null;
        }
    }

}


