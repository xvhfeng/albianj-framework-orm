package org.albianj.impl.dal.expr.sql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectField {

    public static  final String AllFields = "*";

    /**
     * 需要查询的表的实体
     */
    private Class<?> tableClass;

    /**
     * sql字段所在的表
     * 可能是表的名字也可能是表的别名
     * 为空的时候即默认为from的中的表名
     */
    private String table;

    /**
     * 数据库中sql字段的名称
     */
    private String field;
    /**
     * 数据库中sql字段as的名称
     */
    private String as;

    /**
     * 对于字段使用的函数
     */
    private ISqlFuncExpr sqlFuncExpr;

    /**
     * 创建查询的字段列表
     * 主要为链式写法提供方便
     * @param fields
     * @return
     */
    public static List<SelectField> makes(SelectField... fields){
        List<SelectField> list = new ArrayList<>();
        for(SelectField f : fields) {
            list.add(f);
        }
        return list;
    }
}
