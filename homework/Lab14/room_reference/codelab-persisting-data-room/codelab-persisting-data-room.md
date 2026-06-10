# 使用 Room 持久保留数据 

> 本文基于同目录下的 `codelab-persisting-data-room-annotated.md` 改写。
>
> 原文更像“跟着步骤做”的 Codelab；这个 v2 版本会更详细地解释：
>
> - 为什么 Android 应用需要数据库
> - 为什么要用 Room，而不是直接用 SQLite
> - 为什么要拆出 `Entity`、`Dao`、`Database`、`Repository`
> - 为什么 `Database` 要做成单例
> - 为什么 `ItemsRepository` 看起来只是转发 DAO，却仍然值得存在
> - 从用户点击 Save 到数据进入数据库，中间到底发生了什么
>
> 如果你是第一次接触 Room，建议先不要急着背代码。先理解每个文件“负责什么”和“为什么存在”，后面写代码会轻松很多。

---

## 1. 先理解这个 Codelab 要解决什么问题

在没有数据库之前，应用里的数据通常只能短暂存在。

举个例子：你做了一个商品库存 App，用户在添加商品页面输入：

- 商品名称：Apple
- 价格：3.5
- 数量：10

如果你只是把这些数据存在普通变量里，应用一关闭，数据就没了。即使页面跳转、进程被系统回收，也可能丢失。

所以我们需要一种方式，把数据真正保存到手机本地。这个过程叫做**持久化**。

持久化的意思是：数据不会因为页面销毁、应用重启而消失。

这个 Codelab 使用的持久化工具是 **Room**。

---

## 2. Room、SQLite 和数据库之间是什么关系

Android 设备本身支持 SQLite 数据库。

SQLite 是一种轻量级数据库，适合放在手机这种本地设备上。它不是一个独立服务器，而是一个存在于应用内部的数据文件。

如果直接使用 SQLite，你通常需要自己写很多代码：

- 创建数据库文件
- 创建表
- 拼 SQL 字符串
- 执行插入、查询、更新、删除
- 把查询结果一列一列读出来
- 手动转换成 Kotlin 对象
- 自己处理线程问题
- 自己处理数据库版本升级

这些工作容易出错，也比较繁琐。

Room 就是在 SQLite 上面包了一层更适合 Android/Kotlin 使用的 API。

你可以这样理解：

```text
你的 Kotlin 代码
      ↓
Room
      ↓
SQLite
      ↓
手机本地数据库文件
```

Room 的价值不是“替代 SQLite”，而是**帮你更安全、更方便地使用 SQLite**。

Room 主要带来几个好处：

- 用 Kotlin 数据类表示数据库表
- 用接口方法表示数据库操作
- 编译时检查 SQL 是否有明显错误
- 支持协程和 Flow
- 减少大量模板代码
- 更容易配合 ViewModel、Repository 等架构组件

所以，学习 Room 的重点不是“Room 有哪些注解”，而是理解它如何把数据库操作拆成几个清晰的角色。

---

## 3. 这个 Inventory 应用最终要做什么

这个项目是一个库存管理应用。

它的核心数据是商品 `Item`。每个商品有：

- `id`：商品编号
- `name`：商品名称
- `price`：商品价格
- `quantity`：商品数量

这个 Codelab 会先实现“添加商品并保存到数据库”的功能。

后续 Codelab 会继续实现：

- 显示所有商品
- 查看某个商品详情
- 修改商品
- 删除商品

所以本篇重点是打好数据层基础。

---

## 4. 学 Room 前先建立一个整体模型

Room 里最重要的三个组件是：

- `Entity`
- `DAO`
- `Database`

在这个项目里，对应文件是：

- `Item.kt`
- `ItemDao.kt`
- `InventoryDatabase.kt`

它们的关系可以这样看：

```text
Item.kt
  定义数据库里 items 表长什么样

ItemDao.kt
  定义可以对 items 表做哪些操作

InventoryDatabase.kt
  创建数据库，并提供 ItemDao
```

如果用生活化一点的比喻：

```text
Entity   = 表格格式
DAO      = 操作表格的人
Database = 存放所有表格的文件柜
```

再结合 Android 应用架构，还会多出几个角色：

- `ItemsRepository`
- `OfflineItemsRepository`
- `AppContainer`
- `InventoryApplication`
- `ItemEntryViewModel`
- `ItemEntryScreen`

它们不是 Room 必须的三大组件，但它们是 Android 推荐架构中很重要的部分。

完整关系大概是：

```text
用户点击 Save
    ↓
ItemEntryScreen
    ↓
ItemEntryViewModel
    ↓
ItemsRepository
    ↓
OfflineItemsRepository
    ↓
ItemDao
    ↓
InventoryDatabase
    ↓
SQLite 数据库文件
```

这条链路一开始看起来长，但它的好处是每一层只做自己该做的事。

---

## 5. 为什么不直接在界面里写数据库代码

新手最容易问的问题是：

既然点击 Save 后要保存数据，那为什么不直接在 `ItemEntryScreen` 里面调用数据库？

比如这样写：

```kotlin
Button(onClick = {
    // 直接打开数据库
    // 直接插入数据
}) {
    Text("Save")
}
```

表面上看，这样最短。

但它会带来很多问题。

第一，界面代码会变复杂。

Compose 页面本来应该负责显示 UI、响应用户输入。如果它还负责创建数据库、拼 SQL、处理线程，代码会很快变得混乱。

第二，测试会变困难。

如果数据库代码直接写在界面里，你想单独测试保存逻辑，就必须启动整个 UI，甚至还要准备真实数据库。

第三，后续改动会很痛苦。

假设以后数据不只来自本地数据库，还要同步到网络。直接写在界面里的代码就会到处改。

第四，生命周期容易出问题。

数据库操作不能阻塞主线程，必须放到协程或后台线程里。界面层直接处理这些细节，很容易造成取消不及时或内存泄漏。

所以更好的方式是分层：

```text
UI 层：只负责界面和用户操作
ViewModel 层：负责界面状态和业务动作
Repository 层：负责提供数据操作入口
DAO 层：负责真正访问数据库表
Database 层：负责创建和管理数据库
```

这就是为什么项目里会有这么多看似“绕一圈”的类。

它们不是为了把简单事情复杂化，而是为了让项目变大以后仍然可维护。

---

## 6. 添加 Room 依赖项

要使用 Room，需要在模块级 Gradle 文件中加入依赖。

一般是在 `app/build.gradle.kts` 的 `dependencies` 代码块中添加：

```kotlin
// Room
implementation("androidx.room:room-runtime:${rootProject.extra["room_version"]}")
ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
implementation("androidx.room:room-ktx:${rootProject.extra["room_version"]}")
```

这三个依赖分别负责不同事情。

### `room-runtime`

这是 Room 的核心运行库。

它提供：

- `@Entity`
- `@Dao`
- `@Database`
- `@Insert`
- `@Update`
- `@Delete`
- `@Query`
- `RoomDatabase`
- `Room.databaseBuilder`

