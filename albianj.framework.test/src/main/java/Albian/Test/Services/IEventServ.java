package Albian.Test.Services;

import Albian.Test.Model.Impl.Event;
import org.albianj.dal.api.object.IAblObj;
import org.albianj.kernel.api.service.IAblServ;
import org.albianj.kernel.api.service.IAlbianService;

import java.util.List;

public interface IEventServ extends IAlbianService {
    boolean insert();

    List<Event> load();
}
