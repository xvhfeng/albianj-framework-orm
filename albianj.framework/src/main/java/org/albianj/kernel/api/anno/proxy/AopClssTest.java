package org.albianj.kernel.api.anno.proxy;


@AblAopAnno
public class AopClssTest {

    @AblAopPointAnno(
            when = AopFlag.Brf | AopFlag.Aft,
//            classes = @AblWatchClassAnno(watch ={ AopClssTest.class},exclusion = AopClssTest.class),
//            pkgs = @AblWatchPkg(watch = "",exclusion = ""),
            raises = @AblWatchThrow(watch = Throwable.class,exclusion = Exception.class),
            beginWith = "get",
            expr = "/*get$/",
            tagAnnos = AblAopIgnoreAnno.class
    )
    public void runBeforeMethod() {
        /**
         select（DataObj.clss).use(storagename).choose(read-router).from(table)
         .where(expr)
         .orderby(expr)
         .groupby(expr,having(expr))


         select（getter[]).use(storagename).choose(read-router).from(VO-table)
         .where(expr)
         .orderby(expr)
         .groupby(expr,having(expr))
         .build();


          select（getter[]).use(storagename).choose(read-router).from(table)
           .join(table,on())
            .where(expr)
            .orderby(expr)
            .groupby(expr,having(expr))
         .build();

            select（getter[],expr).use(storagename).choose(read-router).from(table)
            .join(table,on())
             .where(expr)
             .orderby(expr)
             .groupby(expr,having(expr))
             .build()

             insert(DataObj.clss).use(storagename).choose(read-router)
            .into(table).set(getter[]).values(datd-obj...)
            .build()

             insert(DataObj.clss).use(storagename).choose(read-router).into(table).set(getter[]).values(datd-obj...)


             delete(DataObj.clss).use(storagename).choose(read-router).from(table)
             where(expr)

             update(DataObj.clss).use(storagename).choose(read-router).from(table).set(getter[])
             .where(expr)

             insOrUpd(DataObj.clss).use(storagename).choose(read-router).from(table).set(getter[]).values(datas...)
             .where(expr)

             AblServRouter.newDMLCtx().add(insert...).add(delete).add(insOrUpd...).commit(session);

            AblServRouter.newDQLCtx().query(select...).commit(session);


         */
    }

    public void runAfterMethod(){

    }

    public void runWhenThrow() {

    }
}
