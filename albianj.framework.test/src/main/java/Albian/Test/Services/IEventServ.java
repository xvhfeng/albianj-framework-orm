package Albian.Test.Services;

import Albian.Test.Model.Impl.Event;
import org.albianj.api.kernel.service.IAlbianService;

import java.util.List;

public interface IEventServ extends IAlbianService {
    boolean insert();

    List<Event> load();
}
