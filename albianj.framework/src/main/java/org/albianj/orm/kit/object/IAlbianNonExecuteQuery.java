package org.albianj.orm.kit.object;

import org.albianj.orm.kit.object.filter.IChainExpression;

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
