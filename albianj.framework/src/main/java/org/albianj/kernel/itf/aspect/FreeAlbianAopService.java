package org.albianj.kernel.itf.aspect;

import org.albianj.kernel.itf.service.FreeAlbianService;
import org.albianj.kernel.itf.service.IAlbianService;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public abstract class FreeAlbianAopService extends FreeAlbianService implements IAlbianAopService {

    public void before(AlbianAspectContext ctx, IAlbianService service, Method method, Object[] args) {
        return;
    }

    public void after(AlbianAspectContext ctx, IAlbianService service, Method method, Object rc, Throwable t, Object[] args) {
        return;
    }

}
