package Albian.Test.Model.Impl;

import lombok.*;
import org.albianj.dal.api.object.FreeAblObj;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrgMultiUser extends FreeAblObj {

    private String id;
    private String userName;
    private String password;



}
