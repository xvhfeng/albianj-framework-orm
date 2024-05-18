package org.albianj.api.kernel.aop;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public interface IAblServProxy extends org.albianj.api.kernel.service.IAblServ {
    void before(AlbianAopContext ctx, org.albianj.api.kernel.service.IAblServ service, Method method, Object[] args);

    void after(AlbianAopContext ctx, org.albianj.api.kernel.service.IAblServ service, Method method, Object rc, Throwable t, Object[] args);
}
