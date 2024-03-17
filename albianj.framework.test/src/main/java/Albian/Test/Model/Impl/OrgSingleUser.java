package Albian.Test.Model.Impl;

import lombok.*;
import org.albianj.api.dal.object.FreeAblObj;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrgSingleUser extends FreeAblObj {
    private String id;
    private String userName;
    private String password;


}
