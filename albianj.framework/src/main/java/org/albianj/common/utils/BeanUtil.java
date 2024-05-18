package org.albianj.common.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanUtil {
    /**
     * copy fields value from source to dest
     * @param dest 目标对象
     * @param src 源对象
     * @param excptFields no need copy field in dest
     * @param fieldMapping no same name field bewtten dest and src
     *                     mapping: key: source field name
     *                              value: dest field name
     * @param <T>
     * @param <F>
     */
    public  static <T,F> void copy(T dest, F src, Set<String> excptFields, Map<String,String> fieldMapping){
        try {
            if(null == dest || null == src){
                return;
            }

            Field[] fFields = src.getClass().getDeclaredFields();
            if(SetUtil.isNullOrEmpty(fFields)) {
                return;
            }
            for (Field f : fFields){
                String fName = f.getName();
                String tName = null;
                if(!SetUtil.isNullOrEmpty(fieldMapping) && fieldMapping.containsKey(fName)) {
                    tName = fieldMapping.get(fName);
                } else {
                    tName = fName;
                }

                if(!SetUtil.isNullOrEmpty(excptFields) && excptFields.contains(tName)){
                    continue;
                }

               Field t =  dest.getClass().getDeclaredField(tName);
                if(null != t){
                    t.setAccessible(true);
                    f.setAccessible(true);

                    t.set(dest,f.get(src));
                }
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public  static <T,F> void copy(T dest, F src) {
        copy(dest,src,null,null);
    }
}
