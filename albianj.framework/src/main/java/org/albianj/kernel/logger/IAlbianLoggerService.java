package org.albianj.kernel.logger;

import org.albianj.common.comment.Comments;
import org.albianj.AblBltinServsNames;
import org.albianj.kernel.service.IAlbianService;

/**
 * Created by xuhaifeng on 17/2/9.
 */
@Comments("logger service的v2版本，解决log-v1中无法正确标识文件位置问题")
public interface IAlbianLoggerService extends IAlbianService {

    @Comments("Albianj Logger Service2在server.xml中的标识")
    String Name = AblBltinServsNames.AlbianLoggerServiceName;

    void log(Object sessionId, String logName, LogLevel level, String format, Object... paras);

    void log(Object sessionId, String logName, LogLevel level, Throwable t, String format, Object... paras);

    void logAndThrowAgain(Object sessionId, String logName, LogLevel level, Throwable t, String format, Object... paras);

    void logAndThrowNew(Object sessionId, String logName, LogLevel level, String format, Object... paras) ;

    void logAndThrowNew(Object sessionId, String logName, LogLevel level, Throwable newThrow, String format, Object... paras);

    void logAndThrowNew(Object sessionId, String logName, LogLevel level, Throwable newThrow, Throwable t, String format, Object... paras);

    void throwNew(Object sessionId, String logName,String format, Object... paras);


//    void log(Object sessionId,Class<?> clzz, LogTarget target, LogLevel level, String format, Object... paras);
//
//    void log(Object sessionId,Class<?> clzz,  LogTarget target, LogLevel level, Throwable t, String format, Object... paras);
//
//    void logAndThrowAgain(Object sessionId,Class<?> clzz, LogTarget target, LogLevel level, Throwable t, String format, Object... paras);
//
//    void logAndThrowNew(Object sessionId,Class<?> clzz, LogTarget target, LogLevel level, String format, Object... paras) ;
//
//    void logAndThrowNew(Object sessionId,Class<?> clzz, LogTarget target, LogLevel level, Throwable newThrow, String format, Object... paras);
//
//    void logAndThrowNew(Object sessionId, Class<?> clzz,LogTarget target, LogLevel level, Throwable newThrow, Throwable t, String format, Object... paras);


//    AlbServRouter.trace(session,clzz,LogLevel.DEBUG).log("","").throw().done();
}
