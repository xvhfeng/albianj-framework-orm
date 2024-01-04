package Albian.Test.Model.Impl;

import Albian.Test.Model.IUTF8M64;
import org.albianj.orm.itf.object.FreeAlbianObject;
import org.albianj.orm.anno.AlbianObjectDataFieldRant;
import org.albianj.orm.anno.AlbianObjectRant;

@AlbianObjectRant(Interface = IUTF8M64.class)
public class UTF8M64 extends FreeAlbianObject implements IUTF8M64 {

    @AlbianObjectDataFieldRant(IsPrimaryKey = true, DbFieldName = "id")
    private int id = 0;

    @AlbianObjectDataFieldRant(DbFieldName = "v")
    private String v;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getV() {
        return this.v;
    }

    @Override
    public void setV(String v) {
        this.v = v;
    }
}
