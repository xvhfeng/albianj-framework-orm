package org.albianj.api.dal.context.dactx;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.api.dal.object.IAblObj;

@Data
@NoArgsConstructor
public class AlbianObjectWarp  {
    private QueryOpt queryOpt = QueryOpt.Save;
    private IAblObj entry = null;
    private String storageAliasName = null;
    private String tableAliasName = null;
    private boolean queryAutoId = false;

    public AlbianObjectWarp(QueryOpt queryOpt, IAblObj entry) {
        this.queryOpt = queryOpt;
        this.entry = entry;
    }
}
