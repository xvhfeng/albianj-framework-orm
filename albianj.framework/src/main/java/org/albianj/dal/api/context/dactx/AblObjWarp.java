package org.albianj.dal.api.context.dactx;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.dal.api.object.IAblObj;

@Data
@NoArgsConstructor
public class AblObjWarp {
    private QryOpt qryOpt = QryOpt.Save;
    private IAblObj entry = null;
    private String storageAliasName = null;
    private String tableAliasName = null;
    private boolean queryAutoId = false;

    public AblObjWarp(QryOpt qryOpt, IAblObj entry) {
        this.qryOpt = qryOpt;
        this.entry = entry;
    }
}
