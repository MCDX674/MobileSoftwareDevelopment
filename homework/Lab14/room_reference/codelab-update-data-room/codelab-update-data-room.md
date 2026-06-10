# 使用 Room 读取和更新数据 

> 
>
> 上一个 Room Codelab 解决的是“怎么把数据写进数据库”。这个 Codelab 解决的是“怎么把数据库中的数据读出来显示到界面上，并继续支持更新、删除和编辑”。
>
> 这个 v2 版本会重点解释：
>
> - 为什么主页列表要用 `Flow`
> - 为什么 ViewModel 要把 `Flow` 转成 `StateFlow`
> - 为什么 Compose 页面要用 `collectAsState()`
> - 为什么详情页需要 `SavedStateHandle`
> - 为什么编辑页用 `.first()`，而详情页用 `stateIn()`
> - 为什么卖出商品本质上是一次 `Update`
> - 删除后为什么列表会自动刷新
> - 每一步应该打开哪个文件、添加哪段代码、运行后检查什么
>
> 如果你刚学完“使用 Room 持久保留数据”，这篇可以看作后半部分：把 Inventory 应用从“能保存”补全成“能读取、显示、修改、删除”的完整 CRUD 应用。

---

## 1. 这个 Codelab 接着上一个做什么

上一个 Codelab 里，你已经完成了 Room 的基础数据层：

```text
Item.kt
  Entity，定义 items 表

ItemDao.kt
  DAO，定义插入、查询、更新、删除方法

InventoryDatabase.kt
  Database，创建 Room 数据库并提供 DAO

ItemsRepository.kt
  Repository 接口，给 ViewModel 使用

OfflineItemsRepository.kt
  Repository 的本地数据库实现

AppContainer.kt
  创建 Repository

ItemEntryViewModel.kt
  调用 Repository 保存新商品
```

用户现在可以进入 Add Item 页面，输入商品并保存。

但是应用还有几个明显问题：

- 主页列表仍然是空的
- 点击商品后详情页没有真实数据
- Sell 按钮不能减少库存
- Delete 按钮确认后不会真正删除商品
- Edit 页面打开后没有填充原来的商品数据
- Edit 页面保存后不会更新数据库

所以这个 Codelab 的目标是补全剩余功能：

```text
Read    读取商品列表和商品详情
Update  更新商品数据，包括编辑和卖出
Delete  删除商品
```

也就是把 Inventory 应用变成完整的 CRUD 应用。

---

## 2. 先复习：CRUD 是什么

CRUD 是数据库应用中最常见的四类操作：

```text
C = Create  创建，也就是新增数据
R = Read    读取，也就是查询数据
U = Update  更新，也就是修改数据
D = Delete  删除，也就是移除数据
```

对应到 Inventory 应用：

```text
Create
  Add Item 页面新增商品

Read
  主页显示商品列表
  详情页显示单个商品

Update
  Sell 按钮减少库存数量
  Edit 页面修改商品名称、价格、数量

Delete
  Delete 按钮删除商品
```

上一个 Codelab 主要完成了 Create。

这个 Codelab 会完成 Read、Update、Delete。

---

## 3. 这次最重要的新概念：数据要自动流向 UI

在传统写法里，显示列表可能是这样：

```text
打开页面
    ↓
查询一次数据库
    ↓
把结果显示出来
    ↓
数据库变化
    ↓
手动重新查询
    ↓
手动刷新列表
```

这种方式的问题是：你必须记得在每个可能改变数据的地方手动刷新。

比如：

- 新增商品后刷新
- 删除商品后刷新
- 修改商品后刷新
- 卖出商品后刷新

如果忘了某个地方，UI 就会显示旧数据。

Room + Flow + Compose 可以让这个过程变成反应式：

```text
UI 开始观察数据库
    ↓
Room 发出当前数据
    ↓
Compose 显示数据
    ↓
数据库变化
    ↓
Room 自动发出新数据
    ↓
Compose 自动重组 UI
```

你不再手动“刷新列表”。

数据变了，UI 自动变。

这就是这个 Codelab 的核心思想。

---

## 4. Flow、StateFlow、State 分别是什么

这部分非常关键。

原文里会写出这样的链路：

```text
Room DAO 返回 Flow
    ↓
ViewModel 用 stateIn 转成 StateFlow
    ↓
Compose 用 collectAsState 转成 State
    ↓
UI 根据 State 重组
```

很多新手会被这几个名词绕晕。

我们逐个看。

### Flow

`Flow` 是 Kotlin 协程中的数据流。

它可以持续发出数据。

Room DAO 中的查询可以返回 `Flow`：

```kotlin
@Query("SELECT * from items ORDER BY name ASC")
fun getAllItems(): Flow<List<Item>>
```

意思是：

```text
我不是只查询一次列表。
我会观察 items 表。
只要表里的数据变化，我就发出新的列表。
```

这就是为什么添加、删除、更新商品后，列表可以自动刷新。

### StateFlow

`StateFlow` 也是一种数据流，但它更适合表示“状态”。

它有几个特点：

- 总是有一个当前值
- 会缓存最新值
- 新的观察者一订阅，就能立刻拿到最新值
- 适合放在 ViewModel 中暴露给 UI

ViewModel 不直接把 DAO 的 `Flow<List<Item>>` 暴露给页面，而是把它转换成：

```kotlin
StateFlow<HomeUiState>
```

这样 UI 得到的是明确的界面状态，而不是原始数据库数据。

### State

Compose UI 最终需要的是 Compose 能识别的状态。

所以页面中会写：

```kotlin
val homeUiState by viewModel.homeUiState.collectAsState()
```

`collectAsState()` 的作用是：

```text
收集 StateFlow
    ↓
转换成 Compose State
    ↓
State 变化时触发重组
```

所以完整链路是：

```text
数据库 items 表
    ↓
Room Flow<List<Item>>
    ↓
ViewModel StateFlow<HomeUiState>
    ↓
Compose State<HomeUiState>
    ↓
HomeScreen 重组
```

---

## 5. 为什么不直接把 Flow 放到 Composable 里

你可能会问：

既然 DAO 已经返回 `Flow<List<Item>>`，为什么不在 `HomeScreen` 里直接调用 Repository？

比如：

```kotlin
@Composable
fun HomeScreen(...) {
    val items = repository.getAllItemsStream().collectAsState(...)
}
```

这种写法不推荐。

原因有几个。

第一，Composable 不应该负责拿数据源。

页面层应该负责显示 UI，而不是关心 Repository 从哪里来。

第二，ViewModel 能在配置变化中保存状态。

比如旋转屏幕时，Activity 可能重建，但 ViewModel 可以继续存在。

第三，ViewModel 可以把原始数据转换成 UI 状态。

数据库返回的是：

```kotlin
Flow<List<Item>>
```

页面真正需要的是：

```kotlin
HomeUiState
```

以后如果页面还需要 loading、error、筛选状态，也可以都放进 UiState。

