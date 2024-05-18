package org.albianj.impl.dal.sqlpara.typeadapter.impl;

import org.albianj.impl.dal.sqlpara.typeadapter.ITypeAdapter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeTypeAdapter implements ITypeAdapter<ZonedDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss z");

    public  String unBox(ZonedDateTime date) {
        return date.format(formatter);
    }

    public ZonedDateTime toBox(Object value){
        if(String.class.isAssignableFrom(value.getClass())) {
            return ZonedDateTime.parse(value.toString(), formatter);
        } else {
            return ZonedDateTime.ofInstant(((Instant) value), ZoneId.systemDefault());
        }
    }
}
