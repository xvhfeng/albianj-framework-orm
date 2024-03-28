package org.albianj.dal.api.context.dactx;

public enum QryOpt {
    Create(1),
    Update(2),
    Save(3),
    Delete(4);

    private int val;
    QryOpt(int val){
        this.val = val;
    }

    public int getVal(){
        return this.val;
    }
}
