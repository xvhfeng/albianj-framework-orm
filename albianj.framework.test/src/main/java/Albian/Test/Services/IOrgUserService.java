package Albian.Test.Services;


import org.albianj.api.kernel.service.IAblServ;

public interface IOrgUserService extends IAblServ {
    final String Name = "OrgUserService";

    boolean login(String uname, String pwd) ;

    boolean addUser(String uname, String pwd) ;

    boolean modifyPwd(String uname, String orgPwd, String newPwd) ;

    boolean batchAddUser() ;

    void queryMulitUserById() ;

    int  testInExpr();

    boolean tranOptUser();
}
