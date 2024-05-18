package Albian.Test.Services.Impl;

import Albian.Test.Model.Impl.OrgSingleUser;
import Albian.Test.Services.IOrgUserService;
import Albian.Test.Services.Metadata.StorageInfo;
import org.albianj.api.kernel.service.FreeAblServ;
import org.albianj.api.dal.context.dactx.IDMLCtx;
import org.albianj.api.dal.context.dactx.IDQLCtx;
import org.albianj.api.dal.context.dactx.QryOpt;
import org.albianj.api.dal.object.OOpt;
import org.albianj.api.dal.object.filter.FltExpr;
import org.albianj.api.dal.object.filter.IChaExpr;
import org.albianj.AblServRouter;
import org.albianj.api.dal.service.IAlbianDataAccessService;
import org.albianj.api.dal.service.DrOpt;


import java.util.List;

public class OrgUserService extends FreeAblServ implements IOrgUserService {
    int idx = 0;
    private IAlbianDataAccessService da;

    @Override
    public boolean login(String uname, String pwd)  {
        // where条件推荐使用表达式这种写法
        IChaExpr wheres = new FltExpr("UserName", OOpt.eq, uname);
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IDQLCtx qctx = da.newQueryContext();
        OrgSingleUser user = qctx.useStorage(StorageInfo.SingleUserStorageName).fromTable("SingleUser") //指定到storage
                .loadObject("sessionId", OrgSingleUser.class, DrOpt.Rdr, wheres);
        if (user.getPassword().equals(pwd)) {
            return true;
        }
        // 如果还有查询，可以使用reset对其进行重置，再根据实际的需求进行组合使用
        qctx.reset();
        return false;
    }

    @Override
    public boolean addUser(String uname, String pwd)  {
        //创建对象请使用此方法
        OrgSingleUser user = AblServRouter.newInstance("SessionId", OrgSingleUser.class);
        user.setId(String.valueOf(System.currentTimeMillis()));
        user.setPassword(pwd);
        user.setUserName(uname);


        // 创建保存数据的上下文，不推荐使用save或者是create等诸如此类的原来的方法及其重载
        IDMLCtx dctx = da.newDataAccessContext();
        return dctx.add(QryOpt.Save, user, StorageInfo.SingleUserStorageName, "SingleUser").commit("Sessionid");
    }

    @Override
    public boolean modifyPwd(String uname, String orgPwd, String newPwd)  {
        // 如果是更改数据库记录，必须先需要load一下数据库记录，
        IChaExpr wheres = new FltExpr("UserName", OOpt.eq, uname);
        IDQLCtx qctx = da.newQueryContext();
        OrgSingleUser user = qctx.useStorage(StorageInfo.SingleUserStorageName).fromTable("SingleUser") //指定到storage
                // 如果需要及其精确，使用LoadType.exact，并且指定主数据库或根据DataRouter走WriteRouters配置
                .loadObject("sessionId", OrgSingleUser.class, DrOpt.Rdr, wheres);
        if (user.getPassword().equals(orgPwd)) {
            user.setPassword(newPwd);
            IDMLCtx dctx = da.newDataAccessContext();
            return dctx.add(QryOpt.Save, user, StorageInfo.SingleUserStorageName, "SingleUser").commit("Sessionid");
        }
        return false;
    }

    @Override
    public boolean batchAddUser()  {
        IDMLCtx dctx = da.newDataAccessContext();
        OrgSingleUser mu1 = AblServRouter.newInstance("sessionId", OrgSingleUser.class);
        String id1 = String.format("%d_%d_%d_%d", System.currentTimeMillis(), ++idx, 1, 1);
        mu1.setId(id1);
        mu1.setUserName("mu1_org");
        mu1.setPassword("mu1pwd_org");

        OrgSingleUser mu2 = AblServRouter.newInstance("sessionId", OrgSingleUser.class);
        String id2 = String.format("%d_%d_%d_%d", System.currentTimeMillis(), ++idx, 2, 2);
        mu2.setId(id2);
        mu2.setUserName("mu2_org");
        mu2.setPassword("mu2pwd_org");

        OrgSingleUser user = AblServRouter.newInstance("SessionId", OrgSingleUser.class);
        user.setId(String.valueOf(System.currentTimeMillis()));
        user.setPassword("batcher_by_org");
        user.setUserName("batcher_by_org");
        //同时使用数据路由与单数据库保存
        dctx.add(QryOpt.Save, mu1)
                .add(QryOpt.Save, mu2)
                .add(QryOpt.Save, user, StorageInfo.SingleUserStorageName)
                .commit("sessionId");


        return false;
    }

    @Override
    public void queryMulitUserById()  {
        // where条件推荐使用表达式这种写法
        IChaExpr whrs1 = new FltExpr("Id", OOpt.eq, "1539240117605_1_1_1");
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IDQLCtx qctx = da.newQueryContext();
        OrgSingleUser mu1 = qctx.loadObject("sessionId", OrgSingleUser.class, DrOpt.Rdr, whrs1);
        System.out.println(String.format("MU1:id->%s uname->%s pwd->%s",
                mu1.getId(), mu1.getUserName(), mu1.getPassword()));
        qctx.reset();

        IChaExpr whrs2 = new FltExpr("Id", OOpt.eq, "1539240117606_2_2_2");
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        OrgSingleUser mu2 = qctx.loadObject("sessionId", OrgSingleUser.class, DrOpt.Rdr, whrs2);
        System.out.println(String.format("MU2:id->%s uname->%s pwd->%s",
                mu2.getId(), mu2.getUserName(), mu2.getPassword()));


    }

    @Override
    public int  testInExpr(){
       String[] ids = { "1710389812081_2_2_2" ,"1710389924309_2_2_2" ,"1710395088024_2_2_2"};
        IChaExpr whrs1 = new FltExpr("Id", OOpt.in, ids);

        IDQLCtx qctx = da.newQueryContext();
        List<OrgSingleUser> mu1 = qctx.loadObjects("sessionId", OrgSingleUser.class, DrOpt.Rdr, whrs1);
        return mu1.size();
    }

    @Override
    public boolean tranOptUser() {
        return false;
    }
}