第四，ViewModel 更容易维护和复用。

数据转换逻辑集中在 ViewModel 中，页面只负责显示状态和触发事件。

所以推荐结构是：

```text
Composable
    只收集 ViewModel 暴露的 UI 状态

ViewModel
    调用 Repository
    把数据转换成 UI 状态

Repository
    调用 DAO
```

---

## 开始操作前：按应用功能顺序操作

这一篇不是单纯读说明。你应该一边看一边改项目。

如果你已经完成了上一篇“使用 Room 持久保留数据”，可以直接使用自己的项目。

如果你使用课堂提供的 Inventory 示例项目，请从已经完成 Room 基础写入功能的版本开始。

开始前先运行一次应用，确认当前状态：

```text
1. Add Item 页面可以新增商品。
2. 主页列表还是空的，或不会显示数据库中的真实商品。
3. 点击商品后的详情、Sell、Delete、Edit 还没有真正连接数据库。
```

本篇 v2 会保留必要解释，但主线只按应用功能操作。

操作顺序是：

```text
第一步：更新首页界面状态
  HomeViewModel.kt
  AppViewModelProvider.kt

第二步：显示商品目录数据
  HomeScreen.kt

第三步：显示商品详情
  InventoryNavGraph.kt
  ItemDetailsScreen.kt
  ItemDetailsViewModel.kt
  AppViewModelProvider.kt

第四步：实现商品销售功能
  ItemDetailsViewModel.kt
  ItemDetailsScreen.kt

第五步：删除商品实体
  ItemDetailsViewModel.kt
  ItemDetailsScreen.kt

第六步：修改商品实体
  ItemDetailsScreen.kt
  ItemEditViewModel.kt
  AppViewModelProvider.kt
  ItemEditScreen.kt
```

下面每个大步骤都会先告诉你“做什么”，再解释“为什么这样做”。

---

## 6. 首页列表：从数据库读取所有商品

首页的目标是显示数据库中所有商品。

起始代码里，`HomeScreen` 调用 `HomeBody` 时传的是空列表：

```kotlin
HomeBody(
    itemList = listOf(),
    onItemClick = navigateToItemUpdate,
    modifier = modifier.padding(innerPadding)
        .fillMaxSize()
)
```

这意味着不管数据库里有什么，主页永远显示空列表。

我们要改成：

```text
HomeViewModel 从 Repository 获取商品列表
HomeScreen 收集 HomeViewModel 的状态
HomeBody 显示状态里的商品列表
```

本步骤的实操目标是：

```text
把 HomeScreen 里写死的 listOf()
替换成 HomeViewModel 从 Room 数据库持续观察到的商品列表
```

---

## 7. HomeUiState：为什么要有首页界面状态

起始代码中有：

```kotlin
data class HomeUiState(val itemList: List<Item> = listOf())
```

这表示首页 UI 需要的数据。

目前首页只需要商品列表，所以只有一个字段：

```kotlin
itemList: List<Item>
```

为什么不直接在页面里用 `List<Item>`？

因为 UI 状态通常会越来越复杂。

以后可能会变成：

```kotlin
data class HomeUiState(
    val itemList: List<Item> = listOf(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedSortType: SortType = SortType.Name
)
```

提前用 `HomeUiState` 包起来，是一种更可扩展的习惯。

---

## 8. HomeViewModel：把 Flow 转成 StateFlow

### 操作：修改 `HomeViewModel.kt`

打开 `ui/home/HomeViewModel.kt`。

起始代码里通常已经有 `HomeUiState` 和 `TIMEOUT_MILLIS`：

```kotlin
class HomeViewModel : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiState(val itemList: List<Item> = listOf())
```

现在要做三件事：

```text
1. 给 HomeViewModel 添加 ItemsRepository 构造参数。
2. 调用 getAllItemsStream() 读取所有商品。
3. 把 Flow<List<Item>> 转成 StateFlow<HomeUiState>。
```

需要的导入通常是：

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
```

最终的 `HomeViewModel` 大概是这样：

```kotlin
package com.example.inventory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(itemsRepository: ItemsRepository) : ViewModel() {

    val homeUiState: StateFlow<HomeUiState> =
        itemsRepository.getAllItemsStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiState(val itemList: List<Item> = listOf())
```

改完后先不要急着运行。因为 `HomeViewModel` 现在有构造参数，系统默认还不知道怎么创建它。下一步要更新 `AppViewModelProvider.kt`。

下面拆开解释。

### 为什么 HomeViewModel 需要 ItemsRepository

```kotlin
class HomeViewModel(itemsRepository: ItemsRepository) : ViewModel()
```

首页要显示商品列表，所以它需要从数据层读取商品。

但 ViewModel 不直接调用 DAO。

它通过 Repository：

```kotlin
itemsRepository.getAllItemsStream()
```

这样 ViewModel 不知道底层是 Room、网络还是其他数据源。

这和上一个 Codelab 里的架构保持一致。

### `getAllItemsStream()` 返回什么

Repository 的方法是：

```kotlin
fun getAllItemsStream(): Flow<List<Item>>
```

它返回的是所有商品的流。

实际实现会委托给 DAO：

```kotlin
override fun getAllItemsStream(): Flow<List<Item>> = itemDao.getAllItems()
```

DAO 查询是：

```kotlin
@Query("SELECT * from items ORDER BY name ASC")
fun getAllItems(): Flow<List<Item>>
```

所以首页数据来源是：

```text
items 表
    ↓
ItemDao.getAllItems()
    ↓
OfflineItemsRepository.getAllItemsStream()
    ↓
HomeViewModel.homeUiState
```

### 为什么要 `.map { HomeUiState(it) }`

原始数据类型是：

```kotlin
Flow<List<Item>>
```

但是 ViewModel 要暴露给 UI 的类型是：

```kotlin
StateFlow<HomeUiState>
```

所以需要先把 `List<Item>` 转成 `HomeUiState`：

```kotlin
.map { HomeUiState(it) }
```

这里的 `it` 就是数据库发出的商品列表。

等价于：

```kotlin
.map { itemList ->
    HomeUiState(itemList = itemList)
}
```

### 为什么要 `.stateIn(...)`

`.map { ... }` 之后仍然是普通 `Flow<HomeUiState>`。

我们希望 ViewModel 暴露的是 `StateFlow<HomeUiState>`。

所以使用：

```kotlin
.stateIn(...)
```

`stateIn` 会把冷流转换成热的状态流。

### 什么是冷流和热流

简单理解：

```text
冷流 Flow
  有人收集时才开始工作
  每个收集者可能触发一套新的执行流程

热流 StateFlow
  自己维护当前状态
  多个收集者共享同一份状态
  新收集者能立即拿到最新值
```

ViewModel 暴露 UI 状态时，`StateFlow` 更合适。

### `scope = viewModelScope`

```kotlin
scope = viewModelScope
```

表示这个 `StateFlow` 的生命周期和 ViewModel 绑定。

当 ViewModel 被清理时，里面的数据收集也会取消。

这样不会出现页面已经没了，但数据库还在被观察的情况。

### `started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)`

```kotlin
started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
```

意思是：

只有 UI 正在观察这个状态时，数据流才保持活跃。

当没有观察者时，等待一小段时间再停止。

这里的：

```kotlin
private const val TIMEOUT_MILLIS = 5_000L
```

表示等待 5 秒。

为什么要等 5 秒？

因为有些场景 UI 会短暂消失又回来。

比如旋转屏幕：

```text
旧 Activity 销毁
    ↓
