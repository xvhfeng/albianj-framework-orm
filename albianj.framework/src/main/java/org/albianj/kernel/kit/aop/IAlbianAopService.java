package org.albianj.kernel.kit.aop;

import org.albianj.kernel.kit.service.IAlbianService;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public interface IAlbianAopService extends IAlbianService {
    void before(AlbianAopContext ctx, IAlbianService service, Method method, Object[] args);

    void after(AlbianAopContext ctx, IAlbianService service, Method method, Object rc, Throwable t, Object[] args);
}
