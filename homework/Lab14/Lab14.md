# Lab14：使用 Room 完成 Bus Schedule 应用

## 实验背景

本次实验基于 Bus Schedule（公交时刻表）起始项目，练习使用 Room 读取本地预置数据库，并把数据库中的公交站点与到站时间显示到 Jetpack Compose 界面中。

起始代码已经完成了 Compose UI、导航和列表展示逻辑，但数据仍然来自 `BusScheduleViewModel` 中写死的示例数据。项目中已经提供了一个 SQLite 数据库文件：

```text
app/src/main/assets/database/bus_schedule.db
```

本实验的目标是：将现有 `BusSchedule` 数据类转换为 Room Entity，创建 DAO 和 RoomDatabase，并让 ViewModel 从数据库中读取真实数据。

**room_reference** 为 Room 参考文档文件夹，提交代码时无需提交此文件夹内的任何文件！

---

## 前提条件

- 已掌握 Kotlin 数据类、注解和接口的基本用法
- 已学习 Room 的基本概念：`Entity`、`Dao`、`Database`
- 了解 SQLite 表、列、主键和查询语句
- 了解 Kotlin Flow 的基本作用
- 了解 ViewModel 向 Compose UI 暴露数据的基本方式
- 已完成或阅读 `Lab14/room_reference/` 中的 Room 参考文档

---

## 实验目标

完成本实验后，你应能够：

- 为 Android 项目添加 Room 依赖和 KSP 编译器依赖
- 使用 `@Entity`、`@PrimaryKey`、`@ColumnInfo` 映射数据库表
- 使用 `@Dao` 和 `@Query` 编写数据库查询接口
- 使用 `Room.databaseBuilder()` 创建 Room 数据库
- 使用 `createFromAsset()` 从 `assets` 中的预置数据库初始化数据
- 通过 ViewModel 调用 DAO，并向 Compose 页面提供 `Flow<List<BusSchedule>>`
- 验证列表页和站点详情页都能显示数据库中的真实公交时刻数据

---

## 所需资源

### 起始代码

本目录中的 `basic-android-kotlin-compose-training-bus-schedule/` 是 Bus Schedule 的起始项目代码。请在 Android Studio 中打开该目录即可开始实验。

本实验页面原始内容已经合并到本文中。起始代码已经包含：

- Compose UI
- 顶部应用栏
- 完整时刻表页面
- 单个站点时刻页面
- 页面导航
- `assets/database/bus_schedule.db` 预置数据库

需要补全的是 Room 数据层。完成后，应用会从数据库中加载真实数据，而不是显示 ViewModel 中的示例数据。

起始代码结构概述：

| 文件 | 说明 |
|------|------|
| `MainActivity.kt` | 应用入口，调用 `BusScheduleApp()` |
| `data/BusSchedule.kt` | 公交时刻数据类，**需要转换为 Room Entity** |
| `ui/BusScheduleViewModel.kt` | 当前返回示例数据，**需要改为读取 DAO** |
| `ui/BusScheduleScreens.kt` | Compose UI 与 Navigation，已完成，通常无需修改 |
| `assets/database/bus_schedule.db` | 预置 SQLite 数据库，包含真实公交时刻数据 |
| `build.gradle.kts` | 项目级 Gradle 配置，**需要添加 Room 版本号** |
| `app/build.gradle.kts` | app 模块 Gradle 配置，**需要添加 Room 依赖** |

### 预置数据库

数据库中已有一张表，结构如下：

```sql
CREATE TABLE Schedule(
  id INTEGER NOT NULL,
  stop_name TEXT NOT NULL,
  arrival_time INTEGER NOT NULL,
  PRIMARY KEY (id)
);
```

数据概况：

| 内容 | 数量 |
|------|------|
| 时刻表记录 | 31 条 |
| 公交站点 | 10 个 |

前几条数据示例：

```text
1 | Main Street      | 1617202800
2 | Park Street      | 1617203520
3 | Maple Avenue     | 1617204300
4 | Broadway Avenue  | 1617205260
```

`arrival_time` 是 Unix 时间戳。起始 UI 已经负责把时间戳格式化成类似 `9:00 AM` 的显示文本。

---

## 起始代码现状分析

当前应用可以运行，但只显示一条示例数据：

```text
Example Street | 12:00 AM
```

起始状态参考：

![起始代码示例数据](img/3603c91854cada9a.png)

原因在于 `BusScheduleViewModel.kt` 中的两个方法仍然使用 `flowOf()` 返回写死数据：

- `getFullSchedule()`：应该返回完整公交时刻表
- `getScheduleFor(stopName: String)`：应该返回某个站点的所有到站时间

项目已经具备以下 UI 能力：

- 首页展示公交站点和到站时间列表
- 点击某个站点后进入详情页
- 详情页展示该站点的所有到站时间
- 顶部应用栏和返回按钮已经实现

因此本实验主要修改数据层和 ViewModel，不需要重写 Compose UI。

---

## 实验任务

### 任务一：打开起始项目

