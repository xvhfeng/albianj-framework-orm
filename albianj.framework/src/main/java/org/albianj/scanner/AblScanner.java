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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AblScanner {

    private static final Logger logger = LoggerFactory.getLogger(AblScanner.class);

    private synchronized static void putClzzAttr(Map<String, Map<String, ClassAttr>> scanerClzzes,
                                                 ClassAttr attr) {
        Map<String, ClassAttr> map = null;
        String rantName = attr.getBlgAnno().getClass().getName();
        synchronized (scanerClzzes) {
            if (scanerClzzes.containsKey(rantName)) {
                map = scanerClzzes.get(rantName);
            } else {
                map = new LinkedHashMap<String, ClassAttr>();
                scanerClzzes.put(rantName, map);
            }
            map.put(attr.getClzzFullName(), attr);
        }
    }

    /**
     * 根据指定的Annos过滤得到所有标注了Annos的类信息
     *
     * @param classLoader
     * @param pkgs
     * @param filter
     * @param parser
     * @return 按照每一个anno作为key，返回解析后的所有class的信息
     * Map：
     * key - anno的全名
     * value - Map类型，解析后的类信息
     * key - 解析的class全名
     * value - AblRantAttr，所有解析的结果，具体看AblRantAttr结构
     * @throws Throwable
     */
    public static Map<String, Map<String, ClassAttr>> filter(ClassLoader classLoader,
                                                             List<String> pkgs,
                                                             IAblAnnoFilter filter,
                                                             Map<String, AnnoParserAdpter> annos,
                                                             IAblAnnoResolver parser)
            throws Throwable {
        Map<String, Map<String, ClassAttr>> scanerClzzes = new LinkedHashMap<>();
        for (String pkg : pkgs) {
            filter(classLoader, pkg, filter, annos, parser, scanerClzzes);
        }
        return scanerClzzes;
    }

    private static void filter(ClassLoader classLoader,
                               String pkg,
                               IAblAnnoFilter filter,
                               Map<String, AnnoParserAdpter> annos,
                               IAblAnnoResolver parser,
                               Map<String, Map<String, ClassAttr>> scanerClzzes)
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
                        isFilterChildDir, filter, annos, parser, scanerClzzes);
            } else if ("file".equals(protocol)) { // 解析文件
                String pkgPath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                findClzzInDir(classLoader, pkgName,
                        pkgPath, isFilterChildDir, filter, annos, parser, scanerClzzes);
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
     * @param annos
     * @param parser
     * @return
     * @throws Throwable
     */
    private static String scanJar(ClassLoader classLoader,
                                  String pkgName,
                                  String pkgDir,
                                  URL url,
                                  boolean isFilterChildDir,
                                  IAblAnnoFilter filter,
                                  Map<String, AnnoParserAdpter> annos,
                                  IAblAnnoResolver parser,
                                  Map<String, Map<String, ClassAttr>> scanerClzzes)
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
                        parse(classLoader, pkgName, clzzSimpleName, filter, parser);
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
                                     IAblAnnoFilter filter,
                                     Map<String, AnnoParserAdpter> annos,
                                     IAblAnnoResolver parser,
                                     Map<String, Map<String, ClassAttr>> scanerClzzes)
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
                        annos,
                        parser,
                        scanerClzzes);
            } else {
                String clzzSimpleName = file.getName().substring(0,
                        file.getName().length() - 6); // 截断".class"后缀
                parse(classLoader, pkgName, clzzSimpleName, filter, parser);
            }
        }
    }

    private static ClassAttr parse(ClassLoader classLoader,
                                   String pkgName,
                                   String clzzSimpleName,
                                   IAblAnnoFilter filter,
                                   IAblAnnoResolver parser)
            throws Throwable {
        String fullClassName = pkgName + '.' + clzzSimpleName;
        Class<?> cls = classLoader.loadClass(fullClassName);
        Class<? extends Annotation> annoClzz = filter.found(cls);
        if (null == annoClzz) {
            return null;
        }
        return  parser.parseBeanClass(cls, annoClzz);
    }
}