没有它，就无法使用 Room 的基本功能。

### `room-compiler`

Room 很多代码不是你手写出来的，而是在编译时自动生成的。

比如你写了：

```kotlin
@Dao
interface ItemDao {
    @Insert
    suspend fun insert(item: Item)
}
```

你只定义了接口，没有写实现类。

那真正执行插入数据库的代码是谁写的？

答案是 Room 编译器生成的。

`room-compiler` 就负责读取这些注解，然后在编译时生成实现代码。

这里使用的是：

```kotlin
ksp("androidx.room:room-compiler:...")
```

KSP 是 Kotlin Symbol Processing，比旧的 KAPT 更适合 Kotlin 项目，编译速度也通常更好。

### `room-ktx`

这是 Room 的 Kotlin 扩展。

它让 Room 更好地支持：

- `suspend` 函数
- Kotlin 协程
- `Flow`

本项目里的 DAO 会写成：

```kotlin
suspend fun insert(item: Item)
fun getAllItems(): Flow<List<Item>>
```

这些 Kotlin 风格的写法就依赖 Room KTX。

---

## 7. 第一步：创建 Entity，也就是数据库表

### Entity 是什么

Entity 是数据库中的表。

在 Kotlin 代码里，Entity 通常是一个 `data class`。

在数据库里，它会变成一张表。

比如这个类：

```kotlin
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val quantity: Int
)
```

可以理解成数据库里有一张表：

```text
表名：items

+----+-------+-------+----------+
| id | name  | price | quantity |
+----+-------+-------+----------+
| 1  | Apple | 3.5   | 10       |
| 2  | Book  | 20.0  | 5        |
+----+-------+-------+----------+
```

每个 `Item` 对象，对应表里的一行。

每个属性，对应表里的一列。

### 为什么 `Item` 要用 `data class`

起始代码可能是普通类：

```kotlin
class Item(
    val id: Int,
    val name: String,
    val price: Double,
    val quantity: Int
)
```

Room 实体通常写成 `data class`：

```kotlin
data class Item(
    val id: Int,
    val name: String,
    val price: Double,
    val quantity: Int
)
```

`data class` 的好处是 Kotlin 编译器会自动生成一些常用方法：

- `toString()`
- `equals()`
- `hashCode()`
- `copy()`
- `componentN()`

这些方法让它更适合表示“数据”。

比如你之后要修改某个商品的数量，可以用：

```kotlin
val newItem = oldItem.copy(quantity = 20)
```

这比手动重新构造一个对象更方便。

### 为什么要加 `@Entity`

`@Entity` 告诉 Room：

这个 Kotlin 类不是普通类，它要映射成数据库表。

```kotlin
@Entity(tableName = "items")
data class Item(...)
```

如果没有 `@Entity`，Room 不会把它当成数据库表。

### 为什么表名叫 `items`

这里写了：

```kotlin
@Entity(tableName = "items")
```

如果不写 `tableName`，Room 默认会用类名 `Item` 当表名。

但数据库表名通常更喜欢：

- 小写
- 复数
- 用下划线分隔多个单词

所以这里使用 `items`。

这不是必须的，但命名更清楚。

### 为什么一定要有主键

数据库表中的每一行都需要一个唯一标识。

这就像学生表里的学号、身份证号，不能重复。

在 Room 中，用 `@PrimaryKey` 标记主键：

```kotlin
@PrimaryKey(autoGenerate = true)
val id: Int = 0
```

`id` 就是每个商品的唯一编号。

如果没有主键，Room 不知道更新或删除时应该操作哪一行。

例如你要更新一个商品：

```kotlin
@Update
suspend fun update(item: Item)
```

Room 会根据 `item.id` 去数据库里找对应行。

如果 `id = 5`，Room 大概会执行类似这样的操作：

```sql
UPDATE items
SET name = ?, price = ?, quantity = ?
WHERE id = 5
```

如果没有主键，Room 就不知道 `WHERE` 条件该用什么。

### 为什么 `autoGenerate = true`

```kotlin
@PrimaryKey(autoGenerate = true)
val id: Int = 0
```

`autoGenerate = true` 表示 id 由数据库自动生成。

插入新商品时，你不用自己决定 id 是 1、2、3 还是 100。

你只需要创建：

```kotlin
Item(
    name = "Apple",
    price = 3.5,
    quantity = 10
)
```

因为 `id` 默认是 0，所以可以不传。

Room 插入数据库时，会让 SQLite 自动生成真正的 id。

### 为什么 id 默认值是 0

```kotlin
val id: Int = 0
```

当 `autoGenerate = true` 时，Room 看到主键值是 0，会把它当成“还没有真正 id 的新对象”，然后让数据库生成一个新 id。

这就是为什么新建商品时不需要手写 id。

### 完整的 `Item.kt`

```kotlin
package com.example.inventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val quantity: Int
)
```

---

## 8. 第二步：创建 DAO，也就是数据库操作接口

### DAO 是什么

DAO 是 Data Access Object，数据访问对象。

它负责定义“可以对数据库做什么操作”。

对 `items` 表来说，我们可能需要：

- 插入一个商品
- 更新一个商品
- 删除一个商品
- 查询某一个商品
- 查询所有商品

这些操作都放在 `ItemDao` 里。

### 为什么要有 DAO

如果没有 DAO，你可能会在代码各处直接写 SQL。

比如在添加页面写插入 SQL，在详情页写查询 SQL，在编辑页写更新 SQL。

这样会导致：

- SQL 分散在项目各处
- 很难统一维护
- 很难测试
- 很容易写错
- UI 层知道太多数据库细节

DAO 的作用就是把数据库操作集中起来。

其他层只要调用 Kotlin 方法，不需要关心底层 SQL 怎么执行。

比如：

```kotlin
itemDao.insert(item)
```

比直接在 ViewModel 里写 SQL 清楚得多。

### 创建 `ItemDao.kt`

```kotlin
package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * from items WHERE id = :id")
    fun getItem(id: Int): Flow<Item>

    @Query("SELECT * from items ORDER BY name ASC")
    fun getAllItems(): Flow<List<Item>>
}
```

下面逐个解释。

### 为什么 `ItemDao` 是接口

```kotlin
@Dao
interface ItemDao
```

你可能会疑惑：接口没有方法实现，那数据库操作到底是谁做？

Room 会在编译时帮你生成实现类。

你只要告诉 Room：

- 哪个方法是插入
- 哪个方法是更新
- 哪个方法是删除
- 哪个方法对应哪条 SQL 查询

Room 编译器会自动生成真正的代码。

所以 DAO 是“声明我要做什么”，Room 负责生成“具体怎么做”。

### 为什么要加 `@Dao`

`@Dao` 告诉 Room：

这个接口是数据库访问接口，需要由 Room 生成实现。

如果不加 `@Dao`，Room 不会处理这个接口。

### 插入：`@Insert`

