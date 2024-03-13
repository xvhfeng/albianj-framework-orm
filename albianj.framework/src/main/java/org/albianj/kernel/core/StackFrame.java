package org.albianj.kernel.core;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.kernel.ServRouter;
import org.albianj.kernel.common.utils.LangUtil;
import org.albianj.kernel.common.utils.StringsUtil;

@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class StackFrame {
    static  {
        ServRouter._FilterStackFrameClasses.add(StackFrame.class.getName());
    }
    private String filename;

    private String className;

    private String methodName;

    private int line;

    public String pickStackInfoWapper(){
        StackTraceElement s = LangUtil.findCalledStackFilter(1, ServRouter._FilterStackFrameClasses);
        this.className = s.getClassName();
        this.line = s.getLineNumber();
        this.filename = s.getFileName();
        this.methodName = s.getMethodName();
        return StringsUtil.nonIdxFmt("{}:{} {}@{}",this.filename,this.line,this.className,this.methodName);
    }
}
