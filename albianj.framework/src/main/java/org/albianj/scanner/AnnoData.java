package org.albianj.scanner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.albianj.api.kernel.attr.IAblServAttrParser;

import java.lang.annotation.Annotation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnoData {
    private String annoFullName;
    private Class<? extends Annotation> anno;
    private IAblServAttrParser parser;
}
