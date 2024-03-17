package Albian.Test.Model.Impl;

import lombok.*;
import org.albianj.api.dal.object.FreeAblObj;
import org.albianj.api.dal.object.rants.AblEntityFieldRant;
import org.albianj.api.dal.object.rants.AblObjRant;

@AblObjRant
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UTF8M64 extends FreeAblObj {

    @AblEntityFieldRant(IsPrimaryKey = true, FieldName = "id")
    private int id = 0;

    @AblEntityFieldRant(FieldName = "v")
    private String v;


}
