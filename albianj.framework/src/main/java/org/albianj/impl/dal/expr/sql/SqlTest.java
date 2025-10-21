package org.albianj.impl.dal.expr.sql;

import org.albianj.api.dal.object.OOpt;
import org.albianj.impl.dal.expr.ISqlExpr;

public class SqlTest {

    public static  void main(String...argv){

//        Select.make().query(SelectField.makes(SelectField.builder().tableClass(SqlTest.class).field(SelectField.AllFields).build()))
//                .from(From.builder().table("table").build(),
//                        From.builder().tableClass(SqlTest.class).build(),
//                        From.builder().queryExpr(Select.make()).build())
//                .join(Join.builder().joinOpt(JoinOpt.Inner).tableClass(SqlTest.class).as("a").on(OnExpr.builder().build()).build())
//                .join(Join.builder().joinOpt(JoinOpt.Left).tableClass(SqlTest.class).as("a").on().build())
//                .join(Join.builder().joinOpt(JoinOpt.Right).tableClass(SqlTest.class).as("a").on().build())
//                .where()
//                .orderBy()
//                .groupBy()
//                .union(Select.make());


//        ISqlExpr select =  Select.builder()
//                .selectClass(SqlTest.class)
//                .selectFields(SelectField.makes(SelectField.builder().table("tablename").field("fieldname").as("asfieldname").build(),
//                        SelectField.builder().field("fieldname").as("asfieldname").build()))
//                .storage("").readRouter("").from(From.builder().table("table").build())
//                .dataRouter(null)
//                .join(Join.builder().style(JoinOpt.Inner).table("join table").as("jointable as")
//                        .on(OnExpr.builder().leftTable("").leftField("").opt(OOpt.eq).rightTable("").rightField("")
//                                .and(OnExpr.builder().leftTable("").leftField("").opt(OOpt.eq).rightTable("").rightField("")
//                                        .and(OnExpr.builder().leftTable("").leftField("").opt(OOpt.eq).value()
//                                        .build())
//
//                                .build()).build())
//                .build();
//        String sql = select.toSqlText();
//        System.out.println(sql);
//
//        Select query = Select.builder().from(From.builder().table("").build()).build();
//        Select.builder().from(From.builder().queryExpr(query).as("child table").build());
    }
}
