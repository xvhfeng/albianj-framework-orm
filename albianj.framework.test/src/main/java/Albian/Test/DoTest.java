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


//            String v = "@徐海峰(xuhaifeng)\uD83C\uDF4E\uD83D\uDCB0\uD83D\uDCF1\uD83C\uDF19\uD83C\uDF41\uD83C\uDF42\uD83C\uDF43\uD83C\uDF37\uD83D\uDC8E\uD83D\uDD2A\uD83D\uDD2B\uD83C\uDFC0⚽⚡\uD83D\uDC44\uD83D\uDC4D\uD83D\uDD25tm都是啥玩意jb玩意";
//            IUTF8M64Service utf = AlbianServiceHub.getSingletonService(IUTF8M64Service.class, IUTF8M64Service.ServiceId);
//            int id = 100;
//            utf.saveUtf8M64(id, v);
//            String v1 = utf.getUtf8M64(id);
//            System.out.println(v1);


//            final IAlbianStorageParserService stgService = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
//            for( int i = 0; i < 1200; i++){
//                new Thread(new Runnable(){
//                    @Override
//                    public void run() {
//                        IStorageAttribute stgAttr = stgService.getStorageAttribute("SpxDBCP");
//                        IRunningStorageAttribute runStgAttr = new RunningStorageAttribute(stgAttr,stgAttr.getDatabase());
//                        Connection conn = stgService.getConnection("sessionId:" + Thread.currentThread().getId(), runStgAttr);
//
//                        int sec = 1 * 1000;
//                        long  ts = System.currentTimeMillis() % sec;
//                        if(0 == ts){
//                            ts = 1 * 1000;
//                        }
//                        try {
//                            Thread.sleep(ts);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        if(null == conn){
//                            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
//                                    "DBPOOLMAIN", AlbianLoggerLevel.Info,
//                                    "get conn is null.");
//                        } else {
//                            stgService.returnConnection("ses", runStgAttr, conn);
//                        }
//
//                    }
//                }).start();
//                if(0 == (i % 300)){
//                    Thread.sleep(3000);
//                }
//            }
//            Thread.sleep(50000000);


//            IUserService us = AlbianServiceRouter.getSingletonService(IUserService.class, IUserService.Name);
//            us.addUser("uname-SpxDBCP", "pwd");
//            if (us.login("uname-SpxDBCP", "pwd")) {
//                System.out.println("login success.");
//            }
//            System.out.println("login fail.");
//            if (us.modifyPwd("uname-SpxDBCP", "pwd", "newpwd-SpxDBCP")) {
//                System.out.println("modify password success.");
//            }
//            System.out.println("modify password fail.");

//            if(us.batchAddUser()){
//                System.out.println("batch add use success");
//            } else {
//                System.out.println("batch add user fail.");
//            }
//            us.queryMulitUserById();
//            test2();
//            return;

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
