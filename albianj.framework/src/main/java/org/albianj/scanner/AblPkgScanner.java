package org.albianj.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AblPkgScanner {

    private static final Logger logger = LoggerFactory.getLogger(AblPkgScanner.class);

    private synchronized static void putClzzAttr( Map<String, Map<String, AblClassAttr>>  scanerClzzes,
                                                  AblClassAttr attr) {
        Map<String, AblClassAttr> map = null;
        String rantName = attr.getBelongAnno().getClass().getName();
        synchronized (scanerClzzes) {
            if (scanerClzzes.containsKey(rantName)) {
                map = scanerClzzes.get(rantName);
            } else {
                map = new LinkedHashMap<String, AblClassAttr>();
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
    public static Map<String, Map<String, AblClassAttr>> filter(ClassLoader classLoader,
                                                                List<String> pkgs,
                                                                IAblAnnoFilter filter,
                                                                Map<String, AnnoData> annos,
                                                                IAblAnnoParser parser)
            throws Throwable {
        Map<String, Map<String, AblClassAttr>>  scanerClzzes = new LinkedHashMap<>();
        for(String pkg : pkgs) {
            filter(classLoader,pkg,filter,annos,parser,scanerClzzes);
        }
        return scanerClzzes;
    }

    private static void filter(ClassLoader classLoader,
                              String pkg,
                              IAblAnnoFilter filter,
                               Map<String, AnnoData> annos,
                              IAblAnnoParser parser,
                               Map<String, Map<String, AblClassAttr>>  scanerClzzes)
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
                         isFilterChildDir, filter, annos, parser,scanerClzzes);
            } else if ("file".equals(protocol)) { // 解析文件
                String pkgPath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                findClzzInDir(classLoader, pkgName,
                        pkgPath, isFilterChildDir, filter, annos, parser,scanerClzzes);
            }
        }
    }

    /**
     * 解析jar协议的情况
     * 可能用不到
     * 方法是重构自动生成的，需要测试
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
                                  Map<String, AnnoData> annos,
                                  IAblAnnoParser parser,
                                  Map<String, Map<String, AblClassAttr>>  scanerClzzes)
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
                        loadClass(classLoader, pkgName, clzzSimpleName, filter, annos, parser,scanerClzzes);
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
                                     Map<String, AnnoData> annos,
                                     IAblAnnoParser parser,
                                     Map<String, Map<String, AblClassAttr>>  scanerClzzes)
            throws Throwable {
        File dir = new File(pkgPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return ((isFilterChildDir && file.isDirectory()) ||
                        (file.getName().endsWith(".class")));
            }
        });
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
                loadClass(classLoader, pkgName, clzzSimpleName, filter, annos, parser,scanerClzzes);
            }
        }
    }

    private static void loadClass(ClassLoader classLoader,
                                  String pkgName,
                                  String clzzSimpleName,
                                  IAblAnnoFilter filter,
                                  Map<String, AnnoData> annos,
                                  IAblAnnoParser parser,
                                  Map<String, Map<String, AblClassAttr>>  scanerClzzes)
            throws Throwable {
        String fullClassName = pkgName + '.' + clzzSimpleName;
        Class<?> cls = classLoader.loadClass(fullClassName);
        AblClassAttr attr = filter.found(cls, annos);
        if (null != attr) {
            AblClassAttr info = parser.parseBeanClass(attr);
            putClzzAttr(scanerClzzes,attr);
        }
    }
}
