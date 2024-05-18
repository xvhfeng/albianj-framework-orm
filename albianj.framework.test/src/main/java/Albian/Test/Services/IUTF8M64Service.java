package Albian.Test.Services;


import org.albianj.api.kernel.service.IAblServ;

public interface IUTF8M64Service extends IAblServ {

    public static String ServiceId = "UTF8M64Service";

    boolean saveUtf8M64(int id, String v) ;

    String getUtf8M64(int id) ;
}