```kotlin
@Insert(onConflict = OnConflictStrategy.IGNORE)
suspend fun insert(item: Item)
```

这个方法表示插入一个商品。

调用时：

```kotlin
itemDao.insert(item)
```

Room 会把 `item` 转换成一条插入语句，大概类似：

```sql
INSERT INTO items(name, price, quantity)
VALUES (?, ?, ?)
```

### 为什么插入方法是 `suspend`

数据库操作不能在主线程直接执行。

Android 的主线程负责：

- 绘制界面
- 响应点击
- 处理输入
- 执行动画

如果你在主线程执行耗时数据库操作，界面会卡住。

严重时会出现 ANR，也就是“应用无响应”。

所以 Room 要求很多数据库操作放在协程里执行。

`suspend` 的意思是：这个函数可以挂起，需要在协程中调用。

比如：

```kotlin
viewModelScope.launch {
    itemsRepository.insertItem(item)
}
```

或者在 Compose 页面中：

```kotlin
coroutineScope.launch {
    viewModel.saveItem()
}
```

### 为什么用 `OnConflictStrategy.IGNORE`

```kotlin
@Insert(onConflict = OnConflictStrategy.IGNORE)
```

`onConflict` 表示插入时发生冲突怎么办。

最常见的冲突是主键重复。

比如数据库里已经有一条 `id = 1` 的商品，你又插入一个 `id = 1` 的商品。

Room 需要知道该怎么处理。

常见策略有：

- `IGNORE`：忽略新数据，保留旧数据
- `REPLACE`：用新数据替换旧数据
- `ABORT`：中止操作

这里使用 `IGNORE`，表示如果发生冲突，就不插入新数据。

在本项目里，新商品的 id 是自动生成的，一般不容易冲突。这里更多是为了让插入行为更明确。

### 更新：`@Update`

```kotlin
@Update
suspend fun update(item: Item)
```

`@Update` 会根据主键更新已有行。

假设传入：

```kotlin
Item(
    id = 3,
    name = "Apple",
    price = 4.0,
    quantity = 20
)
```

Room 会找到 `id = 3` 的那一行，把它更新成新值。

如果数据库里没有 `id = 3` 的行，通常就不会更新任何行。

### 删除：`@Delete`

```kotlin
@Delete
suspend fun delete(item: Item)
```

`@Delete` 也是根据主键删除。

传入哪个 `Item`，Room 就根据它的 `id` 找到对应行并删除。

### 查询单个商品：`@Query`

```kotlin
@Query("SELECT * from items WHERE id = :id")
fun getItem(id: Int): Flow<Item>
```

这条 SQL 的意思是：

```sql
SELECT * FROM items WHERE id = ?
```

其中 `:id` 会绑定到函数参数 `id`。

比如你调用：

```kotlin
itemDao.getItem(5)
```

Room 会查询：

```sql
SELECT * FROM items WHERE id = 5
```

### 为什么返回 `Flow<Item>`

`Flow` 可以理解成“会持续发出数据的数据流”。

如果返回普通的 `Item`，那就是一次性查询：

```text
查询一次 → 得到一个 Item → 结束
```

如果返回 `Flow<Item>`，它是可观察的：

```text
开始观察数据库
    ↓
数据库发出当前 Item
    ↓
如果这条数据变了
    ↓
Room 自动发出新的 Item
```

这样 UI 就可以随着数据库变化自动更新。

对于 Compose 来说，这非常重要，因为 Compose 擅长根据状态重新绘制界面。

### 查询所有商品

```kotlin
@Query("SELECT * from items ORDER BY name ASC")
fun getAllItems(): Flow<List<Item>>
```

这表示查询所有商品，并按名称升序排列。

`ASC` 是 ascending，升序。

如果商品名称是：

```text
Book
Apple
Car
```

查询结果会变成：

```text
Apple
Book
Car
```

返回 `Flow<List<Item>>` 表示：

- 当前表里有多少商品，就发出一个列表
- 如果商品新增、删除、修改，Room 会重新发出新的列表

### 为什么增删改是 `suspend`，查询是 `Flow`

这是一个重要区别。

增删改通常是一次性动作：

```text
插入一次 → 完成
更新一次 → 完成
删除一次 → 完成
```

所以它们适合写成：

```kotlin
suspend fun insert(item: Item)
suspend fun update(item: Item)
suspend fun delete(item: Item)
```

查询通常要被 UI 持续观察：

```text
列表页面打开
    ↓
显示当前商品列表
    ↓
数据库变化
    ↓
列表自动刷新
```

所以查询适合返回：

```kotlin
Flow<List<Item>>
```

---

## 9. 第三步：创建 Database，也就是数据库入口

### Database 是什么

`InventoryDatabase` 是 Room 数据库类。

它负责：

- 告诉 Room 数据库里有哪些表
- 告诉 Room 数据库版本是多少
- 提供 DAO
- 创建数据库实例
- 保证整个应用只使用一个数据库实例

完整代码如下：

```kotlin
package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
```

这段代码非常关键，下面详细拆解。

### 为什么要继承 `RoomDatabase`

```kotlin
abstract class InventoryDatabase : RoomDatabase()
```

Room 数据库类必须继承 `RoomDatabase`。

这表示它是一个 Room 管理的数据库，而不是普通 Kotlin 类。

Room 会基于这个抽象类生成实际数据库实现。

你不需要自己写 SQLiteOpenHelper，也不需要自己管理底层连接。

### 为什么这个类是 `abstract`

```kotlin
abstract class InventoryDatabase : RoomDatabase()
```

因为你不会直接手动创建它的实例。

你不会写：

```kotlin
InventoryDatabase()
```

真正的实现类由 Room 在编译时生成。

你只声明数据库结构，Room 负责生成具体实现。

### `@Database` 注解是什么意思

```kotlin
@Database(entities = [Item::class], version = 1, exportSchema = false)
```

这个注解告诉 Room：

这个类是一个数据库类。

它里面有三个重要参数。

### `entities = [Item::class]`

```kotlin
entities = [Item::class]
```

这表示数据库中包含 `Item` 这张表。

如果将来还有分类表 `Category`，可能会变成：

```kotlin
@Database(
    entities = [Item::class, Category::class],
    version = 2,
    exportSchema = false
)
```

Room 必须提前知道数据库里有哪些实体。

如果你创建了 `Item` 实体，但没有放进 `entities`，Room 不会把它加入数据库。

### `version = 1`

```kotlin
version = 1
```

数据库版本号用于处理数据库结构变化。

比如一开始 `Item` 有：

- id
- name
- price
- quantity

后来你想加一个字段：

```kotlin
val description: String
```

这就改变了数据库表结构。

数据库版本号必须从 1 升到 2。

否则 Room 会发现代码中的表结构和设备上的旧数据库不一致。

### `exportSchema = false`

```kotlin
exportSchema = false
```

Schema 可以理解成数据库结构说明。

如果设置为 `true`，Room 会导出 schema 文件，方便追踪数据库结构变化。

