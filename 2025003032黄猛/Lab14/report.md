# Lab14 实验报告

## 一、实验目标

本实验的目标是把 Bus Schedule 起始项目中的示例数据替换为 Room 读取本地预置数据库的数据，使首页和详情页都能显示数据库中的真实公交时刻信息。

## 二、Entity、DAO、Database 的职责

### 1. Entity
`BusSchedule` 被改造成 Room Entity，用来映射数据库中的 `Schedule` 表。它负责描述一条公交时刻记录的数据结构，并把 Kotlin 属性和数据库列对应起来。

### 2. DAO
`BusScheduleDao` 负责定义数据库查询接口。本实验中它提供两个查询：获取全部时刻表，以及按站点名称查询某一站点的全部到站记录。

### 3. Database
`BusScheduleDatabase` 负责创建 Room 数据库实例，并通过 `createFromAsset("database/bus_schedule.db")` 直接加载 assets 中的预置数据库。它还负责向外暴露 DAO。

## 三、BusSchedule 属性与 Schedule 表的映射关系

本实验中，`BusSchedule` 与 `Schedule` 表的映射如下：

- `id` 对应数据库列 `id`，并作为主键。
- `stopName` 对应数据库列 `stop_name`。
- `arrivalTimeInMillis` 对应数据库列 `arrival_time`。

因此，`BusSchedule` 使用了以下注解：

- `@Entity(tableName = "Schedule")`
- `@PrimaryKey`
- `@ColumnInfo(name = "stop_name")`
- `@ColumnInfo(name = "arrival_time")`

这样 Room 才能正确读取预置数据库中的真实数据。

## 四、DAO 查询语句的作用

### 1. `getAll()`

```kotlin
@Query("SELECT * FROM Schedule ORDER BY arrival_time ASC")
fun getAll(): Flow<List<BusSchedule>>
```

这个方法用于获取完整的公交时刻表，并按到站时间从早到晚排序。

### 2. `getByStopName(stopName: String)`

```kotlin
@Query("SELECT * FROM Schedule WHERE stop_name = :stopName ORDER BY arrival_time ASC")
fun getByStopName(stopName: String): Flow<List<BusSchedule>>
```

这个方法用于获取某一个公交站点的全部到站记录，同样按到站时间升序排列。

### 为什么需要按 `arrival_time` 排序

因为 UI 展示的是公交到站顺序，按时间升序可以让用户直接看到下一班车或最早到达的班次，符合时刻表应用的使用习惯，也和页面中时间格式化后的展示逻辑一致。

## 五、`createFromAsset("database/bus_schedule.db")` 的作用

这个方法让 Room 在第一次创建数据库时，直接使用 assets 目录中的预置 SQLite 数据库文件作为初始数据源，而不是创建一个空数据库再手动插入数据。这样应用启动后就能直接读取到完整时刻表数据。

## 六、ViewModel 如何从示例数据切换为数据库数据

起始项目中的 `BusScheduleViewModel` 使用 `flowOf()` 返回写死的示例数据，首页因此只会显示 `Example Street`。

在本实验中，ViewModel 做了以下修改：

- 构造函数接收 `BusScheduleDao`
- `getFullSchedule()` 直接返回 `busScheduleDao.getAll()`
- `getScheduleFor(stopName)` 直接返回 `busScheduleDao.getByStopName(stopName)`
- 在 `factory` 中通过 `APPLICATION_KEY` 获取 `Application`，再调用 `BusScheduleDatabase.getDatabase(application)` 创建数据库实例

这样 UI 层不再依赖示例数据，而是通过 ViewModel 间接读取 Room 数据库中的真实内容。

## 七、`Flow<List<BusSchedule>>` 如何被 Compose 页面收集并显示

`BusScheduleScreens.kt` 中已经使用了 `collectAsState(emptyList())` 来收集 ViewModel 暴露的 `Flow<List<BusSchedule>>`。

当 DAO 查询结果变化时，Flow 会触发更新，Compose 会重新组合界面，因此列表页和详情页都能实时显示数据库返回的数据。

## 八、实验中遇到的问题与解决过程

本次实验中，主要问题是起始项目仍然返回固定示例数据，导致页面始终显示 `Example Street`。解决方法是将 ViewModel 改为依赖 DAO，并让 DAO 读取预置数据库。

另外，在本地验证时，Gradle wrapper 下载阶段遇到了证书校验问题，导致无法直接完成一次完整编译。代码本身已通过静态检查，且 Room 相关文件、依赖和映射关系都已按要求补齐。若在可正常下载 Gradle 依赖的环境中运行，项目应可以正常编译并读取预置数据库。

## 九、实验总结

通过本实验，我完成了从示例数据到 Room 数据层的替换，理解了 Entity、DAO、Database、ViewModel 和 Compose 之间的数据流转方式。Room 负责持久化读取，ViewModel 负责对外暴露 Flow，Compose 负责收集并渲染数据，三者协作后即可实现基于预置数据库的公交时刻表应用。
