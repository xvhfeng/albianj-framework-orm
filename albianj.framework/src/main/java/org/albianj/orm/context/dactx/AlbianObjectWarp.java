package org.albianj.orm.context.dactx;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.orm.object.IAlbianObject;

@Data
@NoArgsConstructor
public class AlbianObjectWarp  {
    private QueryOpt queryOpt = QueryOpt.Save;
    private IAlbianObject entry = null;
    private String storageAliasName = null;
    private String tableAliasName = null;
    private boolean queryAutoId = false;

    public AlbianObjectWarp(QueryOpt queryOpt, IAlbianObject entry) {
        this.queryOpt = queryOpt;
        this.entry = entry;
    }
}
