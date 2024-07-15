package org.albianj.common.config;

/**
 * 占位符变量
 *  所有的占位符都使用${}来表示，支持的有：
 *  file:pathname 表示从文件系统中查找pathname的绝对路径
 *  classpath:name 表示classpath路径下面接name的路径，classpath/name,不管是路径还是文件，都一样
 *  ${name} 表示从进程的env中获取
 *  优先级：
 *      命令行参数。
 *      通过 System.getProperties() 获取的 Java 系统参数。
 *      操作系统环境变量。
 *      从 java:comp/env 得到的 JNDI 属性。
 *      通过 RandomValuePropertySource 生成的“random.*”属性。
 *      应用 Jar 文件之外的属性文件。(通过config.location参数，启动时指定的配置文件路径)
 *      应用 Jar 文件内部的属性文件。 （classpath中的参数）
 *      在应用配置 Java 类（包含“@Configuration”注解的 Java 类）中通过“@PropertySource”注解声明的属性文件。
 *      通过“SpringApplication.setDefaultProperties”声明的默认属性。
 */
public class PlaceHolderMatcher {
}