新 Activity 创建
```

中间可能有短暂间隔。

如果立刻停止数据流，又马上重新启动，会产生不必要的开销。

5 秒超时可以让短暂配置变化更平滑。

### `initialValue = HomeUiState()`

```kotlin
initialValue = HomeUiState()
```

`StateFlow` 必须有初始值。

数据库查询还没返回时，首页先显示一个空列表状态。

等 Room 发出真实数据后，UI 会自动刷新。

---

## 9. AppViewModelProvider：为什么又要更新 Factory

### 操作：在 Factory 中创建 `HomeViewModel`

打开 `ui/AppViewModelProvider.kt`。

找到 `HomeViewModel` 对应的 `initializer`。如果原来是无参创建，改成传入 Repository：

```kotlin
initializer {
    HomeViewModel(inventoryApplication().container.itemsRepository)
}
```

这个改动完成后，`HomeViewModel` 的构造参数错误应该消失。

`HomeViewModel` 原本可能没有构造参数。

现在它需要：

```kotlin
HomeViewModel(itemsRepository: ItemsRepository)
```

系统默认不知道怎么创建这个 ViewModel。

所以要在 `AppViewModelProvider.kt` 里通过 `initializer` 告诉系统如何创建它。

完整思想是：

```text
ViewModel 需要 Repository
    ↓
Repository 存在 AppContainer 中
    ↓
AppContainer 存在 InventoryApplication 中
    ↓
Factory 从 Application 取出 Repository
    ↓
Factory 创建 HomeViewModel
```

这和上一个 Codelab 中 `ItemEntryViewModel` 的注入方式一致。

---

## 10. HomeScreen：收集 StateFlow 并显示列表

### 操作：修改 `HomeScreen.kt`

打开 `ui/home/HomeScreen.kt`。

先添加需要的导入：

```kotlin
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.ui.AppViewModelProvider
```

然后给 `HomeScreen` 添加 `HomeViewModel` 参数：

```kotlin
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    navigateToItemUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    ...
}
```

在 `HomeScreen` 函数内部收集状态：

```kotlin
val homeUiState by viewModel.homeUiState.collectAsState()
```

最后找到 `HomeBody()` 调用，把原来的空列表：

```kotlin
itemList = listOf()
```

改成真实状态：

```kotlin
HomeBody(
    itemList = homeUiState.itemList,
    onItemClick = navigateToItemUpdate,
    modifier = modifier.padding(innerPadding)
        .fillMaxSize()
)
```

现在运行应用：

```text
1. 如果数据库里已经有商品，首页应该显示商品列表。
2. 如果列表为空，先进入 Add Item 添加几个商品。
3. 返回首页后，列表应该自动出现新商品。
```

### `by` 是什么

上面收集状态时用了 `by`。

`collectAsState()` 返回的是 `State<HomeUiState>`。

如果不用 `by`，需要写：

```kotlin
val homeUiState = viewModel.homeUiState.collectAsState()
HomeBody(itemList = homeUiState.value.itemList, ...)
```

用了 `by` 之后，可以直接写：

```kotlin
homeUiState.itemList
```

它只是让代码更简洁。

### 首页自动刷新的完整链路

```text
用户新增商品
    ↓
ItemDao.insert()
    ↓
items 表发生变化
    ↓
Room 让 getAllItems() 的 Flow 发出新列表
    ↓
HomeViewModel 的 StateFlow 得到新 HomeUiState
    ↓
HomeScreen 的 collectAsState() 收到新状态
    ↓
Compose 重组
    ↓
HomeBody 显示新列表
```

你没有写任何“刷新列表”的代码。

这就是反应式数据流的价值。

---

## 11. 详情页：为什么需要 itemId

主页显示的是商品列表。

当用户点击其中一个商品时，要进入详情页。

详情页必须知道：

```text
用户点击的是哪一个商品？
```

答案就是传递商品 id。

### 操作：确认列表点击时传递 `itemId`

先检查 `ui/navigation/InventoryNavGraph.kt`。

首页路由里应该把被点击商品的 id 拼进详情页路由：

```kotlin
HomeScreen(
    navigateToItemEntry = { navController.navigate(ItemEntryDestination.route) },
    navigateToItemUpdate = {
        navController.navigate("${ItemDetailsDestination.route}/${it}")
    }
)
```

这里的 `it` 就是 `HomeBody` 中被点击商品的 id。

如果这里没有传 id，后面的详情页 ViewModel 就无法知道要查询哪一条记录。

这里的 `it` 是被点击商品的 id。

假设用户点击 `id = 3` 的商品，导航路由可能变成：

```text
item_details/3
```

详情页根据这个 id 查询数据库：

```text
SELECT * FROM items WHERE id = 3
```

---

## 12. SavedStateHandle：从导航参数拿 id

详情页的 ViewModel 会使用 `SavedStateHandle`。

核心代码是从 `savedStateHandle` 里读取路由参数：

```kotlin
private val itemId: Int = checkNotNull(savedStateHandle[ItemDetailsDestination.itemIdArg])
```

### `SavedStateHandle` 是什么

`SavedStateHandle` 是 ViewModel 用来读取和保存状态的工具。

在 Navigation Compose 中，它经常用来读取导航参数。

比如路由里有：

```text
item_details/{itemId}
```

当实际导航到：

```text
item_details/3
```

ViewModel 可以通过 `SavedStateHandle` 拿到：

```kotlin
itemId = 3
```

### 为什么不直接从 Composable 传 id 给 ViewModel

因为 ViewModel 是由系统和 Factory 创建的。

导航参数已经保存在 `SavedStateHandle` 里。

通过 `SavedStateHandle` 获取参数，是 Navigation Compose 中更标准、更稳定的方式。

它也能更好地处理进程重建、状态恢复等场景。

---

## 13. 详情页 ViewModel：读取单个商品

### 操作：修改 `ItemDetailsViewModel.kt`

打开 `ui/item/ItemDetailsViewModel.kt`。

给 ViewModel 添加两个构造参数：

```kotlin
class ItemDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {
    private val itemId: Int = checkNotNull(savedStateHandle[ItemDetailsDestination.itemIdArg])
}
```

这段只是构造函数和 `itemId` 的示意。下面的 `uiState`、`reduceQuantityByOne()`、`deleteItem()` 都要继续放在同一个 `ItemDetailsViewModel` 类的花括号里。

需要的导入通常包括：

```kotlin
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
```

然后在类中添加 `uiState`：

```kotlin
val uiState: StateFlow<ItemDetailsUiState> =
    itemsRepository.getItemStream(itemId)
        .filterNotNull()
        .map {
            ItemDetailsUiState(itemDetails = it.toItemDetails())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ItemDetailsUiState()
        )
