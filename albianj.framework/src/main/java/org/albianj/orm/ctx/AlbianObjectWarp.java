package org.albianj.orm.ctx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.orm.kit.dactx.AlbianDataAccessOpt;
import org.albianj.orm.kit.object.IAlbianObject;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AlbianObjectWarp {
    private int persistenceOpt = AlbianDataAccessOpt.Save;
    private IAlbianObject entry = null;
    private String storageAliasName = null;
    private String tableAliasName = null;
    private boolean queryIdentitry = false;

    public AlbianObjectWarp(int opt, IAlbianObject entry) {
        this.persistenceOpt = opt;
        this.entry = entry;
    }




}
