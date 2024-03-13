package org.albianj.kernel.core;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.kernel.ServRouter;
import org.albianj.kernel.common.util.LangUtil;
import org.albianj.kernel.common.utils.StringsUtil;

@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class StackFrame {
    static  {
        ServRouter._filterStackFrameClasses.add(StackFrame.class.getName());
    }
    private String filename;

    private String className;

    private String methodName;

    private int line;

    public String pickStackInfoWapper(){
//        /*
//            0 - it is function self
//            1 - AlbianLoggerService2.logger function
//            2 - function of called AlbianLoggerService2.logger
//         */
//        StackTraceElement s = Thread.currentThread().getStackTrace()[2];
//        // filter,then called from AlbianServiceHub.log,then stacktrace will +1
//        if(s.getClassName().contentEquals("AlbianServiceHub") || s.getClassName().contentEquals("AlbianServiceRouter") ) {
//            s = Thread.currentThread().getStackTrace()[3];
//        }
        StackTraceElement s = LangUtil.findCalledStackFilter(1, ServRouter._filterStackFrameClasses);
        this.className = s.getClassName();
        this.line = s.getLineNumber();
        this.filename = s.getFileName();
        this.methodName = s.getMethodName();
//        StackFrame si = new StackFrame(s.getFileName(), s.getClassName(), s.getMethodName(), s.getLineNumber());
        return StringsUtil.nonIdxFmt("{}:{} {}@{}",this.filename,this.line,this.className,this.methodName);
    }
}
