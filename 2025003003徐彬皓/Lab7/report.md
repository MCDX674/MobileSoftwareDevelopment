# Lab7 实验报告
## 1. 应用整体结构说明
本次实验构建的课程网格应用采用**分层式结构**组织代码，核心分为数据层、UI 组合项层，整体结构清晰且符合 Compose 开发规范，具体如下：

### （1）数据层
- **数据类（Topic.kt）**：位于 `com.example.myapplication7.model` 包下，定义 `Topic` 数据类，封装单个课程主题的核心数据（名称资源 ID、课程数量、图片资源 ID），是应用的数据模型基础。
- **数据源（DataSource.kt）**：位于 `com.example.myapplication7.data` 包下，通过 `object` 单例创建 `topics` 列表，集中管理 24 个课程主题的静态数据，避免数据分散，便于统一维护和复用。

### （2）UI 组合项层
- **入口层（MainActivity.kt）**：应用主入口，通过 `setContent` 挂载 Compose 界面，核心是调用顶层组合项 `CoursesApp`。
- **顶层组合项**：`CoursesApp` 作为界面根组合项，负责调用 `TopicGrid` 并设置网格整体内边距。
- **网格组合项**：`TopicGrid` 基于 `LazyVerticalGrid` 实现两列可滚动网格，通过 `items` 遍历数据源，为每个 `Topic` 实例渲染 `TopicCard`。
- **卡片组合项**：`TopicCard` 实现单个课程卡片的布局与样式，是网格的最小 UI 单元，负责将 `Topic` 数据渲染为可视化卡片。

### （3）资源层
- 字符串资源：`strings.xml` 中定义所有课程主题名称，通过资源 ID 解耦硬编码，支持多语言扩展。
- 图片资源：`drawable` 目录下存放课程主题图片和装饰图标（`ic_grain.xml`），通过资源 ID 与数据类关联。

## 2. `Topic` 数据类的字段设计与选择理由
### 字段设计
```kotlin
data class Topic(
    @StringRes val nameResId: Int,
    val courseCount: Int,
    @DrawableRes val imageResId: Int
)
```

### 选择理由
| 字段 | 类型/注解 | 选择理由 |
|------|-----------|----------|
| `nameResId` | `@StringRes Int` | 采用字符串资源 ID 而非直接存储字符串：<br>1. 符合 Android 资源管理规范，支持多语言适配；<br>2. 避免硬编码字符串，降低维护成本；<br>3. `@StringRes` 注解可让 IDE 校验资源合法性，减少运行时错误。 |
| `courseCount` | `Int` | 课程数量是纯数字型数据，无需复杂封装，直接使用基本类型 `Int` 即可满足需求，兼顾性能和简洁性。 |
| `imageResId` | `@DrawableRes Int` | 采用图片资源 ID 而非图片文件路径/位图：<br>1. 利用 Android 资源系统的缓存和适配能力，适配不同分辨率屏幕；<br>2. `@DrawableRes` 注解确保传入的是合法的图片资源 ID，避免无效资源导致的崩溃；<br>3. 资源 ID 是整型常量，占用内存少，便于列表高效渲染。 |

此外，使用 `data class` 而非普通 `class`：  
- 自动生成 `equals()`、`hashCode()`、`toString()` 等方法，便于数据比较和调试；  
- 自动生成 `copy()` 方法，便于数据拷贝和修改；  
- 符合 Kotlin 中“数据承载类”的设计规范，语义更清晰。

## 3. 卡片布局实现思路
单个课程卡片（`TopicCard`）采用**多层嵌套组合项**实现，核心思路是“先整体、后局部，先布局、后样式”，具体嵌套结构如下：