```

如果你的 `ItemDetailsViewModel` 中还没有超时时间常量，添加：

```kotlin
companion object {
    private const val TIMEOUT_MILLIS = 5_000L
}
```

### 操作：更新 `AppViewModelProvider.kt`

打开 `ui/AppViewModelProvider.kt`，找到 `ItemDetailsViewModel` 的初始化器，改成：

```kotlin
initializer {
    ItemDetailsViewModel(
        this.createSavedStateHandle(),
        inventoryApplication().container.itemsRepository
    )
}
```

`createSavedStateHandle()` 负责把导航参数交给 ViewModel。

`ItemDetailsViewModel` 的核心状态就是上面添加的 `uiState`。

### `getItemStream(itemId)` 返回什么

Repository 中：

```kotlin
fun getItemStream(id: Int): Flow<Item?>
```

DAO 中：

```kotlin
@Query("SELECT * from items WHERE id = :id")
fun getItem(id: Int): Flow<Item>
```

它表示观察某一个 id 对应的商品。

如果这个商品变化，详情页也会自动更新。

### 为什么返回 `Item?`

因为数据库里可能没有这个 id。

比如：

- 商品已经被删除
- 路由参数错误
- 数据库为空

所以 Repository 接口使用 `Item?` 表示“可能没有”。

### 为什么用 `filterNotNull()`

```kotlin
.filterNotNull()
```

过滤掉 null。

也就是说，只有真的查到了商品，后面的 `map` 才会执行。

如果不加 `filterNotNull()`，后面就需要处理 null：

```kotlin
map { item ->
    if (item == null) ...
}
```

教学项目里先简单过滤。

### 为什么要把 Item 转成 ItemDetailsUiState

数据库实体是：

```kotlin
Item
```

详情页需要的是：

```kotlin
ItemDetailsUiState
```

所以用：

```kotlin
ItemDetailsUiState(itemDetails = it.toItemDetails())
```

这里的 `toItemDetails()` 会把数据库实体转换成 UI 可显示的数据结构。

---

## 14. ItemDetailsScreen：收集详情页状态

### 操作：修改 `ItemDetailsScreen.kt`

打开 `ui/item/ItemDetailsScreen.kt`。

添加 ViewModel 参数：

```kotlin
@Composable
fun ItemDetailsScreen(
    navigateToEditItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    ...
}
```

需要的导入通常是：

```kotlin
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.ui.AppViewModelProvider
```

在 `ItemDetailsScreen()` 内收集详情页状态：

```kotlin
val uiState = viewModel.uiState.collectAsState()
```

然后把 `uiState.value` 传给详情内容。

不同版本起始代码中参数名可能略有不同：有的叫 `itemUiState`，有的叫 `itemDetailsUiState`。以你项目中 `ItemDetailsBody()` 的函数参数名为准，核心是传入 `uiState.value`：

```kotlin
ItemDetailsBody(
    itemDetailsUiState = uiState.value,
    onSellItem = { },
    onDelete = { },
    modifier = modifier.padding(innerPadding)
)
```

如果你的函数参数名是 `itemUiState`，就写：

```kotlin
ItemDetailsBody(
    itemUiState = uiState.value,
    onSellItem = { },
    onDelete = { },
    modifier = modifier.padding(innerPadding)
)
```

运行应用后检查：

```text
1. 首页能显示商品列表。
2. 点击某个商品。
3. 详情页应该显示这个商品的名称、价格和数量，不再是空白。
4. 此时 Sell、Delete、Edit 可能还没有真正工作，下一步再实现。
```

### 为什么这里用了 `.value`

这里没有使用 `by`，所以 `uiState` 是 `State<ItemDetailsUiState>`。

要取里面的值，需要：

```kotlin
uiState.value
```

如果写成：

```kotlin
val uiState by viewModel.uiState.collectAsState()
```

就可以直接传：

```kotlin
itemUiState = uiState
```

两种写法都可以。

---

## 15. 详情页自动更新的完整链路

假设用户打开 `id = 2` 的商品详情页。

数据链路是：

```text
导航路由 item_details/2
    ↓
SavedStateHandle 取出 itemId = 2
    ↓
ItemDetailsViewModel 调用 getItemStream(2)
    ↓
Repository 调用 ItemDao.getItem(2)
    ↓
Room 查询 items 表中 id = 2 的行
    ↓
Flow 发出 Item
    ↓
map 转成 ItemDetailsUiState
    ↓
stateIn 转成 StateFlow
    ↓
ItemDetailsScreen collectAsState
    ↓
详情页显示商品名称、价格、数量
```

如果这个商品之后被更新：

```text
items 表中 id = 2 的行变化
    ↓
Room 重新发出 Item
    ↓
详情页状态更新
    ↓
