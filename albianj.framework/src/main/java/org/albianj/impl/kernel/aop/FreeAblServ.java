package org.albianj.impl.kernel.aop;

import org.albianj.api.kernel.aop.AlbianAopContext;
import org.albianj.api.kernel.aop.IAblServProxy;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public abstract class FreeAblServ extends org.albianj.api.kernel.service.FreeAblServ implements IAblServProxy {

    public void before(AlbianAopContext ctx, IAblServProxy service, Method method, Object[] args) {
        return;
    }

    public void after(AlbianAopContext ctx, IAblServProxy service, Method method, Object rc, Throwable t, Object[] args) {
        return;
    }

}
