package org.albianj.scanner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.kernel.api.attr.IAblAnnoResolver;

import java.lang.annotation.Annotation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnoParserAdpter {
    private String annoFullName;
    private Class<? extends Annotation> anno;
    private IAblAnnoResolver parser;
}
