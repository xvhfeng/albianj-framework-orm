package org.albianj.api.dal.context.dactx;

public enum QueryOpt {
    Create(1),
    Update(2),
    Save(3),
    Delete(4);

    private int val;
    QueryOpt(int val){
        this.val = val;
    }

    public int getVal(){
        return this.val;
    }
}
