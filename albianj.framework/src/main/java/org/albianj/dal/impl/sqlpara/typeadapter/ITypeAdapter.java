package org.albianj.dal.impl.sqlpara.typeadapter;

public interface ITypeAdapter<T> {
     String unBox(T value) ;
     T toBox(Object value);
}