教学项目里常设置为 `false`，减少配置负担。

生产项目里更建议设置为 `true`，方便做版本迁移和审计。

### 为什么要写 `abstract fun itemDao()`

```kotlin
abstract fun itemDao(): ItemDao
```

这个方法表示：

这个数据库可以提供一个 `ItemDao`。

外部想操作 `items` 表时，需要先从数据库拿到 DAO：

```kotlin
val dao = database.itemDao()
```

然后再调用：

```kotlin
dao.insert(item)
dao.getAllItems()
```

所以 `Database` 不是直接暴露所有 SQL 操作，而是通过 DAO 暴露操作入口。

这让结构更清晰：

```text
Database：提供 DAO
DAO：提供表操作
Entity：定义表结构
```

### 为什么要做成单例

这是很多新手最容易忽略，但非常重要的点。

数据库实例不应该到处创建。

如果你在多个页面、多个 ViewModel 里分别创建数据库，可能出现：

- 多个数据库连接同时存在
- 浪费内存
- 线程竞争更复杂
- 数据状态难以统一
- 初始化开销变大

所以推荐整个应用只创建一个数据库实例。

这就是单例模式。

```kotlin
companion object {
    @Volatile
    private var Instance: InventoryDatabase? = null

    fun getDatabase(context: Context): InventoryDatabase {
        return Instance ?: synchronized(this) {
            Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                .fallbackToDestructiveMigration()
                .build()
                .also { Instance = it }
        }
    }
}
```

你可以理解成：

```text
第一次调用 getDatabase()
    → 发现 Instance 是 null
    → 创建数据库
    → 保存到 Instance
    → 返回数据库

第二次调用 getDatabase()
    → 发现 Instance 已经有值
    → 直接返回同一个数据库
```

### 为什么要用 `companion object`

`companion object` 可以让你通过类名直接访问里面的方法和变量。

比如：

```kotlin
InventoryDatabase.getDatabase(context)
```

你不需要先创建一个 `InventoryDatabase` 对象。

这正好适合单例模式。

### 为什么要用 `@Volatile`

```kotlin
@Volatile
private var Instance: InventoryDatabase? = null
```

应用中可能有多个线程。

一个线程创建了数据库实例，另一个线程要立刻看到这个结果。

`@Volatile` 的作用是保证 `Instance` 的变化对所有线程可见。

如果不加它，某个线程可能还以为 `Instance` 是 null，于是重复创建数据库。

简单理解：

```text
@Volatile = 不要让线程看到过期的 Instance 值
```

### 为什么要用 `synchronized`

```kotlin
synchronized(this) {
    ...
}
```

假设两个线程几乎同时第一次调用 `getDatabase()`。

它们都发现：

```kotlin
Instance == null
```

如果没有锁，两个线程可能都去创建数据库。

`synchronized` 可以保证同一时间只有一个线程进入这段代码。

这就避免了重复创建。

### `Instance ?: synchronized(this)` 是什么意思

```kotlin
return Instance ?: synchronized(this) {
    ...
}
```

这是 Kotlin 的 Elvis 操作符。

意思是：

```text
如果 Instance 不为 null，就直接返回 Instance
如果 Instance 是 null，就进入 synchronized 创建
```

它是单例懒加载的常见写法。

### `Room.databaseBuilder` 是什么

```kotlin
Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
```

这行代码真正创建 Room 数据库。

三个参数分别是：

```kotlin
context
InventoryDatabase::class.java
"item_database"
```

#### 第一个参数：`context`

Room 需要 `Context` 才能知道数据库文件应该放在哪里。

这里应该传应用级别的 context。

所以后面会使用：

```kotlin
context.applicationContext
```

或者从 `Application` 传入 context。

不要随便传 Activity 的 context，因为 Activity 会销毁，可能造成不必要的引用问题。

#### 第二个参数：数据库类

```kotlin
InventoryDatabase::class.java
```

告诉 Room 要创建哪个数据库。

#### 第三个参数：数据库文件名

```kotlin
"item_database"
```

这是实际保存在设备上的数据库文件名。

你可以在 Android Studio 的 Database Inspector 里看到这个名字。

### 为什么要写 `.fallbackToDestructiveMigration()`

```kotlin
.fallbackToDestructiveMigration()
```

这表示：

如果数据库版本升级了，但你没有提供迁移方案，Room 可以直接删除旧数据库并重新创建。

这在教学项目里很方便，因为你不需要先学习复杂的迁移。

但它有一个严重后果：

用户旧数据会丢失。

所以生产应用不应该随便用它。

生产应用应该写 `Migration`，告诉 Room 怎么从旧表结构迁移到新表结构。

### `.build()` 做什么

```kotlin
.build()
```

执行前面的配置，真正创建数据库实例。

### `.also { Instance = it }` 是什么

```kotlin
.also { Instance = it }
```

`also` 会拿到刚刚创建好的数据库实例，并把它保存到 `Instance`。

等下一次调用 `getDatabase()` 时，就可以直接复用。

---

## 10. 第四步：创建 Repository，也就是数据层入口

### Repository 是什么

Repository 通常翻译成“存储库”。

它的作用是给应用其他部分提供数据操作入口。

在这个项目中，ViewModel 不直接调用 `ItemDao`，而是调用 `ItemsRepository`。

```text
ViewModel
    ↓
ItemsRepository
    ↓
ItemDao
    ↓
Room Database
```

### 新手最常见疑问：为什么需要 Repository

你可能会觉得：

`OfflineItemsRepository` 只是把 DAO 方法转发了一遍。

比如：

```kotlin
override suspend fun insertItem(item: Item) = itemDao.insert(item)
```

看起来多此一举。

但 Repository 的价值不是这一行代码本身，而是它隔离了“数据从哪里来”。

今天数据来自 Room：

```text
ItemsRepository → OfflineItemsRepository → Room
```

以后数据可能来自网络：

```text
ItemsRepository → NetworkItemsRepository → Retrofit API
```

也可能本地和网络都要用：

```text
ItemsRepository → SyncItemsRepository → Room + API
```

如果 ViewModel 直接调用 DAO，那么 ViewModel 就知道了底层使用 Room。

将来你要换数据源，ViewModel 也要改。

如果 ViewModel 只依赖 Repository 接口，那么只要 Repository 方法不变，ViewModel 就不用管底层实现怎么变。

这就是抽象的价值。

### Repository 还能让测试更容易

假设你要测试 `ItemEntryViewModel`。

如果 ViewModel 直接依赖 `ItemDao`，测试时可能需要准备真实数据库。

如果 ViewModel 依赖 `ItemsRepository` 接口，你可以创建一个假的实现：

```kotlin
class FakeItemsRepository : ItemsRepository {
    val insertedItems = mutableListOf<Item>()

    override suspend fun insertItem(item: Item) {
        insertedItems.add(item)
    }

    // 其他方法按测试需要实现
}
```

这样就可以不启动数据库，直接测试 ViewModel 的逻辑。