Compose 重组
```

这就是详情页也使用 `Flow` 的原因。

---

## 16. Sell 功能本质是什么

Sell 按钮的意思是卖出一个商品。

从数据库角度看，它不是一个新操作。

它就是更新商品的库存数量：

```text
quantity = quantity - 1
```

所以 Sell 对应的是：

```text
Update
```

也就是 DAO 的：

```kotlin
@Update
suspend fun update(item: Item)
```

---

## 17. ItemDetailsViewModel：减少库存

### 操作：添加 `reduceQuantityByOne()`

回到 `ItemDetailsViewModel.kt`。

添加协程导入：

```kotlin
import kotlinx.coroutines.launch
```

在 `ItemDetailsViewModel` 类中添加：

```kotlin
fun reduceQuantityByOne() {
    viewModelScope.launch {
        val currentItem = uiState.value.itemDetails.toItem()
        if (currentItem.quantity > 0) {
            itemsRepository.updateItem(currentItem.copy(quantity = currentItem.quantity - 1))
        }
    }
}
```

同时，把 `uiState` 的 `map` 改成计算 `outOfStock`：

```kotlin
val uiState: StateFlow<ItemDetailsUiState> =
    itemsRepository.getItemStream(itemId)
        .filterNotNull()
        .map {
            ItemDetailsUiState(
                outOfStock = it.quantity <= 0,
                itemDetails = it.toItemDetails()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ItemDetailsUiState()
        )
```

下面逐行解释。

### 为什么用 `viewModelScope.launch`

`updateItem()` 最终会调用 DAO 的 `suspend fun update()`。

挂起函数必须在协程中调用。

这里在 ViewModel 内部执行数据库操作，所以使用：

```kotlin
viewModelScope.launch
```

`viewModelScope` 的生命周期跟 ViewModel 绑定。

当详情页的 ViewModel 被清理时，未完成的协程也会取消。

### 为什么从 `uiState.value` 获取当前商品

```kotlin
val currentItem = uiState.value.itemDetails.toItem()
```

详情页已经在观察当前商品。

`uiState.value` 就是当前 UI 中显示的商品状态。

点击 Sell 时，从当前状态拿商品，然后减少数量。

### 为什么要检查 `quantity > 0`

```kotlin
if (currentItem.quantity > 0)
```

库存不能卖成负数。

如果数量已经是 0，再点击 Sell，不应该变成 -1。

这是业务规则。

### 为什么用 `copy`

```kotlin
currentItem.copy(quantity = currentItem.quantity - 1)
```

`Item` 是 data class，所以有 `copy()` 方法。

它会创建一个新对象，保留原来的其他字段，只修改 `quantity`。

等价于：

```kotlin
Item(
    id = currentItem.id,
    name = currentItem.name,
    price = currentItem.price,
    quantity = currentItem.quantity - 1
)
```

但 `copy()` 更简洁，也更不容易写错字段。

### 一个真实项目中的注意点

这个实现适合教学项目。

如果真实库存系统有高并发，比如多个地方同时卖同一个商品，就要考虑事务或直接写 SQL 更新：

```sql
UPDATE items
SET quantity = quantity - 1
WHERE id = :id AND quantity > 0
```

这样可以避免两个操作同时读取到相同库存后重复扣减的问题。

当前 Codelab 是单用户本地 App，使用 `copy()` 更新已经足够。

---

## 18. 把 Sell 按钮连接到 ViewModel

### 操作：修改 `ItemDetailsScreen.kt` 的 `onSellItem`

打开 `ItemDetailsScreen.kt`，找到 `ItemDetailsBody()` 调用。

把空的 Sell 回调：

```kotlin
onSellItem = { }
```

改成：

```kotlin
onSellItem = { viewModel.reduceQuantityByOne() }
```

完整调用类似：

```kotlin
ItemDetailsBody(
    itemDetailsUiState = uiState.value,
    onSellItem = { viewModel.reduceQuantityByOne() },
    onDelete = { },
    modifier = modifier.padding(innerPadding)
)
```

如果你的参数名是 `itemUiState`，保持项目中的参数名即可。

运行应用后检查：

```text
1. 打开某个商品详情页。
2. 点击 Sell。
3. Quantity in Stock 应该减少 1。
4. 减到 0 后，Sell 按钮应该禁用。
```

点击按钮后的链路是：

```text
用户点击 Sell
    ↓
ItemDetailsBody 调用 onSellItem
    ↓
ItemDetailsScreen 调用 viewModel.reduceQuantityByOne()
    ↓
ViewModel 获取当前 Item
    ↓
quantity 减 1
    ↓
Repository.updateItem()
    ↓
DAO.update()
    ↓
Room 更新数据库
    ↓
详情页 Flow 自动收到新数据
    ↓
UI 上的数量自动变化
```

---

## 19. outOfStock：为什么按钮会自动禁用

详情页 UI 状态里通常有：

```kotlin
data class ItemDetailsUiState(
    val outOfStock: Boolean = true,
    val itemDetails: ItemDetails = ItemDetails()
)
```

在 ViewModel 中，根据数据库数据计算：

```kotlin
val uiState: StateFlow<ItemDetailsUiState> =
    itemsRepository.getItemStream(itemId)
        .filterNotNull()
        .map {
            ItemDetailsUiState(
                outOfStock = it.quantity <= 0,
                itemDetails = it.toItemDetails()
            )
        }.stateIn(...)
```

### 为什么 `outOfStock` 不单独手动维护

`outOfStock` 是由 `quantity` 推导出来的。

```text
quantity <= 0
    ↓
outOfStock = true
```

所以不应该再额外创建一个独立变量手动控制。

否则容易出现状态不一致：

```text
quantity = 0
但 outOfStock = false
```

正确方式是让 UI 状态由数据推导：

```text
数据库中的 quantity
    ↓
ViewModel 计算 outOfStock
    ↓
按钮 enabled 状态
```

这就是数据驱动 UI。

---

## 20. Delete 功能本质是什么

Delete 按钮会删除当前商品。

数据库层对应 DAO 的：

```kotlin
@Delete
suspend fun delete(item: Item)
```

Room 会根据 `item.id` 删除对应行。

所以删除操作需要当前商品的完整实体，至少要有正确的主键 id。

---

## 21. ItemDetailsViewModel：删除当前商品

### 操作：添加 `deleteItem()`

打开 `ItemDetailsViewModel.kt`。

在 `ItemDetailsViewModel` 类中添加：

```kotlin
suspend fun deleteItem() {
    itemsRepository.deleteItem(uiState.value.itemDetails.toItem())
}
```

这里不在 ViewModel 内部启动 `viewModelScope.launch`，是为了让页面可以在同一个协程里完成：

```text
先删除
再 navigateBack()
```

### 为什么 `deleteItem()` 是 suspend

因为 Repository 的删除方法是：

```kotlin
suspend fun deleteItem(item: Item)
```

最终调用 DAO 的：

```kotlin
suspend fun delete(item: Item)
```

数据库写操作不能直接在主线程执行，所以需要挂起函数。

### 为什么这里不直接用 `viewModelScope.launch`

Sell 功能里，ViewModel 自己启动协程：

```kotlin
fun reduceQuantityByOne() {
    viewModelScope.launch { ... }
}
```

删除功能里，Codelab 写成：

```kotlin
suspend fun deleteItem()
```

然后由 Composable 启动协程调用它。

两种方式都能工作。

这里让页面在同一个协程里完成：

```text
删除
    ↓
返回上一页
```

所以 `deleteItem()` 保持为 suspend，让调用方决定删除后做什么。

---

## 22. 删除确认对话框为什么重要

删除是破坏性操作。

如果用户误点 Delete，数据可能直接丢失。

所以起始代码中已经有确认对话框。

流程是：

```text
点击 Delete
    ↓
显示确认对话框
    ↓
点击 Yes
    ↓
执行真正删除
```

相关代码类似：

```kotlin
if (deleteConfirmationRequired) {
    DeleteConfirmationDialog(
        onDeleteConfirm = {
            deleteConfirmationRequired = false
            onDelete()
        },
        ...
    )
}
```

页面不会在点击 Delete 按钮的一瞬间删除，而是在确认后调用 `onDelete()`。

---

## 23. ItemDetailsScreen：执行删除并返回

### 操作：在页面中调用删除并返回首页

打开 `ItemDetailsScreen.kt`。

添加导入：

```kotlin
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
```

在 `ItemDetailsScreen()` 内部创建协程作用域：

```kotlin
val coroutineScope = rememberCoroutineScope()
```

然后把 `ItemDetailsBody()` 的 `onDelete` 改成：

```kotlin
onDelete = {
    coroutineScope.launch {
        viewModel.deleteItem()
        navigateBack()
    }
}
```

完整调用类似：

```kotlin
ItemDetailsBody(
    itemDetailsUiState = uiState.value,
    onSellItem = { viewModel.reduceQuantityByOne() },
    onDelete = {
        coroutineScope.launch {
            viewModel.deleteItem()
            navigateBack()
        }
    },
    modifier = modifier.padding(innerPadding)
)
```

运行应用后检查：

```text
1. 打开某个商品详情页。
2. 点击 Delete。
3. 确认对话框出现后点击 Yes。
4. 应用返回首页。
5. 被删除的商品应该从列表中消失。
```

### 为什么删除后要 `navigateBack()`

因为当前详情页显示的是刚刚被删除的商品。

商品已经不存在了，继续留在详情页没有意义。

所以删除成功后回到首页列表。

### 删除后首页为什么会自动少一项

因为首页正在观察：

```kotlin
getAllItemsStream()
```

删除发生后：

```text
items 表少了一行
    ↓
Room 发出新的商品列表
    ↓
HomeViewModel.homeUiState 更新
    ↓
HomeScreen 重组
    ↓
列表中删除的商品消失
```

这仍然不需要手动刷新。

---

## 24. Edit 功能分成两件事

编辑商品不是一个单独动作，它包含两个阶段：

```text
打开编辑页
    ↓
根据 itemId 读取旧数据
    ↓
填充输入框

用户修改后点击 Save
    ↓
校验输入
    ↓
把 UI 状态转成 Item
    ↓
调用 updateItem()
    ↓
更新数据库
```

所以编辑功能要解决：

- 怎么把旧数据填进输入框
- 怎么保存修改后的数据

---

## 25. 点击详情页 FAB：把 id 传给编辑页

详情页中的编辑 FAB 需要导航到编辑页面。

关键是把当前商品 id 传过去：

### 操作：修改详情页编辑 FAB

打开 `ItemDetailsScreen.kt`。

找到 `FloatingActionButton` 的 `onClick`，把它改成传当前商品 id：

```kotlin
FloatingActionButton(
    onClick = { navigateToEditItem(uiState.value.itemDetails.id) },
    modifier = ...
)
```

如果这里继续导航到固定路由，或者没有传 id，编辑页就只能打开空表单，无法知道要编辑哪一条商品。

如果不传 id，编辑页就不知道要编辑哪个商品。

假设当前商品 id 是 4，那么导航可能类似：

```text
item_edit/4
```

编辑页 ViewModel 再通过 `SavedStateHandle` 读取这个 id。

---

## 26. ItemEditViewModel：初始化时读取旧数据

### 操作：修改 `ItemEditViewModel.kt`

打开 `ui/item/ItemEditViewModel.kt`。

给构造函数添加 `SavedStateHandle` 和 `ItemsRepository`：

```kotlin
class ItemEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {
    private val itemId: Int = checkNotNull(savedStateHandle[ItemEditDestination.itemIdArg])
}
```

这段只是构造函数和 `itemId` 的示意。下面的 `init`、`updateUiState()`、`updateItem()` 都要继续放在同一个 `ItemEditViewModel` 类的花括号里。

需要的导入通常包括：

```kotlin
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
```

然后在类中添加 `init`，让编辑页打开时读取旧商品数据：

```kotlin
init {
    viewModelScope.launch {
        itemUiState = itemsRepository.getItemStream(itemId)
            .filterNotNull()
            .first()
            .toItemUiState(true)
    }
}
```

### `init` 什么时候执行

`init` 代码块在 ViewModel 创建时执行。

打开编辑页时，系统创建 `ItemEditViewModel`。

然后 `init` 自动运行。

这正好适合“进入页面时加载旧数据”。

### 为什么这里也需要 `SavedStateHandle`

编辑页也需要知道要编辑哪个商品。

构造函数中的 `itemId` 来自导航路由。

### 为什么编辑页用 `.first()`

这是本 Codelab 的一个重点。

详情页使用：

```kotlin
getItemStream(itemId)
    .filterNotNull()
    .map { ... }
    .stateIn(...)
```

编辑页使用：

```kotlin
getItemStream(itemId)
    .filterNotNull()
    .first()
```

为什么不同？

因为需求不同。

详情页是展示页面。

它应该持续反映数据库变化。

比如用户在详情页点击 Sell，库存变化后详情页数量应该立刻更新。

所以详情页需要持续观察。

编辑页是表单页面。

它只需要打开时加载一次旧数据，填充输入框。

之后用户会在输入框里修改。

如果编辑页持续观察数据库，可能出现问题：

```text
用户正在输入新名称
    ↓
数据库发来旧数据或其他更新
    ↓
输入框被覆盖
```

所以编辑页只取第一次数据，然后让用户自由编辑。

这就是 `.first()` 的原因。

### 为什么要 `.toItemUiState(true)`

数据库里拿到的是：

```kotlin
Item
```

编辑表单需要的是：

```kotlin
ItemUiState
```

所以用：

```kotlin
.toItemUiState(true)
```

这里传 `true` 表示初始数据是有效的。

因为从数据库里读出的商品通常已经是完整合法数据。

---

## 27. AppViewModelProvider：创建 ItemEditViewModel

### 操作：更新 `AppViewModelProvider.kt`

打开 `ui/AppViewModelProvider.kt`。

找到 `ItemEditViewModel` 的 `initializer`，改成：

```kotlin
initializer {
    ItemEditViewModel(
        this.createSavedStateHandle(),
        inventoryApplication().container.itemsRepository
    )
}
```

改完后运行应用，进入商品详情页，点击编辑 FAB。

此时编辑页的输入框应该已经填入原来的商品名称、价格和数量。

`ItemEditViewModel` 需要两个参数：

```kotlin
savedStateHandle
itemsRepository
```

所以 Factory 要同时传入 `SavedStateHandle` 和 Repository。

### `createSavedStateHandle()` 是什么

它会创建 ViewModel 需要的 `SavedStateHandle`。

这样 ViewModel 就能读取导航参数。

### 为什么又传 Repository

编辑页需要：

- 读取旧商品
- 更新商品

这两个操作都属于数据层操作，所以通过 Repository 完成。

---

## 28. 编辑页的输入状态

编辑页面复用了添加商品页面的输入表单。

这意味着它也使用：

```kotlin
ItemUiState
ItemDetails
```

`ItemDetails` 中价格和数量是字符串：

```kotlin
data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val quantity: String = "",
)
```

原因和添加页面一样：

输入框里的内容本质是文本。

用户输入过程中，价格和数量不一定总是合法数字。

---

## 29. updateUiState：用户输入时更新状态

### 操作：在 `ItemEditViewModel.kt` 中添加输入更新函数

继续打开 `ItemEditViewModel.kt`。

添加：

```kotlin
fun updateUiState(itemDetails: ItemDetails) {
    itemUiState =
        ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
}
```

这个函数和添加商品页面里的写法类似：用户每改一个输入框，ViewModel 就更新 `itemUiState`，并重新计算 Save 按钮是否可用。

页面中绑定：

```kotlin
ItemEntryBody(
    itemUiState = viewModel.itemUiState,
    onItemValueChange = viewModel::updateUiState,
    onSaveClick = { },
    modifier = modifier.padding(innerPadding)
)
```

### `viewModel::updateUiState` 是什么

这是函数引用。

它等价于：

```kotlin
onItemValueChange = { itemDetails ->
    viewModel.updateUiState(itemDetails)
}
```

每当输入框内容变化，表单会把新的 `ItemDetails` 传给 ViewModel。

ViewModel 更新 `itemUiState`。

Compose 看到状态变化后重组界面。

### 为什么 Save 按钮会自动禁用

`ItemEntryBody` 通常会根据：

```kotlin
itemUiState.isEntryValid
```

控制 Save 按钮是否可点击。

`updateUiState()` 每次输入变化都会重新校验：

```kotlin
isEntryValid = validateInput(itemDetails)
```

所以当某个字段为空时：

```text
validateInput = false
    ↓
