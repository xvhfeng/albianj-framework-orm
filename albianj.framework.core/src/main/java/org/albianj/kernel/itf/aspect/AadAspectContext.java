package org.albianj.kernel.itf.aspect;

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
public class AadAspectContext { //} implements IAlbianAopContext {
    private Object data = null;
}
