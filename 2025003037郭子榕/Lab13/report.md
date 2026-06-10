# Lab13 网络书架应用实验报告

## 一、实验背景

本实验要求通过 Apifox Mock 接口下载图片数据，综合练习 Kotlin 协程、Retrofit 网络请求、Gson JSON 解析、Coil 图片加载、Repository 数据层封装以及依赖注入等 Android 现代开发技术。项目采用 MVVM 架构，使用 Jetpack Compose 构建 UI，实现一个可展示远程图片列表的书架应用。

------

## 二、实验目标

1. 使用 Retrofit 创建网络服务接口并请求 JSON 数据。
2. 使用 Gson 解析 JSON 响应为 Kotlin 数据对象。
3. 通过 Repository 隔离数据源，支持网络和离线兜底数据。
4. 使用 Coil 的 `AsyncImage` 加载并显示远程图片。
5. 使用 `LazyVerticalGrid` 构建响应式图片网格界面。
6. 设计 Loading、Success、Error 三种 UI 状态并实现对应界面。
7. 利用 `viewModelScope` 和 `StateFlow`/`mutableStateOf` 进行状态管理。
8. 实现点击条目弹出详情弹窗功能。

------

## 三、实验环境

- **开发工具**：Android Studio
- **语言**：Kotlin
- **最低 SDK**：API 24 (Android 7.0)
- **架构**：MVVM + Repository 模式
- **核心依赖**：
    - Retrofit 2.11.0 + Gson Converter
    - Coil Compose 2.7.0
    - Lifecycle ViewModel Compose 2.8.7
    - Jetpack Compose BOM 2023.08.00

------

## 四、项目结构

text

```
com.example.bookshelf/
├── BookshelfApplication.kt          // 自定义 Application，提供依赖容器
├── MainActivity.kt                  // 入口 Activity
├── data/
│   ├── AppContainer.kt              // 依赖注入容器接口与实现
│   ├── BooksRepository.kt           // Repository 接口
│   └── NetworkBooksRepository.kt    // 网络+离线数据源实现
├── model/
│   ├── Book.kt                      // 领域模型
│   └── BookDto.kt                   // 网络传输对象，包含扩展函数转换
├── network/
│   ├── ApiConfig.kt                 // 基础 URL 配置
│   └── BookshelfApiService.kt       // Retrofit API 接口
└── ui/
    └── theme/                       // Compose 主题与页面
        ├── BookshelfScreen.kt       // UI 界面 Composable
        ├── BookshelfViewModel.kt    // ViewModel 与 UI 状态定义
        ├── Color.kt, Theme.kt, Type.kt // 主题文件（项目自动生成）
```



------

## 五、实现过程

### 5.1 创建项目并添加依赖

新建 Empty Activity 项目，包名 `com.example.bookshelf`，在 `app/build.gradle.kts` 中添加 Retrofit、Gson、Coil、Lifecycle-ViewModel 等依赖，并在 `AndroidManifest.xml` 中添加 `INTERNET`权限。

### 5.2 定义数据模型

**`BookDto.kt`**
使用 `@SerializedName` 映射 JSON 字段，适应 Mock 接口返回的 `img_src` 字段。
提供扩展函数 `asExternalModel()`，将 DTO 转换为 UI 层使用的 `Book` 对象。

**`Book.kt`**
领域模型，包含 `id`、`title`、`coverUrl`。其中 `title` 根据 `id` 生成，方便列表展示。

### 5.3 搭建网络层

**`ApiConfig.kt`** 定义 Mock 接口基础地址：
`https://m1.apifoxmock.com/m1/8321477-8085280-default/`

**`BookshelfApiService.kt`**
使用 Retrofit 注解 `@GET("photos")` 定义 suspend 函数 `getBooks()`，返回 `List<BookDto>`。

### 5.4 实现 Repository 与依赖注入

**`BooksRepository` 接口**
定义 `getBooks()` 和 `getBook(id)` 两个挂起函数。

**`NetworkBooksRepository`**
实现接口，内部持有 `BookshelfApiService` 实例。
`getBooks()` 优先尝试网络请求，若失败则调用 `OfflineBooksRepository` 返回本地兜底数据。

**`OfflineBooksRepository`**
提供固定 6 本虚构书籍数据，保证离线或网络异常时应用仍可展示内容。

**依赖注入容器**

- `AppContainer` 接口定义 `booksRepository` 属性。
- `DefaultAppContainer` 实现该接口，内部创建 Retrofit 实例和 `NetworkBooksRepository`。
- `BookshelfApplication` 在 `onCreate()` 中初始化 `container = DefaultAppContainer()`，并通过 `AndroidManifest.xml` 注册为自定义 Application。

