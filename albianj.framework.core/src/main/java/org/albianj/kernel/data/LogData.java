package org.albianj.kernel.data;

import org.albianj.kernel.itf.builtin.logger.LogLevel;
import org.albianj.kernel.itf.builtin.logger.LogTarget;

public class LogData {

    private boolean ifexp = true;
    private Object sessionId;
    private LogTarget target = LogTarget.Running;

    private LogLevel level = LogLevel.Info;

    private String format;

    private Object[] paras;

    private Throwable broken;

    private RuntimeException newThrow;

    private boolean isThrowNew = false;

    private String throwFormat;

    private Object[] throwParas;


    private StackTraceElement stack;


    public static LogData builder(Object sessionId,LogTarget target,LogLevel level) {
        LogData logData = new LogData();
        logData.sessionId = sessionId;
        logData.target = target;
        logData.level = level;
        logData.stack = Thread.currentThread().getStackTrace()[1];
        return logData;
    }

    public LogData ifExp(boolean ifexp){
        this.ifexp = ifexp;
        return this;
    }

    public LogData broken(Throwable broken){
        this.broken = broken;
        return this;
    }

    public LogData format(String format,Object... paras){
        this.format = format;
        if(null != paras) {
            this.paras = paras;
        }
        return this;
    }

    public LogData thenThrow(String format,Object... paras) {
        this.isThrowNew = true;
        this.throwFormat = format;
        this.throwParas = paras;
        return this;
    }

    public LogData thenThrow(RuntimeException newThrow) {
        this.isThrowNew = true;
        this.newThrow = newThrow;
        return this;
    }

    public LogData thenThrow() {
        this.isThrowNew = true;
        return this;
    }

    public boolean done(){
        return this.ifexp;
    }


}
