package org.albianj.impl.dal.expr.sql;

import lombok.*;
import org.albianj.api.dal.object.IAblDr;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.impl.dal.expr.ISqlExpr;
import org.albianj.impl.dal.expr.SqlExprOpt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Select implements ISqlExpr {
    private SqlExprOpt  exprOpt = SqlExprOpt.Select;
    private Class<?> selectClass;
    private List<SelectField> selectFields;
    private String storage;
    private String readRouter;
    private List<From> froms;
    private IAblDr dataRouter;
    private List<Join> joins = new ArrayList<>();
    private String as;

    public static Select make(){
        return new Select();
    }

    public Select query(SelectField... selectFields){
        List<SelectField> fs = Arrays.stream(selectFields).toList();
        query(fs);
        return this;
    }

    public Select query(List<SelectField>  selectFields){
        this.selectFields = selectFields;
        return this;

    }

    public Select from(From... froms){
        List<From> fs = Arrays.stream(froms).toList();
        from(fs);
        return this;
    }

    public Select from(List<From>  froms){
        this.froms = froms;
        return this;
    }

    public Select join(Join join){
        joins.add(join);
        return this;
    }

    public Select where(){
        return this;
    }

    public Select orderBy(){
        return this;
    }

    public Select groupBy(){
        return this;
    }

    public Select union(Select slt){
        return this;
    }






//    private MybpSFunction[] _getters = null；
//    public <T,R> Select(MybpSFunction<T,R>... getters) {
//        this._getters = getters;
//    }

//    public <T,R> Select() {
//        this._getters = getters;
//    }
    @Override
    public String toSqlText(){
        //make fields info
        StringBuilder sb = new StringBuilder();
//        if(null != this.clzz) {
//
//        }
//        if(SetUtil.isNotEmpty(_getters)) {
//
//        }

        return StringsUtil.nonIdxFmt("{} {}",exprOpt.getKeyName(),sb.toString());
    }
}
