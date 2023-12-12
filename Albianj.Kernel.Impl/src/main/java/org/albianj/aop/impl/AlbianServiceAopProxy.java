package org.albianj.aop.impl;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.albianj.aop.*;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.IAlbianService;
import org.albianj.verify.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public class AlbianServiceAopProxy implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AlbianServiceAopProxy.class);
    IAlbianService _service = null;
    Map<String, IAlbianServiceAopAttribute> _aopAttributes = null;


    public Object newInstance(IAlbianService service, Map<String, IAlbianServiceAopAttribute> aopAttributes) {
        this._service = service;
        this._aopAttributes = aopAttributes;
        try {
            Enhancer enhancer = new Enhancer();  //增强类
            //不同于JDK的动态代理。它不能在创建代理时传obj对 象，obj对象必须被CGLIB包来创建
            enhancer.setClassLoader(AlbianClassLoader.getInstance());

            enhancer.setSuperclass(this._service.getClass()); //设置被代理类字节码（obj将被代理类设置成父类；作为产生的代理的父类传进来的）。CGLIB依据字节码生成被代理类的子类
            enhancer.setCallback(this);    //设置回调函数，即一个方法拦截
            Object proxy = enhancer.create(); //创建代理类
            return proxy;
        } catch (Exception e) {
            logger.error("AlbianServiceAopProxy newInstance is error ",e);
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
        AlbianAopAttribute attr = rm.getAnnotation(AlbianAopAttribute.class);
        if (null != attr && attr.avoid()) {
            Object rc = methodProxy.invokeSuper(proxy, args);
            return rc;
        }

        if (Validate.isNullOrEmpty(_aopAttributes)) {
            Object rc = methodProxy.invokeSuper(proxy, args);
            return rc;
        }

        IAlbianAopContext ctx = new AlbianAopContext();

        Object rc = null;
        for (IAlbianServiceAopAttribute asaa : _aopAttributes.values()) {
            IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
                    IAlbianAopService.class, asaa.getServiceName(), false);
            if (null == aas) continue;

            if (asaa.matches(mName)) {
                try {
                    aas.before(ctx, _service, method, args);
                } catch (Throwable e) {
                    logger.error("AOPService AlbianRuntime execute before method in the aop service:{} "
                        + "for real service:{} is fail.", asaa.getServiceName(), this._service.getServiceName(), e);
                }
            }
        }

        Throwable throwable = null;
        try {
            rc = methodProxy.invokeSuper(proxy, args);
        } catch (Throwable e) {
            throwable = e;
            logger.error("AOPService AlbianRuntime exception in proxy service:{} method:{} ",
                this._service.getServiceName(), mName, e);
        }

        for (IAlbianServiceAopAttribute asaa : _aopAttributes.values()) {
            IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
                    IAlbianAopService.class, asaa.getServiceName(), false);
            if (null == aas) continue;

            if (asaa.matches(mName)) {
                try {
                    aas.after(ctx, _service, method, rc, throwable, args);
                } catch (Throwable e) {
                    logger.error("AOPService AlbianRuntime exception in the after method in the aop service:{} "
                        + "for real service:{}is fail. ", asaa.getServiceName(), this._service.getServiceName(), e);
                }
            }
        }

        if (null != throwable) throw throwable;
        return rc;

    }
}
