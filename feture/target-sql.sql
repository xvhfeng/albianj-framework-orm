select（DataObj.clss).use(storagename).choose(read-router).from(table)
         .where(expr)
         .orderby(expr)
         .groupby(expr,having(expr))


         select（getter[]).use(storagename).choose(read-router).from(VO-table)
         .where(expr)
         .orderby(expr)
         .groupby(expr,having(expr))
         .commit(sessionid);


          select（getter[]).use(storagename).choose(read-router).from(table)
           .join(table,on())
            .where(expr)
            .orderby(expr)
            .groupby(expr,having(expr))
            .commit(sessionid);

            select（getter[],expr).use(storagename).choose(read-router).from(table)
            .join(table,on())
             .where(expr)
             .orderby(expr)
             .groupby(expr,having(expr))
             .commit(sessionid);

             insert(DataObj.clss).use(storagename).choose(read-router).into(table).set(getter[]).values(datd-obj...)

             insert(DataObj.clss).use(storagename).choose(read-router).into(table).set(getter[]).values(datd-obj...)


             delete(DataObj.clss).use(storagename).choose(read-router).from(table)
             where(expr)

             update(DataObj.clss).use(storagename).choose(read-router).from(table).set(getter[])
             .where(expr)

             insOrUpd(DataObj.clss).use(storagename).choose(read-router).from(table).set(getter[]).values(datas...)
             .where(expr)

             AblServRouter.newDMLCtx().add(insert...).add(delete).add(insOrUpd...).commit(session);

            AblServRouter.newDQLCtx().query(select...).commit(session);