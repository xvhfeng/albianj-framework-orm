package Albian.Test.Services.Impl;

import Albian.Test.Model.Impl.Event;
import Albian.Test.Services.IEventServ;
import org.albianj.AblServRouter;
import org.albianj.dal.api.context.dactx.IDMLCtx;
import org.albianj.dal.api.context.dactx.IDQLCtx;
import org.albianj.dal.api.context.dactx.QryOpt;
import org.albianj.dal.api.object.OOpt;
import org.albianj.dal.api.object.filter.FltExpr;
import org.albianj.dal.api.object.filter.IChaExpr;
import org.albianj.dal.api.service.DrOpt;
import org.albianj.dal.api.service.IAlbianDataAccessService;
import org.albianj.kernel.api.anno.serv.AblServAnno;
import org.albianj.kernel.api.anno.serv.AblServiceRant;
import org.albianj.kernel.api.anno.serv.AlbianServiceFieldRant;
import org.albianj.kernel.api.anno.serv.AlbianServiceFieldType;
import org.albianj.kernel.api.logger.LogLevel;
import org.albianj.kernel.api.service.FreeAlbianService;

import java.time.LocalDateTime;
import java.util.List;


@AblServiceRant(Id= "EventServ",Interface = IEventServ.class)
public class EventServ extends FreeAlbianService implements IEventServ {
    @AlbianServiceFieldRant(Type = AlbianServiceFieldType.Ref, Value = "AlbianDataAccessService")
    private IAlbianDataAccessService da;

    @Override
    public boolean insert(){
        Event e = AblServRouter.newInstance("SessionId", Event.class);
        e.setUid("action from albianj");
        e.setAction(AblServRouter.make32UUID());
        e.setFunction("action from albianj");
        e.setBid("action from albianj");
        e.setCid("action from albianj");
        e.setLoginId("action from albianj");
        e.setTimes(LocalDateTime.now());
        e.setUrl("action from albianj");
        e.setModule("action from albianj");

        AblServRouter.log("batch", LogLevel.Info,"");
        IDMLCtx dctx = da.newDataAccessContext();
        for(int i = 0;i < 100;i++){
            dctx.add(QryOpt.Create, e);
        }
        return dctx.commit("Sessionid");
    }

    @Override
    public List<Event> load(){
        IChaExpr wheres = new FltExpr(Event::getAction, OOpt.eq, "action1");
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IDQLCtx qctx = da.newQueryContext();
        List<Event> events = qctx //指定到storage
                .loadObjects("sessionId", Event.class, DrOpt.Rdr, wheres);
        return events;
    }
}
