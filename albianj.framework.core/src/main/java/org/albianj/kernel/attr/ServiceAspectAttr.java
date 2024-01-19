package org.albianj.kernel.attr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.albianj.common.utils.StringsUtil;

/**
 * Created by xuhaifeng on 16/5/31.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServiceAspectAttr {
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
        return StringsUtil.isNullOrEmptyOrAllSpace(this.fullName)
                ?
                StringsUtil.isNullOrEmptyOrAllSpace(this.beginWith) || name.startsWith(this.beginWith)
                        && StringsUtil.isNullOrEmptyOrAllSpace(this.notBeginWith) || (!name.startsWith(this.notBeginWith)
                        && StringsUtil.isNullOrEmptyOrAllSpace(this.endWith) || (name.endsWith(this.endWith)
                        && StringsUtil.isNullOrEmptyOrAllSpace(this.notEndWith) || (!name.endsWith(this.notEndWith)
                        && StringsUtil.isNullOrEmptyOrAllSpace(this.contain) || (name.contains(this.contain)
                        && StringsUtil.isNullOrEmptyOrAllSpace(this.notContain) || !name.contains(this.notContain)))))
                : name.equals(this.fullName);

    }
}
