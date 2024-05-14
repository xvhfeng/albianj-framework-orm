package org.albianj.dal.impl.sqlpara;

import org.albianj.dal.impl.sqlpara.typeadapter.ITypeAdapter;
import org.albianj.dal.impl.sqlpara.typeadapter.impl.LocalDateTimeTypeAdapter;
import org.albianj.dal.impl.sqlpara.typeadapter.impl.LocalDateTypeAdapter;
import org.albianj.dal.impl.sqlpara.typeadapter.impl.ZonedDateTimeTypeAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class DbValueFormatter {

    private static ITypeAdapter localDTTA = new LocalDateTimeTypeAdapter();
    private static ITypeAdapter localDTA = new LocalDateTypeAdapter();
    private static ITypeAdapter localZTTA = new ZonedDateTimeTypeAdapter();

    public static Object toSqlValue(Object value){
        Class<?> cls = value.getClass();
        if(LocalDateTime.class.isAssignableFrom(cls)) {
            return localDTTA.unBox(value);
        } else if(LocalDate.class.isAssignableFrom(cls)) {
            return localDTA.unBox( value);
        }else if(ZonedDateTime.class.isAssignableFrom(cls)){
            return localZTTA.unBox(value);
        }
        return value.toString();
    }
}
