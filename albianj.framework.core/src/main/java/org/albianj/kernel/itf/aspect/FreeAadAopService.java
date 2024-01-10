package org.albianj.kernel.itf.aspect;

import org.albianj.kernel.itf.service.FreeAlbianService;
import org.albianj.kernel.itf.service.IAlbianService;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public abstract class FreeAadAopService extends FreeAlbianService implements IAadAopService {

    public void before(AadAspectContext ctx, IAlbianService service, Method method, Object[] args) {
        return;
    }

    public void after(AadAspectContext ctx, IAlbianService service, Method method, Object rc, Throwable t, Object[] args) {
        return;
    }

}
