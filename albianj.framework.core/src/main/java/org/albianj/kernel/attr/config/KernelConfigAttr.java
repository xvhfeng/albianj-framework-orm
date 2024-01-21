package org.albianj.kernel.attr.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KernelConfigAttr {
    private String machineKey = null;
    private String machineId = null ;
    private String appName = null;
}
