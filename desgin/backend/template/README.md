# 机油监测平台后端代码介绍
此后端代码基于Java语言和SprngBoot框架开发，输入数据库为Clickhouse，输出数据库为Postgresql

**oil-common文件夹**和**oil-pojo文件夹**主要存储的是辅助文件，
功能代码存储在**oil-template文件夹**里

接下来对**oil-template文件夹**进行详细介绍

## [docker-compose.yml](oil-template/docker-compose.yml)
此文件主要用于服务器多容器部署

## [Dockerfile](oil-template/Dockerfile)
用于构建后端服务的 Docker 镜像

## [pom.xml](oil-template/pom.xml)
Maven 构建配置文件，定义依赖、插件等

## target文件夹
target 文件夹是 Maven 构建系统生成的编译输出目录，包含编译后的类文件、打包后的 JAR 文件、资源文件等。
它不需要手动维护，但对部署和调试非常重要

## src文件夹
此文件夹是该 Java 后端项目的核心源码目录，遵循标准的 Maven 项目结构

整个项目代码都集中在 src/main/java/com/oil 文件夹下，接下来对于文件进行介绍

### config文件夹（项目配置核心模块）
该目录下的类负责整个系统的配置与初始化工作，包括数据库、多数据源、跨域、MVC配置和 WebSocket 支持， 
是 Spring Boot 应用中非常关键的一部分

#### [ClickhouseDataSourceConfig.java](oil-template/src/main/java/com/oil/config/ClickhouseDataSourceConfig.java)
作用：配置连接 ClickHouse 数据库的数据源

使用 DataSourceBuilder 手动创建 ClickHouse 的 JDBC 连接

#### [PostgresDataSourceConfig.java](oil-template/src/main/java/com/oil/config/PostgresDataSourceConfig.java)
作用：配置连接 PostgreSQL 数据库的数据源

使用 DataSourceBuilder 手动创建 PostgreSQL 的 JDBC 连接

#### [MybatisPlusInterceptionConfig.java](oil-template/src/main/java/com/oil/config/MybatisPlusInterceptionConfig.java)
作用：配置 MyBatis Plus 的插件系统

添加分页插件、性能分析插件、数据权限控制等，提高对数据库查询的灵活性与可扩展性

#### [CorsConfiguration.java](oil-template/src/main/java/com/oil/config/CorsConfiguration.java)
作用：开启并配置跨域请求（CORS）

支持前后端分离场景中浏览器的跨域调用，保证前端可以安全地访问后端接口

#### [MvcConfig.java](oil-template/src/main/java/com/oil/config/MvcConfig.java)
作用：自定义 Spring MVC 行为

注册拦截器，配置静态资源访问路径

#### [WebSocketConfiguration.java](oil-template/src/main/java/com/oil/config/WebSocketConfiguration.java)
作用：配置 WebSocket 支持

注册端点，设置消息代理、STOMP 协议路径，支持后端主动向客户端推送消息

### controller文件夹（接口控制器模块）
该目录下包含了系统中各项功能的 HTTP 控制器类，负责接收前端请求、调用 service 层处理业务逻辑，
并将结果返回给前端。属于典型的 MVC 架构中的 C（Controller）层

#### [OilLifeController.java](oil-template/src/main/java/com/oil/controller/OilLifeController.java)
管理车辆查询信息：机油寿命信息、TBOX工况信息、工况损失分布情况、TBOX恶劣工况分布、历史换油记录、机油更换时间

支持车辆信息查询等接口

前端发出查询请求，负责接收前端请求、调用 service 层处理业务逻辑，并将结果返回给前端

#### [OilSampleController.java](oil-template/src/main/java/com/oil/controller/OilSampleController.java)
管理油品信息

支持机油样本新增、查询等接口

#### [VehicleController.java](oil-template/src/main/java/com/oil/controller/VehicleController.java)
管理车辆信息

支持车辆新增、修改、查询等接口

#### [UserController.java](oil-template/src/main/java/com/oil/controller/UserController.java)
用户注册、登录、权限验证

支持用户管理、修改密码、查询信息等接口

