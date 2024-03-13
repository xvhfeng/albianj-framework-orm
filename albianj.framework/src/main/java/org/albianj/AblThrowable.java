package org.albianj;

public class AblThrowable extends RuntimeException{


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
