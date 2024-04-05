package org.albianj.scanner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.kernel.api.anno.serv.SetWhenOpt;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoSetAnnoAttr {
    /**
     * field的值
     */
    private String value;
    /**
     * field被赋值的时间点
     */
    private SetWhenOpt setWhenOpt = SetWhenOpt.BeforeInit;
    /**
     * 当赋值时
     */
    private boolean throwIfNull = false;
}