### `ItemsRepository` 接口

```kotlin
package com.example.inventory.data

import kotlinx.coroutines.flow.Flow

interface ItemsRepository {
    fun getAllItemsStream(): Flow<List<Item>>

    fun getItemStream(id: Int): Flow<Item?>

    suspend fun insertItem(item: Item)

    suspend fun deleteItem(item: Item)

    suspend fun updateItem(item: Item)
}
```

这个接口描述了数据层对外提供的能力。

注意它没有提 Room，也没有提 DAO。

这很重要。

对 ViewModel 来说，它只知道：

```text
我可以通过 ItemsRepository 获取、插入、删除、更新 Item。
```

它不需要知道：

```text
底层是不是 Room
数据库文件叫什么
DAO 方法怎么写
SQL 查询是什么
```

### 为什么方法名叫 `getAllItemsStream`

```kotlin
fun getAllItemsStream(): Flow<List<Item>>
```

这里用了 `Stream`，是因为返回值是 `Flow`。

它不是一次性返回一个列表，而是返回一个会持续发出列表变化的数据流。

更准确地说：

```text
getAllItemsStream()
    返回一个 Flow
    Flow 会发出当前列表
    数据库变了以后 Flow 会继续发出新列表
```

### 为什么 `getItemStream(id)` 返回 `Flow<Item?>`

```kotlin
fun getItemStream(id: Int): Flow<Item?>
```

这里是 `Item?`，表示可能查不到。

比如用户传入 `id = 999`，数据库里没有这条商品，就可能返回 null。

接口层把这种情况表达出来，调用方就应该处理 null。

### `OfflineItemsRepository` 实现类

```kotlin
package com.example.inventory.data

import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val itemDao: ItemDao) : ItemsRepository {
    override fun getAllItemsStream(): Flow<List<Item>> = itemDao.getAllItems()

    override fun getItemStream(id: Int): Flow<Item?> = itemDao.getItem(id)

    override suspend fun insertItem(item: Item) = itemDao.insert(item)

    override suspend fun deleteItem(item: Item) = itemDao.delete(item)

    override suspend fun updateItem(item: Item) = itemDao.update(item)
}
```

### 为什么叫 `OfflineItemsRepository`

`Offline` 表示这个实现使用的是离线数据源，也就是本地数据库。

如果以后有网络版本，可以有：

```kotlin
class NetworkItemsRepository(...) : ItemsRepository
```

如果以后有缓存同步版本，可以有：

```kotlin
class DefaultItemsRepository(...) : ItemsRepository
```

命名的关键是让别人一看就知道这个实现的数据来源。

### 为什么构造函数里传入 `ItemDao`

```kotlin
class OfflineItemsRepository(private val itemDao: ItemDao)
```

Repository 自己不创建 DAO。

它只是使用外部传进来的 DAO。

这叫依赖注入。

好处是：

- Repository 更容易测试
- Repository 不关心数据库怎么创建
- 创建依赖的责任交给更外层的容器

如果 Repository 自己写：

```kotlin
val database = InventoryDatabase.getDatabase(context)
val itemDao = database.itemDao()
```

那它就必须依赖 `Context`，也必须知道数据库创建细节。

这样职责就混在一起了。

正确分工是：

```text
InventoryDatabase：创建 DAO
AppContainer：组装 Repository 和 DAO
OfflineItemsRepository：使用 DAO 完成数据操作
```

---

## 11. 第五步：创建 AppContainer，统一管理依赖

### AppContainer 是什么

`AppContainer` 是一个简单的依赖容器。

它负责创建应用中需要长期使用的对象。

在这个项目里，它主要创建：

```kotlin
ItemsRepository
```

### 为什么需要 AppContainer

如果没有 AppContainer，ViewModel 可能要自己创建 Repository。

比如：

```kotlin
class ItemEntryViewModel(context: Context) : ViewModel() {
    private val dao = InventoryDatabase.getDatabase(context).itemDao()
    private val repository = OfflineItemsRepository(dao)
}
```

这样不好。

原因是：

- ViewModel 不应该直接依赖 `Context`
- ViewModel 不应该负责创建数据库
- 多个 ViewModel 可能重复创建 Repository
- 测试时不好替换依赖

所以我们把依赖创建集中放到容器里。

### 常见的 `AppContainer.kt`

```kotlin
package com.example.inventory.data

import android.content.Context

interface AppContainer {
    val itemsRepository: ItemsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(InventoryDatabase.getDatabase(context).itemDao())
    }
}
```

### 为什么 `AppContainer` 是接口

```kotlin
interface AppContainer {
    val itemsRepository: ItemsRepository
}
```

接口描述“这个容器能提供什么”。

真实运行时使用 `AppDataContainer`。

测试时可以提供另一个容器。

这和 `ItemsRepository` 的设计思路一样：面向接口，而不是具体实现。

### 为什么使用 `by lazy`

```kotlin
override val itemsRepository: ItemsRepository by lazy {
    OfflineItemsRepository(InventoryDatabase.getDatabase(context).itemDao())
}
```

`by lazy` 表示延迟初始化。

也就是说，应用启动时不会立刻创建 Repository 和数据库。

只有第一次访问：

```kotlin
container.itemsRepository
```

时，才会执行 `{ ... }` 里的代码。

这样做有几个好处：

- 应用启动更轻
- 没用到数据库时不创建数据库
- Repository 只创建一次

### 这行代码到底做了什么

```kotlin
OfflineItemsRepository(InventoryDatabase.getDatabase(context).itemDao())
```

拆开看就是：

```kotlin
val database = InventoryDatabase.getDatabase(context)
val dao = database.itemDao()
val repository = OfflineItemsRepository(dao)
```

所以它完成了三件事：

1. 获取数据库单例
2. 从数据库拿到 DAO
3. 把 DAO 传给 Repository

最终 ViewModel 拿到的是 `ItemsRepository`，而不是 DAO 或 Database。

---

## 12. 第六步：在 Application 中保存容器

### 为什么需要 `InventoryApplication`

`Application` 是整个 Android 应用级别的对象。

它的生命周期比 Activity、Screen、ViewModel 都更长。

应用启动时会创建 Application。

应用进程存在期间，它通常一直存在。

所以适合把全局依赖容器放在 Application 中。

常见代码如下：

```kotlin
package com.example.inventory

import android.app.Application
import com.example.inventory.data.AppContainer
import com.example.inventory.data.AppDataContainer

class InventoryApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
```

### 为什么不放在 Activity 里

Activity 可能因为旋转屏幕、配置变化等原因被销毁重建。

如果依赖容器放在 Activity 里，可能会重复创建。

Application 更适合保存应用级别的对象。

### 为什么 `container` 用 `lateinit`

```kotlin
lateinit var container: AppContainer
```

因为 `container` 需要在 `onCreate()` 中初始化。

在属性声明处还不能直接使用 `this` 创建 `AppDataContainer`。

所以先声明，稍后初始化。

### 还需要在 Manifest 中声明 Application