#### [TodoController.java](oil-template/src/main/java/com/oil/controller/TodoController.java)
管理申请列表

支持同意申请、拒绝申请、查看申请等接口

### handler文件夹（全局异常处理模块）
#### [GlobalExceptionHandler.java](oil-template/src/main/java/com/oil/handler/GlobalExceptionHandler.java)
统一处理后端系统中的运行时异常，并返回清晰、规范的错误响应

### interceptor文件夹（登录拦截与权限校验模块）
#### [LoginInterceptor.java](oil-template/src/main/java/com/oil/interceptor/LoginInterceptor.java)
实现用户身份验证的拦截器，主要用于登录校验

### mapper文件夹（数据库模块）
这个包进一步划分为两个子包，分别对应两个不同的数据源：ClickHouse和PostgreSQL

#### ClickHouse文件夹
##### [TboxMapper.java](oil-template/src/main/java/com/oil/mapper/clickhouse/TboxMapper.java)
操作 ClickHouse 中的 TBox 数据表，查询车辆运行参数、实时状态或历史大数据记录

#### PostgreSQL文件夹
这个包主要通过 MyBatis 操作 PostgreSQL 数据库中与油品、车辆、用户等相关的表

##### [OilChangeMapper.java](oil-template/src/main/java/com/oil/mapper/postgres/OilChangeMapper.java)
定义一个MyBatis-Plus 的数据访问接口（DAO 层），用于操作 PostgreSQL 数据库中与“机油运行时间”相关的数据

##### [OilConfigMapper.java](oil-template/src/main/java/com/oil/mapper/postgres/OilConfigMapper.java)
定义一个基于 MyBatis-Plus 的数据库访问接口类，用于与 PostgreSQL 数据库中的换油配置表（loader.loader_oil_config_tb）进行交互

定义了两个方法：方法1批量删除设备，一般配合 XML Mapper 实现；方法2查询最大机油寿命，查询指定设备编号 deviceNo 对应的最大油寿命 oil_max_life

##### [OilConfigMapper.xml](oil-template/src/main/java/com/oil/mapper/postgres/OilConfigMapper.xml)
此文件是配合 OilConfigMapper.java 使用的 MyBatis 映射文件，用于定义 SQL 执行逻辑，删除设备编号对应的换油配置记录。
这个 XML 文件补全了 OilConfigMapper.java 中的核心业务逻辑，使得整个换油配置的批量删除操作得以实现而无需写注解 SQL

##### [OilLifeMapper.java](oil-template/src/main/java/com/oil/mapper/postgres/OilLifeMapper.java)
此文件是一个用于访问 PostgreSQL 数据库中 换油寿命信息表 的 MyBatis-Plus 接口类，该类负责对 loader.loader_oil_life_tb 表进行操作

定义了两个方法：方法1用于获取多个车辆的换油寿命信息；方法2查询主键为 2 的一条换油寿命记录

##### [OilLifeMapper.xml](oil-template/src/main/java/com/oil/mapper/postgres/OilLifeMapper.xml)
此文件是 MyBatis 的 XML 映射文件，对应于 OilLifeMapper.java 中的 getOilLifeMap 方法，
这个 XML 用于从数据库中查询每辆车最新的换油寿命百分比

##### [OilSampleMapper.java](oil-template/src/main/java/com/oil/mapper/postgres/OilSampleMapper.java)
此文件是一个使用 MyBatis-Plus 构建的 PostgreSQL 数据库映射接口类，用于处理与油样数据（OilSample）表相关的操作，定义一个批量删除方法

##### [OilSampleMapper.xml](oil-template/src/main/java/com/oil/mapper/postgres/OilSampleMapper.xml)
与 OilSampleMapper.java 搭配使用的 MyBatis 映射文件，定义了名为 deleteByOilSampleList 的 SQL 语句，用于对油样数据进行批量逻辑删除

##### [TodoMapper.java](oil-template/src/main/java/com/oil/mapper/postgres/TodoMapper.java)
定义一个使用 MyBatis-Plus 实现的 PostgreSQL 数据访问接口类，用于管理待办事项（Todo）的数据库操作

