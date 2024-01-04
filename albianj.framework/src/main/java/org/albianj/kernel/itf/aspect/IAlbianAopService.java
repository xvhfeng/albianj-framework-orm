package org.albianj.kernel.itf.aspect;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public interface IAlbianAopService {
    void before(AlbianAspectContext ctx, Object service, Method method, Object[] args);

    void after(AlbianAspectContext ctx, Object service, Method method, Object rc, Throwable t, Object[] args);
}
