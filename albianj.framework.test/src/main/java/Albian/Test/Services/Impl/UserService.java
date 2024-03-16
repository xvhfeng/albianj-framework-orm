package Albian.Test.Services.Impl;

import Albian.Test.Model.IMultiUser;
import Albian.Test.Model.IOrgMultiUser;
import Albian.Test.Model.ISingleUser;
import Albian.Test.Services.IUserService;
import Albian.Test.Services.Metadata.StorageInfo;
import org.albianj.dal.object.filter.FilterGroupExpression;
import org.albianj.dal.object.filter.IFilterGroupExpression;
import org.albianj.kernel.logger.LogLevel;
import org.albianj.kernel.anno.serv.AlbianServiceFieldRant;
import org.albianj.kernel.anno.serv.AlbianServiceFieldType;
import org.albianj.kernel.anno.serv.AlbianServiceRant;
import org.albianj.kernel.service.FreeAlbianService;
import org.albianj.dal.context.dactx.IIduCtx;
import org.albianj.dal.context.dactx.ISltCtx;
import org.albianj.dal.context.dactx.QueryOpt;
import org.albianj.dal.object.OperatorOpt;
import org.albianj.dal.object.filter.FilterExpression;
import org.albianj.dal.object.filter.IChainExpression;
import org.albianj.AblServRouter;
import org.albianj.dal.service.IAlbianDataAccessService;
import org.albianj.dal.service.QueryToOpt;

import java.math.BigInteger;
import java.util.List;

// service必须使用此特性进行标注，否则albianj不对其进行解析
@AlbianServiceRant(Id = "UserService", Interface = IUserService.class)
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
        IChainExpression wheres = new FilterExpression("UserName", OperatorOpt.eq, uname);
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        ISltCtx qctx = da.newQueryContext();
        ISingleUser user = qctx.useStorage(StorageInfo.SingleUserStorageName) //指定到storage
                .loadObject("sessionId", ISingleUser.class, QueryToOpt.ReaderRouter, wheres);
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
        ISingleUser user = AblServRouter.newInstance("SessionId", ISingleUser.class);
        user.setId(BigInteger.valueOf(System.currentTimeMillis()));
        user.setPassword(pwd);
        user.setUserName(uname);


        // 创建保存数据的上下文，不推荐使用save或者是create等诸如此类的原来的方法及其重载
        IIduCtx dctx = da.newDataAccessContext();
        return dctx.add(QueryOpt.Save, user, StorageInfo.SingleUserStorageName).commit("Sessionid");
    }

    @Override
    public boolean modifyPwd(String uname, String orgPwd, String newPwd)  {
        // 如果是更改数据库记录，必须先需要load一下数据库记录，
        IChainExpression wheres = new FilterExpression("UserName", OperatorOpt.eq, uname);
        ISltCtx qctx = da.newQueryContext();
        ISingleUser user = qctx.useStorage(StorageInfo.SingleUserStorageName) //指定到storage
                // 如果需要及其精确，使用LoadType.exact，并且指定主数据库或根据DataRouter走WriteRouters配置
                .loadObject("sessionId", ISingleUser.class, QueryToOpt.ReaderRouter, wheres);
        if (user.getPassword().equals(orgPwd)) {
            user.setPassword(newPwd);
            IIduCtx dctx = da.newDataAccessContext();
            return dctx.add(QueryOpt.Save, user, StorageInfo.SingleUserStorageName).commit("Sessionid");
        }
        return false;
    }

    @Override
    public boolean batchAddUser()  {
        IIduCtx dctx = da.newDataAccessContext();
        IMultiUser mu1 = AblServRouter.newInstance("sessionId", IMultiUser.class);
        String id1 = String.format("%d_%d_%d_%d", System.currentTimeMillis(), ++idx, 1, 1);
        mu1.setId(id1);
        mu1.setUserName("mu1");
        mu1.setPassword("mu1pwd");

        IMultiUser mu2 = AblServRouter.newInstance("sessionId", IMultiUser.class);
        String id2 = String.format("%d_%d_%d_%d", System.currentTimeMillis(), ++idx, 2, 2);
        mu2.setId(id2);
        mu2.setUserName("mu2");
        mu2.setPassword("mu2pwd");

//        ISingleUser user = AlbianServiceHub.newInstance("SessionId", ISingleUser.class);
//        user.setId(BigInteger.valueOf(System.currentTimeMillis()));
//        user.setPassword("batcher");
//        user.setUserName("batcher");
        //同时使用数据路由与单数据库保存
        return dctx.add(QueryOpt.Save, mu1)
                .add(QueryOpt.Save, mu2)
//                .add(AlbianDataAccessOpt.Save, user, StorageInfo.SingleUserStorageName)
                .commit("sessionId");


    }

    @Override
    public void queryMulitUserById()  {
        // where条件推荐使用表达式这种写法
        IFilterGroupExpression whrs1 = new FilterGroupExpression();
        whrs1.add("Id", OperatorOpt.eq, "1710318557305_1_1_1")
                .and("userName",OperatorOpt.eq,"mu1");

//        IFilterGroupExpression st = new FilterGroupExpression();
//        st.add("userName",LogicalOperation.Equal,1).or("userName","st",LogicalOperation.Equal,3);
//        whrs1.and(st);

        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        ISltCtx qctx = da.newQueryContext();
        IMultiUser mu1 = qctx.loadObject("sessionId", IMultiUser.class, QueryToOpt.ReaderRouter, whrs1);
        AblServRouter.log("batchid", LogLevel.Debug,"MUser_1: id->{} name->{} pwd->{}",mu1.getId(),mu1.getUserName(),mu1.getPassword());
//        System.out.println(String.format("MU1:id->%s uname->%s pwd->%s",
//                mu1.getId(), mu1.getUserName(), mu1.getPassword()));
        qctx.reset();

        IChainExpression whrs2 = new FilterExpression("Id", OperatorOpt.eq, "1710318557305_2_2_2");
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IMultiUser mu2 = qctx.loadObject("sessionId", IMultiUser.class, QueryToOpt.ReaderRouter, whrs2);
        AblServRouter.log("batchid", LogLevel.Debug,"MUser_2: id->{} name->{} pwd->{}",mu2.getId(),mu2.getUserName(),mu2.getPassword());
//        System.out.println(String.format("MU2:id->%s uname->%s pwd->%s",
//                mu2.getId(), mu2.getUserName(), mu2.getPassword()));


    }

    @Override
    public int  testInExpr(){
        String[] ids = { "1710389812081_2_2_2" ,"1710389924309_2_2_2" ,"1710395088024_2_2_2"};
        IChainExpression whrs1 = new FilterExpression("Id", OperatorOpt.in, ids);

        ISltCtx qctx = da.newQueryContext();
        List<IOrgMultiUser> mu1 = qctx.useStorage("MUserStorage2").fromTable("MUser_2")
                .loadObjects("sessionId", IOrgMultiUser.class, QueryToOpt.ReaderRouter, whrs1);
        return mu1.size();
    }

    @Override
    public int  testLikeExpr(){
        IChainExpression whrs1 = new FilterExpression("Id", OperatorOpt.like, "17103%");

        ISltCtx qctx = da.newQueryContext();
        List<IOrgMultiUser> mu1 = qctx.useStorage("MUserStorage2").fromTable("MUser_2")
                .loadObjects("sessionId", IOrgMultiUser.class, QueryToOpt.ReaderRouter, whrs1);
        return mu1.size();
    }

    @Override
    public boolean tranOptUser() {
        return false;
    }

}
