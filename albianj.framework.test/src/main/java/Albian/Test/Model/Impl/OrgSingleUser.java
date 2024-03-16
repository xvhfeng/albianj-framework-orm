package Albian.Test.Model.Impl;

import lombok.*;
import org.albianj.api.dal.object.FreeAlbianObject;

import java.math.BigInteger;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrgSingleUser extends FreeAlbianObject  {
    private String id;
    private String userName;
    private String password;


}
