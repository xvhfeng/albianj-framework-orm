package org.albianj.impl.dal.sqlpara.typeadapter.impl;

import org.albianj.impl.dal.sqlpara.typeadapter.ITypeAdapter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public  class LocalDateTimeTypeAdapter  implements ITypeAdapter<LocalDateTime> {
    private static DateTimeFormatter chinese = DateTimeFormatter.ofPattern ( "yyyy-MM-dd HH:mm:ss" );
    private static DateTimeFormatter fmt = new DateTimeFormatterBuilder()
        .appendOptional(chinese)
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSS"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))
            .appendOptional(DateTimeFormatter.ISO_DATE_TIME)
                .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0).toFormatter();
//            .parseDefaulting(ChronoField.MICRO_OF_SECOND,0)
//            .toFormatter();


    public  String unBox(LocalDateTime date) {
        return date.format(chinese);
    }

    public LocalDateTime toBox(Object value){
        if(String.class.isAssignableFrom(value.getClass())) {
            return LocalDateTime.parse(value.toString(), fmt);
        } else {
            return LocalDateTime.ofInstant(((Instant) value),ZoneId.systemDefault());
        }
    }
}