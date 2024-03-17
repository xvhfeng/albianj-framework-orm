package Albian.Test.Model.Impl;

import lombok.*;
import org.albianj.api.dal.object.FreeAlbianObject;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrgMultiUser extends FreeAlbianObject  {

    private String id;
    private String userName;
    private String password;



}