在 Android Studio 中打开：

```text
Lab14/basic-android-kotlin-compose-training-bus-schedule/
```

等待 Gradle 同步完成后，先运行一次应用，确认当前只显示示例数据。然后浏览以下文件：

- `data/BusSchedule.kt`
- `ui/BusScheduleViewModel.kt`
- `ui/BusScheduleScreens.kt`
- `app/src/main/assets/database/bus_schedule.db`

---

### 任务二：添加 Room 依赖

#### 目标

让项目能够使用 Room 注解、Room 运行时和 Room KSP 编译器。

#### 步骤

1. 在项目级 `build.gradle.kts` 的 `extra.apply {}` 中添加 Room 版本号：

```kotlin
set("room_version", "2.7.0")
```

2. 在 `app/build.gradle.kts` 的 `dependencies` 中添加：

```kotlin
implementation("androidx.room:room-ktx:${rootProject.extra["room_version"]}")
implementation("androidx.room:room-runtime:${rootProject.extra["room_version"]}")
ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
```

> `app/build.gradle.kts` 中已经配置了 `com.google.devtools.ksp` 插件，不需要重复添加插件。

---

### 任务三：将 BusSchedule 转换为 Room Entity

#### 目标

修改 `data/BusSchedule.kt`，让 `BusSchedule` 能映射到数据库中的 `Schedule` 表。

#### 要求

`BusSchedule` 需要满足以下映射关系：

| Kotlin 属性 | 数据库列 | 说明 |
|------------|----------|------|
| `id` | `id` | 主键 |
| `stopName` | `stop_name` | 公交站点名称 |
| `arrivalTimeInMillis` | `arrival_time` | 到站时间戳 |

#### 提示

需要使用以下 Room 注解：

```kotlin
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
```

示例结构：

```kotlin
@Entity(tableName = "Schedule")
data class BusSchedule(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "stop_name") val stopName: String,
    @ColumnInfo(name = "arrival_time") val arrivalTimeInMillis: Int
)
```

注意表名是 `Schedule`，不是 `BusSchedule`。如果表名或列名写错，Room 无法正确读取预置数据库。

最终 Entity 需要匹配数据库中的 `Schedule` 表结构：

![Schedule 表结构](img/9587f9a5f035e552.png)

---

### 任务四：创建数据访问对象 DAO

#### 目标

在 `data/` 包下新建 `BusScheduleDao.kt`，定义读取数据库的方法。

#### 要求

DAO 至少包含两个查询方法：

| 方法 | 返回值 | 作用 |
|------|--------|------|
| `getAll()` | `Flow<List<BusSchedule>>` | 获取完整时刻表 |
| `getByStopName(stopName: String)` | `Flow<List<BusSchedule>>` | 获取指定站点的所有时刻 |

两个查询都必须按 `arrival_time ASC` 升序排序。

#### 参考结构

```kotlin
@Dao
interface BusScheduleDao {
    @Query("SELECT * FROM Schedule ORDER BY arrival_time ASC")
    fun getAll(): Flow<List<BusSchedule>>

    @Query("SELECT * FROM Schedule WHERE stop_name = :stopName ORDER BY arrival_time ASC")
    fun getByStopName(stopName: String): Flow<List<BusSchedule>>
}
```

---

### 任务五：创建 Room 数据库

#### 目标

在 `data/` 包下新建 `BusScheduleDatabase.kt`，创建 Room 数据库，并从 `assets/database/bus_schedule.db` 初始化数据。

#### 要求

1. 数据库类继承 `RoomDatabase`
2. 使用 `@Database` 声明 Entity
3. 提供 `BusScheduleDao`
4. 使用单例模式创建数据库实例
5. 使用 `createFromAsset("database/bus_schedule.db")` 加载预置数据库

#### 参考结构

```kotlin
@Database(entities = [BusSchedule::class], version = 1, exportSchema = false)
abstract class BusScheduleDatabase : RoomDatabase() {
    abstract fun busScheduleDao(): BusScheduleDao

    companion object {
        @Volatile
        private var Instance: BusScheduleDatabase? = null

        fun getDatabase(context: Context): BusScheduleDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    BusScheduleDatabase::class.java,
                    "bus_schedule_database"
                )
                    .createFromAsset("database/bus_schedule.db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
```

不要使用 `allowMainThreadQueries()`。数据库查询应通过 Room、Flow 和协程机制在合适的线程中完成。

---

### 任务六：更新 ViewModel

#### 目标

修改 `ui/BusScheduleViewModel.kt`，删除示例数据，让 ViewModel 从 DAO 读取真实数据。

#### 要求

1. `BusScheduleViewModel` 构造函数接收 `BusScheduleDao`
2. `getFullSchedule()` 返回 DAO 的完整列表查询
3. `getScheduleFor(stopName)` 返回 DAO 的站点过滤查询
4. `factory` 中通过 `APPLICATION_KEY` 获取 Application Context，再创建数据库实例
5. 删除 `flowOf()` 示例数据

#### 参考结构

