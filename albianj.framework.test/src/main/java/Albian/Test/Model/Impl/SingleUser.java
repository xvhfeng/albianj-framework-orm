package Albian.Test.Model.Impl;

import lombok.*;
import org.albianj.dal.api.object.FreeAblObj;
import org.albianj.dal.api.object.rants.AblEntityFieldRant;
import org.albianj.dal.api.object.rants.AblObjRant;

import java.math.BigInteger;

//如果使用特性模式，必须使用此标注，否则albianj不会对其进行解析
@AblObjRant
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SingleUser extends FreeAblObj {

    @AblEntityFieldRant(IsPrimaryKey = true)
    private BigInteger id;
    private String userName;
    @AblEntityFieldRant(FieldName = "Pwd")
    private String password;


}
