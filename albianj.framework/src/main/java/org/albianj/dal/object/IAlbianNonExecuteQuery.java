package org.albianj.dal.object;

import org.albianj.dal.object.filter.IChainExpression;

/**
 * Created by xuhaifeng on 16/12/28.
 */
public interface IAlbianNonExecuteQuery {

    Class<? extends IAlbianObject> getAlbianObjectClass();

    void setAlbianObjectClass(Class<? extends IAlbianObject> albianObjectClass);

    String getInnerCommandText();

    void setInnerCommandText(String innerCommandText);

    IChainExpression getCommandFilter();

    void setCommandFilter(IChainExpression commandFilter);
}
