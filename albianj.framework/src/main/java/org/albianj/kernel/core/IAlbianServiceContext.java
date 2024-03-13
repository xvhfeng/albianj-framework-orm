package org.albianj.kernel.core;

import org.albianj.kernel.common.comment.Comments;

/**
 * Created by xuhaifeng on 17/2/19.
 */
@Comments("Albian Service的上下文")
public interface IAlbianServiceContext {
    Object getSessionId();

    void setSessionId(Object sessionId);

}
