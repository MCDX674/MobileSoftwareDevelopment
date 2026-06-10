# Lab14：使用 Room 完成 Bus Schedule 公交时刻表应用 实验报告
## 一、实验概述
本次实验基于 Android Jetpack Room 持久化组件，完成公交时刻表应用的数据层改造。项目原有 Compose UI、页面导航、时间格式化等功能，本次核心任务是**将硬编码示例数据替换为本地预置 SQLite 数据库数据**。通过 Room 的 Entity、DAO、Database 三大核心组件，实现数据库映射、数据查询、单例数据库管理，结合 ViewModel + Flow 完成数据分层与页面自动刷新，最终实现从 `assets` 预置数据库读取真实公交数据并展示。

## 二、实验目的
1. 掌握 Room 三大核心组件：Entity、DAO、Database 的作用与使用规范。
2. 学会使用 `@Entity`、`@ColumnInfo`、`@PrimaryKey` 完成 Kotlin 数据类与数据库表的映射。
3. 掌握 Room DAO 中 `@Query` 注解编写 SQL 查询语句，结合 Flow 实现数据监听。
4. 理解 `createFromAsset` 加载 assets 目录下预置数据库的用法与场景。
5. 掌握 Room 数据库单例模式实现，避免重复创建数据库实例。
6. 理解 ViewModel 与 Room DAO 的协作流程，实现数据层与 UI 层解耦。
7. 掌握 Compose 中 `collectAsStateWithLifecycle` 收集 Flow 数据流，实现页面自动更新。

## 三、实验环境
- 开发工具：Android Studio Hedgehog / Iguana
- 开发语言：Kotlin
- 核心组件：Jetpack Compose、Navigation、ViewModel、Room 2.7.0、KSP
- 数据源：`app/src/main/assets/database/bus_schedule.db` 预置 SQLite 数据库
- 最低适配版本：Android 7.0 (API 24)

## 四、核心组件职责说明
### 1. Entity（BusSchedule）
Entity 是 Room 的**数据库实体类**，作用是将 Kotlin 数据类与 SQLite 数据表做双向映射。
- 使用 `@Entity(tableName = "Schedule")` 指定映射的数据库表名，必须与预置库表名完全一致。
- `@PrimaryKey` 标记主键字段 `id`，对应数据表 `id` 列。
- `@ColumnInfo(name = "xxx")` 解决 Kotlin 驼峰命名与数据库下划线命名的差异：
  - `stopName` → 数据库 `stop_name`
  - `arrivalTimeInMillis` → 数据库 `arrival_time`
- 该类仅负责**数据结构映射**，不包含业务逻辑。

### 2. DAO（BusScheduleDao）
DAO（数据访问对象）是 Room 的**数据访问接口**，专门定义数据库增删改查方法，是应用操作数据库的唯一入口。
- 使用 `@Dao` 标记接口，Room 会自动生成接口实现类。
- 本实验定义两个核心查询方法，返回值为 `Flow<List<BusSchedule>>`：Flow 具备数据流监听能力，数据库数据变化时自动通知页面刷新。
- 所有查询语句基于标准 SQL 编写，严格匹配预置数据表结构。

### 3. Database（BusScheduleDatabase）
Database 是 Room 的**数据库主类**，继承自 `RoomDatabase`，负责：
1. 关联所有 Entity 实体，声明数据库版本。
2. 暴露 DAO 实例，供上层调用。
3. 通过**单例模式**管理数据库实例，避免多次创建数据库造成性能损耗与内存泄漏。
4. 调用 `createFromAsset()` 加载 `assets` 目录下的预置数据库，实现应用初始化时直接导入已有数据。

## 五、数据表映射关系
预置数据库表名为 `Schedule`，共 3 列，映射规则如下：

| Kotlin 属性名            | 数据库列名     | 注解配置                          | 字段说明               |
|--------------------------|----------------|-----------------------------------|------------------------|
| id                       | id             | `@PrimaryKey`                     | 数据表主键，自增整数   |
| stopName                 | stop_name      | `@ColumnInfo(name = "stop_name")` | 公交站点名称           |
| arrivalTimeInMillis      | arrival_time   | `@ColumnInfo(name = "arrival_time")` | 到站时间戳（Unix 时间） |

> 注意：数据库使用下划线命名规范，Kotlin 使用驼峰命名规范，必须通过 `@ColumnInfo` 显式绑定，否则会出现字段匹配失败、查询结果为空的问题。

