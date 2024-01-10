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
package org.albianj.kernel;

import org.albianj.AblThrowable;
import org.albianj.common.apj.util.LangUtil;
import org.albianj.common.utils.CheckUtil;
import org.albianj.kernel.bkt.ServicesBkt;
import org.albianj.kernel.itf.builtin.logger.IAadLoggerService;
import org.albianj.kernel.itf.builtin.logger.LogLevel;
import org.albianj.kernel.itf.builtin.logger.LogTarget;
import org.albianj.kernel.starter.AlbianServiceCreator;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.security.PrivilegedActionException;
import java.sql.SQLException;
import java.util.UUID;

/**
 * albianj的service管理类，交由albianj托管的service全部由这个类提供获取service。
 */
public class ServRouter extends ServicesBkt {


    /**
     * 获取service.xml中配置的service.
     * 注意： 1：获取的service都是单例模式
     *
     * @param <T>                获取serivce的定义接口类
     * @param cla                获取serivce的定义接口类的class信息
     * @param id                 service。xml中配置的id
     * @param isThrowIfException 是否在获取service出错或者没有获取service时候抛出异常，true为抛出异常；false不抛出异常，但是service返回null
     * @return 返回获取的service
     * @throws IllegalArgumentException id在service.xml中找不到或者是获取的service不能转换陈cla提供的class信息，将抛出遗产
     */
    public static <T> T getService(Object sessionId,Class<T> cla, String id, boolean isThrowIfException) {
        String servId = id;
        if(CheckUtil.isNullOrEmptyOrAllSpace(servId)) {
            if(null == cla) {
                ServRouter.throwIfTrue(isThrowIfException,"Service Id is nullOrEmpty and interface is Null.");
                return null;
            }
            servId = cla.getName();
        }

        try {
            Object serv  =  getService(servId);

            if (null == serv)
                return null;
            return cla.cast(serv);
        } catch (Throwable exc) {
            log(sessionId, LogTarget.Running,LogLevel.Warn,exc,
                    "get service {} for class {} is fail.",id,cla.getName());
            throwAgainIfTrue(isThrowIfException,exc);
        }
        return null;
    }

    public static <T> T getService(Object sessionId,Class<T> cla, boolean isThrowIfException) {
        return getService(sessionId,cla,null,isThrowIfException);
    }

    /**
     * 获取service.xml中配置的service.
     * 注意： 1：获取的service都是单例模式
     *
     * @param <T> 获取serivce的定义接口类
     * @param cla 获取serivce的定义接口类的class信息
     * @param id  service。xml中配置的id
     * @return 返回获取的service，在获取service出错或者没有获取service时候抛出异常
     * @throws IllegalArgumentException id在service.xml中找不到或者是获取的service不能转换陈cla提供的class信息，将抛出遗产
     */
    public static <T> T getService(Object sessionId,Class<T> cla, String id) {
        return getService(sessionId,cla, id, false);
    }

    public static  <T> T getService(Object sessionId,Class<T> cla) {
        return getService(sessionId,cla,null,false);
    }


    public static <T> T newService(Object sessionId,Class<T> clzz){
        return newService(sessionId,clzz,false);
    }

    public static <T> T newService(Object sessionId,Class<T> clzz,boolean isThrowIfException){
        log(sessionId,LogTarget.Running,LogLevel.Warn,
                "Service:{} maybe exist,so you can use single-mode by getService() first.",clzz.getName());
        try {
            return clzz.cast(AlbianServiceCreator.newService(sessionId,clzz));
        }catch (Throwable t){
            throwAgainIfTrue(isThrowIfException,t);
        }
        return null;
    }

    public static <T> T newService(Object sessionId,String servId){
        return newService(sessionId,servId,false);
    }

    public static <T> T newService(Object sessionId,String servId,boolean isThrowIfException){
        log(sessionId,LogTarget.Running,LogLevel.Warn,
                "Service:{} maybe exist,so you can use single-mode by getService() first.",servId);
        try {
            return (T) (AlbianServiceCreator.newService(sessionId,servId));
        }catch (Throwable t){
            throwAgainIfTrue(isThrowIfException,t);
        }
        return null;
    }

