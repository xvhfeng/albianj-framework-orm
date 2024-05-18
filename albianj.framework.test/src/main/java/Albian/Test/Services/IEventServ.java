package Albian.Test.Services;

import Albian.Test.Model.Impl.Event;
import org.albianj.api.kernel.service.IAblServ;

import java.util.List;

public interface IEventServ extends IAblServ {
    boolean insert();

    List<Event> load();
}