如果项目中使用了自定义 Application，需要在 `AndroidManifest.xml` 的 `<application>` 标签里指定：

```xml
<application
    android:name=".InventoryApplication"
    ... >
```

否则系统不会使用你的 `InventoryApplication`，`container` 也不会被创建。

---

## 13. 第七步：让 ViewModel 使用 Repository

### ViewModel 在这里负责什么

`ItemEntryViewModel` 是添加商品页面的 ViewModel。

它主要负责：

- 保存输入框状态
- 判断输入是否有效
- 把 UI 字符串转换成数据库实体
- 调用 Repository 保存商品

它不应该负责：

- 创建数据库
- 创建 DAO
- 直接写 SQL
- 管理数据库连接

### UI 状态为什么和数据库实体不一样

起始代码中通常有两个数据类：

```kotlin
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val quantity: String = "",
)
```

注意：

```kotlin
price: String
quantity: String
```

但是数据库实体里是：

```kotlin
price: Double
quantity: Int
```

为什么 UI 层不直接用 `Double` 和 `Int`？

因为输入框里的内容本质上是文本。

用户输入过程中可能出现：

- 空字符串 `""`
- 只输入了小数点 `"."`
- 输入了非法字符 `"abc"`
- 输入还没完成 `"3."`

这些中间状态不一定能转换成合法数字。

所以 UI 状态用 `String` 更符合真实输入过程。

等点击保存时，再尝试转换成数据库需要的类型。

### 转换函数

```kotlin
fun ItemDetails.toItem(): Item = Item(
    id = id,
    name = name,
    price = price.toDoubleOrNull() ?: 0.0,
    quantity = quantity.toIntOrNull() ?: 0
)

fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    price = price.toString(),
    quantity = quantity.toString()
)
```

### 为什么要有 `toItem()`

`toItem()` 把 UI 输入数据转换成数据库实体。

```text
ItemDetails
    name: String
    price: String
    quantity: String

        ↓ toItem()

Item
    name: String
    price: Double
    quantity: Int
```

保存到数据库时必须使用 `Item`，因为 Room 认识的是 Entity。

### 为什么用 `toDoubleOrNull()` 和 `toIntOrNull()`

```kotlin
price.toDoubleOrNull() ?: 0.0
quantity.toIntOrNull() ?: 0
```

如果直接写：

```kotlin
price.toDouble()
```

当用户输入 `"abc"` 时，应用会崩溃。

`toDoubleOrNull()` 更安全。

如果转换失败，它返回 null。

然后通过 `?:` 给默认值。

不过要注意：真实项目中更推荐在保存前做更严格校验，而不是把非法输入直接变成 0。

### 给 ViewModel 注入 Repository

原本的 ViewModel 可能没有构造参数。

现在需要改成：

```kotlin
package com.example.inventory.ui.item

import androidx.lifecycle.ViewModel
import com.example.inventory.data.ItemsRepository

class ItemEntryViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState = ItemUiState(
            itemDetails = itemDetails,
            isEntryValid = validateInput(itemDetails)
        )
    }

    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && price.isNotBlank() && quantity.isNotBlank()
        }
    }
}
```

### 为什么 `itemsRepository` 是构造函数参数

```kotlin
class ItemEntryViewModel(private val itemsRepository: ItemsRepository) : ViewModel()
```

这叫构造函数注入。

意思是：ViewModel 需要 Repository，但它不自己创建，而是由外部传进来。

好处是：

- ViewModel 更专注于自己的逻辑
- 更容易测试
- 可以替换不同 Repository 实现
- 不需要在 ViewModel 中保存 Context

### 为什么 `saveItem()` 是 `suspend`

```kotlin
suspend fun saveItem()
```

因为它内部调用：

```kotlin
itemsRepository.insertItem(...)
```

而 Repository 的插入方法最终会调用 DAO 的：

```kotlin
suspend fun insert(item: Item)
```

数据库写入是耗时操作，需要在协程中运行。

所以 `saveItem()` 也必须是 `suspend`。

### `saveItem()` 的完整流程

```kotlin
suspend fun saveItem() {
    if (validateInput()) {
        itemsRepository.insertItem(itemUiState.itemDetails.toItem())
    }
}
```

逐步拆解：

```text
1. validateInput()
   检查 name、price、quantity 是否为空

2. itemUiState.itemDetails
   获取当前输入框里的内容

3. toItem()
   把 UI 字符串数据转换成数据库实体 Item

4. itemsRepository.insertItem(...)
   通过 Repository 保存到数据源

5. OfflineItemsRepository 调用 itemDao.insert(...)

6. Room 执行数据库插入
```

---

## 14. 第八步：配置 ViewModelProvider.Factory

### 为什么不能直接创建 ViewModel

普通 ViewModel 没有参数时，可以由系统直接创建。

但现在 `ItemEntryViewModel` 需要一个参数：

```kotlin
ItemEntryViewModel(private val itemsRepository: ItemsRepository)
```

系统不知道这个 `itemsRepository` 应该从哪里来。

所以需要提供一个 Factory。

Factory 的作用就是告诉系统：

当你需要创建 `ItemEntryViewModel` 时，请这样创建。

### 常见的 `AppViewModelProvider.kt`

代码大概如下：

```kotlin
package com.example.inventory.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.inventory.InventoryApplication
import com.example.inventory.ui.item.ItemEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ItemEntryViewModel(inventoryApplication().container.itemsRepository)
        }
    }
}

fun CreationExtras.inventoryApplication(): InventoryApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)
```

### 这段代码在做什么

关键是这一行：

```kotlin
ItemEntryViewModel(inventoryApplication().container.itemsRepository)
```

它做了几件事：

1. 从 `CreationExtras` 里拿到当前应用的 `InventoryApplication`
2. 从 `InventoryApplication` 中拿到 `container`
3. 从 `container` 中拿到 `itemsRepository`
4. 把 `itemsRepository` 传给 `ItemEntryViewModel`

也就是：

```text
Application
    ↓
AppContainer
    ↓
ItemsRepository
    ↓
ItemEntryViewModel
```

### 为什么这里要通过 Application 拿 Repository

因为 Repository 是应用级别依赖。

它不属于某一个页面，也不应该每个页面自己创建。

Application 中保存的容器可以在多个 ViewModel 中共享同一套依赖。

这就是手动依赖注入的基本思路。

---

## 15. 第九步：在 Compose 页面中调用保存

### 页面层负责什么

`ItemEntryScreen` 是添加商品页面。

它应该负责：

- 显示输入框
- 接收用户输入
- 显示保存按钮
- 点击保存时调用 ViewModel
- 保存成功后返回上一页

它不应该负责：

- 插入数据库
- 创建 Repository
- 创建 DAO
- 写 SQL

### 为什么要用 `rememberCoroutineScope`

因为 `saveItem()` 是 `suspend` 函数。

挂起函数不能直接在普通 `onClick` 里调用。

错误示例：

