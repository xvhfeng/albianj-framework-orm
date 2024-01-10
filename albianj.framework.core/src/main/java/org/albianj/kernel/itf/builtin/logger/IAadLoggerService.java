package org.albianj.kernel.itf.builtin.logger;

import org.albianj.common.anno.AblCommentAnno;

/**
 * Created by xuhaifeng on 17/2/9.
 */
@AblCommentAnno("logger service的v2版本，解决log-v1中无法正确标识文件位置问题")
public interface IAadLoggerService {

//    @AblCommentRant("Albianj Logger Service2在server.xml中的标识")
//    String Name = BuiltinServicesBkt.AlbianLoggerServiceName;

    void log(Object sessionId, LogTarget target, LogLevel level, String format, Object... paras);

    void log(Object sessionId, LogTarget target, LogLevel level, Throwable t, String format, Object... paras);

    void logAndThrowAgain(Object sessionId, LogTarget target, LogLevel level, Throwable t, String format, Object... paras);

    void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, String format, Object... paras) ;

    void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, Throwable newThrow, String format, Object... paras);

    void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, Throwable newThrow, Throwable t, String format, Object... paras);

    void throwNew(Object sessionId, String format, Object... paras);
}
