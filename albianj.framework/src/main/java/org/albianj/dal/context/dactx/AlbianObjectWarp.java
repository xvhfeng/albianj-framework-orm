package org.albianj.dal.context.dactx;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.dal.object.IAlbianObject;

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