isEntryValid = false
    ↓
Save 按钮禁用
```

---

## 30. updateItem：保存编辑结果

### 操作：在 `ItemEditViewModel.kt` 中添加保存函数

继续在 `ItemEditViewModel.kt` 中添加：

```kotlin
suspend fun updateItem() {
    if (validateInput(itemUiState.itemDetails)) {
        itemsRepository.updateItem(itemUiState.itemDetails.toItem())
    }
}
```

注意：这里必须使用编辑页当前状态里的 `id`。如果前面加载旧数据时丢了 id，`@Update` 就找不到原来的数据库行。

流程：

```text
点击 Save
    ↓
validateInput 检查输入
    ↓
ItemDetails.toItem()
    ↓
Repository.updateItem()
    ↓
DAO.update()
    ↓
Room 根据 id 更新数据库
```

### 为什么更新时 id 很重要

`@Update` 根据主键找到要更新哪一行。

所以 `itemUiState.itemDetails.toItem()` 转出来的 `Item` 必须保留原来的 id。

如果 id 变成 0，Room 就不知道要更新原来的商品，可能更新不到任何行。

这也是为什么编辑页加载旧数据时，要把 id 一起放进 `ItemDetails`。

---

## 31. ItemEditScreen：点击 Save 后保存并返回

### 操作：修改 `ItemEditScreen.kt`

打开 `ui/item/ItemEditScreen.kt`。

添加导入：

```kotlin
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
```

在 `ItemEditScreen()` 内创建协程作用域：

```kotlin
val coroutineScope = rememberCoroutineScope()
```

找到 `ItemEntryBody()` 调用。

把输入变化回调绑定到 ViewModel：

```kotlin
onItemValueChange = viewModel::updateUiState
```

把保存回调改成：

```kotlin
onSaveClick = {
    coroutineScope.launch {
        viewModel.updateItem()
        navigateBack()
    }
}
```

完整调用类似：

```kotlin
ItemEntryBody(
    itemUiState = viewModel.itemUiState,
    onItemValueChange = viewModel::updateUiState,
    onSaveClick = {
        coroutineScope.launch {
            viewModel.updateItem()
            navigateBack()
        }
    },
    modifier = modifier.padding(innerPadding)
)
```

运行应用后检查：

```text
1. 打开某个商品详情页。
2. 点击编辑 FAB。
3. 编辑页应该显示旧数据。
4. 修改名称、价格或数量。
5. 点击 Save。
6. 返回后，详情页或首页应该显示更新后的数据。
7. 如果把某个必填字段清空，Save 按钮应该禁用。
```

### 为什么要启动协程

`updateItem()` 是挂起函数。

挂起函数不能直接在普通点击回调里调用。

所以需要：

```kotlin
coroutineScope.launch { ... }
```

### 为什么保存后返回

用户点击 Save 后，通常期望回到上一页。

如果从详情页进入编辑页，保存后返回详情页。

由于详情页通过 Flow 观察数据库，编辑后的新数据会自动显示。

如果再返回首页，首页列表也会自动显示新数据。

---

## 32. 编辑功能完整链路

打开编辑页：

```text
用户在详情页点击编辑 FAB
    ↓
