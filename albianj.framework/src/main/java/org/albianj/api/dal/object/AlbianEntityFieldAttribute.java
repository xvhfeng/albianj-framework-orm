package org.albianj.api.dal.object;



import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Types;

@Data
@NoArgsConstructor
public class AlbianEntityFieldAttribute  {

    /*
        Name is the field name,maybe begin with '_' or word
        PropertyName is the property name,allways the setter and getter name,
                    but it must begin with lower letter,and  begin without '_'
        FieldName is the sql field name,the same as sql,default is PropertyName with begin with upper letter


        in the map,key is PropertyName with all lower letter and value is IAlbianEntityFieldAttribute
        so if you make where must use PropertyName
     */


    private String name = null;
    private String sqlFieldName = null;
    private boolean allowNull = true;
    private int length = -1;
    private boolean primaryKey = false;
    private int databaseType = Types.NVARCHAR;
    private boolean save = true;
    private String varField = null;
    private boolean autoGenKey = false;

    private Field entityField = null;
    private String propertyName = null;
    private Method propertySetter = null;
    private Method propertyGetter = null;
}
