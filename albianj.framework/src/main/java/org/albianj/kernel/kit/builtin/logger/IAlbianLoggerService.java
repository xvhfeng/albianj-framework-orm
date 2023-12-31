package org.albianj.kernel.kit.logger;

import org.albianj.common.anno.AlbianCommentRant;
import org.albianj.kernel.bkt.AlbianBuiltinServicesBkt;
import org.albianj.kernel.kit.service.IAlbianService;

/**
 * Created by xuhaifeng on 17/2/9.
 */
@AlbianCommentRant("logger service的v2版本，解决log-v1中无法正确标识文件位置问题")
public interface IAlbianLoggerService extends IAlbianService {

    @AlbianCommentRant("Albianj Logger Service2在server.xml中的标识")
    String Name = AlbianBuiltinServicesBkt.AlbianLoggerServiceName;

    void log(Object sessionId, LogTarget target, LogLevel level, String format, Object... paras);

    void log(Object sessionId, LogTarget target, LogLevel level, Throwable t, String format, Object... paras);

    void logAndThrowAgain(Object sessionId, LogTarget target, LogLevel level, Throwable t, String format, Object... paras);

    void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, String format, Object... paras) ;

    void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, Throwable newThrow, String format, Object... paras);

    void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, Throwable newThrow, Throwable t, String format, Object... paras);

    void throwNew(Object sessionId, String format, Object... paras);
}
