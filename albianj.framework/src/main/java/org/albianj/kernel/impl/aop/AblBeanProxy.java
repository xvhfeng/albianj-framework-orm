package org.albianj.kernel.impl.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.albianj.ServRouter;
import org.albianj.kernel.api.aop.AlbianAopContext;
import org.albianj.kernel.api.aop.IAlbianAopService;
import org.albianj.kernel.api.service.IAblServ;
import org.albianj.common.utils.SetUtil;
import org.albianj.kernel.api.anno.proxy.AlbianProxyIgnoreRant;
import org.albianj.kernel.api.attr.AlbianServiceAopAttribute;
import org.albianj.kernel.api.logger.LogLevel;
import org.albianj.kernel.api.service.IAlbianService;
import org.albianj.loader.AlbianClassLoader;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public class AblBeanProxy implements MethodInterceptor {

    IAlbianService _service = null;
    Map<String, AlbianServiceAopAttribute> _aopAttributes = null;
    String sessionId = null;


    public Object newInstance(String sessionId, IAlbianService service, Map<String, AlbianServiceAopAttribute> aopAttributes)  {
        this._service = service;
        this._aopAttributes = aopAttributes;
        try {
            Enhancer enhancer = new Enhancer();  //增强类
            //不同于JDK的动态代理。它不能在创建代理时传obj对 象，obj对象必须被CGLIB包来创建
            enhancer.setClassLoader(AlbianClassLoader.getInstance());

            enhancer.setSuperclass(this._service.getClass()); //设置被代理类字节码（obj将被代理类设置成父类；作为产生的代理的父类传进来的）。CGLIB依据字节码生成被代理类的子类
            enhancer.setCallback(this);    //设置回调函数，即一个方法拦截
            enhancer.setInterfaces(new Class[]{IAblServ.class});
            Object proxy = enhancer.create(); //创建代理类
            return proxy;
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(sessionId, LogLevel.Error,e,
                    "AlbianServiceAopProxy newInstance is error ");
        }
        return null;
    }


    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String mName = method.getName();
        if (mName.equals("hashCode")
                || mName.equals("toString")
                || mName.equals("equals")
                || mName.equals("clone")
                || mName.equals("finalize")) {
            Object rc = methodProxy.invokeSuper(proxy, args);
            return rc;
        }

        Method rm = this._service.getClass().getMethod(mName, method.getParameterTypes());
        AlbianProxyIgnoreRant attr = rm.getAnnotation(AlbianProxyIgnoreRant.class);
        if (null != attr && attr.ignore()) {
            Object rc = methodProxy.invokeSuper(proxy, args);
            return rc;
        }

        if (SetUtil.isEmpty(_aopAttributes)) {
            Object rc = methodProxy.invokeSuper(proxy, args);
            return rc;
        }

        AlbianAopContext ctx = new AlbianAopContext();

        Object rc = null;
        for (AlbianServiceAopAttribute asaa : _aopAttributes.values()) {
            IAlbianAopService aas = ServRouter.getService(sessionId,
                    IAlbianAopService.class, asaa.getServiceName(), false);
            if (null == aas) continue;

            if (asaa.matches(mName)) {
                try {
                    aas.before(ctx, _service, method, args);
                } catch (Throwable e) {
                    ServRouter.logAndThrowAgain(sessionId,LogLevel.Error,e,
                            "AOPService AlbianRuntime execute before method in the aop service:{} for real service:{} is fail.",
                            asaa.getServiceName(), this._service.getServiceName());
                }
            }
        }

        Throwable throwable = null;
        try {
            rc = methodProxy.invokeSuper(proxy, args);
        } catch (Throwable e) {
            throwable = e;
            ServRouter.log(sessionId,LogLevel.Error,e,
                    "AOPService AlbianRuntime exception in proxy service:{} method:{} ",
                    this._service.getServiceName(), mName);
        }

        for (AlbianServiceAopAttribute asaa : _aopAttributes.values()) {
            IAlbianAopService aas = ServRouter.getService(sessionId,
                    IAlbianAopService.class, asaa.getServiceName(), false);
            if (null == aas) continue;

            if (asaa.matches(mName)) {
                try {
                    aas.after(ctx, _service, method, rc, throwable, args);
                } catch (Throwable e) {
                    ServRouter.log(sessionId,LogLevel.Warn,e,
                            "AOPService AlbianRuntime exception in the after method in the aop service:{} for real service:{} is fail. ",
                            asaa.getServiceName(), this._service.getServiceName());
                }
            }
        }

        if (null != throwable) throw throwable;
        return rc;

    }
}