### 核心组合项嵌套关系
```
Card（卡片容器）
└── Row（水平排列：图片 + 文字区域）
    ├── Image（课程主题图片）
    └── Column（垂直排列：主题名称 + 课程数量行）
        ├── Text（主题名称）
        └── Row（水平排列：装饰图标 + 课程数量）
            ├── Image（ic_grain 装饰图标）
            ├── Spacer（图标与数字间距）
            └── Text（课程数量）
```

### 关键实现细节
1. **外层容器：Card**  
   使用 `Card` 作为卡片根容器，设置 `RoundedCornerShape(8.dp)` 实现圆角效果，`fillMaxWidth()` 让卡片占满网格列宽，符合“两列网格”的视觉要求。

2. **水平布局：Row**  
   卡片内部通过 `Row` 实现“图片 + 文字区域”的水平排列，保证图片居左、文字区域居右的核心布局结构。

3. **图片区域：Image**  
   - 设置 `size(68.dp)` 固定图片宽高，`aspectRatio(1f)` 确保图片为正方形（适配不同屏幕）；  
   - `clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))` 让图片左上角/左下角与卡片圆角对齐；  
   - `contentScale = ContentScale.Crop` 保证图片填充容器且不变形。

4. **文字区域：Column**  
   - `padding(16.dp)` 实现文字区域上下左右 16dp 内边距，符合 UI 规格；  
   - `align(Alignment.CenterVertically)` 让文字区域在 Row 中垂直居中，与图片高度匹配。

5. **课程数量行：Row**  
   - `verticalAlignment = Alignment.CenterVertically` 让图标和数字垂直居中；  
   - `Spacer(Modifier.width(8.dp))` 实现图标与数字之间 8dp 的间距，符合 UI 规格；  
   - 装饰图标设置 `size(16.dp)`，保证尺寸统一。

6. **文字样式**  
   - 主题名称：`MaterialTheme.typography.bodyMedium`，符合规格要求；  
   - 课程数量：`MaterialTheme.typography.labelMedium`，与设计稿一致。

## 4. 网格布局实现思路（`LazyVerticalGrid` 参数配置说明）
网格布局通过 `LazyVerticalGrid` 实现，核心是配置列数、间距、内边距，确保符合“两列、可滚动、指定间距”的需求，参数配置及说明如下：

### 核心代码
```kotlin
LazyVerticalGrid(
    columns = GridCells.Fixed(2), // 两列网格
    modifier = modifier, // 接收外部传入的内边距（8dp）
    verticalArrangement = Arrangement.spacedBy(8.dp), // 垂直方向卡片间距
    horizontalArrangement = Arrangement.spacedBy(8.dp) // 水平方向卡片间距
) {
    items(topics) { topic ->
        TopicCard(topic = topic) // 遍历渲染每个卡片
    }
}
```

### 参数详解
| 参数 | 配置值 | 作用 |
|------|--------|------|
| `columns` | `GridCells.Fixed(2)` | 指定网格为固定 2 列，是实现“两列网格”的核心参数；`GridCells.Fixed(n)` 表示强制分为 n 列，列宽自动均分可用宽度。 |
| `modifier` | 外部传入 `Modifier.padding(8.dp)` | 为整个网格设置四周 8dp 的内边距，保证网格与屏幕边缘有间距，符合 UI 规格。 |
| `verticalArrangement` | `Arrangement.spacedBy(8.dp)` | 控制垂直方向上相邻卡片的间距为 8dp，实现卡片上下间距统一。 |
| `horizontalArrangement` | `Arrangement.spacedBy(8.dp)` | 控制水平方向上相邻卡片的间距为 8dp，实现卡片左右间距统一。 |
| `items(topics)` | 遍历 `DataSource.topics` | `LazyVerticalGrid` 是“懒加载”网格，仅渲染当前可见区域的卡片，提升列表滚动性能；`items` 方法接收数据列表，为每个元素生成 `TopicCard`。 |