```kotlin
onSaveClick = {
    viewModel.saveItem()
}
```

正确方式是启动协程：

```kotlin
val coroutineScope = rememberCoroutineScope()

onSaveClick = {
    coroutineScope.launch {
        viewModel.saveItem()
        navigateBack()
    }
}
```

### `rememberCoroutineScope()` 的作用

`rememberCoroutineScope()` 会创建一个和当前 Composable 生命周期相关的协程作用域。

当这个 Composable 离开界面时，这个作用域会被取消。

这比随便创建一个全局协程更安全。

### 保存按钮完整逻辑

在 `ItemEntryScreen.kt` 中类似这样：

```kotlin
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ItemEntryScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        ItemEntryBody(
            itemUiState = viewModel.itemUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveItem()
                    navigateBack()
                }
            },
            modifier = modifier.padding(innerPadding)
        )
    }
}
```

### 为什么保存后调用 `navigateBack()`

保存成功后，用户通常应该回到上一页。

流程是：

```text
点击 Save
    ↓
启动协程
    ↓
保存到数据库
    ↓
返回上一页
```

这里把 `navigateBack()` 放在 `saveItem()` 后面，是为了先保存，再离开页面。

### 一个小问题：保存失败怎么办

这个 Codelab 的代码比较简单，只检查了是否为空。

真实项目中可能还要处理：

- 价格不是合法数字
- 数量不是合法整数
- 数量不能小于 0
- 保存失败时显示错误
- 保存过程中禁用按钮
- 防止重复点击

教学阶段先聚焦 Room 的核心流程。

---

## 16. 从点击 Save 到数据库写入：完整调用链

这是本篇最重要的部分。

当用户点击 Save，完整过程如下：

```text
1. 用户点击 Save 按钮

2. ItemEntryScreen 的 onSaveClick 被触发

3. coroutineScope.launch 启动协程

4. 调用 viewModel.saveItem()

5. ViewModel 调用 validateInput()

6. ViewModel 把 ItemDetails 转成 Item

7. ViewModel 调用 itemsRepository.insertItem(item)

8. 实际实现类 OfflineItemsRepository 接收调用

9. OfflineItemsRepository 调用 itemDao.insert(item)

10. Room 生成的 DAO 实现执行插入

11. SQLite 把数据写入 item_database 文件里的 items 表

12. saveItem() 返回

13. 页面执行 navigateBack()
```

用代码连接起来就是：

```kotlin
// ItemEntryScreen.kt
onSaveClick = {
    coroutineScope.launch {
        viewModel.saveItem()
        navigateBack()
    }
}
```

```kotlin
// ItemEntryViewModel.kt
suspend fun saveItem() {
    if (validateInput()) {
        itemsRepository.insertItem(itemUiState.itemDetails.toItem())
    }
}
```

```kotlin
// OfflineItemsRepository.kt
override suspend fun insertItem(item: Item) = itemDao.insert(item)
```

```kotlin
// ItemDao.kt
@Insert(onConflict = OnConflictStrategy.IGNORE)
suspend fun insert(item: Item)
```

Room 编译器会生成 `insert()` 的具体实现。

你不用写底层 SQLite 插入代码。

---

## 17. 用一张图理解各层职责

```text
+----------------------+
| ItemEntryScreen      |
|----------------------|
| 显示输入框和按钮      |
| 用户点击 Save         |
+----------+-----------+
           |
           v
+----------------------+
| ItemEntryViewModel   |
|----------------------|
| 保存 UI 状态          |
| 校验输入              |
| ItemDetails -> Item   |
+----------+-----------+
           |
           v
+----------------------+
| ItemsRepository      |
|----------------------|
| 数据层抽象接口        |
| 不关心底层数据源      |
+----------+-----------+
           |
           v
+----------------------+
| OfflineItemsRepository|
|----------------------|
| 使用本地 DAO 实现接口 |
+----------+-----------+
           |
           v
+----------------------+
| ItemDao              |
|----------------------|
| 定义数据库操作方法    |
| insert/update/query   |
+----------+-----------+
           |
           v
+----------------------+
| InventoryDatabase    |
|----------------------|
| Room 数据库入口       |
| 提供 DAO              |
| 管理数据库单例        |
+----------+-----------+
           |
           v
+----------------------+
| SQLite 数据库文件     |
|----------------------|
| item_database         |
| items 表              |
+----------------------+
```

---

## 18. 为什么这个项目用了接口和实现类

项目里有：

```kotlin
interface ItemsRepository
class OfflineItemsRepository : ItemsRepository
```

这是一种常见设计。

接口负责规定能力：

```text
你必须能获取商品、插入商品、删除商品、更新商品
```

实现类负责具体做法：

```text
我用 Room 数据库来实现这些能力
```

好处是调用方依赖接口，不依赖具体实现。

比如 ViewModel 只知道：

```kotlin
private val itemsRepository: ItemsRepository
```

它不知道实际是：

```kotlin
OfflineItemsRepository
```

这样以后替换实现时更容易。

---

## 19. 为什么 Room 推荐配合 Flow

Room 查询可以直接返回 `Flow`：

```kotlin
@Query("SELECT * from items ORDER BY name ASC")
fun getAllItems(): Flow<List<Item>>
```

这和 Compose 的状态模型很契合。

传统方式可能是：

```text
打开页面
    ↓
查询一次数据库
    ↓
显示列表
    ↓
新增一条数据
    ↓
手动重新查询
    ↓
手动刷新 UI
```

使用 Flow 后：

```text
打开页面
    ↓
开始收集 Flow
    ↓
Room 发出当前列表
    ↓
新增一条数据
    ↓
Room 自动发出新列表
    ↓
UI 自动刷新
```

也就是说，你不用手动告诉列表“刷新一下”。

数据库变化会通过 Flow 传递到 UI 状态。

这个 Codelab 当前主要实现插入，后续显示列表时 Flow 的优势会更明显。

---

## 20. Database Inspector：确认数据真的写进去了

因为这个 Codelab 结束时，列表展示功能还没完全实现，你可能会问：

我怎么知道数据真的保存进数据库了？

答案是用 Android Studio 的 Database Inspector。

### 使用步骤

1. 在 API 26 或更高版本的模拟器或真机上运行应用
2. 打开 Android Studio
3. 选择 `View > Tool Windows > App Inspection`
4. 打开 `Database Inspector`
5. 选择正在运行的应用进程
6. 展开数据库 `item_database`
7. 查看 `items` 表
8. 勾选 `Live updates`

添加商品后，你应该能看到 `items` 表里出现新行。

### 为什么表里可能叫 `items`

因为 Entity 里写了：

```kotlin
@Entity(tableName = "items")
```

### 为什么数据库叫 `item_database`

因为创建数据库时写了：

```kotlin
Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
```

所以 Database Inspector 里会看到这个数据库名。

---

## 21. 常见错误和排查方法

### 1. 忘记添加 Room 依赖

表现：