### 5.5 设计 UI 状态与 ViewModel

**状态定义（`BookshelfUiState`）**
使用 `sealed interface` 定义三种状态：

- `Loading`：加载中
- `Success(books, selectedBook?)`：加载成功，包含书籍列表和可选选中的书籍
- `Error(message)`：加载失败，包含错误信息

**`BookshelfViewModel`**

- 构造函数接收 `BooksRepository`，通过 `companion object` 的 `Factory` 从 Application 容器获取。
- 使用 `mutableStateOf` 持有 `uiState`（或更推荐 `StateFlow`），在 `viewModelScope` 中启动协程请求数据。
- `getBooks()` 方法负责更新状态：开始置 `Loading`，成功后置 `Success`，异常置 `Error`。
- `selectBook(book)` 设置 `selectedBook` 以弹出详情。
- `closeBookDetail()` 将 `selectedBook` 置 `null` 关闭弹窗。

### 5.6 构建 Compose UI

**`BookshelfScreen`**
顶层 Scaffold，包含顶部应用栏和根据 `uiState` 切换的内容：

- `Loading` 时显示居中圆形进度条。
- `Error` 时显示错误信息和重试按钮。
- `Success` 时显示 `BooksGridScreen`，并在有 `selectedBook` 时弹出 `AlertDialog`。

**`BooksGridScreen`**
使用 `LazyVerticalGrid`（自适应列宽 150dp）展示书籍卡片，每张卡片点击调用 `onBookClick`。

**`BookCard`**
使用 `Card` 组件，内部包含 Coil 的 `AsyncImage` 加载封面，以及标题文本。

**`BookDetailDialog`**
`AlertDialog` 展示大尺寸图片、书籍编号和原始图片地址，点击“关闭”按钮调用 `onDismiss`。

------

## 六、遇到的问题与解决方案

### 问题 1：编译报红 `import androidx.lifecycle.viewmodel.compose.viewModel`

**原因**：未正确添加 `lifecycle-viewmodel-compose` 依赖或未同步。
**解决**：确认 `build.gradle.kts` 中添加 `implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")`，点击 Sync Now，必要时 Clean → Rebuild。

### 问题 2：应用闪退（ClassCastException）

**原因**：`AndroidManifest.xml` 中缺少 `android:name=".BookshelfApplication"`，导致 ViewModel Factory 中 `APPLICATION_KEY` 获取的是普通 Application 而非自定义的 `BookshelfApplication`，强制转换失败。
**解决**：在 `<application>` 标签中添加 `android:name=".BookshelfApplication"`。

### 问题 3：UI 不更新，始终停留在 Loading 状态

**原因**：ViewModel 中使用 `mutableStateOf` 委托，但在 Composable 中直接用普通变量 `val uiState = viewModel.uiState` 读取，未委托订阅状态，导致无法重组。
**解决**：将 Composable 中的读取改为 `val uiState by viewModel::uiState`（或换用 `StateFlow` + `collectAsState()`），使 Compose 能感知状态变化并自动重组界面。

### 问题 4：依赖虚线下划线警告

**原因**：IDE 提示部分库未使用，因代码尚未引用时即出现。
**解决**：确认代码导入后警告自动消失，不影响编译运行，可忽略。

------

## 七、运行结果

1. 应用启动后展示顶栏标题“Bookshelf 网络书架”，中间显示加载指示器。
2. 网络正常时，从 Mock 接口获取图片数据，以自适应网格展示，每张卡片包含封面和标题。
3. 点击卡片弹出详情对话框，显示大图、编号和原图地址，点击“关闭”按钮返回。
4. 关闭网络后重启应用，由于网络请求失败，自动回退至离线数据，显示 6 本本地书籍。
5. 若网络异常且离线数据也加载失败，则显示错误信息与“重试”按钮，点击后重新加载。

------

## 八、实验总结

通过本次实验，我掌握了以下技能：

- 使用 Retrofit 和 Gson 快速构建网络数据请求与解析。
- 运用 Repository 模式实现数据源隔离，并通过依赖注入灵活切换真实与离线数据。
- 结合 Coil 库高效加载远程图片，并处理加载状态。
- 利用 Jetpack Compose 的声明式 UI 构建响应式网格布局和对话框。
- 采用 `sealed interface` 定义 UI 状态，配合 ViewModel 和 StateFlow/Compose State 实现清晰的单向数据流。
- 学会了排查 Android 开发中常见的依赖配置、Manifest 声明以及 Compose 状态订阅问题。