navigateToEditItem(currentItemId)
    ↓
导航到 item_edit/{itemId}
    ↓
ItemEditViewModel 通过 SavedStateHandle 取 itemId
    ↓
init 中 getItemStream(itemId).first()
    ↓
数据库返回当前商品
    ↓
toItemUiState(true)
    ↓
输入框显示旧数据
```

保存编辑：

```text
用户修改输入框
    ↓
updateUiState 更新 itemUiState
    ↓
点击 Save
    ↓
updateItem()
    ↓
ItemDetails.toItem()
    ↓
Repository.updateItem()
    ↓
DAO.update()
    ↓
Room 更新数据库
    ↓
navigateBack()
    ↓
详情页或首页通过 Flow 自动显示新数据
```

---

## 33. 三个页面的数据需求对比

### 首页 Home

需要所有商品，并且要持续更新。

```text
getAllItemsStream()
    → Flow<List<Item>>
    → map to HomeUiState
    → stateIn
```

### 详情页 Details

需要某一个商品，并且要持续更新。

```text
getItemStream(itemId)
    → Flow<Item?>
    → filterNotNull
    → map to ItemDetailsUiState
    → stateIn
```

### 编辑页 Edit

只需要打开页面时读取一次商品，用来填充表单。

```text
getItemStream(itemId)
    → Flow<Item?>
    → filterNotNull
    → first()
    → toItemUiState(true)
