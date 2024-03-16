package Albian.Test.Model.Impl;

import Albian.Test.DataRouters.MultiUserDataRouter;
import lombok.*;
import org.albianj.api.dal.object.FreeAlbianObject;
import org.albianj.api.dal.object.rants.AlbianObjectDataFieldRant;
import org.albianj.api.dal.object.rants.AlbianObjectDataRouterRant;
import org.albianj.api.dal.object.rants.AlbianObjectDataRoutersRant;
import org.albianj.api.dal.object.rants.AlbianObjectRant;


@AlbianObjectRant(
        DataRouters = @AlbianObjectDataRoutersRant( // 数据路由配置
                DataRouter = MultiUserDataRouter.class, //指定数据路由算法
                ReaderRouters = { // 配置读路由
                        @AlbianObjectDataRouterRant(Name = "MUserRead1", StorageName = "MUserStorage1", TableName = "MUser"),
                        @AlbianObjectDataRouterRant(Name = "MUserRead2", StorageName = "MUserStorage2",TableName = "MUser")
                },
                WriterRouters = { //配置写路由
                        @AlbianObjectDataRouterRant(Name = "MUserWrite1", StorageName = "MUserStorage1", TableName = "MUser"),
                        @AlbianObjectDataRouterRant(Name = "MUserWrite2", StorageName = "MUserStorage2",TableName = "MUser")
                }
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder@ToString
public class MultiUser extends FreeAlbianObject {
    @AlbianObjectDataFieldRant(IsPrimaryKey = true)
    private String id;
    private String userName;
    @AlbianObjectDataFieldRant(FieldName = "Pwd")
    private String password;

}
