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
package org.albianj.kernel.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringsUtil extends StringUtils {
    /*
     * Turns a hex encoded string into a byte array. It is specifically meant to
     * "reverse" the toHex(byte[]) method.
     *
     * @param hex a hex encoded String to transform into a byte array.
     *
     * @return a byte array representing the hex String[
     */
    public static final byte[] decodeHex(String hex) {
        char[] chars = hex.toCharArray();
        byte[] bytes = new byte[chars.length / 2];
        int byteCount = 0;
        for (int i = 0; i < chars.length; i += 2) {
            int newByte = 0x00;
            newByte |= hexCharToByte(chars[i]);
            newByte <<= 4;
            newByte |= hexCharToByte(chars[i + 1]);
            bytes[byteCount] = (byte) newByte;
            byteCount++;
        }
        return bytes;
    }

    /**
     * Returns the the byte value of a hexadecmical char (0-f). It's assumed
     * that the hexidecimal chars are lower case as appropriate.
     *
     * @param ch a hexedicmal character (0-f)
     * @return the byte value of the character (0x00-0x0F)
     */
    private static final byte hexCharToByte(char ch) {
        switch (ch) {
            case '0':
                return 0x00;
            case '1':
                return 0x01;
            case '2':
                return 0x02;
            case '3':
                return 0x03;
            case '4':
                return 0x04;
            case '5':
                return 0x05;
            case '6':
                return 0x06;
            case '7':
                return 0x07;
            case '8':
                return 0x08;
            case '9':
                return 0x09;
            case 'a':
                return 0x0A;
            case 'b':
                return 0x0B;
            case 'c':
                return 0x0C;
            case 'd':
                return 0x0D;
            case 'e':
                return 0x0E;
            case 'f':
                return 0x0F;
        }
        return 0x00;
    }

    public static String padLeft(String s, int length) {
        if (null == s) {
            return null;
        }
        if (s.length() > length) {
            return s.substring(0, length);
        }
        byte[] bs = new byte[length];
        byte[] ss = s.getBytes();
        Arrays.fill(bs, (byte) (48 & 0xff));
        System.arraycopy(ss, 0, bs, length - ss.length, ss.length);
        return new String(bs);
    }

    public static String censoredZero(String s) {
        if (isNullOrEmptyOrAllSpace(s)) {
            return null;
        }
        int idx = s.lastIndexOf("0");
        if (-1 == idx) {
            return s;
        } else {
            return s.substring(idx);
        }
    }

    public static String captureName(String name) {
        char[] cs = name.toCharArray();
        if ('a' <= cs[0] && 'z' >= cs[0])
            cs[0] -= 32;
        return String.valueOf(cs);

    }

    public static String uppercasingFirstLetter(String name) {
        char[] cs = name.toCharArray();
        if ('a' <= cs[0] && 'z' >= cs[0])
            cs[0] -= 32;
        return String.valueOf(cs);

    }


    public static String lowercasingFirstLetter(String txt) {
        char[] cs = txt.toCharArray();
        if ('A' <= cs[0] && 'Z' >= cs[0])
            cs[0] += 32;
        return String.valueOf(cs);
    }

    /**
     *  String result = MessageFormat.format("At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.",planet, new Date(), event);
     * @param format
     * @param paras
     * @return
     */
    public static String fmt(String format, Object ...paras){
        if(null== paras || 0 == paras.length){
            return format;
        }
        return MessageFormat.format(format,paras);
    }

    public static  String nonIdxFmt(String formatTemplate,Object...objects){

        String regex = "\\{\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(formatTemplate);
        int i = 0;
        StringBuilder sb = new StringBuilder();
        while (matcher.find()){
            Object obj = objects[i];
            String replacement = java.util.regex.Matcher.quoteReplacement(String.valueOf(obj));
            matcher.appendReplacement(sb,replacement);
            i++;
        }
        matcher.appendTail(sb); // 必现补上这一句，否者后面的内容截断了
        return sb.toString();
    }


    public static String join(Object... args){
        if(null == args || 0 == args.length) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(Object arg : args){
            sb.append(arg);
        }
        return sb.toString();
    }

    // 自定义正则表达式
    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z0-9]");
    private static final char UNDERLINE = '_';

    public static String camelToUnderline(String str) {
        Matcher matcher = HUMP_PATTERN.matcher(str);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, UNDERLINE + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    public static String underlineToCamel(String param) {
        if (StringUtils.isBlank(param)) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = Character.toLowerCase(param.charAt(i));
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean isNullOrEmpty(String value) {
        return null == value || value.isEmpty();
    }

    public static boolean isNullOrEmptyOrAllSpace(String value) {
        return null == value || value.trim().isEmpty();
    }

    /** @return ((null == s) || (0 == s.trim().length())); */
    public static boolean isNullOrEmptyTrimmed(String s) {
    return ((null == s) || (0 == s.length())
    || (0 == s.trim().length()));
    }

    /** @return ((null == s) || (0 == s.length())); */
   public static boolean isEmpty(String s) {
       return ((null == s) || (0 == s.length()));
   }

    /**
     * Splits <code>text</code> at whitespace.
     *
     * @param text <code>String</code> to split.
     */
    public static String[] split(String text) {
        return strings(text).toArray(new String[0]);
    }

    /**
     * Splits <code>input</code> at commas, trimming any white space.
     *
     * @param input <code>String</code> to split.
     * @return List of String of elements.
     */
    public static List<String> commaSplit(String input) {
        return anySplit(input, ",");
    }

    /**
     * Splits <code>input</code>, removing delimiter and trimming any white space. Returns an empty collection if the input is null.
     * If delimiter is null or empty or if the input contains no delimiters, the input itself is returned after trimming white
     * space.
     *
     * @param input <code>String</code> to split.
     * @param delim <code>String</code> separators for input.
     * @return List of String of elements.
     */
    public static List<String> anySplit(String input, String delim) {
        if (null == input) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();

        if (isEmpty(delim) || (!input.contains(delim))) {
            result.add(input.trim());
        } else {
            StringTokenizer st = new StringTokenizer(input, delim);
            while (st.hasMoreTokens()) {
                result.add(st.nextToken().trim());
            }
        }
        return result;
    }

    /**
     * Splits strings into a <code>List</code> using a <code>StringTokenizer</code>.
     *
     * @param text <code>String</code> to split.
     */
    public static List<String> strings(String text) {
        if (isEmpty(text)) {
            return Collections.emptyList();
        }
        List<String> strings = new ArrayList<>();
        StringTokenizer tok = new StringTokenizer(text);
        while (tok.hasMoreTokens()) {
            strings.add(tok.nextToken());
        }
        return strings;
    }

    /** inefficient way to replace all instances of sought with replace */
    public static String replace(String in, String sought, String replace) {
        if (isEmpty(in) || isEmpty(sought)) {
            return in;
        }
        StringBuilder result = new StringBuilder();
        final int len = sought.length();
        int start = 0;
        int loc;
        while (-1 != (loc = in.indexOf(sought, start))) {
            result.append(in.substring(start, loc));
            if (!isEmpty(replace)) {
                result.append(replace);
            }
            start = loc + len;
        }
        result.append(in.substring(start));
        return result.toString();
    }

    /** render i right-justified with a given width less than about 40 */
    public static String toSizedString(long i, int width) {
        String result = "" + i;
        int size = result.length();
        if (width > size) {
            final String pad = "                                              ";
            final int padLength = pad.length();
            if (width > padLength) {
                width = padLength;
            }
            int topad = width - size;
            result = pad.substring(0, topad) + result;
        }
        return result;
    }

    /**
     * Trim ending lines from a StringBuffer, clipping to maxLines and further removing any number of trailing lines accepted by
     * checker.
     *
     * @param checker returns true if trailing line should be elided.
     * @param stack StringBuffer with lines to elide
     * @param maxLines int for maximum number of resulting lines
     */
    public static void elideEndingLines(StringChecker checker, StringBuffer stack, int maxLines) {
        if (null == checker || (null == stack) || (0 == stack.length())) {
            return;
        }
        final LinkedList<String> lines = new LinkedList<>();
        StringTokenizer st = new StringTokenizer(stack.toString(), "\n\r");
        while (st.hasMoreTokens() && (0 < --maxLines)) {
            lines.add(st.nextToken());
        }
        st = null;

        String line;
        int elided = 0;
        while (!lines.isEmpty()) {
            line = lines.getLast();
            if (!checker.acceptString(line)) {
                break;
            } else {
                elided++;
                lines.removeLast();
            }
        }
        if ((elided > 0) || (maxLines < 1)) {
            final int EOL_LEN = LangUtil.EOL.length();
            int totalLength = 0;
            while (!lines.isEmpty()) {
                totalLength += EOL_LEN + lines.getFirst().length();
                lines.removeFirst();
            }
            if (stack.length() > totalLength) {
                stack.setLength(totalLength);
                if (elided > 0) {
                    stack.append("    (... " + elided + " lines...)");
                }
            }
        }
    }

    /**
    * Select from input String[] based on suffix-matching
    * @param inputs String[] of input - null ignored
    * @param suffixes String[] of suffix selectors - null ignored
    * @param ignoreCase if true, ignore case
    * @return String[] of input that end with any input
    */
    public static String[] endsWith(String[] inputs, String[] suffixes,
    boolean ignoreCase) {
    if (SetUtil.isNullOrEmpty(inputs) || SetUtil.isNullOrEmpty(suffixes)) {
    return new String[0];
    }
    if (ignoreCase) {
    String[] temp = new String[suffixes.length];
    for (int i = 0; i < temp.length; i++) {
    String suff = suffixes[i];
    temp[i] = (null == suff ? null : suff.toLowerCase());
    }
    suffixes = temp;
    }
    ArrayList result = new ArrayList();
    for (int i = 0; i < inputs.length; i++) {
    String input = inputs[i];
    if (null == input) {
    continue;
    }
    if (!ignoreCase) {
    input = input.toLowerCase();
    }
    for (int j = 0; j < suffixes.length; j++) {
    String suffix = suffixes[j];
    if (null == suffix) {
    continue;
    }
    if (input.endsWith(suffix)) {
    result.add(input);
    break;
    }
    }
    }
    return (String[]) result.toArray(new String[0]);
    }

    /**
     * copy non-null two-dimensional String[][]
     *
     */
    public static String[][] copyStrings(String[][] in) {
        String[][] out = new String[in.length][];
        for (int i = 0; i < out.length; i++) {
            out[i] = new String[in[i].length];
            System.arraycopy(in[i], 0, out[i], 0, out[i].length);
        }
        return out;
    }

    /**
     * @param input ignored if null
     * @param sink the StringBuffer to add input to - return false if null
     * @param delimiter the String to append to input when added - ignored if empty
     * @return true if input + delimiter added to sink
     */
    static boolean addIfNotEmpty(String input, StringBuffer sink, String delimiter) {
        if (isEmpty(input) || (null == sink)) {
            return false;
        }
        sink.append(input);
        if (!isEmpty(delimiter)) {
            sink.append(delimiter);
        }
        return true;
    }

    /** clip StringBuffer to maximum number of lines */
    public  static String clipBuffer(StringBuffer buffer, int maxLines) {
    if ((null == buffer) || (1 > buffer.length())) return "";
    StringBuffer result = new StringBuffer();
    int j = 0;
    final int MAX = maxLines;
    final int N = buffer.length();
    for (int i = 0, srcBegin = 0; i < MAX; srcBegin += j) {
    // todo: replace with String variant if/since getting char?
    char[] chars = new char[128];
    int srcEnd = srcBegin+chars.length;
    if (srcEnd >= N) {
    srcEnd = N-1;
    }
    if (srcBegin == srcEnd) break;
    //log("srcBegin:" + srcBegin + ":srcEnd:" + srcEnd);
    buffer.getChars(srcBegin, srcEnd, chars, 0);
    for (j = 0; j < srcEnd-srcBegin/*chars.length*/; j++) {
    char c = chars[j];
    if (c == '\n') {
    i++;
    j++;
    break;
    }
    }
    try { result.append(chars, 0, j); }
    catch (Throwable t) { }
    }
    return result.toString();
    }

    /** check if input contains any packages to elide. */
    public static class StringChecker {
        public static StringChecker TEST_PACKAGES = new StringChecker(new String[] { "org.aspectj.testing",
                "org.eclipse.jdt.internal.junit", "junit.framework.",
        "org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner" });

        String[] infixes;

        /** @param infixes adopted */
        StringChecker(String[] infixes) {
            this.infixes = infixes;
        }

        /** @return true if input contains infixes */
        public boolean acceptString(String input) {
            boolean result = false;
            if (!isEmpty(input)) {
                for (int i = 0; !result && (i < infixes.length); i++) {
                    result = (input.contains(infixes[i]));
                }
            }
            return result;
        }
    }
}
