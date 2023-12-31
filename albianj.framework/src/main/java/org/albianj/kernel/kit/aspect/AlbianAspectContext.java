package org.albianj.kernel.kit.aspect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by xuhaifeng on 16/7/25.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AlbianAspectContext { //} implements IAlbianAopContext {
    private Object data = null;
}
