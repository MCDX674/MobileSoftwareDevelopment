# Lab14 使用 Room 完成 Bus Schedule 应用 实验报告
## 一、实验基本信息
- 实验名称：使用 Room 完成 Bus Schedule 公交时刻表应用
- 实验环境：Android Studio、Kotlin、Jetpack Compose、Room 2.7.0
- 实验目的：掌握 Room 持久化库使用，将预置 SQLite 数据库数据通过 Entity、DAO、Database 三层结构读取，结合 ViewModel + Flow 向 Compose 界面展示真实公交时刻表数据。

## 二、实验原理与知识点
Room 是 Android 官方封装的 SQLite 持久化库，简化原生 SQL 开发，核心分为三大组件：
1. **Entity（实体类）**：将 Kotlin 数据类映射为数据库中的数据表，通过注解定义表名、列名、主键。
2. **DAO（数据访问对象）**：定义数据库增删改查接口，使用注解编写 SQL 语句，Room 自动生成实现代码。
3. **Database（数据库类）**：Room 数据库主入口，继承 `RoomDatabase`，管理数据库实例、版本、实体集合，统一对外提供 DAO。

结合 Kotlin Flow 实现数据可观察，搭配 ViewModel 解耦数据层与 UI 层，保证界面生命周期安全，最终在 Jetpack Compose 中收集并展示数据流。

## 三、实验步骤与核心代码实现
### （一）配置 Room 依赖
1. **项目级 build.gradle.kts（project-build.gradle.kts）**
在全局配置中声明 Room 版本：
```kotlin
extra.apply {
    set("room_version", "2.7.0")
}
```

2. **模块级 build.gradle.kts（app-build.gradle.kts）**
添加 Room 运行库、Kotlin 扩展库与 KSP 注解编译器：
```kotlin
dependencies {
    implementation("androidx.room:room-runtime:${rootProject.extra["room_version"]}")
    implementation("androidx.room:room-ktx:${rootProject.extra["room_version"]}")
    ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
}
```
同步 Gradle，完成环境配置。

### （二）创建 Room Entity：BusSchedule.kt
将原有数据类改造为数据库实体，映射 `Schedule` 表，匹配表结构与字段：
```kotlin
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 映射数据库中的 Schedule 表
@Entity(tableName = "Schedule")
data class BusSchedule(
    // 主键，对应数据库 id 列
    @PrimaryKey val id: Int,
    // 映射 stop_name 列
    @ColumnInfo(name = "stop_name") val stopName: String,
    // 映射 arrival_time 时间戳列
    @ColumnInfo(name = "arrival_time") val arrivalTimeInMillis: Int
)
```

### （三）创建 DAO：BusScheduleDao.kt
定义数据查询接口，使用 `@Query` 编写 SQL，返回 Flow 实现数据监听：
```kotlin
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BusScheduleDao {
    // 查询所有公交记录，按到站时间升序排列
    @Query("SELECT * FROM Schedule ORDER BY arrival_time ASC")
    fun getAll(): Flow<List<BusSchedule>>

    // 根据站点名称查询对应记录，按到站时间升序排列
    @Query("SELECT * FROM Schedule WHERE stop_name = :stopName ORDER BY arrival_time ASC")
    fun getByStopName(stopName: String): Flow<List<BusSchedule>>
}
```

### （四）创建 Room 数据库：BusScheduleDatabase.kt
定义数据库类，使用**单例模式**保证全局唯一数据库实例，通过 `createFromAsset` 加载预置数据库：
```kotlin
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 绑定实体类、数据库版本，关闭导出架构文件
@Database(entities = [BusSchedule::class], version = 1, exportSchema = false)
abstract class BusScheduleDatabase : RoomDatabase() {
    // 向外暴露 DAO 对象
    abstract fun busScheduleDao(): BusScheduleDao

    companion object {
        // volatile 保证多线程可见性
        @Volatile
        private var INSTANCE: BusScheduleDatabase? = null

        fun getDatabase(context: Context): BusScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    BusScheduleDatabase::class.java,
                    "bus_schedule_database"
                )
                // 加载 assets 下预置的 SQLite 数据库
                .createFromAsset("database/bus_schedule.db")
                .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
```

### （五）改造 ViewModel：BusScheduleViewModel.kt
移除原有硬编码示例数据，对接 DAO，通过 ViewModel 向 UI 提供数据流，并自定义 ViewModel 工厂：
```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.busschedule.data.BusScheduleDao
import com.example.busschedule.data.BusScheduleDatabase
import kotlinx.coroutines.flow.Flow

class BusScheduleViewModel(
    private val busScheduleDao: BusScheduleDao
) : ViewModel() {
    // 获取全量时刻表
    fun getFullSchedule(): Flow<List<BusSchedule>> = busScheduleDao.getAll()

    // 获取单个站点时刻表
    fun getScheduleFor(stopName: String): Flow<List<BusSchedule>> =
        busScheduleDao.getByStopName(stopName)

    companion object {
        // 自定义 ViewModel 工厂，初始化数据库与 DAO
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                val db = BusScheduleDatabase.getDatabase(application)
                BusScheduleViewModel(db.busScheduleDao())
            }
        }
    }
}
```

