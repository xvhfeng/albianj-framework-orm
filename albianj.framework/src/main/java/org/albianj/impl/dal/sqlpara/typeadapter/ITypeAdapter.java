package org.albianj.impl.dal.sqlpara.typeadapter;

public interface ITypeAdapter<T> {
     String unBox(T value) ;
     T toBox(Object value);
}
