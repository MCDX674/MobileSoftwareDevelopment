# Lab13：创建 Bookshelf 网络书架应用

## 一、实验目的

本实验基于 MVVM 架构，结合 Retrofit 网络通信、Repository 数据仓库模式、Coil 图片加载以及 Jetpack Compose 声明式 UI，实现一个从网络获取并展示图书图片列表的 Bookshelf 应用。

通过本实验，掌握以下核心知识点：

1. 使用 Retrofit 创建网络服务接口并请求 JSON 数据；
2. 使用 Gson 将 JSON 响应解析为 Kotlin 数据对象；
3. 使用 Repository 隔离数据层，通过依赖注入替换真实或离线数据源；
4. 使用 Coil 的 `AsyncImage` 从 URL 加载图片；
5. 使用 `LazyVerticalGrid` 构建书架网格界面；
6. 为 Loading / Success / Error 设计清晰的 UI 状态；
7. 使用 Apifox Mock 接口完成可运行的网络数据练习。

---

## 二、实验内容与实现过程

### 1. 为什么使用 Apifox Mock 接口

在实际开发中，前端开发常受后端接口开发进度的影响。本实验采用 Apifox Mock 接口模拟真实服务端数据，原因如下：

**（1）保证接口稳定性：** Mock 接口持续返回预设数据，不受后端开发进度和服务器状态影响，确保实验顺利进行。

**（2）支持前后端并行开发：** 根据约定好的接口文档提前完成数据请求与界面开发，提高开发效率。

**（3）便于功能测试：** 可灵活修改返回数据，模拟正常返回、空数据、异常数据、网络错误等场景，验证应用的健壮性。

**（4）降低后期适配成本：** Mock 数据结构与真实接口保持一致，后续接入正式后端时仅需修改接口地址即可完成迁移。

本实验使用的 Mock 接口地址：

```
https://m1.apifoxmock.com/m1/8321477-8085280-default/photos
```

返回数据格式：

```json
[
  {
    "id": "1",
    "img_src": "https://picsum.photos/id/10/800/600"
  },
  {
    "id": "2",
    "img_src": "https://picsum.photos/id/11/800/600"
  }
]
```

---

### 2. Retrofit 服务接口定义

#### （1）添加依赖

在 `build.gradle.kts` 中添加网络与图片加载依赖：

```kotlin
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("io.coil-kt:coil-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
```

#### （2）定义 API 接口与配置

`ApiConfig.kt` 存放基础 URL：

```kotlin
object ApiConfig {
    const val BASE_URL = "https://m1.apifoxmock.com/m1/8321477-8085280-default/"
}
```

`BookshelfApiService.kt` 定义 Retrofit 服务接口：

```kotlin
interface BookshelfApiService {
    @GET("photos")
    suspend fun getBooks(): List<BookDto>
}
```

说明：
- `@GET("photos")` 指定 HTTP GET 请求及路径，最终请求完整 URL 为 `BASE_URL + "photos"`；
- `suspend` 关键字使其支持 Kotlin 协程，避免在主线程执行网络操作；
- 方法返回 `List<BookDto>`，Retrofit 结合 Gson Converter 自动将 JSON 数组解析为 Kotlin 对象列表。

#### （3）数据模型与 DTO 转换

`BookDto` 对应 JSON 响应结构，使用 `@SerializedName` 处理字段名映射：

```kotlin
data class BookDto(
    val id: String = "",
    @SerializedName("img_src")
    val imgSrc: String = "",
)

fun BookDto.asExternalModel(): Book {
    return Book(
        id = id,
        title = "Book #$id",
        coverUrl = imgSrc
    )
}
```

`Book` 是应用内部使用的领域模型，避免网络层 DTO 直接暴露给 UI：

```kotlin
data class Book(
    val id: String,
    val title: String,
    val coverUrl: String
)
```

---

### 3. Repository 如何隔离网络数据源

Repository 位于 ViewModel 与数据源之间，负责统一管理数据获取逻辑。

#### （1）定义 Repository 接口

```kotlin
interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}
```

ViewModel 只依赖该接口，不直接访问 Retrofit，便于替换数据源和编写测试。

