package org.albianj.orm.impl.rant;

import org.albianj.kernel.common.utils.StringsUtil;

public class FieldConvert {

    public static String fieldName2SqlFieldName(String fieldName) {
        return fieldName2PropertyName(fieldName);
    }

    public static String fieldName2PropertyName(String fieldName) {
        if (fieldName.startsWith("_")) {
            char[] fieldNames = fieldName.toCharArray();
            if ('A' <= fieldNames[1] && 'Z' >= fieldNames[1])
                fieldNames[1] += 32;
            return new String(fieldNames, 1, fieldNames.length - 1);
        }
        return StringsUtil.lowercasingFirstLetter(fieldName);
    }
}
