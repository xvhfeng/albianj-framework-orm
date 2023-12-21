
##AlbianJ使用文档

## 一、项目配置

基于springboot使用albianj
1.maven依赖
<dependency>
<groupId>albianj-framework</groupId>
<artifactId>albianj-framework-spring-starter</artifactId>
<version>201906-Alpha</version>
</dependency>
2.配置文件
目前配置文件只需要Kernel.properties,storage.xml,persistence.xml，log4j.xml
Kernel.properties albianj基础配置，基本不会有变动
storage.xml 数据库配置
persistence.xml 配置model包路径，让albianj扫描到例子：
<AlbianObjects>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<Packages>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<Package Path="com.example.demo.model.impl" Enable="true"></Package>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Packages>
</AlbianObjects>
log4j.xml 日志配置
需要在application.properties配置文件添加albianj.config配置（albianj配置文件路径）


##  二、生成代码
用代码生成器生成model，router，service代码
model：
model继承FreeAlbianObject 
model通过注解标识table，storage，name，router，还有接口信息。
@AlbianObjectRant(Interface = IMsgConfig.class, DataRouters = @AlbianObjectDataRoutersRant(DataRouter = MsgConfigDataRouter.class, ReaderRouters = {@AlbianObjectDataRouterRant(Name = "MsgConfig_r", StorageName = "ccInnerdataDBr", TableName = "msg_config")}, WriterRouters = { @AlbianObjectDataRouterRant(Name = "MsgConfig_w", StorageName = "ccInnerdataDB", TableName = "msg_config")}))

标识主键：@AlbianObjectDataFieldRant(IsPrimaryKey = true)

router：
如果不需要分库分表则不需要修改，直接用自动生成的代码即可，如果需要分库分别则需要进行分库分别规则处理。

mappingWriterRoutingStorage 写操作storage路由规则，返回数据为写storagename
mappingReaderRoutingStorage 读操作storage路由规则，返回数据为读storagename
mappingWriterTable 写table路由规则，返回数据为读写操作表
mappingReaderTable 读table路由规则，返回数据为读操作表

具体的路由规则在方法里面实现。

service：
对数据库表增删改查操作。需要注入IAlbianDataAccessService 注入方式有两种：
1.private final IAlbianDataAccessService dao = AlbianServiceRouter.getService(
        IAlbianDataAccessService.class, IAlbianDataAccessService.Name, true);
2.@Autowired
    private IAlbianDataAccessService dao;

在starter里面，会把albianj的服务丢到spring容器里面进行管理，因此注解方式也可以获取对应的service。

对数据库操作：
查询条件：不支持in操作
查询条件枚举LogicalOperation枚举 Equal 等于，NotEqual不等于：Greater大于，Less小于，GreaterOrEqual大于等于，LessOrEqual小于等于，Is，isnot
括号分组：分组里面进行多个or操作，括号不可以单独用
 IFilterGroupExpression group = new FilterGroupExpression();
group.add("CRID", LogicalOperation.Equal, CRid);
group.or("CRID", LogicalOperation.Equal, CRid);
排序：
LinkedList<IOrderByCondition> orderByConditions = new LinkedList<>();
            IOrderByCondition idxAsc = new OrderByCondition();
            idxAsc.setFieldName("CRID");
            idxAsc.setSortStyle(SortStyle.Asc);
            orderByConditions.add(idxAsc);
排序可以根据多个字段进行排序，添加多个OrderByCondition 就可以。


单个条件查询：
 IChainExpression where = new FilterExpression();
where.add("CRID", LogicalOperation.Equal, CRID);//等于
多个条件查询：
IChainExpression where = new FilterExpression();
            where.add("CRID", LogicalOperation.Equal, CRID);
            where.and("CBID", LogicalOperation.Equal, CBID);

返回单条数据
dao.loadObject（）
返回多条数据
dao.loadObjects（）




















