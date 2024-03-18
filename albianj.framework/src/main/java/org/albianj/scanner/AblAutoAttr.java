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
    private String value;
    private String id;
    private SetWhenOpt setWhenOpt = SetWhenOpt.BeforeInit;
    private boolean throwIfNull = false;
}
