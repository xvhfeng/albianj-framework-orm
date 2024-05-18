package Albian.Test.Services.Impl;

import Albian.Test.Model.Impl.Event;
import Albian.Test.Services.IEventServ;
import org.albianj.AblServRouter;
import org.albianj.api.dal.context.dactx.IDMLCtx;
import org.albianj.api.dal.context.dactx.IDQLCtx;
import org.albianj.api.dal.context.dactx.QryOpt;
import org.albianj.api.dal.object.OOpt;
import org.albianj.api.dal.object.filter.FltExpr;
import org.albianj.api.dal.object.filter.IChaExpr;
import org.albianj.api.dal.service.DrOpt;
import org.albianj.api.dal.service.IAlbianDataAccessService;
import org.albianj.api.kernel.anno.serv.AblServFieldRant;
import org.albianj.api.kernel.anno.serv.AblServFieldType;
import org.albianj.api.kernel.anno.serv.AblServRant;
import org.albianj.api.kernel.logger.LogLevel;
import org.albianj.api.kernel.service.FreeAblServ;

import java.time.LocalDateTime;
import java.util.List;


@AblServRant
public class EventServ extends FreeAblServ implements IEventServ {
    @AblServFieldRant(Type = AblServFieldType.Ref, Value = "AlbianDataAccessService")
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
