# Lab13：创建 Bookshelf 网络书架应用实验报告
## 一、实验概述
本次实验基于**Kotlin协程、Retrofit、Gson、Coil、Repository模式**，实现一个网络书架应用。核心功能：从Apifox Mock接口获取书籍封面数据，以网格形式展示图片，处理加载/成功/失败三种UI状态，支持网络异常时的离线数据兜底，点击图片可查看详情弹窗。

## 二、核心技术与实现逻辑
### 1. 网络请求核心（Retrofit + Gson）
定义基础接口与数据解析，适配协程实现异步网络请求：
```kotlin
// 接口地址
const val BASE_URL = "https://m1.apifoxmock.com/m1/8321477-8085280-default/"

// 数据模型
data class Book(val id: String, val coverUrl: String)

// 网络服务接口
interface BookshelfApiService {
    @GET("photos")
    suspend fun getBooks(): List<BookDto>
}

// Retrofit初始化
val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

### 2. Repository数据层（解耦核心）
统一数据获取接口，实现**网络数据源+离线兜底数据源**，ViewModel无需关心数据来源：
```kotlin
// 数据仓库接口
interface BooksRepository {
    suspend fun getBooks(): List<Book>
}

// 网络数据仓库
class NetworkRepository(private val api: BookshelfApiService) : BooksRepository {
    override suspend fun getBooks() = api.getBooks().map { it.toBook() }
}

// 离线兜底仓库
class OfflineRepository : BooksRepository {
    override suspend fun getBooks() = listOf(
        Book("1", "https://picsum.photos/id/100/800"),
        Book("2", "https://picsum.photos/id/101/800")
    )
}
```

### 3. UI状态管理
定义密封接口管理`加载中/成功/失败`状态，ViewModel控制状态切换：
```kotlin
// UI状态
sealed interface UiState {
    object Loading : UiState
    data class Success(val books: List<Book>) : UiState
    data class Error(val msg: String) : UiState
}

// ViewModel
class BooksViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { loadBooks() }

    fun loadBooks() = viewModelScope.launch {
        _uiState.value = UiState.Loading
        try {
            val data = NetworkRepository(apiService).getBooks()
            _uiState.value = UiState.Success(data)
        } catch (e: Exception) {
            _uiState.value = UiState.Error("加载失败")
        }
    }
}
```

### 4. Compose界面实现
使用网格布局展示图片，根据状态自动切换界面，支持点击弹窗：
- 加载中：显示进度条
- 加载成功：两列网格展示书籍封面
- 加载失败：错误提示+重试按钮
- 点击图片：弹出封面详情弹窗

## 三、运行结果
1. **加载中**：界面中央显示圆形加载进度指示器
2. **加载成功**：两列整齐网格展示网络图片，带书籍ID标识
3. **加载失败**：红色错误文字+重试按钮，点击可重新请求
4. **断网测试**：自动切换为离线兜底图片，保证界面可用
5. **交互功能**：点击任意书籍，弹出高清封面弹窗

## 四、常见问题与解决
1. **JSON解析失败**：使用`@SerializedName`绑定JSON字段名
2. **网络请求崩溃**：用`viewModelScope`协程执行网络操作
3. **图片不显示**：添加网络权限，校验图片URL
4. **断网无内容**：新增OfflineRepository实现离线数据兜底

## 五、实验总结
本次实验完成了完整的Android网络应用开发，掌握了**网络请求、数据解析、状态管理、UI渲染**全流程。
核心收获：
1. 学会使用Retrofit+协程实现安全网络请求
2. 理解Repository模式解耦数据层与UI层
3. 掌握Compose多状态UI的实现方式
4. 实现网络异常的容错处理，提升应用健壮性
