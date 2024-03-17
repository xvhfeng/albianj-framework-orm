package Albian.Test.Model.Impl;

import lombok.*;
import org.albianj.api.dal.object.FreeAlbianObject;
import org.albianj.api.dal.object.rants.AlbianObjectDataFieldRant;
import org.albianj.api.dal.object.rants.AlbianObjectRant;

import java.math.BigInteger;

//如果使用特性模式，必须使用此标注，否则albianj不会对其进行解析
@AlbianObjectRant
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SingleUser extends FreeAlbianObject  {

    @AlbianObjectDataFieldRant(IsPrimaryKey = true)
    private BigInteger id;
    private String userName;
    @AlbianObjectDataFieldRant(FieldName = "Pwd")
    private String password;


}
