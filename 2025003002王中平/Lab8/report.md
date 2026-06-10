# Lab8 构建超级英雄列表应用 实验报告

## 一、应用整体结构说明
项目采用**分层模块化结构**，职责拆分清晰：
1. **model 层**：存放 Hero 数据类、HeroesRepository 静态数据源，统一管理实体数据与模拟数据。
2. **ui/theme 层**：自定义 Material3 颜色、形状、字体排版、全局主题，统一控制全局 UI 样式。
3. **页面层**
   - `HeroesScreen.kt`：封装英雄列表 `LazyColumn` 和单个列表项 `HeroItem` 可组合函数。
   - `MainActivity.kt`：程序入口，通过 `Scaffold` 整合顶部应用栏与列表主体，承载全局主题。
4. **资源层**：strings 字符串资源、drawable 英雄图片资源、font 自定义 Cabin 字体资源。

整体遵循**数据与 UI 分离、样式统一主题管理、组件复用**的 Compose 开发思想。

## 二、Hero 数据类设计与理由
### 1. 数据类代码
```kotlin
data class Hero(
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int
)
```

### 2. 字段说明
- `nameRes`：英雄名称字符串资源 ID
- `descriptionRes`：英雄描述字符串资源 ID
- `imageRes`：英雄头像图片资源 ID

### 3. 设计理由
1. 使用**数据类 data class** 自动提供解构、equals、toString 等方法，适合列表实体模型。
2. 加入 `@StringRes`、`@DrawableRes` 资源注解，编译时校验资源 ID 类型，避免传错资源。
3. 不直接存放字符串和图片位图，只存**资源 ID**，符合 Android 资源规范化管理，支持多语言、主题切换。

## 三、HeroesRepository 数据源组织方式
1. 采用 `object` 单例对象作为仓库类，全局唯一、无需实例化，适合静态模拟数据源。
2. 内部定义 `heroes` 只读 List，一次性初始化 6 个 Hero 对象，绑定对应字符串和图片资源。
3. 数据集中托管在 Repository，UI 层只需直接调用 `HeroesRepository.heroes`，实现**数据与视图解耦**。
4. 后续如需改为网络请求、本地数据库，只需修改 Repository 内部实现，UI 层无需改动。

## 四、英雄列表项布局实现思路
1. 外层使用 **Material3 Card** 作为卡片容器，采用主题中等圆角、轻微阴影，符合设计规格。
2. 卡片内部采用 **Row 水平布局**：左侧文字 Column、右侧正方形头像。
3. 左侧 Column 垂直排列：英雄名称 + 英雄描述，分别绑定主题预设文字样式。
4. 右侧使用固定 **72dp 正方形 Box** 包裹 Image，设置 `clip` 裁剪为 8dp 小圆角，`ContentScale.Crop` 居中裁剪填满区域。
5. 严格遵循设计规格：
   - 卡片内边距 16dp
   - 文字与图片间距 16dp
   - 图片尺寸 72dp、圆角 8dp
   - 英雄名称使用 `displaySmall`、描述使用 `bodyLarge`

## 五、LazyColumn 列表实现和间距配置
1. 使用 `LazyColumn` 实现高性能可滚动列表，仅加载屏幕可见项，优于普通 Column。
2. 通过 `contentPadding = PaddingValues(16.dp)` 设置列表整体四周留白。
3. 通过 `verticalArrangement = Arrangement.spacedBy(8.dp)` 设置列表项之间垂直间距 8dp。
4. 使用 `items(heroes)` 遍历数据源，逐个复用 `HeroItem` 列表项组件。
5. 接收 Scaffold 传入的 `innerPadding`，避免列表内容被顶部应用栏遮挡。

## 六、Material 主题配置说明
### 1. 颜色配置
- 自定义浅色/深色两套完整配色方案，适配 Material3 规范。
- 主题自动跟随系统深浅色模式切换，控件颜色自动适配。

### 2. 形状配置
- 定义 small 8dp、medium 16dp 圆角，分别用于图片圆角和卡片圆角，全局统一控件弧度。

### 3. 字体排版
- 引入自定义 Cabin 字体（常规、粗体），封装为 FontFamily。
- 预设三级文字样式：
  - displayLarge：顶部应用栏标题
  - displaySmall：英雄名称
  - bodyLarge：英雄描述
- 全局文本统一使用主题 Typography，不用硬编码字号和字体。

### 4. 全局主题
- 封装 `SuperheroesTheme` 可组合函数，统一注入 colorScheme、typography、shapes。
- 配置状态栏适配，自动根据深浅色模式切换状态栏图标明暗。

## 七、顶部应用栏和状态栏处理说明
### 1. 顶部应用栏
- 使用 `CenterAlignedTopAppBar` 实现居中标题栏。
- 标题使用应用名称字符串资源，字体采用主题 `displayLarge` 大标题样式。
- 借助 `Scaffold` 嵌套顶部栏与列表内容，自动管理内边距。

### 2. 状态栏适配
1. 通过 `SideEffect` 获取当前 Activity 窗口。
2. 动态设置状态栏颜色为主题主色调。
3. 根据深色/浅色主题自动切换状态栏文字明暗，保证文字清晰可读。
4. 实现应用界面与系统状态栏视觉融合，无突兀分割色块。

## 八、遇到的问题与解决过程
1. **资源文件名报错**
   - 问题：字体、图片文件名含大写和空格，编译报错。
   - 解决：统一改为小写+下划线命名，符合 Android 资源命名规范。

2. **图片布局变形、尺寸不固定**
   - 问题：原始图片大小不一，撑开卡片布局。
   - 解决：固定 72dp 正方形容器，设置 `ContentScale.Crop` 裁剪并固定圆角。

3. **列表内容被顶部栏遮挡**
   - 问题：LazyColumn 顶部内容被 TopAppBar 盖住。
   - 解决：接收 Scaffold 的 innerPadding 传给列表 padding，自动避让系统栏和顶部栏。

4. **深浅色模式文字看不清**
   - 问题：切换深色主题后状态栏文字与背景融合。
   - 解决：通过 WindowCompat 动态控制状态栏图标明暗适配。

5. **字体不生效**
   - 问题：导入字体后排版样式没变化。
   - 解决：正确放置到 res/font 目录，在 Type.kt 正确绑定字体文件与字重。

## 九、实验总结
本次实验综合运用 Kotlin 数据类、静态仓库数据源、Compose 布局、LazyColumn 滚动列表、Material3 自定义主题、资源管理、状态栏适配等知识点。完成了规范化的超级英雄列表应用，实现了深浅色主题自动切换、组件样式统一、布局严格遵循设计规格，加深了对 Compose 模块化开发、主题统一管理、数据与 UI 分离思想的理解，掌握了 Material3 自定义配色、字体、形状的完整配置流程。

---