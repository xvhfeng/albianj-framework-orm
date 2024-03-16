package Albian.Test.Services.Impl;

import Albian.Test.Model.Impl.UTF8M64;
import Albian.Test.Services.IUTF8M64Service;
import Albian.Test.Services.Metadata.StorageInfo;
import org.albianj.api.kernel.anno.serv.AlbianServiceFieldRant;
import org.albianj.api.kernel.anno.serv.AlbianServiceFieldType;
import org.albianj.api.kernel.anno.serv.AlbianServiceRant;
import org.albianj.api.kernel.service.FreeAlbianService;
import org.albianj.api.dal.context.dactx.IIduCtx;
import org.albianj.api.dal.context.dactx.ISltCtx;
import org.albianj.api.dal.context.dactx.QueryOpt;
import org.albianj.api.dal.object.OperatorOpt;
import org.albianj.api.dal.object.filter.FilterExpression;
import org.albianj.api.dal.object.filter.IChainExpression;
import org.albianj.api.dal.service.IAlbianDataAccessService;
import org.albianj.api.dal.service.QueryToOpt;


@AlbianServiceRant(Id = "UTF8M64Service")
public class UTF8M64Service extends FreeAlbianService implements IUTF8M64Service {

    @AlbianServiceFieldRant(Type = AlbianServiceFieldType.Ref, Value = "AlbianDataAccessService")
    private IAlbianDataAccessService da;

    @Override
    public boolean saveUtf8M64(int id, String v)  {
        UTF8M64 utf8m64 = new UTF8M64();
        IIduCtx dctx = da.newDataAccessContext();
        utf8m64.setId(id);
        utf8m64.setV(v);
        return dctx.add(QueryOpt.Save, utf8m64, StorageInfo.UTF8Mb64TestStorageName, "tb_test_emoji_1").commit("Sessionid");
    }

    @Override
    public String getUtf8M64(int id)  {
        IChainExpression wheres = new FilterExpression("id", OperatorOpt.eq, id);
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        ISltCtx qctx = da.newQueryContext();
        UTF8M64 utf8m64 = qctx.useStorage(StorageInfo.UTF8Mb64TestStorageName).fromTable("tb_test_emoji_1") //指定到storage
                .loadObject("sessionId", UTF8M64.class, QueryToOpt.ReaderRouter, wheres);
        return utf8m64.getV();
    }
}
