package org.albianj.kernel.kit.aop;

import org.albianj.kernel.kit.service.FreeAlbianService;
import org.albianj.kernel.kit.service.IAlbianService;

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
