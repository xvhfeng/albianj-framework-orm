package Albian.Test;

import Albian.Test.Services.IOrgUserService;
import Albian.Test.Services.IUserService;
import org.albianj.ServRouter;
import org.albianj.kernel.api.logger.LogLevel;
import org.albianj.loader.AblApp;
import org.albianj.AblServRouter;
import org.albianj.loader.IAblCmdLineApp;
import org.albianj.scanner.annos.AblScanRant;

@AblScanRant(value = {"dsdss","dsdscsds"})
public class DoTest implements IAblCmdLineApp {
    public static void main(String[] argv) {
        try {

            AblApp.run(DoTest.class,argv[0]);
            test1();
            return;

        } catch (Throwable t) {
            t.printStackTrace();
        }
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
