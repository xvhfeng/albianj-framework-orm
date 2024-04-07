package Albian.Test.Services.Impl;

import org.albianj.kernel.api.anno.proxy.*;
import org.albianj.kernel.api.anno.serv.AblServAnno;

@AblAopAnno(pkgs = {@AblWatchPkg(watch = "org.albianj.test.*",exclusion = "org.albianj.test.impl")},
classes = {@AblWatchClassAnno(watch = OrgUserInlService.class,exclusion = UserService.class)})
@AblServAnno
public class UseProxy {

    @AblAopPointAnno(beginWith = "get",when = AopWhen.Brf)
    public void before() {

    }

    @AblAopPointAnno(when = AopWhen.Aft | AopWhen.Brf)
    public void around(){

    }

    @AblAopPointAnno(beginWith = "get",when = AopWhen.Aft)
    public void after(){

    }

    @AblAopPointAnno(beginWith = "get",when = AopWhen.Thr,
            raises = @AblWatchThrow(watch = Throwable.class))
    public void raise(){

    }

    @AblAopIgnoreAnno
    private void test(){

    }
}
