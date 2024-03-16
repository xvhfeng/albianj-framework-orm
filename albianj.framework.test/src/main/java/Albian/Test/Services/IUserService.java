package Albian.Test.Services;


import org.albianj.kernel.service.IAlbianService;

public interface IUserService extends IAlbianService {
    final String Name = "UserService";

    boolean login(String uname, String pwd) ;

    boolean addUser(String uname, String pwd) ;

    boolean modifyPwd(String uname, String orgPwd, String newPwd) ;

    boolean batchAddUser() ;

    void queryMulitUserById() ;

    int  testInExpr();

    int  testLikeExpr();

    boolean tranOptUser();
}
