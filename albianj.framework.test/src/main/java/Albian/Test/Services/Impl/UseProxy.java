package Albian.Test.Services.Impl;

import org.albianj.kernel.api.anno.proxy.*;
import org.albianj.kernel.api.anno.serv.AblServAnno;

@AblAopAnno(pkgs = {@AblWatchPkg(watch = "org.albianj.test.*",exclusion = "org.albianj.test.impl")},
classes = {@AblWatchClassAnno(watch = OrgUserInlService.class,exclusion = UserService.class)})
@AblServAnno
public class UseProxy {

    @AblAopPointAnno(beginWith = "get",when = AopFlag.Brf)
    public void before() {

    }

    @AblAopPointAnno(when = AopFlag.Aft | AopFlag.Brf)
    public void around(){

    }

    @AblAopPointAnno(beginWith = "get",when = AopFlag.Aft)
    public void after(){

    }

    @AblAopPointAnno(beginWith = "get",when = AopFlag.Thr,
            raises = @AblWatchThrow(watch = Throwable.class))
    public void raise(){

    }

    @AblAopIgnoreAnno
    private void test(){

    }
}
