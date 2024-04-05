package org.albianj.kernel.api.anno.serv;

public enum AblFnOpt {

    Normal(0),
    Ctor(1),
    Dtor(2),
    Factory(3);

    private int val;

    AblFnOpt(int val){
        this.val = val;
    }

    public int getVal(){
        return  this.val;
    }
}
