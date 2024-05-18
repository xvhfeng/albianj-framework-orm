package org.albianj.api.dal.context.dactx;

public enum QryOpt {
    Create(1),
    Update(2),
    Save(3),
    Delete(4),
    Upsert(5);

    private int val;
    QryOpt(int val){
        this.val = val;
    }

    public int getVal(){
        return this.val;
    }
}
