package org.albianj.common.chars;


import org.albianj.common.utils.StringsUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static String compress(String str, String encoder) throws Exception {
        if (StringsUtil.isNullEmptyTrimmed(str)) {
            return str;
        }
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzip = new GZIPOutputStream(obj);
            gzip.write(str.getBytes(encoder));
        } catch (Throwable t) {

        } finally {
            if (null != obj) {
                obj.close();
            }
        }
        return obj.toString(encoder);
    }

    public static String decompress(String str, String encoder) throws Exception {
        if (StringsUtil.isNullEmptyTrimmed(str)) {
            return str;
        }
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes(encoder)));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, encoder));
        StringBuilder outStr = new StringBuilder();
        String line;
        while ((line = bf.readLine()) != null) {
            outStr.append(line);
        }
        return outStr.toString();
    }

    public static String compress(String str) throws Exception {
        return compress(str, "UTF-8");
    }

    public static String decompress(String str) throws Exception {
        return decompress(str, "UTF-8");
    }

    public static void zipFiles(String srcFilename, String destFilename) {
        Path sourceFile = Path.of(srcFilename); // 要压缩的文件路径
        Path zipFile = Path.of(destFilename); // 输出的zip文件路径

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            // 创建一个ZipEntry对象，代表要压缩的文件
            ZipEntry zipEntry = new ZipEntry(sourceFile.getFileName().toString());
            zipOutputStream.putNextEntry(zipEntry);

            // 读取源文件并写入到ZipOutputStream中
            try (FileInputStream fileInputStream = new FileInputStream(sourceFile.toFile())) {
                byte[] buffer = new byte[10 * 1024 * 1024];
                int length;
                while ((length = fileInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, length);
                }
            }

            zipOutputStream.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String compressStringBuffer(String str, String inEncoding) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(str.getBytes(inEncoding));
            return out.toString(inEncoding);
        }

    }

    public static String uncompressStringBuffer(String str, String outEncoding) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes(outEncoding));
             GZIPInputStream gunzip = new GZIPInputStream(in)
        ) {

            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(outEncoding);
        }
    }
}