    public static void log(Object sessionId, LogTarget target, LogLevel level, String format, Object... paras)  {
        IAadLoggerService ls = getService(sessionId, IAadLoggerService.class, IAadLoggerService.Name);
        if(null != ls) {
            ls.log(sessionId,target,level,format,paras);
        }
    }

    public static void log(Object sessionId, LogTarget target, LogLevel level, Throwable t, String format, Object... paras)  {
        IAadLoggerService ls = getService(sessionId, IAadLoggerService.class, IAadLoggerService.Name);
        if(null != ls) {
            ls.log(sessionId,target,level,t,format,paras);
        }
    }

    public static void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, String format, Object... paras)  {
        IAadLoggerService ls = getService(sessionId, IAadLoggerService.class, IAadLoggerService.Name);
        if(null != ls) {
            ls.logAndThrowNew(sessionId,target,level,format,paras);
        }
    }

    public static void logAndThrowAgain(Object sessionId, LogTarget target, LogLevel level, Throwable t, String format, Object... paras) {
        IAadLoggerService ls = getService(sessionId, IAadLoggerService.class, IAadLoggerService.Name);
        if(null != ls) {
            ls.logAndThrowAgain(sessionId,target,level,t,format,paras);
        }
    }

    public static void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, Throwable newThrow, String format, Object... paras) {
        IAadLoggerService ls = getService(sessionId, IAadLoggerService.class, IAadLoggerService.Name);
        if(null != ls) {
            ls.logAndThrowNew(sessionId,target,level,newThrow,format,paras);
        }
    }

    public static void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, Throwable t, Throwable newThrow, String format, Object... paras) {
        IAadLoggerService ls = getService(sessionId, IAadLoggerService.class, IAadLoggerService.Name);
        if(null != ls) {
            ls.logAndThrowNew(sessionId,target,level, newThrow, t, format,paras);
        }
    }

    public static void throwIfTrue(boolean cond,String msg){
        if(cond){
            throw new AblThrowable(msg);
        }
    }

    public static void throwIfFalse(boolean cond,String msg){
        if(cond){
            throw new AblThrowable(msg);
        }
    }

    public static void throwIfNull(Object v,String msg){
        if(null == v){
            throw new AblThrowable(msg);
        }
    }

    public static void throwIfNonNull(Object v,String msg){
        if(null != v){
            throw new AblThrowable(msg);
        }
    }

    public static void throwAgain(String msg){
            throw new AblThrowable(msg);
    }

    public static void throwAgain(Throwable t){
        throw new AblThrowable(t);
    }

    public static void throwAgainIfTrue(boolean cond,Throwable t){
        if(cond) {
            throw new AblThrowable(t);
        }
    }

    public static void throwAgain(String msg,Throwable t){
        throw new AblThrowable(msg,t);
    }

    public static void throwAgainIfTrue(boolean cond,String msg,Throwable t){
        if(cond) {
            throw new AblThrowable(msg,t);
        }
    }



    public static synchronized String make32UUID() {
        return UUID.randomUUID().randomUUID().toString().replaceAll("-", "");
    }

    public static synchronized BigInteger makeBatchId(){
        return new BigInteger(make32UUID());
    }

    /**
     * Shorthand for "if null, throw IllegalArgumentException"
     *
     * @throws IllegalArgumentException "null {name}" if o is null
     */
    public static void throwIaxIfNull(final Object o, final String name) {
        if (null == o) {
            String message = "null " + (null == name ? "input" : name);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Shorthand for "if not null or not assignable, throw IllegalArgumentException"
     *
     * @param c the Class to check - use null to ignore type check
     * @throws IllegalArgumentException "null {name}" if o is null
     */
    public static final void throwIaxIfNotAssignable(final Object ra[], final Class<?> c, final String name) {
        throwIaxIfNull(ra, name);
        String label = (null == name ? "input" : name);
        for (int i = 0; i < ra.length; i++) {
            if (null == ra[i]) {
                String m = " null " + label + "[" + i + "]";
                throw new IllegalArgumentException(m);
            } else if (null != c) {
                Class<?> actualClass = ra[i].getClass();
                if (!c.isAssignableFrom(actualClass)) {
                    String message = label + " not assignable to " + c.getName();
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }

    /**
     * Shorthand for "if not null or not assignable, throw IllegalArgumentException"
     *
     * @throws IllegalArgumentException "null {name}" if o is null
     */
    public static final void throwIaxIfNotAssignable(final Object o, final Class<?> c, final String name) {
        throwIaxIfNull(o, name);
        if (null != c) {
            Class<?> actualClass = o.getClass();
            if (!c.isAssignableFrom(actualClass)) {
                String message = name + " not assignable to " + c.getName();
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Shorthand for "if false, throw IllegalArgumentException"
     *
     * @throws IllegalArgumentException "{message}" if test is false
     */
    public static final void throwIaxIfFalse(final boolean test, final String message) {
        if (!test) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * @return "({UnqualifiedExceptionClass}) {message}"
     */
    public static String renderExceptionShort(Throwable e) {
        if (null == e) {
            return "(Throwable) null";
        }
        return "(" + LangUtil.unqualifiedClassName(e) + ") " + e.getMessage();
    }

    /**
     * Renders exception <code>t</code> after unwrapping and eliding any test packages.
     *
     * @param t <code>Throwable</code> to print.
     * @see LangUtil.StringChecker#TEST_PACKAGES
     */
    public static String renderException(Throwable t) {
        return renderException(t, true);
    }

    /**
     * Renders exception <code>t</code>, unwrapping, optionally eliding and limiting total number of lines.
     *
     * @param t <code>Throwable</code> to print.
     * @param elide true to limit to 100 lines and elide test packages
     * @see LangUtil.StringChecker#TEST_PACKAGES
     */
    public static String renderException(Throwable t, boolean elide) {
        if (null == t) {
            return "null throwable";
        }
        t = unwrapException(t);
        StringBuffer stack = stackToString(t, false);
        if (elide) {
            LangUtil.elideEndingLines(LangUtil.StringChecker.TEST_PACKAGES, stack, 100);
        }
        return stack.toString();
    }

    /** Dump message and stack to StringBuffer. */
    public static StringBuffer stackToString(Throwable throwable, boolean skipMessage) {
        if (null == throwable) {
            return new StringBuffer();
        }
        StringWriter buf = new StringWriter();
        PrintWriter writer = new PrintWriter(buf);
        if (!skipMessage) {
            writer.println(throwable.getMessage());
        }
        throwable.printStackTrace(writer);
        try {
            buf.close();
        } catch (IOException ioe) {
        } // ignored
        return buf.getBuffer();
    }

    /** @return Throwable input or tail of any wrapped exception chain */
    public static Throwable unwrapException(Throwable t) {
        Throwable current = t;
        Throwable next = null;
        while (current != null) {
            // Java 1.2 exceptions that carry exceptions
            if (current instanceof InvocationTargetException) {
                next = ((InvocationTargetException) current).getTargetException();
            } else if (current instanceof ClassNotFoundException) {
                next = ((ClassNotFoundException) current).getException();
            } else if (current instanceof ExceptionInInitializerError) {
                next = ((ExceptionInInitializerError) current).getException();
            } else if (current instanceof PrivilegedActionException) {
                next = ((PrivilegedActionException) current).getException();
            } else if (current instanceof SQLException) {
                next = ((SQLException) current).getNextException();
            }
            // ...getException():
            // javax.naming.event.NamingExceptionEvent
            // javax.naming.ldap.UnsolicitedNotification
            // javax.xml.parsers.FactoryConfigurationError
            // javax.xml.transform.TransformerFactoryConfigurationError
            // javax.xml.transform.TransformerException
            // org.xml.sax.SAXException
            // 1.4: Throwable.getCause
            // java.util.logging.LogRecord.getThrown()
            if (null == next) {
                break;
            } else {
                current = next;
                next = null;
            }
        }
        return current;
    }
}
