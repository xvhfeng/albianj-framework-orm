package org.albianj.orm.context.dactx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.orm.object.IAlbianObject;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AlbianObjectWarp {
    private int persistenceOpt = AlbianDataAccessOpt.Save;
    private IAlbianObject entry = null;
    private String storageAliasName = null;
    private String tableAliasName = null;
    private boolean queryIdentitry = false;

    public AlbianObjectWarp(int opt, IAlbianObject entry) {
        this.persistenceOpt = opt;
        this.entry = entry;
    }

//    public AlbianObjectWarp() {
//    }
//
//    @Override
//    public int getPersistenceOpt() {
//        return opt;
//    }
//
//    @Override
//    public void setPersistenceOpt(int opt) {
//        this.opt = opt;
//    }
//
//    @Override
//    public IAlbianObject getEntry() {
//        return entry;
//    }
//
//    @Override
//    public void setEntry(IAlbianObject entry) {
//        this.entry = entry;
//    }
//
//    @Override
//    public String getStorageAliasName() {
//        return this.storageAliasName;
//    }
//
//    @Override
//    public void setStorageAliasName(String storageAliasName) {
//        this.storageAliasName = storageAliasName;
//    }
//
//    @Override
//    public String getTableAliasName() {
//        return this.tableAliasName;
//    }
//
//    @Override
//    public void setTableAliasName(String tableAliasName) {
//        this.tableAliasName = tableAliasName;
//    }
//
//    @Override
//    public boolean isQueryIdentitry() {
//        return this.queryIdentitry;
//    }
//
//    @Override
//    public void setQueryIdentitry(boolean queryIdentitry) {
//        this.queryIdentitry = queryIdentitry;
//    }


}