### 关键设计思路
1. **懒加载特性**：`LazyVerticalGrid` 继承 Compose 懒加载容器的特性，避免一次性渲染 24 个卡片，降低内存占用，提升滚动流畅度。
2. **间距分层控制**：  
   - 网格整体内边距（`modifier.padding(8.dp)`）：控制网格与屏幕边缘的距离；  
   - 卡片间间距（`spacedBy(8.dp)`）：控制卡片之间的距离；  
   两者结合实现“网格边缘留白 + 卡片间距”的双层间距效果，符合设计稿要求。
3. **列宽自适应**：`GridCells.Fixed(2)` 让两列宽度自动均分网格可用宽度，适配不同屏幕尺寸，无需手动计算列宽。

## 5. 遇到的问题与解决过程
### 问题 1：图片圆角与卡片圆角不匹配
#### 现象
卡片设置了整体圆角，但图片仅填充左上角/左下角，右侧圆角被截断，视觉效果不一致。
#### 原因
图片默认是矩形，未针对卡片圆角做裁剪，且 `Card` 的圆角不会自动裁剪子元素。
#### 解决过程
1. 为 `Image` 添加 `clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))` 修饰符，仅裁剪图片的左上角和左下角（与卡片左侧圆角对齐）；  
2. 确保 `Image` 的圆角半径与 `Card` 一致（8dp），保证视觉统一；  
3. 验证不同屏幕尺寸下的显示效果，确认圆角无变形。

### 问题 2：网格卡片间距不符合规格
#### 现象
网格卡片之间的间距过大/过小，且网格与屏幕边缘无间距。
#### 原因
初期仅设置了 `horizontalArrangement` 和 `verticalArrangement`，未设置网格整体内边距；且间距值错误设置为 16dp（规格要求 8dp）。
#### 解决过程
1. 在 `CoursesApp` 中调用 `TopicGrid` 时，传入 `Modifier.padding(8.dp)`，为网格添加整体内边距；  
2. 将 `horizontalArrangement` 和 `verticalArrangement` 的 `spacedBy` 参数改为 8dp；  
3. 运行应用，通过“布局检查器”（Layout Inspector）验证间距值，确认符合 8dp 规格。

### 问题 3：文字区域垂直居中失效
#### 现象
文字区域在 `Row` 中偏上，与图片高度不匹配，视觉上不协调。
#### 原因
`Column`（文字区域）未设置垂直居中对齐，默认居顶排列。
#### 解决过程
1. 为 `Column` 添加 `Modifier.align(Alignment.CenterVertically)` 修饰符，让文字区域在 `Row` 中垂直居中；  
2. 调整 `Column` 的内边距（16dp），确保文字区域上下间距对称；  
3. 预览不同卡片（如课程数量多/少的卡片），确认文字区域始终居中。

### 问题 4：LazyVerticalGrid 导入失败
#### 现象
IDE 提示无法解析 `LazyVerticalGrid` 和 `GridCells`。
#### 原因
项目 Compose 依赖版本过低，未包含 `foundation-lazy-grid` 组件。
#### 解决过程
1. 查阅 Compose 官方文档，确认 `LazyVerticalGrid` 属于 `androidx.compose.foundation:foundation-lazy-grid` 库；  
2. 在项目 `build.gradle`（Module 级别）中升级 Compose 版本至 1.2.0+（支持该组件的最低版本）；  
3. 同步项目（Sync Project with Gradle Files），验证导入成功。

### 问题 5：卡片宽度不一致
#### 现象
部分卡片宽度偏窄，两列网格列宽不统一。
#### 原因
`TopicCard` 未设置 `fillMaxWidth()`，导致卡片宽度仅包裹内容，而非占满列宽。
#### 解决过程
1. 为 `Card` 添加 `Modifier.fillMaxWidth()`，强制卡片占满所在列的宽度；  
2. 移除 `Row` 中多余的宽度限制修饰符，确保布局层级的宽度传递正常；  
3. 测试不同屏幕尺寸（手机、平板），确认列宽始终均分。