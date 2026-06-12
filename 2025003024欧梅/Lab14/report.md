# Lab14 使用 Room 完成公交时刻表应用 实验报告
## 1. Entity、DAO、Database 职责
1. **Entity（BusSchedule）**：数据实体类，使用 Room 注解映射 SQLite 数据表 `Schedule`，定义表结构、字段与主键，实现 Kotlin 类和数据库表的绑定。
2. **DAO（BusScheduleDao）**：数据访问接口，使用 `@Query` 编写 SQL 查询语句，定义数据读取方法，是程序操作数据库的入口。
3. **Database（BusScheduleDatabase）**：Room 数据库主类，继承 RoomDatabase，管理数据库实例、版本，提供 DAO 对象，并通过单例模式保证全局只有一个数据库连接。

## 2. 实体类与数据表映射关系
数据库表名：`Schedule`
- 类属性 `id` → 数据表列 `id`，作为主键
- 类属性 `stopName` → 数据表列 `stop_name`（通过 @ColumnInfo 映射）
- 类属性 `arrivalTimeInMillis` → 数据表列 `arrival_time`（通过 @ColumnInfo 映射）

## 3. DAO 查询语句说明
1. `SELECT * FROM Schedule ORDER BY arrival_time ASC`：查询整张表所有数据，并按到站时间戳**升序**排列，保证页面时间从早到晚展示。
2. `SELECT * FROM Schedule WHERE stop_name = :stopName ORDER BY arrival_time ASC`：根据传入的站点名称筛选数据，同样按时间升序排序。
排序原因：业务需求要求时刻表按到站先后顺序展示，提升阅读体验。

## 4. createFromAsset 作用
`createFromAsset("database/bus_schedule.db")` 作用是：读取项目 `assets` 目录下预置的 SQLite 数据库文件，**应用首次启动时直接加载已有数据**，无需代码手动插入测试数据。

## 5. ViewModel 数据切换逻辑
原有 ViewModel 使用 `flowOf()` 硬编码模拟假数据；改造后 ViewModel 构造函数接收 DAO 对象，直接调用 DAO 的查询方法获取数据库数据，彻底替换示例数据。同时通过 ViewModel 工厂获取全局 Context，创建 Room 数据库实例。

## 6. Flow 在 Compose 中的使用
DAO 返回 `Flow<List<BusSchedule>>`，Flow 具备**可观察、自动更新**特性。Compose 页面使用 `collectAsState` 收集 Flow 数据流，当数据库数据变化时，UI 会自动刷新，实现数据与界面联动。

## 7. 实验问题与解决
1. 问题：编译提示 Room 相关类找不到。
   解决：检查 Room 运行库、ktx、ksp 编译器依赖是否完整添加，同步 Gradle。
2. 问题：运行后仍显示示例数据。
   解决：清空 ViewModel 内假数据代码，确认调用 DAO 接口。
3. 问题：数据库表找不到导致闪退。
   解决：核对 Entity 中 `tableName` 为 `Schedule`，和数据库表名严格一致。