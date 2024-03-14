package org.albianj.impl.kernel.logger;

import org.albianj.AblThrowable;
import org.albianj.ServRouter;
import org.albianj.common.comment.Comments;
import org.albianj.common.utils.StringsUtil;
import org.albianj.kernel.core.StackFrame;
import org.albianj.kernel.logger.IAlbianLoggerService;
import org.albianj.kernel.logger.LogLevel;
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

    static {
        ServRouter._FilterStackFrameClasses.add(AlbianLoggerService.class.getName());
    }
    public String getServiceName() {
        return Name;
    }

    @Override
    public void log(Object sessionId, String logName, LogLevel level, String format, Object... paras) {
       String stackInfo =  new StackFrame().pickStackInfoWapper();
       String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(logName,level,msg,null);
    }

    @Override
    public void log(Object sessionId, String logName, LogLevel level, Throwable t, String format, Object... paras) {
        String stackInfo =  new StackFrame().pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(logName,level,msg,t);

    }

    @Override
    public void logAndThrowNew(Object sessionId, String logName, LogLevel level, String format, Object... paras) {
        String stackInfo =  new StackFrame().pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(logName,level,msg,null);
        throw new AblThrowable(msg);
    }


    @Override
    public void logAndThrowAgain(Object sessionId, String logName, LogLevel level, Throwable t, String format, Object... paras)  {
        String stackInfo = new StackFrame().pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(logName,level,msg,t);
        throw  new AblThrowable(t);
    }

    @Override
    public void logAndThrowNew(Object sessionId, String logName, LogLevel level, Throwable newThrow, String format, Object... paras) {
        String stackInfo = new StackFrame().pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(logName,level,msg,null);
        throw  new AblThrowable(newThrow);
    }

    @Override
    public void logAndThrowNew(Object sessionId, String logName, LogLevel level, Throwable newThrow, Throwable t, String format, Object... paras)  {
        String stackInfo = new StackFrame().pickStackInfoWapper();
        String msg = makeLogInfo(sessionId,stackInfo,format,paras);
        flushToFile(logName,level,msg,t);
        throw  new AblThrowable(newThrow);
    }

    @Override
    public void throwNew(Object sessionId,String logName, String format, Object... paras)  {
        String stackInfo = new StackFrame().pickStackInfoWapper();
        String msg = makeLogInfo(sessionId, stackInfo, format, paras);
        AblThrowable newThrow = new AblThrowable(msg);
        flushToFile(logName, LogLevel.Error, msg, null);
        throw newThrow;
    }

//    private Logger getLogger(String name) {
//        return LoggerFactory.getLogger(name);
//    }

//    private Logger getLogger(Class<?> clzz) {
//        return LoggerFactory.getLogger(clzz);
//    }

    private String makeLogInfo(Object sessionId,String stackInfo, String format, Object... values) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ticket: [");
        if (null != sessionId) {
            sb.append(sessionId).append(" | ");
        }
        sb.append(stackInfo).append("] ").append(" ==>> ");
        StringBuilder msg = new StringBuilder();
        Formatter f = new Formatter(msg);
        f.format(format, values);
        sb.append(msg);
        String fmt = sb.toString();
        return StringsUtil.nonIdxFmt(fmt,values);
    }

    private void flushToFile(String logName, LogLevel level, String ctx, Throwable e) {
        Logger logger = LoggerFactory.getLogger(logName);
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
