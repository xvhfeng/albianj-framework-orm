package Albian.Test.Services.Impl;

import Albian.Test.Model.Impl.UTF8M64;
import Albian.Test.Services.IUTF8M64Service;
import Albian.Test.Services.Metadata.StorageInfo;
import org.albianj.api.kernel.anno.serv.AblServFieldRant;
import org.albianj.api.kernel.anno.serv.AblServFieldType;
import org.albianj.api.kernel.anno.serv.AblServRant;
import org.albianj.api.kernel.service.FreeAblServ;
import org.albianj.api.dal.context.dactx.IDMLCtx;
import org.albianj.api.dal.context.dactx.IDQLCtx;
import org.albianj.api.dal.context.dactx.QryOpt;
import org.albianj.api.dal.object.OOpt;
import org.albianj.api.dal.object.filter.FltExpr;
import org.albianj.api.dal.object.filter.IChaExpr;
import org.albianj.api.dal.service.IAlbianDataAccessService;
import org.albianj.api.dal.service.DrOpt;


@AblServRant(Id = "UTF8M64Service")
public class UTF8M64Service extends FreeAblServ implements IUTF8M64Service {

    @AblServFieldRant(Type = AblServFieldType.Ref, Value = "AlbianDataAccessService")
    private IAlbianDataAccessService da;

    @Override
    public boolean saveUtf8M64(int id, String v)  {
        UTF8M64 utf8m64 = new UTF8M64();
        IDMLCtx dctx = da.newDataAccessContext();
        utf8m64.setId(id);
        utf8m64.setV(v);
        return dctx.add(QryOpt.Save, utf8m64, StorageInfo.UTF8Mb64TestStorageName, "tb_test_emoji_1").commit("Sessionid");
    }

    @Override
    public String getUtf8M64(int id)  {
        IChaExpr wheres = new FltExpr("id", OOpt.eq, id);
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IDQLCtx qctx = da.newQueryContext();
        UTF8M64 utf8m64 = qctx.useStorage(StorageInfo.UTF8Mb64TestStorageName).fromTable("tb_test_emoji_1") //指定到storage
                .loadObject("sessionId", UTF8M64.class, DrOpt.Rdr, wheres);
        return utf8m64.getV();
    }
}
