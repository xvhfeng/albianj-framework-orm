package org.albianj.dal.api.object.filter;

public enum ExprOpt {

    Filter(1,"Filter Expr"),
    FilterGroup(2,"Filter Group Expr");

    private int key;
    private String word;

    ExprOpt(int key, String word){
        this.key = key;
        this.word = word;
    }
    public String getWord(){
        return this.word;
    }

}
