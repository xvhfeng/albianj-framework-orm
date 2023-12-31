package org.albianj.kernel.kit.builtin.logger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class StackFrame {
    private String filename;

    private String className;

    private String methodName;

    private int line;

    public String pickStackInfoWapper(){
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
        this.className = s.getClassName();
        this.line = s.getLineNumber();
        this.filename = s.getFileName();
        this.methodName = s.getMethodName();
//        StackFrame si = new StackFrame(s.getFileName(), s.getClassName(), s.getMethodName(), s.getLineNumber());
        return this.toString();
    }
}
