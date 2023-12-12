package org.albianj.except;

/**
 * 运行时异常,可处理也可以不处理
 * 当不处理的时候,最外层会直接抛出该异常,可能会导致线程/进程假死,退出等
 */
public class AlbianRuntimeException extends RuntimeException {

    private Throwable innerThrows = null;
    private String msg = null;

    public AlbianRuntimeException() {
        super();
    }

    public AlbianRuntimeException(String msg) {
        super(msg);
    }

    public AlbianRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public AlbianRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AlbianRuntimeException(String className, String methodName, int line, String msg) {
        String fmt = "%s:%d in %s throws -> %s.";
        this.msg = String.format(fmt, className, line, methodName, msg);
    }

    @Override
    public String toString() {
        return this.msg;
    }

    public String getMessage() {
        return this.msg;
    }

    public String getLocalizedMessage() {
        return this.msg;
    }

}