```kotlin
class BusScheduleViewModel(
    private val busScheduleDao: BusScheduleDao
) : ViewModel() {

    fun getFullSchedule(): Flow<List<BusSchedule>> = busScheduleDao.getAll()

    fun getScheduleFor(stopName: String): Flow<List<BusSchedule>> =
        busScheduleDao.getByStopName(stopName)

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                val database = BusScheduleDatabase.getDatabase(application)
                BusScheduleViewModel(database.busScheduleDao())
            }
        }
    }
}
```

需要自行补全对应 import，包括：

```kotlin
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.busschedule.data.BusScheduleDao
import com.example.busschedule.data.BusScheduleDatabase
```

---

### 任务七：运行并验证

在模拟器或真机上运行应用，检查以下内容：

- 首页不再显示 `Example Street`
- 首页能显示数据库中的完整公交时刻表
- 数据按到站时间从早到晚排序
- 点击 `Main Street`、`Park Street` 等站点后，可以进入该站点详情页
- 详情页只显示所选站点的到站时间
- 顶部标题在详情页显示当前站点名称
- 返回按钮可以从详情页回到完整时刻表
- 旋转屏幕或重新进入应用后，数据仍然来自数据库
- 浅色和深色模式下界面正常显示

完成后的效果可参考：

![完整时刻表](img/cdb6f9e79137f323.png)

![站点详情](img/6c59e6f57f59bd27.png)

截图请使用 Android Studio 或模拟器内置截图功能，**严禁使用手机拍屏幕**。

---

## 代码结构参考

完成后的核心代码文件结构：

```text
app/
└── src/
    └── main/
        ├── assets/
        │   └── database/
        │       └── bus_schedule.db
        └── java/com/example/busschedule/
            ├── MainActivity.kt
            ├── data/
            │   ├── BusSchedule.kt          # Room Entity
            │   ├── BusScheduleDao.kt       # DAO 查询接口
            │   └── BusScheduleDatabase.kt  # Room 数据库单例
            └── ui/
                ├── BusScheduleScreens.kt   # UI 与导航，通常无需修改
                └── BusScheduleViewModel.kt # 调用 DAO 提供 Flow 数据
```

---

## 提交要求

在自己的文件夹下新建 `Lab14/` 目录，只提交本实验最关键的源码、配置、截图和报告。文件名必须保持一致，便于批改时快速定位。

```text
学号姓名/
└── Lab14/
    ├── project-build.gradle.kts
    ├── app-build.gradle.kts
    ├── BusSchedule.kt
    ├── BusScheduleDao.kt
    ├── BusScheduleDatabase.kt
    ├── BusScheduleViewModel.kt
    ├── screenshot_full_schedule.png
    ├── screenshot_route_schedule.png
    └── report.md
```

> **注意：不要提交整个项目代码。** 只提交上述核心源码文件、配置文件、截图和报告。

`report.md` 需包含：

1. `Entity`、`DAO`、`Database` 三者在本实验中的职责说明
2. `BusSchedule` 属性如何映射到 `Schedule` 表和列
3. 两条 DAO 查询语句的作用，以及为什么需要按 `arrival_time` 排序
4. `createFromAsset("database/bus_schedule.db")` 的作用
5. ViewModel 如何从示例数据切换为数据库数据
6. `Flow<List<BusSchedule>>` 如何被 Compose 页面收集并显示
7. 实验中遇到的问题与解决过程

---

## 验收标准

满足以下条件可视为完成实验：

- 应用可正常编译和运行
- 添加了正确的 Room 依赖和 KSP 编译器依赖
- `BusSchedule` 正确声明为 `Schedule` 表对应的 Room Entity
- `id`、`stopName`、`arrivalTimeInMillis` 与数据库列正确映射
- 创建了 `BusScheduleDao`，包含完整列表查询和按站点查询
- 创建了 `BusScheduleDatabase`，并通过 `createFromAsset()` 使用预置数据库
- ViewModel 不再返回 `flowOf()` 示例数据
- 首页显示数据库中的真实公交时刻表
- 点击站点后详情页只显示该站点的到站时间
- 报告能清晰说明 Room 数据层和 ViewModel 的实现思路

---

## 提示

- 表名和列名必须与 `bus_schedule.db` 中一致：`Schedule`、`stop_name`、`arrival_time`
- `arrivalTimeInMillis` 这个属性名沿用起始代码，尽量不要改名，否则 UI 文件也需要同步修改
- 如果运行后仍然只显示 `Example Street`，说明 ViewModel 仍在使用示例数据
- 如果应用启动时报数据库表不存在，优先检查 `@Entity(tableName = "Schedule")`
- 如果查询结果为空，检查 DAO 的 SQL 条件和 `stopName` 参数是否一致
- 如果 Room 编译报错，先检查是否添加了 `ksp("androidx.room:room-compiler:...")`
- 不要在 Composable 中直接创建数据库，请通过 ViewModel 获取数据

---

## 截止时间

**2026-06-16**，届时关于 Lab14 的 PR 请求将不会被合并。

---
