package org.albianj.kernel.attr;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.common.utils.StringsUtil;

/**
 * Created by xuhaifeng on 16/5/31.
 */
@Data
@NoArgsConstructor
public class AlbianServiceAopAttribute {
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
        if (all) return true;
        return StringsUtil.isNullOrEmptyOrAllSpace(this.fullName)
                ?
                StringsUtil.isNullOrEmptyOrAllSpace(this.beginWith) ? true : name.startsWith(this.beginWith)
                        && StringsUtil.isNullOrEmptyOrAllSpace(this.notBeginWith) ? true : !name.startsWith(this.notBeginWith)
                        && StringsUtil.isNullOrEmptyOrAllSpace(this.endWith) ? true : name.endsWith(this.endWith)
                        && StringsUtil.isNullOrEmptyOrAllSpace(this.notEndWith) ? true : !name.endsWith(this.notEndWith)
                        && StringsUtil.isNullOrEmptyOrAllSpace(this.contain) ? true : name.contains(this.contain)
                        && StringsUtil.isNullOrEmptyOrAllSpace(this.notContain) ? true : !name.contains(this.notContain)
                : name.equals(this.fullName);

    }
}
