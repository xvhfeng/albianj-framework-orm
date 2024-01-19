package org.albianj.kernel.impl.aspect;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.albianj.common.utils.CollectionUtil;
import org.albianj.kernel.anno.AblAspectIgnoreAnno;
import org.albianj.kernel.attr.ServiceAspectAttr;
import org.albianj.kernel.attr.ServiceAttr;
import org.albianj.kernel.attr.GlobalSettings;
import org.albianj.kernel.itf.aspect.AadAspectContext;
import org.albianj.kernel.itf.aspect.IAadAopService;
import org.albianj.kernel.itf.builtin.logger.LogLevel;
import org.albianj.kernel.itf.builtin.logger.LogTarget;
import org.albianj.kernel.ServRouter;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public class ServAspectProxy implements MethodInterceptor {

    Object _realServ = null;
    Map<String, ServiceAspectAttr> _aspectAttrs = null;
    ServiceAttr servAttr;

    GlobalSettings settings;

    public Object newInstance(String sessionId, GlobalSettings settings, ServiceAttr servAttr, Object realServ)  {
        this._realServ = realServ;
        this.servAttr = servAttr;
        this.settings = settings;
        this._aspectAttrs = servAttr.getAspectAttrs();
        try {
            Enhancer enhancer = new Enhancer();  //增强类
            //不同于JDK的动态代理。它不能在创建代理时传obj对 象，obj对象必须被CGLIB包来创建
            enhancer.setClassLoader(settings.getClassLoader());
            enhancer.setSuperclass(servAttr.getSelfClass()); //设置被代理类字节码（obj将被代理类设置成父类；作为产生的代理的父类传进来的）。CGLIB依据字节码生成被代理类的子类
            enhancer.setCallback(this);    //设置回调函数，即一个方法拦截
            Object proxy = enhancer.create(); //创建代理类
            return proxy;
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(sessionId, LogTarget.Running, LogLevel.Error,e,
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

        Method origMethod = servAttr.getSelfClass().getMethod(mName, method.getParameterTypes());
        AblAspectIgnoreAnno attr = origMethod.getAnnotation(AblAspectIgnoreAnno.class);
        if (null != attr && attr.value()) {
            Object rc = methodProxy.invokeSuper(proxy, args);
            return rc;
        }

        if (CollectionUtil.isNullOrEmpty(_aspectAttrs)) {
            Object rc = methodProxy.invokeSuper(proxy, args);
            return rc;
        }

        AadAspectContext ctx = new AadAspectContext();

        Object rc = null;
        for (ServiceAspectAttr aspectAttr : _aspectAttrs.values()) {
            IAadAopService proxyServ = ServRouter.getService("InnerProxyService",
                    IAadAopService.class, aspectAttr.getServiceName(), false);
            if (null == proxyServ) continue;

            if (aspectAttr.matches(mName)) {
                try {
                    proxyServ.before(ctx, _realServ, method, args);
                } catch (Throwable e) {
                    ServRouter.logAndThrowAgain("InnerProxyService",LogTarget.Running,LogLevel.Error,e,
                            "AOPService AlbianRuntime execute before method in the aop service:{} for real service:{} is fail.",
                            aspectAttr.getServiceName(), servAttr.getId());
                }
            }
        }

        Throwable throwable = null;
        try {
            rc = methodProxy.invokeSuper(proxy, args);
        } catch (Throwable e) {
            throwable = e;
            ServRouter.log("InnerProxyService",LogTarget.Running,LogLevel.Error,e,
                    "AOPService AlbianRuntime exception in proxy service:{} method:{} ",
                    servAttr.getId(), mName);
        }

        for (ServiceAspectAttr aspectAttr : _aspectAttrs.values()) {
            IAadAopService aas = ServRouter.getService("InnerProxyService",
                    IAadAopService.class, aspectAttr.getServiceName(), false);
            if (null == aas) continue;

            if (aspectAttr.matches(mName)) {
                try {
                    aas.after(ctx, _realServ, method, rc, throwable, args);
                } catch (Throwable e) {
                    ServRouter.log("InnerProxyService",LogTarget.Running,LogLevel.Warn,e,
                            "AOPService AlbianRuntime exception in the after method in the aop service:{} for real service:{} is fail. ",
                            aspectAttr.getServiceName(), servAttr.getId());
                }
            }
        }

        if (null != throwable) throw throwable;
        return rc;

    }
}