#### （2）网络数据源实现

```kotlin
class NetworkBooksRepository(
    private val bookshelfApiService: BookshelfApiService,
    private val offlineBooksRepository: OfflineBooksRepository = OfflineBooksRepository()
) : BooksRepository {

    override suspend fun getBooks(): List<Book> {
        return try {
            bookshelfApiService.getBooks().map { it.asExternalModel() }
        } catch (e: Exception) {
            offlineBooksRepository.getBooks()
        }
    }

    override suspend fun getBook(id: String): Book {
        return getBooks().firstOrNull { it.id == id }
            ?: throw NoSuchElementException("未找到编号为 $id 的图书")
    }
}
```

`NetworkBooksRepository` 采用网络优先 + 离线兜底策略：优先通过 Retrofit 请求网络数据；若网络异常则自动降级到离线数据源，确保应用在无网络环境下仍可运行。

#### （3）离线数据源实现

```kotlin
class OfflineBooksRepository : BooksRepository {

    private val offlineBooks = listOf(
        Book("1", "Offline Book #1", "https://picsum.photos/id/10/800/600"),
        Book("2", "Offline Book #2", "https://picsum.photos/id/11/800/600"),
        Book("3", "Offline Book #3", "https://picsum.photos/id/12/800/600"),
        Book("4", "Offline Book #4", "https://picsum.photos/id/13/800/600"),
        Book("5", "Offline Book #5", "https://picsum.photos/id/14/800/600"),
        Book("6", "Offline Book #6", "https://picsum.photos/id/15/800/600")
    )

    override suspend fun getBooks(): List<Book> = offlineBooks
    override suspend fun getBook(id: String): Book =
        offlineBooks.firstOrNull { it.id == id }
            ?: throw NoSuchElementException("未找到编号为 $id 的图书")
}
```

#### （4）依赖注入容器

```kotlin
class DefaultAppContainer : AppContainer {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitService: BookshelfApiService by lazy {
        retrofit.create(BookshelfApiService::class.java)
    }

    override val booksRepository: BooksRepository by lazy {
        NetworkBooksRepository(retrofitService)
    }
}
```

`AppContainer` 集中创建 Retrofit、API Service 和 Repository。在 `BookshelfApplication` 中初始化：

```kotlin
class BookshelfApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
```

ViewModel 通过 `Application` 获取容器中的 Repository，实现依赖注入。

---

### 4. Loading / Success / Error 状态切换

#### （1）定义密封接口

```kotlin
sealed interface BookshelfUiState {
    data object Loading : BookshelfUiState
    data class Success(val books: List<Book>, val selectedBook: Book? = null) : BookshelfUiState
    data class Error(val message: String) : BookshelfUiState
}
```

三种状态的含义：

| 状态 | 含义 | UI 表现 |
|------|------|---------|
| Loading | 正在加载数据 | 居中圆形进度指示器 |
| Success | 数据加载成功 | 网格书架列表 + 弹窗详情 |
| Error | 数据加载失败 | 错误提示 + 重试按钮 |

#### （2）ViewModel 状态管理

```kotlin
class BookshelfViewModel(
    private val booksRepository: BooksRepository
) : ViewModel() {

    var uiState: BookshelfUiState by mutableStateOf(BookshelfUiState.Loading)
        private set

    init { getBooks() }

    fun getBooks() {
        viewModelScope.launch {
            uiState = BookshelfUiState.Loading
            uiState = try {
                val books = booksRepository.getBooks()
                if (books.isEmpty()) {
                    BookshelfUiState.Error("没有获取到书架数据")
                } else {
                    BookshelfUiState.Success(books = books)
                }
            } catch (e: Exception) {
                BookshelfUiState.Error(message = e.message ?: "加载失败，请检查网络连接")
            }
        }
    }

    fun selectBook(book: Book) {
        val currentState = uiState
        if (currentState is BookshelfUiState.Success) {
            uiState = currentState.copy(selectedBook = book)
        }
    }

    fun closeBookDetail() {
        val currentState = uiState
        if (currentState is BookshelfUiState.Success) {
            uiState = currentState.copy(selectedBook = null)
        }
    }
}
```

