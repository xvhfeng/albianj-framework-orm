package org.albianj.impl.dal.sqlpara.typeadapter.impl;

import org.albianj.impl.dal.sqlpara.typeadapter.ITypeAdapter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public  class LocalDateTypeAdapter  implements ITypeAdapter<LocalDate> {

    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .appendOptional(DateTimeFormatter.ISO_DATE_TIME)
            .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
            .toFormatter();

    public  String unBox(LocalDate date) {
        return date.format(formatter);
    }

    public LocalDate toBox(Object value){
        if(String.class.isAssignableFrom(value.getClass())) {
            return LocalDate.parse(value.toString(), formatter);
        } else {
            return LocalDate.ofInstant(((Instant) value), ZoneId.systemDefault());
        }
    }
}
