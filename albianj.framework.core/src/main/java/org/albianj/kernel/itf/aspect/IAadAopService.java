package org.albianj.kernel.itf.aspect;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public interface IAadAopService {
    void before(AadAspectContext ctx, Object service, Method method, Object[] args);

    void after(AadAspectContext ctx, Object service, Method method, Object rc, Throwable t, Object[] args);
}