定义三个方法：方法1批量删除待办事项（参数一组 ID），方法2批量同意事项（参数一组 ID），方法3拒绝待办事项（参数一组 ID和拒绝理由）

##### [TodoMapper.xml](oil-template/src/main/java/com/oil/mapper/postgres/TodoMapper.xml)
此文件是 TodoMapper.java 的 SQL 映射文件，定义了三个用于批量操作待办事项的 UPDATE SQL 语句，分别对应删除、同意、拒绝

##### [UserMapper.java](oil-template/src/main/java/com/oil/mapper/postgres/UserMapper.java)
此文件是一个基于 MyBatis-Plus 的用户信息数据访问接口类，主要作用是对 User 实体对应的数据库表（如 loader.user_tb）执行数据操作

定义两个方法：方法1获取公司列表，方法2批量删除用户

##### [UserMapper.xml](oil-template/src/main/java/com/oil/mapper/postgres/UserMapper.xml)
此文件是 UserMapper.java 的 SQL 映射文件，其中定义了一个批量逻辑删除用户的 SQL 语句

##### [VehicleMapper.java](oil-template/src/main/java/com/oil/mapper/postgres/VehicleMapper.java)
此文件是一个基于 MyBatis-Plus 的用户信息数据访问接口类，用于操作与车辆信息（Vehicle）相关的数据库表，主要是 loader.loader_info_tb 表

定义8个方法：方法1查询所有设备所在省份；方法2查询某公司下设备所在省份；方法3批量删除设备（需要 XML 实现）；
方法4查询设备名称；方法5查询所有设备编号；方法6根据地区查询设备编号；方法7全国范围车辆分布（聚类统计）；方法8指定省份下的城市级统计

##### [VehicleMapper.xml](oil-template/src/main/java/com/oil/mapper/postgres/VehicleMapper.xml)
此文件是 VehicleMapper.java 的 MyBatis 映射文件，其中定义了一个用于批量逻辑删除设备记录的方法


### service文件夹（业务接口定义模块）
定义了系统中各核心业务的服务接口，每个接口对应一个业务模块，便于上层（Controller）调用

|   接口类名   |  对应功能模块  |
|:--------:|:--------:|
|   OilConfigService.java    |   油品配置参数服务    |
|   OilLifeService.java    |   油品寿命分析服务    |
|   OilSampleService.java    |   油液样本检测服务    |
|   TodoService.java    |   任务管理服务    |
|   UserService.java    |   用户服务（注册、验证）    |
|   VehicleService.java    |   车辆信息服务    |

#### impl文件夹
接口实现层，调用对应的 Mapper 方法，处理业务判断、封装数据结构

|          实现类          |        实现接口        |  特点  |
|:---------------------:|:------------------:|:--------:|
|   OilConfigServiceImpl.java    |  OilConfigService  |   调用 OilConfigMapper 获取配置列表或更新数据    |
|   OilLifeServiceImpl.java    |   OilLifeService   |   获取油寿命列表、最新记录，可能含寿命评估逻辑    |
|   OilSampleServiceImpl.java    |  OilSampleService  |   调用样本接口，支持按车辆/时间筛选    |
|   TodoServiceImpl.java    |    TodoService     |   提供任务增删查改操作    |
|   UserServiceImpl.java    |    UserService     |   支持用户注册、密码修改、登录验证等    |
|   VehicleServiceImpl.java    |   VehicleService   |   管理车辆信息、关联用户、VIN 查询    |

### task文件夹（定时任务模块）
##### [UpdateTask.java](oil-template/src/main/java/com/oil/task/UpdateTask.java)
作用：定时任务，用于每日定时更新工程机械车辆的机油剩余寿命

### utils文件夹（用户信息管理模块）
##### [UserHolder.java](oil-template/src/main/java/com/oil/utils/UserHolder.java)
功能：基于 ThreadLocal 的用户信息管理器

主要用于在一次 HTTP 请求线程中存储和访问当前登录用户的 ID

### websocket文件夹（WebSocket通信模块）
##### [WebSocketServer.java](oil-template/src/main/java/com/oil/websocket/WebSocketServer.java)
功能：提供服务端 WebSocket 通信能力，实现实时消息推送与反馈机制
