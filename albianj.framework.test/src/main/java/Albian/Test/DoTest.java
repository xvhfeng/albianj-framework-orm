package Albian.Test;

import Albian.Test.Model.Impl.Event;
import Albian.Test.Services.IEventServ;
import Albian.Test.Services.IOrgUserService;
import Albian.Test.Services.IUserService;
import org.albianj.ServRouter;
import org.albianj.api.kernel.anno.AblObjScanRant;
import org.albianj.api.kernel.anno.AblServScanRant;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.loader.AblApp;
import org.albianj.AblServRouter;
import org.albianj.loader.IAblCommandLine;

import java.util.List;

@AblServScanRant(Paths = "Albian.Test.Services.Impl")
@AblObjScanRant(Paths = "Albian.Test.Model.Impl")
public class DoTest implements IAblCommandLine {
    public static void main(String[] argv) {
        try {

            AblApp.run(DoTest.class,null);
//            for(int i = 0; i < 10000; i++ ) {
//
//                testRedShift();
//            }
            test1();
            return;

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void testRedShift(){

        IEventServ eServ = ServRouter.getService("",IEventServ.class);
        eServ.insert();

        List<Event> events = eServ.load();
        AblServRouter.log("Test redshift",LogLevel.Info,"load action;{}",events.size());
      //
    }

    private static void test1()  {
        IUserService us = ServRouter.getService("",IUserService.class, IUserService.Name);
        AblServRouter.log("Test app",LogLevel.Debug,"in expr load count:{}",us.testInExpr());
        AblServRouter.log("Test app",LogLevel.Debug,"like expr load count:{}",us.testLikeExpr());

        if(us.batchAddUser()){
                AblServRouter.log("Test Application", LogLevel.Debug, "batch add users success");
            } else {
                AblServRouter.log("Test Application", LogLevel.Debug, "batch add users  fail");
            }
        us.queryMulitUserById();
        AblServRouter.log("Test Application", LogLevel.Debug, "done query mulit users.");

        AblServRouter.log("Test app",LogLevel.Debug,"in expr load count:{}",us.testInExpr());
        AblServRouter.log("Test app",LogLevel.Debug,"like expr load count:{}",us.testLikeExpr());

        us.qryForTestWhrGetter();

    }

    private static void test2()  {
        IOrgUserService us = ServRouter.getService("",IOrgUserService.class, IOrgUserService.Name);

        if (us.batchAddUser()) {
            System.out.println("batch add use success");
        } else {
            System.out.println("batch add user fail.");
        }
        us.queryMulitUserById();
    }

    @Override
    public void run(String... paras) {
        System.out.println("run test");
        test1();
    }
}