## 六、DAO 查询语句详解
### 1. 查询全部时刻表
```sql
SELECT * FROM Schedule ORDER BY arrival_time ASC
作用：查询数据表中所有公交记录，展示完整时刻表。
排序原因：业务要求按到站时间由早到晚展示，arrival_time 是时间戳，升序即可实现时间正序。
2. 根据站点查询时刻表
sql
SELECT * FROM Schedule WHERE stop_name = :stopName ORDER BY arrival_time ASC
作用：根据传入的站点名称，过滤出该站点的所有到站记录，对应点击站点后的详情页。
排序原因：同一站点可能有多趟公交，同样按到站时间升序排列，保证展示逻辑统一。
参数 :stopName 为 Room 占位符，自动接收方法传入的字符串参数，防止 SQL 注入。

七、createFromAsset 作用说明
createFromAsset("database/bus_schedule.db") 是 Room 提供的专用方法，作用如下：
加载预置数据库：应用首次创建 Room 数据库时，不会创建空表，而是直接读取 app/src/main/assets/database/ 目录下的 bus_schedule.db 文件，将已有表结构与 31 条测试数据导入应用数据库。
适用场景：适合应用上线前已有固定基础数据（如城市站点、配置表）的场景，避免应用首次启动后空数据。
使用要求：文件路径必须严格对应 assets 目录层级，数据库文件名、表名、字段名必须与代码中 Entity 完全一致。
限制：仅在数据库首次创建时生效，后续应用重启不会重复加载。
-- 八、ViewModel 改造思路
1. 改造前
ViewModel 使用 flowOf() 硬编码模拟数据，仅返回一条示例站点数据，无法读取真实数据库。
2. 改造后
构造函数注入 BusScheduleDao，遵循依赖注入思想，解耦数据层。
删除所有硬编码示例数据，方法直接转发调用 DAO 的查询方法，返回 Flow 数据流。
自定义 ViewModel 工厂：通过 APPLICATION_KEY 获取全局 Application 上下文，初始化 Room 数据库，再获取 DAO 实例，最终创建 ViewModel。
优势：ViewModel 不直接接触数据库，只负责转发数据请求，符合 MVVM 分层思想。
九、Flow 数据流在 Compose 中的使用
DAO 方法返回 Flow<List<BusSchedule>>：Flow 是 Kotlin 响应式数据流，天然支持生命周期感知，数据库数据发生变化时会持续发射新数据。
Compose 页面使用 collectAsStateWithLifecycle() 收集 Flow：
该函数具备生命周期感知，页面处于前台时监听数据流，后台自动停止监听，节约系统资源。
收集后转为 Compose 可识别的 State 状态，状态变化时自动重组 UI，实现数据实时刷新。
流转流程：Room 数据库 → DAO (Flow) → ViewModel → Compose UI (collectAsStateWithLifecycle) → 列表刷新。
 ## 十、实验运行结果
1. 整体流程
应用启动 → MainActivity 初始化 Compose 与导航。
ViewModel 通过工厂创建，初始化 Room 数据库并加载 assets 预置数据。
首页（完整时刻表）收集 Flow 数据，展示全部 31 条公交记录，按时间升序排列。
点击任意公交站点，导航跳转至站点详情页，查询并展示该站点所有到站时间。
点击顶部返回按钮，回到完整时刻表页面；旋转屏幕、重启应用数据均正常加载。
2. 界面效果
首页：展示 10 个公交站点、31 条时刻表，时间戳自动格式化为 hh:mm AM/PM 格式。
详情页：仅展示当前选中站点的所有班次，顶部标题为站点名称，返回按钮功能正常。
异常场景：数据表、字段映射正确，无空数据、崩溃、闪退问题。
十一、实验问题与解决方案
问题 1：编译报错，Room 注解无法识别
现象：提示 Unresolved reference: Entity/Dao，项目无法编译。
原因：未正确配置 KSP 插件与 Room 编译器依赖。
解决：
项目级 build.gradle.kts 添加 KSP 插件声明。
App 模块启用 id("com.google.devtools.ksp")。
添加 ksp("androidx.room:room-compiler:2.7.0") 编译器依赖，同步 Gradle 后重新编译。
问题 2：应用运行后仍显示示例数据，未加载数据库内容
现象：页面依旧显示 Example Street，无真实公交数据。
原因：ViewModel 未删除 flowOf 硬编码数据，仍使用模拟数据。
解决：清空 ViewModel 中所有模拟数据，让方法直接调用 DAO 查询接口。
问题 3：查询结果为空，页面无任何数据展示
现象：应用运行正常，但列表空白。
原因 1：@Entity 表名写错（代码表名与数据库 Schedule 不一致）。
原因 2：@ColumnInfo 字段名与数据库下划线名称不匹配。
原因 3：createFromAsset 文件路径错误，未找到预置数据库。
解决：逐一核对表名、列名、assets 文件路径，保证完全一致。
问题 4：数据库多次创建，应用卡顿
现象：频繁启停应用，数据库重复初始化。
原因：未使用单例模式，每次页面创建都新建 Room 实例。
解决：采用双重校验锁实现数据库单例，全局仅保留一个数据库对象。
问题 5：时间展示异常，格式化失败
现象：时间戳转为乱码或错误时间。
原因：代码中时间戳为 Int 类型，格式化时未转为 Long。
解决：时间格式化方法中 timeStamp.toLong() * 1000 完成类型与单位转换（秒 → 毫秒）。
十二、实验总结
本次实验完整完成了 Room 本地数据库的集成与使用，深入理解了 Android 本地持久化的标准分层架构：UI 层 → ViewModel 层 → DAO 层 → Room 数据库。
掌握了 Room 三大核心组件的分工与协作，理解了 ORM 框架（对象关系映射）的设计思想，摆脱原生 SQLite 繁琐的 CURD 写法。
学会了预置数据库的导入方式，掌握 createFromAsset 的使用场景与注意事项。
理解了 Flow 响应式数据流结合 Compose 状态管理的优势，实现页面无感刷新。
强化了 MVVM 分层思想，数据层与 UI 层完全解耦，代码可维护性、可测试性大幅提升。
排查并解决了 Room 配置、字段映射、单例、数据流等典型问题，积累了 Android 本地数据库实战经验。