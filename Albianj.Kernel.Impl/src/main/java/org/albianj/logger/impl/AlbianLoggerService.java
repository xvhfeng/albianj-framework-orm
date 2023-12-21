package org.albianj.logger.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.except.AlbianDisplayableException;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.LogLevel;
import org.albianj.logger.LogTarget;
import org.albianj.service.AlbianServiceRant;
import org.albianj.service.FreeAlbianService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Formatter;

/**
 * Created by xuhaifeng on 17/2/9.
 */
@org.albianj.comment.Comments("新的logger日志类，解决log-v1中无法正确标识文件位置问题")
@AlbianServiceRant(Id = IAlbianLoggerService.Name, Interface = IAlbianLoggerService.class)
public class AlbianLoggerService extends FreeAlbianService implements IAlbianLoggerService {

    public String getServiceName() {
        return Name;
    }

    @Data
    @AllArgsConstructor
    @ToString
    @NoArgsConstructor
    private static class StackFrame {
        private String filename;

        private String className;

        private String methodName;

        private int line;
    }

    @Override
    public void log(Object sessionId, LogTarget target, LogLevel level, String format, Object... paras) {
       String stackInfo =  pickStackInfoWapper();
       String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(target,level,msg,null);
    }

    @Override
    public void log(Object sessionId, LogTarget target, LogLevel level, Throwable t, String format, Object... paras) {
        String stackInfo =  pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(target,level,msg,t);

    }

    @Override
    public void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, String format, Object... paras) throws Throwable {
        String stackInfo =  pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(target,level,msg,null);
        throw new AlbianDisplayableException(msg);
    }


    @Override
    public void logAndThrowAgain(Object sessionId, LogTarget target, LogLevel level, Throwable t, String format, Object... paras) throws Throwable {
        String stackInfo =  pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(target,level,msg,t);
        throw t;
    }




    @Override
    public void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, Throwable newThrow, String format, Object... paras) throws Throwable {
        String stackInfo =  pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(target,level,msg,null);
        throw newThrow;
    }

    @Override
    public void logAndThrowNew(Object sessionId, LogTarget target, LogLevel level, Throwable newThrow, Throwable t, String format, Object... paras) throws Throwable {
        String stackInfo =  pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(target,level,msg,t);
        throw newThrow;
    }

    private Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    private String pickStackInfoWapper(){
        /*
            0 - it is function self
            1 - AlbianLoggerService2.logger function
            2 - function of called AlbianLoggerService2.logger
         */
        StackTraceElement s = Thread.currentThread().getStackTrace()[2];
        // filter,then called from AlbianServiceHub.log,then stacktrace will +1
        if(s.getClassName().contentEquals("AlbianServiceHub") || s.getClassName().contentEquals("AlbianServiceRouter") ) {
            s = Thread.currentThread().getStackTrace()[3];
        }
        StackFrame si = new StackFrame(s.getFileName(), s.getClassName(), s.getMethodName(), s.getLineNumber());
        return si.toString();
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