- `@Entity` 无法导入
- `@Dao` 无法导入
- `RoomDatabase` 无法识别

解决：

- 检查 `room-runtime`
- 检查 `room-compiler`
- 检查 `room-ktx`
- 确认 KSP 插件已配置
- Sync Gradle

### 2. 忘记把 Entity 加到 Database

错误示例：

```kotlin
@Database(entities = [], version = 1)
```

正确示例：

```kotlin
@Database(entities = [Item::class], version = 1, exportSchema = false)
```

如果没把 `Item` 加进去，Room 不会创建 `items` 表。

### 3. DAO 没有加 `@Dao`

错误示例：

```kotlin
interface ItemDao
```

正确示例：

```kotlin
@Dao
interface ItemDao
```

没有 `@Dao`，Room 不会生成实现。

### 4. 主键没有设置

Room 实体必须有主键。

正确示例：

```kotlin
@PrimaryKey(autoGenerate = true)
val id: Int = 0
```

### 5. 数据库版本改了但没有迁移

如果你修改了 Entity 字段，比如新增列，但版本号没改，Room 可能报错。

如果版本号改了，但没写 Migration，也可能报错。

教学项目中用：

```kotlin
.fallbackToDestructiveMigration()
```

可以绕过迁移问题，但会删除旧数据。

### 6. 在主线程执行数据库操作

如果 DAO 方法不是 `suspend`，或者你在不合适的地方调用数据库操作，可能导致主线程问题。

推荐：

- 插入、更新、删除使用 `suspend`
- 在协程中调用
- 查询使用 `Flow`

### 7. ViewModel 创建失败

如果 `ItemEntryViewModel` 加了构造参数，但没有更新 Factory，可能会出现创建 ViewModel 失败。

要确保 Factory 里有：

```kotlin
initializer {
    ItemEntryViewModel(inventoryApplication().container.itemsRepository)
}
```

### 8. 点击 Save 没反应

排查顺序：

1. `onSaveClick` 有没有绑定
2. 有没有启动协程
3. `viewModel.saveItem()` 有没有调用
4. `validateInput()` 是否返回 true
5. `itemsRepository.insertItem()` 是否执行
6. `itemDao.insert()` 是否执行
7. Database Inspector 里是否有新数据

---

## 22. 每个文件的最终职责总结

### `Item.kt`

定义数据库表。

```text
Item 类 = items 表
Item 属性 = items 表的列
Item 对象 = items 表的一行
```

### `ItemDao.kt`

定义数据库操作。

```text
insert()
update()
delete()
getItem()
getAllItems()
```

### `InventoryDatabase.kt`

定义 Room 数据库。

```text
包含哪些 Entity
数据库版本
提供 DAO
创建数据库单例
```

### `ItemsRepository.kt`

定义数据层对外接口。

```text
ViewModel 通过它访问数据
不暴露 Room 细节
```

### `OfflineItemsRepository.kt`

Repository 的本地数据库实现。

```text
内部调用 ItemDao
把 DAO 包装成 Repository 接口
```

### `AppContainer.kt`

创建并保存应用级依赖。

```text
创建 Database
获取 DAO
创建 Repository
```

### `InventoryApplication.kt`

在应用启动时创建依赖容器。

```text
container = AppDataContainer(this)
```

### `AppViewModelProvider.kt`

告诉系统如何创建带参数的 ViewModel。

```text
从 Application 拿 container
从 container 拿 repository
传给 ViewModel
```

### `ItemEntryViewModel.kt`

处理添加商品页面的状态和保存动作。

```text
保存输入状态
校验输入
转换数据
调用 Repository
```

### `ItemEntryScreen.kt`

显示添加商品 UI。

```text
输入框
保存按钮
启动协程调用 saveItem()
保存后返回
```

---

## 23. 用一句话记住每一层

```text
Entity：数据库表长什么样
DAO：能对表做什么操作
Database：怎么拿到数据库和 DAO
Repository：给 ViewModel 的数据入口
AppContainer：负责把依赖创建好
ViewModel：处理页面状态和业务动作
Screen：显示界面并响应用户操作
```

---

## 24. 学完本篇你应该真正理解什么

学完这篇，不只是知道“Room 要写三个类”，而应该理解：

1. Room 是 SQLite 的抽象层，不是全新的数据库。
2. `@Entity` 把 Kotlin 数据类映射成数据库表。
3. `@PrimaryKey` 让每一行数据有唯一身份。
4. `@Dao` 接口声明数据库操作，Room 会生成实现。
5. `@Insert`、`@Update`、`@Delete` 适合一次性写操作，所以通常是 `suspend`。
6. `@Query` 查询可以返回 `Flow`，让 UI 自动响应数据库变化。
7. `InventoryDatabase` 是数据库入口，负责提供 DAO。
8. 数据库实例应该是单例，避免重复创建和资源浪费。
9. `ItemsRepository` 隔离 ViewModel 和 Room，让数据层更可替换、更容易测试。
10. `AppContainer` 是手动依赖注入，把 Database、DAO、Repository 组装起来。
11. ViewModel 不直接创建数据库，而是通过构造函数拿到 Repository。
12. Compose 页面中调用挂起函数需要协程。
13. 点击 Save 后，数据会沿着 Screen → ViewModel → Repository → DAO → Database 的链路写入本地 SQLite。

---

## 25. 建议你如何练习

如果你想真正掌握，而不只是复制代码，可以按下面顺序练习：

1. 先手写 `Item.kt`，确认你能解释每个注解。
2. 手写 `ItemDao.kt`，确认你知道每个方法对应什么 SQL 操作。
3. 手写 `InventoryDatabase.kt`，重点理解单例代码。
4. 手写 `ItemsRepository` 和 `OfflineItemsRepository`，不要觉得它只是转发。
5. 画出从 Save 按钮到数据库写入的调用链。
6. 运行 App，用 Database Inspector 查看 `item_database` 和 `items` 表。
7. 故意注释掉某一层，比如 Factory 里的 Repository 注入，观察会报什么错。
8. 尝试新增一个字段，比如 `description`，观察为什么需要数据库版本迁移。

---

## 26. 最后再看一遍完整架构

```text
用户
 |
 | 点击 Save
 v
ItemEntryScreen
 |
 | coroutineScope.launch
 v
ItemEntryViewModel
 |
 | saveItem()
 | validateInput()
 | ItemDetails.toItem()
 v
ItemsRepository 接口
 |
 | insertItem(item)
 v
OfflineItemsRepository
 |
 | itemDao.insert(item)
 v
ItemDao 接口
 |
 | Room 生成实现
 v
InventoryDatabase
 |
 | item_database
 v
SQLite 本地数据库
```

这就是本 Codelab 的核心。

你可以把它记成一句话：

> UI 不直接碰数据库；ViewModel 通过 Repository 表达数据需求；Repository 通过 DAO 访问 Room；Room 把 Entity 保存到 SQLite。

理解这句话，比单纯背 `@Entity`、`@Dao`、`@Database` 更重要。