### （六）运行验证
部署应用至模拟器/真机，完成功能验证：
1. 首页不再显示 `Example Street` 测试数据，正常加载数据库内 31 条真实记录；
2. 数据按到站时间升序展示；
3. 点击站点可跳转至详情页，仅展示当前站点的所有到站记录；
4. 返回按钮、屏幕旋转、深浅色模式均正常工作。

## 四、核心问题解答
### 1. Entity、DAO、Database 三者的职责
- **Entity（BusSchedule）**：作为**数据表映射模型**，使用注解将 Kotlin 类与 SQLite 的 `Schedule` 表绑定，定义表名、字段、主键，是数据载体。
- **DAO（BusScheduleDao）**：作为**数据访问接口**，封装所有数据库查询操作，编写 SQL 语句，Room 编译时自动生成查询逻辑，隔离数据库操作。
- **Database（BusScheduleDatabase）**：作为**数据库总入口**，管理数据库版本、实体集合，使用单例模式创建唯一数据库连接，统一对外提供 DAO 实例。

### 2. BusSchedule 属性与 Schedule 表的映射关系
预置数据库表名为 `Schedule`，字段映射如下：
- Kotlin 属性 `id` → 数据库列 `id`，被 `@PrimaryKey` 标记为主键；
- Kotlin 属性 `stopName` → 数据库列 `stop_name`，通过 `@ColumnInfo` 指定列名；
- Kotlin 属性 `arrivalTimeInMillis` → 数据库列 `arrival_time`，通过 `@ColumnInfo` 指定列名。

### 3. DAO 查询语句作用及排序原因
1. `SELECT * FROM Schedule ORDER BY arrival_time ASC`：查询**所有公交时刻表数据**，按到站时间从小到大排序；
2. `SELECT * FROM Schedule WHERE stop_name = :stopName ORDER BY arrival_time ASC`：根据传入的站点名称**过滤查询单个站点的所有记录**，同样按到站时间升序。

**排序原因**：公交时刻表需要按照车辆到站先后顺序展示，符合用户查看时刻表的业务逻辑。

### 4. `createFromAsset("database/bus_schedule.db")` 作用
该方法是 Room 提供的预置数据库加载方案，作用为：
应用**首次创建数据库时**，直接读取 `assets/database/` 目录下的 `bus_schedule.db` 文件，将已有数据表和数据导入到本地 Room 数据库中，无需手动编写代码插入初始数据，快速完成数据初始化。

### 5. ViewModel 如何从示例数据切换为数据库数据
1. 删除原代码中 `flowOf()` 硬编码的静态示例数据；
2. ViewModel 构造函数接收 `BusScheduleDao` 对象，作为数据来源；
3. 方法 `getFullSchedule()`、`getScheduleFor()` 直接调用 DAO 对应的查询方法，返回数据库数据流；
4. 自定义 ViewModel 工厂，通过 Application 上下文创建 Room 数据库实例，再获取 DAO 完成 ViewModel 初始化，彻底切换为数据库数据源。

### 6. `Flow<List<BusSchedule>>` 在 Compose 中的展示原理
1. DAO 查询方法返回 `Flow`，Flow 是**可观察数据流**，当数据库数据发生变化时会自动发射新数据；
2. ViewModel 将 Flow 暴露给 Compose 界面，UI 层使用 `collectAsState()` 收集 Flow；
3. `collectAsState` 会将 Flow 转换为 Compose 可识别的状态，数据流更新时，Compose 自动重组界面，实现**数据实时刷新**；
4. 整个过程在后台线程执行，不会阻塞 UI，保证应用流畅性。

### 7. 实验中遇到的问题与解决过程
1. **问题1：运行后仍然显示示例数据**
   原因：ViewModel 中未完全删除原有 `flowOf` 测试代码。
   解决：清空硬编码数据，让方法直接调用 DAO 的查询接口。

2. **问题2：应用启动崩溃，提示表不存在**
   原因：`@Entity(tableName = "")` 表名写错，未和数据库 `Schedule` 表一致。
   解决：修正注解为 `@Entity(tableName = "Schedule")`。

3. **问题3：Room 编译报错，注解无法识别**
   原因：未添加 KSP 编译器依赖。
   解决：在 app 模块依赖中补充 `ksp("androidx.room:room-compiler:2.7.0")`，同步 Gradle。

4. **问题4：查询单个站点数据为空**
   原因：SQL 语句中字段名 `stop_name` 书写错误。
   解决：核对数据库列名，保证 SQL 语句与原表字段完全一致。

## 五、实验总结
本次实验完成了 Room 整套数据层的开发，实现了从**预置 SQLite 数据库**到 Compose UI 的完整数据流转。通过实践掌握了 Room 三大核心组件的使用规范，理解了 Entity 表映射、DAO 数据查询、Database 单例管理的设计思想。

同时结合 Kotlin Flow 实现了响应式数据更新，配合 ViewModel 实现了 UI 与数据层的解耦，保证了页面生命周期安全。实验过程中也排查了依赖配置、表名字段匹配、数据流使用等常见问题，加深了对 Android 本地持久化、Jetpack 组件协作流程的理解，达到了本次实验的学习目标。