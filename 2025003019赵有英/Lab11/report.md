# Lab11：为 Sports 应用添加大屏自适应布局
**学号**：2025003019  
**姓名**：赵有英 
**实验日期**：2026年5月21日

---

## 一、实验目的
1.  掌握 Jetpack Compose 中 `WindowSizeClass` 的使用方法，实现基于窗口尺寸的自适应布局
2.  理解 Material 3 规范中的"列表-详情"（List-Detail）布局模式
3.  学会根据屏幕尺寸动态切换单窗格/双窗格布局
4.  掌握不同布局模式下的导航栏（TopAppBar）和返回键行为处理
5.  提升应用在不同尺寸设备上的用户体验

---

## 二、实验环境
- **Android Studio 版本**：Iguana | 2023.2.1 Patch 2
- **Gradle 版本**：8.11.1
- **Compose 版本**：1.6.8
- **测试设备**：
  - 小屏：Pixel 6 模拟器（API 33）
  - 大屏：840dp宽度自适应模拟器（API 33）

---

## 三、核心概念说明
### 1. WindowSizeClass 简介
`WindowSizeClass` 是 Jetpack Compose 提供的标准化窗口尺寸分类工具，它将设备窗口尺寸划分为不同类别，避免开发者直接处理像素值，大幅简化了自适应布局开发。

`WindowWidthSizeClass` 提供了三种宽度类别：
| 宽度类别 | 典型设备 | 宽度范围 | 布局建议 |
|---------|---------|---------|---------|
| Compact（紧凑） | 竖屏手机 | < 600dp | 单窗格布局 |
| Medium（中等） | 横屏手机、折叠屏内屏 | 600dp ≤ 宽度 < 840dp | 单窗格布局 |
| Expanded（展开） | 平板、桌面端 | ≥ 840dp | 双窗格列表-详情布局 |

### 2. SportsContentType 枚举设计思路
`SportsContentType` 枚举定义了两种内容展示类型：
- `ListOnly`：仅显示列表或详情中的一个，适用于小屏和中屏设备
- `ListAndDetail`：同时显示列表和详情，适用于大屏设备

使用枚举的好处是：
- 将布局逻辑与尺寸判断解耦，代码更清晰
- 便于后续扩展更多布局类型
- 统一管理不同尺寸下的内容展示策略

---

## 四、实现过程与设计说明
### 1. 窗口尺寸检测
在 `MainActivity` 中调用 `calculateWindowSizeClass()` 获取窗口尺寸类别，并将宽度尺寸传递给 `SportsApp` 根组件。使用 `@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)` 注解处理实验性API。

### 2. 自适应布局切换
在 `SportsApp` 中根据 `windowWidthSizeClass` 判断内容类型：
- 当宽度为 `Expanded` 时，使用 `SportsListAndDetail` 双窗格布局
- 其他情况使用原有的单窗格布局

### 3. SportsListAndDetail 布局设计
使用 `Row` 布局实现左右分栏：
- 左侧 `SportsList` 占 `1f` 权重（约1/3宽度），显示运动列表
- 右侧 `SportsDetail` 占 `2f` 权重（约2/3宽度），显示选中运动的详情

比例分配理由：
- 列表只需要显示缩略图和简短信息，不需要太宽
- 详情页包含大量文字内容，需要更宽的空间保证可读性
- 1:2的比例符合Material 3列表-详情布局的最佳实践

### 4. TopAppBar 行为适配
- **大屏模式**：始终显示"Sports"标题，不显示返回按钮（因为列表始终可见，无需返回）
- **小屏模式**：列表页显示"Sports"，详情页显示"Sport Info"并显示返回按钮

### 5. 返回键行为处理
- **小屏模式**：在 `SportsDetail` 中使用 `BackHandler`，按返回键回到列表页
- **大屏模式**：在 `SportsListAndDetail` 中使用 `BackHandler`，按返回键直接退出应用（因为用户已经在主界面）

---

## 五、实验中遇到的问题与解决过程
1.  **Unresolved reference 'Icons' 错误**
    - 问题：使用 `Icons.AutoMirrored.Filled.ArrowBack` 时提示找不到符号
    - 解决：在 `app/build.gradle.kts` 中添加 `material-icons-extended` 依赖，并导入 `androidx.compose.material.icons.Icons`

2.  **模拟器无法拉伸为大屏**
    - 问题：模拟器被锁定为固定手机尺寸，无法自由调整窗口大小
    - 解决：直接运行 `SportsAppExpandedPreview` 预览，Android Studio会自动将模拟器调整为840dp宽度的大屏尺寸

3.  **IDE 缓存导致的假报错**
    - 问题：代码正确但IDE仍显示"Cannot resolve symbol 'uiState'"
    - 解决：添加依赖并同步后，IDE自动重新索引代码，错误自动消失

---

## 六、实验总结
通过本次实验，我掌握了Jetpack Compose中基于`WindowSizeClass`的自适应布局开发方法。实现了Sports应用在小屏和大屏设备上的不同展示效果：小屏使用单窗格导航，大屏使用列表-详情双窗格布局模式。

本次实验让我理解了自适应布局的核心思想：不是为不同设备写不同的代码，而是根据窗口尺寸动态调整内容的展示方式。同时也学会了如何处理不同布局模式下的导航和返回键行为，提升了应用在多设备上的用户体验。