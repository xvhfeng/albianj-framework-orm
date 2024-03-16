package Albian.Test.Model.Impl;

import lombok.*;
import org.albianj.api.dal.object.FreeAlbianObject;
import org.albianj.api.dal.object.rants.AlbianObjectDataFieldRant;
import org.albianj.api.dal.object.rants.AlbianObjectRant;

@AlbianObjectRant
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UTF8M64 extends FreeAlbianObject  {

    @AlbianObjectDataFieldRant(IsPrimaryKey = true, FieldName = "id")
    private int id = 0;

    @AlbianObjectDataFieldRant(FieldName = "v")
    private String v;


}
