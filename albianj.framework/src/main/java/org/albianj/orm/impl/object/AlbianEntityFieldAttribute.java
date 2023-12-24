package org.albianj.orm.impl.object;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbianEntityFieldAttribute extends MemberAttribute { // implements IAlbianEntityFieldAttribute {

    /*
        Name is the field name,maybe begin with '_' or word
        PropertyName is the property name,allways the setter and getter name,
                    but it must begin with lower letter,and  begin without '_'
        FieldName is the sql field name,the same as sql,default is PropertyName with begin with upper letter


        in the map,key is PropertyName with all lower letter and value is IAlbianEntityFieldAttribute
        so if you make where must use PropertyName
     */
    private Field entityField = null;
    private String propertyName = null;
    private Method propertySetter = null;
    private Method propertyGetter = null;

}
