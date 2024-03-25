package org.albianj.scanner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.api.kernel.anno.serv.SetWhenOpt;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AblAutoAttr {
    /**
     * 不管是配置了id还是value，最后全部赋值给id
     * 注意：支持OGNL表达式
     * 当id以@或#开头，即为OGNL表达式
     */
    private String id;
    /**
     * field被赋值的时间点
     */
    private SetWhenOpt setWhenOpt = SetWhenOpt.BeforeInit;
    /**
     * 当赋值时
     */
    private boolean throwIfNull = false;
}