状态切换流程：

```
启动 → Loading → 网络请求 → 成功 → Success（显示网格）
                               失败 → Error（显示错误与重试按钮）
重试 → Loading → ...
点按条目 → Success.copy(selectedBook = book) → 显示详情弹窗
关闭弹窗 → Success.copy(selectedBook = null) → 隐藏弹窗
```

#### （3）Compose 响应式渲染

```kotlin
@Composable
fun BookshelfScreen(
    uiState: BookshelfUiState,
    onRetry: () -> Unit,
    onBookClick: (Book) -> Unit,
    onCloseDetail: () -> Unit
) {
    when (uiState) {
        is BookshelfUiState.Loading -> LoadingScreen()
        is BookshelfUiState.Success -> BooksGridScreen(uiState, onBookClick, onCloseDetail)
        is BookshelfUiState.Error -> ErrorScreen(uiState.message, onRetry)
    }
}
```

Compose 通过 `mutableStateOf` 自动监听状态变化，状态改变时自动重组（recompose）对应 UI 组件，实现响应式更新。

---

## 三、运行截图

![运行截图](screenshot.png)

截图说明：
- 应用首页以网格布局展示从 Apifox Mock 接口获取的图书封面图片
- 顶部标题栏显示 "Bookshelf"
- 加载过程中显示圆形进度指示器
- 数据加载成功后展示图片网格
- 点按任意条目弹出详情弹窗，显示大图、编号和图片 URL

---

## 四、实验过程中遇到的问题及解决方案

### 问题一：JSON 字段名映射错误

**原因：** API 返回的字段为 `img_src`（蛇形命名），而 Kotlin 属性为 `imgSrc`（驼峰命名），导致 Gson 解析失败，图片 URL 为空。

**解决方法：** 使用 `@SerializedName("img_src")` 注解建立字段映射关系，确保 JSON 字段正确绑定到 Kotlin 属性。

### 问题二：网络请求在主线程执行

**原因：** 在 Compose 或 ViewModel 中直接调用 Retrofit 同步方法会导致主线程阻塞，引发界面卡顿甚至 ANR。

**解决方法：** Retrofit 接口方法声明为 `suspend`，在 ViewModel 中通过 `viewModelScope.launch` 启动协程异步执行网络请求，确保网络操作在后台线程进行。

### 问题三：图片无法加载

**原因：** 缺少网络权限声明或未添加图片加载库依赖。

**解决方法：** 在 `AndroidManifest.xml` 中添加 `<uses-permission android:name="android.permission.INTERNET" />`，并在 `build.gradle.kts` 中引入 `io.coil-kt:coil-compose` 依赖。

### 问题四：离线数据源未正确降级

**原因：** 最初 `NetworkBooksRepository` 在网络异常时直接抛出异常，没有兜底策略，导致断网时应用空白。

**解决方法：** 修改 `NetworkBooksRepository.getBooks()` 加入 try-catch 降级逻辑，网络异常时自动回退到 `OfflineBooksRepository`，确保应用在无网络环境下仍可展示书架数据。

---

## 五、实验总结

通过本次 Bookshelf 网络书架应用实验，我系统实践了 Android 现代化应用开发的核心技术栈：

1. **MVVM 架构：** ViewModel 管理 UI 状态，Compose 响应式渲染，职责清晰；
2. **Retrofit + Gson：** 定义声明式网络接口，自动解析 JSON 响应；
3. **Repository 模式：** 隔离数据层，支持网络与离线数据源灵活切换；
4. **依赖注入：** 通过 `AppContainer` 集中管理依赖，便于测试和替换；
5. **Coil 图片加载：** 一行代码实现远程图片的异步加载与缓存；
6. **Apifox Mock：** 实现前后端分离开发，不依赖真实后端即可完成开发。

实验过程中遇到的 JSON 字段映射、主线程阻塞、图片加载失败等问题，通过查阅文档和调试逐一解决，加深了对网络请求、协程调度、数据解析等关键技术的理解。本实验为后续开发更复杂的 Android 网络应用奠定了扎实的基础。
