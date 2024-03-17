package Albian.Test;

import Albian.Test.Services.IOrgUserService;
import Albian.Test.Services.IUserService;
import org.albianj.ServRouter;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.loader.AlbianjApplication;
import org.albianj.AblServRouter;
import org.albianj.loader.IAlbianCommandLineApplication;

public class DoTest implements IAlbianCommandLineApplication {
    public static void main(String[] argv) {
        try {

            AlbianjApplication.run(DoTest.class,argv[0]);
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
