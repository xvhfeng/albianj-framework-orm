package org.albianj.kernel.impl.logger;

import org.albianj.common.comment.Comments;
import org.albianj.kernel.AlbianRuntimeException;
import org.albianj.kernel.logger.IAlbianLoggerService;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.kernel.logger.LogTarget;
import org.albianj.kernel.logger.StackFrame;
import org.albianj.kernel.service.AlbianServiceRant;
import org.albianj.kernel.service.FreeAlbianService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Formatter;

/**
 * Created by xuhaifeng on 17/2/9.
 */
@Comments("新的logger日志类，解决log-v1中无法正确标识文件位置问题")
@AlbianServiceRant(Id = IAlbianLoggerService.Name, Interface = IAlbianLoggerService.class)
public class AlbianLoggerService extends FreeAlbianService implements IAlbianLoggerService {

    public String getServiceName() {
        return Name;
    }

    @Override
    public void log(Object sessionId, LogTarget target, LogLevel level, String format, Object... paras) {
       String stackInfo =  new StackFrame().pickStackInfoWapper();
       String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(target,level,msg,null);
    }

    @Override
    public void log(Object sessionId, LogTarget target, LogLevel level, Throwable t, String format, Object... paras) {
        String stackInfo =  new StackFrame().pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(target,level,msg,t);

    }

    @Override
    public void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, String format, Object... paras) {
        String stackInfo =  new StackFrame().pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(target,level,msg,null);
        throw new AlbianRuntimeException(msg);
    }


    @Override
    public void logAndThrowAgain(Object sessionId, LogTarget target, LogLevel level, Throwable t, String format, Object... paras)  {
        String stackInfo = new StackFrame().pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(target,level,msg,t);
        throw  new AlbianRuntimeException(t);
    }

    @Override
    public void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, Throwable newThrow, String format, Object... paras) {
        String stackInfo = new StackFrame().pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(target,level,msg,null);
        throw  new AlbianRuntimeException(newThrow);
    }

    @Override
    public void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, Throwable newThrow, Throwable t, String format, Object... paras)  {
        String stackInfo = new StackFrame().pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(target,level,msg,t);
        throw  new AlbianRuntimeException(newThrow);
    }

    @Override
    public void throwNew(Object sessionId, String format, Object... paras)  {
        String stackInfo = new StackFrame().pickStackInfoWapper();
        String msg = makeLogInfo(sessionId, stackInfo, format, paras);
        AlbianRuntimeException newThrow = new AlbianRuntimeException(msg);
        flushToFile(LogTarget.Running, LogLevel.Error, msg, null);
        throw newThrow;
    }

    private Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    private String makeLogInfo(Object sessionId,String stackInfo, String format, Object... values) {
        StringBuilder sb = new StringBuilder();
        if (null != sessionId) {
            sb.append("SessionId: [").append(sessionId).append("], ");
        }
        sb.append(stackInfo).append(", ");
        sb.append("Message: ");
        StringBuilder msg = new StringBuilder();
        Formatter f = new Formatter(msg);
        f.format(format, values);
        sb.append(msg);
        return sb.toString();
    }

    private void flushToFile(LogTarget target, LogLevel level, String ctx, Throwable e) {
        Logger logger = getLogger(target.getName());
        switch (level) {
            case Debug:
                if (logger.isDebugEnabled()) {
                    logger.debug(ctx,e);
                    return;
                }
            case Info:
                if (logger.isInfoEnabled()) {
                    logger.info(ctx,e);
                    return;
                }
            case Warn:
                if (logger.isWarnEnabled()) {
                    logger.warn(ctx,e);
                    return;
                }
            case Error:
                if (logger.isErrorEnabled()) {
                    logger.error(ctx,e);
                    return;
                }
            case Mark:
                if (logger.isTraceEnabled()) {
                    logger.trace(ctx,e);
                    return;
                }
            default:
                if (logger.isInfoEnabled()) {
                    logger.info(ctx,e);
                    return;
                }
        }
    }
}
