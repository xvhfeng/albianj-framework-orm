package Albian.Test.Services;


import org.albianj.api.kernel.service.IAblServ;

public interface IUserService extends IAblServ {
    final String Name = "UserService";

    boolean login(String uname, String pwd) ;

    boolean addUser(String uname, String pwd) ;

    boolean modifyPwd(String uname, String orgPwd, String newPwd) ;

    boolean batchAddUser() ;

    void queryMulitUserById() ;

    void qryForTestWhrGetter();

    int  testInExpr();

    int  testLikeExpr();

    boolean tranOptUser();
}
