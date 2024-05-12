package org.albianj.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * 遍历所有的pkg，根据指定的filter过滤出所需要的类，且根据resolver对过滤后的类进行解析
 *
 *  不处理解析后的数据是为了plugin的设计：
 *    一个Plugin需要扫描的Anno不仅仅是唯一的一个，并且数据保存的形式也不具有固定的格式，有时需要使用Map保存
 *    有时需要使用List或者Set等数据结构保存，更甚有需要自定义的数据结构，故这部分不能固定。
 *
 *   scanner只做对于pkg的遍历与解析调用工作，不做具体的识别（filter）和解析（resolver）工作。
 *
 * 注意：
 *   所有的filter过滤自行定义，包括过滤的Anno等，scanner将不会做任何的限定，resolver同理
 */
public class AblScanner {

    private static final Logger logger = LoggerFactory.getLogger(AblScanner.class);

    /**
     * 扫描给定的所有包
     *
     * @param classLoader
     * @param pkgs
     * @param filter
     * @param resolver
     * @return 函数没有返回值。
     *          filter作为中间的过程将返回找到的Anno（由调用者指定），若未找到特定的Anno返回NULL；
     *          程序对于具有返回值的class执行resolver，否则直接过
     *          resolver的结果集调用者自行解决，推荐分门别类的使用cached模式进行保存
     * Map：
     * key - anno的全名
     * value - Map类型，解析后的类信息
     * key - 解析的class全名
     * value - AblRantAttr，所有解析的结果，具体看AblRantAttr结构
     * @throws Throwable
     */
    public static void scan(ClassLoader classLoader,
                             List<String> pkgs,
                             IAblAnnoFinder filter,
                             IAblAnnoResolver resolver)
            throws Throwable {
        for (String pkg : pkgs) {
            filter(classLoader, pkg, filter, resolver);
        }
    }

    private static void filter(ClassLoader classLoader,
                               String pkg,
                               IAblAnnoFinder filter,
                               IAblAnnoResolver resolver)
            throws Throwable {
        // 是否循环迭代
        boolean isFilterChildDir = true;
        String pkgName = pkg;
        String pkgDir = pkgName.replace('.', '/');
        Enumeration<URL> dirs;
        dirs = classLoader.getResources(pkgDir);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            if ("jar".equals(protocol)) { // 解析jar文件
                pkgName = scanJar(classLoader, pkgName, pkgDir, url,
                        isFilterChildDir, filter, resolver);
            } else if ("file".equals(protocol)) { // 解析文件
                String pkgPath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                findClzzInDir(classLoader, pkgName,
                        pkgPath, isFilterChildDir, filter, resolver);
            }
        }
    }

    /**
     * 解析jar协议的情况
     * 可能用不到
     * 方法是重构自动生成的，需要测试
     *
     * @param classLoader
     * @param pkgName
     * @param pkgDir
     * @param url
     * @param isFilterChildDir
     * @param filter
     * @param resolver
     * @return
     * @throws Throwable
     */
    private static String scanJar(ClassLoader classLoader,
                                  String pkgName,
                                  String pkgDir,
                                  URL url,
                                  boolean isFilterChildDir,
                                  IAblAnnoFinder filter,
                                  IAblAnnoResolver resolver)
            throws Throwable {
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if (name.startsWith(pkgDir)) {
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    pkgName = name.substring(0, idx).replace('/', '.');
                }
                if ((idx != -1) || isFilterChildDir) {
                    if (name.endsWith(".class") && !entry.isDirectory()) {
                        String clzzSimpleName = name.substring(
                                pkgName.length() + 1, name.length() - 6);
                        parse(classLoader, pkgName, clzzSimpleName, filter, resolver);
                    }
                }
            }
        }
        return pkgName;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param pkgName
     * @param pkgPath
     * @param isFilterChildDir
     */
    public static void findClzzInDir(ClassLoader classLoader,
                                     String pkgName,
                                     String pkgPath,
                                     final boolean isFilterChildDir,
                                     IAblAnnoFinder filter,
                                     IAblAnnoResolver resolver)
            throws Throwable {
        File dir = new File(pkgPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] files = dir.listFiles(
                file ->
                        ((isFilterChildDir && file.isDirectory())
                                || (file.getName().endsWith(".class")))
        );
        if (null == files) {
            // 空文件夹，里面没有class文件也没有子文件夹
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                findClzzInDir(classLoader,
                        pkgName + "." + file.getName(),
                        file.getAbsolutePath(),
                        isFilterChildDir,
                        filter,
                        resolver);
            } else {
                String clzzSimpleName = file.getName().substring(0,
                        file.getName().length() - 6); // 截断".class"后缀
                parse(classLoader, pkgName, clzzSimpleName, filter, resolver);
            }
        }
    }

    private static void parse(ClassLoader classLoader,
                                   String pkgName,
                                   String clzzSimpleName,
                                   IAblAnnoFinder filter,
                                   IAblAnnoResolver resolver)
            throws Throwable {
        String fullClassName = pkgName + '.' + clzzSimpleName;
        Class<?> cls = classLoader.loadClass(fullClassName);
        Class<?> annoClzz = filter.found(cls);
        if (null == annoClzz) {
            return;
        }
        resolver.parse(cls, annoClzz);
    }
}
