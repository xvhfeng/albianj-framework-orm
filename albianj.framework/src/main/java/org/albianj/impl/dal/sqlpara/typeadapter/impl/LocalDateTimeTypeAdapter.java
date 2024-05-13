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


    DateTimeFormatter f = DateTimeFormatter.ofPattern ( "yyyy-MM-dd HH:mm:ss" );
//            .parseDefaulting(ChronoField.MICRO_OF_SECOND,0)
//            .toFormatter();


    public  String unBox(LocalDateTime date) {
        return date.format(f);
    }

    public LocalDateTime toBox(Object value){
        if(String.class.isAssignableFrom(value.getClass())) {
            return LocalDateTime.parse(value.toString(), f);
        } else {
            return LocalDateTime.ofInstant(((Instant) value),ZoneId.systemDefault());
        }
    }
}