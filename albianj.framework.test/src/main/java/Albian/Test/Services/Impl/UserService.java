package Albian.Test.Services.Impl;

import Albian.Test.Model.Impl.MultiUser;
import Albian.Test.Model.Impl.SingleUser;
import Albian.Test.Services.IUserService;
import Albian.Test.Services.Metadata.StorageInfo;
import org.albianj.dal.api.object.filter.FltGExpr;
import org.albianj.dal.api.object.filter.IFltGExpr;
import org.albianj.kernel.api.logger.LogLevel;
import org.albianj.kernel.api.anno.serv.AlbianServiceFieldRant;
import org.albianj.kernel.api.anno.serv.AlbianServiceFieldType;
import org.albianj.kernel.api.anno.serv.AblServiceRant;
import org.albianj.kernel.api.service.FreeAlbianService;
import org.albianj.dal.api.context.dactx.IDMLCtx;
import org.albianj.dal.api.context.dactx.IDQLCtx;
import org.albianj.dal.api.context.dactx.QryOpt;
import org.albianj.dal.api.object.OOpt;
import org.albianj.dal.api.object.filter.FltExpr;
import org.albianj.dal.api.object.filter.IChaExpr;
import org.albianj.AblServRouter;
import org.albianj.dal.api.service.IAlbianDataAccessService;
import org.albianj.dal.api.service.DrOpt;

import java.math.BigInteger;
import java.util.List;

// service必须使用此特性进行标注，否则albianj不对其进行解析
@AblServiceRant(Id = "UserService", Interface = IUserService.class)
public class UserService extends FreeAlbianService implements IUserService {

    int idx = 0;
    //在没有确认与把握的情况下，慎用之慎用之慎用之（重要的话说三遍）
    //使用albianj的ioc直接对其属性进行赋值
    // 注意，所有使用AlbianServiceFieldRant赋值的值都是单利模式，故在albianj中会自动提升为静态变量状态
    @AlbianServiceFieldRant(Type = AlbianServiceFieldType.Ref, Value = "AlbianDataAccessService")
    private IAlbianDataAccessService da;

    @Override
    public boolean login(String uname, String pwd)  {

        // where条件推荐使用表达式这种写法
        IChaExpr wheres = new FltExpr("UserName", OOpt.eq, uname);
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IDQLCtx qctx = da.newQueryContext();
        SingleUser user = qctx.useStorage(StorageInfo.SingleUserStorageName) //指定到storage
                .loadObject("sessionId", SingleUser.class, DrOpt.Rdr, wheres);
        if (user.getPassword().equals(pwd)) {
            return true;
        }
        // 如果还有查询，可以使用reset对其进行重置，再根据实际的需求进行组合使用
        qctx.reset();
        return false;
    }

    @Override
    public boolean addUser(String uname, String pwd)  {
//        AlbianServiceHub.addLog("Sessionid", IAlbianLoggerService.AlbianRunningLoggerName,
//                AlbianLoggerLevel.Info, "i am %s", "log");

        NullPointerException exc = new NullPointerException();
//        AlbianServiceHub.addLog("Sessionid", IAlbianLoggerService.AlbianRunningLoggerName,
//                AlbianLoggerLevel.Info, exc, "i am %s", "log");

        //创建对象请使用此方法
        SingleUser user = AblServRouter.newInstance("SessionId", SingleUser.class);
        user.setId(BigInteger.valueOf(System.currentTimeMillis()));
        user.setPassword(pwd);
        user.setUserName(uname);


        // 创建保存数据的上下文，不推荐使用save或者是create等诸如此类的原来的方法及其重载
        IDMLCtx dctx = da.newDataAccessContext();
        return dctx.add(QryOpt.Save, user, StorageInfo.SingleUserStorageName).commit("Sessionid");
    }

    @Override
    public boolean modifyPwd(String uname, String orgPwd, String newPwd)  {
        // 如果是更改数据库记录，必须先需要load一下数据库记录，
        IChaExpr wheres = new FltExpr("UserName", OOpt.eq, uname);
        IDQLCtx qctx = da.newQueryContext();
        SingleUser user = qctx.useStorage(StorageInfo.SingleUserStorageName) //指定到storage
                // 如果需要及其精确，使用LoadType.exact，并且指定主数据库或根据DataRouter走WriteRouters配置
                .loadObject("sessionId", SingleUser.class, DrOpt.Rdr, wheres);
        if (user.getPassword().equals(orgPwd)) {
            user.setPassword(newPwd);
            IDMLCtx dctx = da.newDataAccessContext();
            return dctx.add(QryOpt.Save, user, StorageInfo.SingleUserStorageName).commit("Sessionid");
        }
        return false;
    }

