package org.albianj.scanner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AopAnnoAttr {

    /**
     * 监听的class集合
     */
    private Class<?>[] classes = null;
    /**
     * 监听的package的集合
     */
    private String[] pkgs = null;
    /**
     * 不需要监听的class的集合
     */
    private Class<?>[] exclusionClasses = null;
    /**
     * 不需要监听的包的集合
     */
    private String[] exclusionPkgs = null;
}
