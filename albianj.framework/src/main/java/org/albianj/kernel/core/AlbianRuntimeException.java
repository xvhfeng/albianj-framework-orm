package org.albianj.kernel.core;

/**
 * 运行时异常,可处理也可以不处理
 * 当不处理的时候,最外层会直接抛出该异常,可能会导致线程/进程假死,退出等
 */
public class AlbianRuntimeException extends RuntimeException {

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

}