```

一句话记：

```text
展示页面持续观察，编辑页面读取一次。
```

---

## 34. 为什么 UI 状态和数据库实体要分开

这个项目中有：

```kotlin
Item
ItemDetails
ItemUiState
HomeUiState
ItemDetailsUiState
```

看起来很多。

但它们职责不同。

### `Item`

数据库实体。

```text
给 Room 用
代表 items 表中的一行
字段类型适合数据库
```

例如：

```kotlin
val price: Double
val quantity: Int
```

### `ItemDetails`

表单输入数据。

```text
给输入框用
字段类型适合 UI 输入
```

例如：

```kotlin
val price: String
val quantity: String
```

### `ItemUiState`

添加和编辑页面的整体状态。

```text
包含输入框数据
包含输入是否合法
```

### `HomeUiState`

首页状态。

```text
包含商品列表
```

### `ItemDetailsUiState`

详情页状态。

```text
包含商品详情
包含是否缺货 outOfStock
```

分开之后，每一层的数据都更贴合自己的用途。

---

## 35. 常见错误和排查方法

### 1. 首页仍然是空列表

检查：

```text
HomeScreen 是否传了 homeUiState.itemList
HomeViewModel 是否调用 getAllItemsStream()
Factory 是否传入 itemsRepository
数据库里是否真的有数据
```

常见错误是还保留着：

```kotlin
itemList = listOf()
```

### 2. ViewModel 创建时报错

如果 ViewModel 加了构造参数，但 Factory 没更新，会创建失败。

检查 `AppViewModelProvider.kt` 中是否有：

```kotlin
HomeViewModel(inventoryApplication().container.itemsRepository)
```

以及：

```kotlin
ItemDetailsViewModel(
    this.createSavedStateHandle(),
    inventoryApplication().container.itemsRepository
)
```

### 3. 详情页显示空白

检查：

```text
点击列表时是否传了正确 id
导航路由是否包含 itemId
SavedStateHandle 的 key 是否和 destination 中定义一致
getItemStream(itemId) 是否返回数据
filterNotNull 是否过滤掉了所有值
```

### 4. Sell 点了没反应

检查：

```text
onSellItem 是否绑定 viewModel.reduceQuantityByOne()
reduceQuantityByOne 是否调用 updateItem()
quantity 是否已经是 0
outOfStock 是否导致按钮禁用
```

### 5. 删除后还留在详情页

检查 `onDelete` 中是否调用：

```kotlin
navigateBack()
```

并且要放在删除之后：

```kotlin
viewModel.deleteItem()
navigateBack()
```

### 6. 编辑页输入框没有旧数据

检查：

```text
详情页 FAB 是否传了当前商品 id
ItemEditViewModel 是否通过 SavedStateHandle 拿到 itemId
init 中是否调用 getItemStream(itemId).first()
Factory 是否传了 SavedStateHandle 和 Repository
```

### 7. 编辑保存后数据库没有变化

检查：

```text
onSaveClick 是否调用 viewModel.updateItem()
updateItem 是否调用 repository.updateItem()
toItem() 是否保留了原始 id
validateInput 是否返回 true
```

---

## 36. 本 Codelab 每个功能对应哪个 DAO 方法

```text
首页显示列表
  getAllItems()

详情页显示单个商品
  getItem(id)

Sell 减少库存
  update(item.copy(quantity = quantity - 1))

Delete 删除商品
  delete(item)

Edit 加载旧数据
  getItem(id)

Edit 保存修改
  update(item)
```

也就是说，整个应用看起来功能很多，但底层主要依赖 DAO 中的几个核心方法：

```kotlin
insert(item)
update(item)
delete(item)
getItem(id)
getAllItems()
```

---

## 37. 完整数据流总览

### 首页读取所有商品

```text
ItemDao.getAllItems()
    ↓
OfflineItemsRepository.getAllItemsStream()
    ↓
HomeViewModel.homeUiState
    ↓
HomeScreen.collectAsState()
    ↓
HomeBody(itemList)
    ↓
LazyColumn 显示列表
```

### 详情页读取单个商品

```text
Home 列表点击 itemId
    ↓
Navigation route 携带 itemId
    ↓
SavedStateHandle 读取 itemId
    ↓
ItemDao.getItem(itemId)
    ↓
ItemDetailsViewModel.uiState
    ↓
ItemDetailsScreen.collectAsState()
    ↓
ItemDetailsBody 显示详情
```

### Sell

```text
点击 Sell
    ↓
reduceQuantityByOne()
    ↓
currentItem.copy(quantity - 1)
    ↓
Repository.updateItem()
    ↓
DAO.update()
    ↓
Room 更新 items 表
    ↓
详情页 Flow 自动发出新数据
```

### Delete

```text
点击 Delete
    ↓
确认对话框 Yes
    ↓
ViewModel.deleteItem()
    ↓
Repository.deleteItem()
    ↓
DAO.delete()
    ↓
Room 删除 items 表中的行
    ↓
navigateBack()
    ↓
首页 Flow 自动发出新列表
```

### Edit

```text
点击编辑 FAB
    ↓
导航到 edit/{itemId}
    ↓
ItemEditViewModel init
    ↓
getItemStream(itemId).first()
    ↓
填充输入框
    ↓
用户修改
    ↓
updateUiState()
    ↓
点击 Save
    ↓
updateItem()
    ↓
DAO.update()
    ↓
Room 更新数据库
```

---

## 38. 本篇真正要掌握的重点

学完这篇，你应该能解释这些问题：

1. 为什么首页不能继续传 `listOf()`。
2. 为什么 DAO 查询返回 `Flow` 可以让 UI 自动刷新。
3. 为什么 ViewModel 要用 `stateIn()` 把 Flow 转成 StateFlow。
4. 为什么 `stateIn` 需要 `viewModelScope`、`WhileSubscribed` 和 `initialValue`。
5. 为什么 Compose 要用 `collectAsState()` 收集状态。
6. 为什么详情页需要通过导航参数拿 `itemId`。
7. 为什么 `SavedStateHandle` 适合在 ViewModel 中读取导航参数。
8. 为什么详情页持续观察数据，而编辑页只用 `.first()` 读取一次。
9. 为什么 Sell 是 Update 操作。
10. 为什么减少库存时用 data class 的 `copy()`。
11. 为什么缺货状态 `outOfStock` 应该由 `quantity` 推导。
12. 为什么 Delete 需要确认对话框。
13. 为什么删除和编辑后首页不用手动刷新。

---

## 39. 和上一个 Codelab 连起来看

两个 Room Codelab 合起来，完整架构是：

```text
UI 层
  HomeScreen
  ItemEntryScreen
  ItemDetailsScreen
  ItemEditScreen

ViewModel 层
  HomeViewModel
  ItemEntryViewModel
  ItemDetailsViewModel
  ItemEditViewModel

Repository 层
  ItemsRepository
  OfflineItemsRepository

Room 层
  ItemDao
  InventoryDatabase
  Item

SQLite
  item_database
  items 表
```

新增商品：

```text
ItemEntryScreen
    ↓
ItemEntryViewModel
    ↓
Repository
    ↓
DAO.insert()
```

显示列表：

```text
DAO.getAllItems()
    ↓
Repository
    ↓
HomeViewModel StateFlow
    ↓
HomeScreen
```

显示详情：

```text
导航 itemId
    ↓
DAO.getItem(id)
    ↓
ItemDetailsViewModel
    ↓
ItemDetailsScreen
```

修改和删除：

```text
Screen 触发事件
    ↓
ViewModel 调用 Repository
    ↓
DAO.update/delete
    ↓
Room 更新数据库
    ↓
Flow 自动推动 UI 更新
```

---

## 40. 一句话总结

这个 Codelab 的核心不是“多写几个按钮回调”，而是理解 Room 数据如何变成 Compose UI 状态。

你可以把它记成：

> DAO 用 `Flow` 观察数据库，ViewModel 用 `StateFlow` 保存界面状态，Compose 用 `collectAsState()` 显示状态；当数据库被插入、更新或删除时，UI 自动刷新。

理解这句话，你就真正理解了这个 Codelab 的主线。
