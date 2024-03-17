package Albian.Test.Model.Impl;

import Albian.Test.DataRouters.MultiUserDataRouter;
import lombok.*;
import org.albianj.api.dal.object.FreeAblObj;
import org.albianj.api.dal.object.rants.AblEntityFieldRant;
import org.albianj.api.dal.object.rants.AblDrRant;
import org.albianj.api.dal.object.rants.AblDrsRant;
import org.albianj.api.dal.object.rants.AblObjRant;


@AblObjRant()
@AblDrsRant( // 数据路由配置
        DataRouter = MultiUserDataRouter.class, //指定数据路由算法
        ReaderRouters = { // 配置读路由
                @AblDrRant(Name = "MUserRead1", StorageName = "MUserStorage1", TableName = "MUser"),
                @AblDrRant(Name = "MUserRead2", StorageName = "MUserStorage2",TableName = "MUser")
        },
        WriterRouters = { //配置写路由
                @AblDrRant(Name = "MUserWrite1", StorageName = "MUserStorage1", TableName = "MUser"),
                @AblDrRant(Name = "MUserWrite2", StorageName = "MUserStorage2",TableName = "MUser")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder@ToString
public class MultiUser extends FreeAblObj {
    @AblEntityFieldRant(IsPrimaryKey = true)
    private String id;
    private String userName;
    @AblEntityFieldRant(FieldName = "Pwd")
    private String password;

}
