package org.albianj.impl.kernel.aop;

import org.albianj.api.kernel.aop.AlbianAopContext;
import org.albianj.api.kernel.aop.IAlbianAopService;
import org.albianj.api.kernel.service.FreeAlbianService;
import org.albianj.api.kernel.service.IAlbianService;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public abstract class FreeAlbianAopService extends FreeAlbianService implements IAlbianAopService {

    public void before(AlbianAopContext ctx, IAlbianService service, Method method, Object[] args) {
        return;
    }

    public void after(AlbianAopContext ctx, IAlbianService service, Method method, Object rc, Throwable t, Object[] args) {
        return;
    }

}
