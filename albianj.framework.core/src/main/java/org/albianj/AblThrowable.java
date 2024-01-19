package org.albianj;

/**
 * 运行时异常,可处理也可以不处理
 * 当不处理的时候,最外层会直接抛出该异常,可能会导致线程/进程假死,退出等
 */
public class AblThrowable extends RuntimeException {

    public AblThrowable() {
        super();
    }

    public AblThrowable(String msg) {
        super(msg);
    }

    public AblThrowable(Throwable throwable) {
        super(throwable);
    }

    public AblThrowable(String msg, Throwable cause) {
        super(msg, cause);
    }

}