package org.albianj.kernel.api.anno;

public @interface AblServScanAnno {
    /**
     * 待扫描IOC与AOP的包路径
     * @return
     */
    String[] packages() default {};
}
