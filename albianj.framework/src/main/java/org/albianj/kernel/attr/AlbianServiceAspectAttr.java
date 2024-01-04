package org.albianj.kernel.attr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.common.utils.CheckUtil;

/**
 * Created by xuhaifeng on 16/5/31.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AlbianServiceAspectAttr {
    String serviceName = null;
    String beginWith = null;
    String notBeginWith = null;
    String endWith = null;
    String notEndWith = null;
    String contain = null;
    String notContain = null;
    String fullName = null;
    boolean all = false;
    String proxyName = "";

    public boolean matches(String name) {
        if (this.all) return true;
        return CheckUtil.isNullOrEmptyOrAllSpace(this.fullName)
                ?
                CheckUtil.isNullOrEmptyOrAllSpace(this.beginWith) || name.startsWith(this.beginWith)
                        && CheckUtil.isNullOrEmptyOrAllSpace(this.notBeginWith) || (!name.startsWith(this.notBeginWith)
                        && CheckUtil.isNullOrEmptyOrAllSpace(this.endWith) || (name.endsWith(this.endWith)
                        && CheckUtil.isNullOrEmptyOrAllSpace(this.notEndWith) || (!name.endsWith(this.notEndWith)
                        && CheckUtil.isNullOrEmptyOrAllSpace(this.contain) || (name.contains(this.contain)
                        && CheckUtil.isNullOrEmptyOrAllSpace(this.notContain) || !name.contains(this.notContain)))))
                : name.equals(this.fullName);

    }
}
