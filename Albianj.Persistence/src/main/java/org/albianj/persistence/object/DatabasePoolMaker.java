package org.albianj.persistence.object;

import org.albianj.persistence.db.IDataBasePool;

/**
 * project : albianj-framework
 *
 * @ccversion 支持自定义创建Database - liyuqi 2019-07-01 17:14</br>
 */
public interface DatabasePoolMaker {

    /**
     * 返回支持的Style
     *
     * @return style
     */
    IDataBasePool support(String style);
}
