package org.albianj.api.dal.context.dactx;

/**
 * 操作数据库的方式
 */
public enum QryOpt {
    /**
     * 直接在数据库中insert记录
     */
    Create(1),
    /**
     * 在数据库中update已经存在的记录
     */
    Update(2),
    /**
     * 当记录存在的时候，执行update操作，不存在的时候执行insert操作
     * 该机制需要首先使用load方法从数据库中尝试获取数据，并且在load返回的数据上更改
     */
    Save(3),
    /**
     * 在数据库中根据主键删除对应的记录
     */
    Delete(4),
    /**
     * 数据库的insert or update功能，目前支持mysql和pgsql
     * 和save类似，但是不需要使用load先数据库中获取数据,且字段值为null，直接不会写入sql语句
     * 直接根据对应的数据库生成upsert语句（所以，需要数据库具备语法原生支持）
     */
    Upsert(5);

    private int val;
    QryOpt(int val){
        this.val = val;
    }

    public int getVal(){
        return this.val;
    }
}
