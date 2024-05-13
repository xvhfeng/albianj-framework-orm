package Albian.Test.Model.Impl;


import lombok.*;
import org.albianj.api.dal.object.FreeAblObj;
import org.albianj.api.dal.object.rants.AblDrRant;
import org.albianj.api.dal.object.rants.AblDrsRant;
import org.albianj.api.dal.object.rants.AblEntityFieldRant;
import org.albianj.api.dal.object.rants.AblObjRant;
import org.albianj.api.dal.service.AblDrDef;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@AblObjRant()
@AblDrsRant( // 数据路由配置
        DataRouter = AblDrDef.class, //指定数据路由算法
        ReaderRouters = { // 配置读路由
                @AblDrRant(Name = "event-r", StorageName = "CFUA", TableName = "events"),
        },
        WriterRouters = { //配置写路由
                @AblDrRant(Name = "event-w", StorageName = "CFUA", TableName = "events"),
        }
)
public class Event extends FreeAblObj {
    private String uid;
    @AblEntityFieldRant(IsPrimaryKey = true)
    private String action;
    private String function;
    private String bid;
    private String cid;

    @AblEntityFieldRant(FieldName = "login_id")
    private String loginId;

    private LocalDateTime times;

    private String url;

    private String module;
}