    @Override
    public boolean batchAddUser()  {
        IDMLCtx dctx = da.newDataAccessContext();
        MultiUser mu1 = AblServRouter.newInstance("sessionId", MultiUser.class);
        String id1 = String.format("%d_%d_%d_%d", System.currentTimeMillis(), ++idx, 1, 1);
        mu1.setId(id1);
        mu1.setUserName("mu1");
        mu1.setPassword("mu1pwd");

        MultiUser mu2 = AblServRouter.newInstance("sessionId", MultiUser.class);
        String id2 = String.format("%d_%d_%d_%d", System.currentTimeMillis(), ++idx, 2, 2);
        mu2.setId(id2);
        mu2.setUserName("mu2");
        mu2.setPassword("mu2pwd");

//        ISingleUser user = AlbianServiceHub.newInstance("SessionId", ISingleUser.class);
//        user.setId(BigInteger.valueOf(System.currentTimeMillis()));
//        user.setPassword("batcher");
//        user.setUserName("batcher");
        //同时使用数据路由与单数据库保存
        boolean success =  dctx.add(QryOpt.Upsert, mu1)
                .add(QryOpt.Upsert, mu2)
//                .add(AlbianDataAccessOpt.Save, user, StorageInfo.SingleUserStorageName)
                .commit("sessionId");

        dctx.reset();

        mu1.setUserName("mu1");
        mu1.setPassword("reset password.");

        mu2.setUserName("user2 name");
        mu2.setPassword("mu2pwd");
        success =  dctx.add(QryOpt.Upsert, mu1)
                .add(QryOpt.Upsert, mu2)
//                .add(AlbianDataAccessOpt.Save, user, StorageInfo.SingleUserStorageName)
                .commit("sessionId");
        return success;
    }

    @Override
    public void queryMulitUserById()  {
        // where条件不再推荐直接使用id的写法这种写法
        IFltGExpr whrs1 = new FltGExpr();
        whrs1.add("id", OOpt.eq, "1710318557305_1_1_1")
                .and("userName", OOpt.eq,"mu1");

//        IFilterGroupExpression st = new FilterGroupExpression();
//        st.add("userName",LogicalOperation.Equal,1).or("userName","st",LogicalOperation.Equal,3);
//        whrs1.and(st);

        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IDQLCtx qctx = da.newQueryContext();
        MultiUser mu1 = qctx.loadObject("sessionId", MultiUser.class, DrOpt.Rdr, whrs1);
        AblServRouter.log("batchid", LogLevel.Debug,"MUser_1: id->{} name->{} pwd->{}",mu1.getId(),mu1.getUserName(),mu1.getPassword());
//        System.out.println(String.format("MU1:id->%s uname->%s pwd->%s",
//                mu1.getId(), mu1.getUserName(), mu1.getPassword()));
        qctx.reset();

        IChaExpr whrs2 = new FltExpr("id", OOpt.eq, "1710318557305_2_2_2");
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        MultiUser mu2 = qctx.loadObject("sessionId", MultiUser.class, DrOpt.Rdr, whrs2);
        AblServRouter.log("batchid", LogLevel.Debug,"MUser_2: id->{} name->{} pwd->{}",mu2.getId(),mu2.getUserName(),mu2.getPassword());
//        System.out.println(String.format("MU2:id->%s uname->%s pwd->%s",
//                mu2.getId(), mu2.getUserName(), mu2.getPassword()));


    }

    @Override
    public void qryForTestWhrGetter()  {
        // where条件推荐使用表达式这种写法
        IFltGExpr whrs1 = new FltGExpr();
        whrs1.add(MultiUser::getId, OOpt.eq, "1710318557305_1_1_1")
                .and("userName", OOpt.eq, "mu1");

        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IDQLCtx qctx = da.newQueryContext();
        MultiUser mu1 = qctx.loadObject("sessionId", MultiUser.class, DrOpt.Rdr, whrs1);
        AblServRouter.log("batchid", LogLevel.Debug,"MUser_1: id->{} name->{} pwd->{}",mu1.getId(),mu1.getUserName(),mu1.getPassword());


        qctx.reset();

        IChaExpr whrs2 = new FltExpr(MultiUser::getId, OOpt.eq, "1710318557305_2_2_2");
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        MultiUser mu2 = qctx.loadObject("sessionId", MultiUser.class, DrOpt.Rdr, whrs2);
        AblServRouter.log("batchid", LogLevel.Debug,"MUser_2: id->{} name->{} pwd->{}",mu2.getId(),mu2.getUserName(),mu2.getPassword());



    }

    @Override
    public int  testInExpr(){
        String[] ids = { "1710389812081_2_2_2" ,"1710389924309_2_2_2" ,"1710395088024_2_2_2"};
        IChaExpr whrs1 = new FltExpr("Id", OOpt.in, ids);

        IDQLCtx qctx = da.newQueryContext();
        List<MultiUser> mu1 = qctx.useStorage("MUserStorage2").fromTable("MUser_2")
                .loadObjects("sessionId", MultiUser.class, DrOpt.Rdr, whrs1);
        return mu1.size();
    }

    @Override
    public int  testLikeExpr(){
        IChaExpr whrs1 = new FltExpr("Id", OOpt.like, "1710%");

        IDQLCtx qctx = da.newQueryContext();
        List<MultiUser> mu1 = qctx.useStorage("MUserStorage2").fromTable("MUser_2")
                .loadObjects("sessionId", MultiUser.class, DrOpt.Rdr, whrs1);
        return mu1.size();
    }

    @Override
    public boolean tranOptUser() {
        return false;
    }

}
