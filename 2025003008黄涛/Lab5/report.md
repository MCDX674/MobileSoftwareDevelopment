# Lab5 Art Space 应用实验报告

**实验环境**：Android Studio | Kotlin | Jetpack Compose

**实验目标**：基于 Jetpack Compose 实现艺术画廊作品轮播展示应用，掌握 Compose 基础布局、组件使用、状态管理与交互逻辑开发

## 一、应用功能与展示内容

### 1. 应用定位与主题

本应用是基于 **Android Jetpack Compose** 开发的轻量化数字艺术画廊展示应用，核心为用户提供简洁、流畅的艺术作品轮播浏览体验，聚焦基础 UI 搭建与状态交互实现。

### 2. 核心展示内容

应用内置 **3 幅艺术作品**，每幅作品统一展示四类信息：

- 艺术作品图片（带画框视觉效果）
- 作品标题
- 艺术家名称
- 创作年份

支持**无限循环切换**作品，无边界异常，交互稳定。

## 二、界面结构与组件设计

### 1. 界面模块化划分

遵循**单一职责原则**，将界面拆分为三大独立功能区块，结构清晰、便于维护：

1. 艺术作品展示区：核心区域，模拟实体画框展示作品图片
2. 作品信息描述区：文本区域，展示当前作品的标题、作者、年份
3. 交互控制区：操作区域，包含上一张 / 下一张切换按钮

### 2. 核心可组合项

- 布局组件：`Column`、`Row`、`Surface`
- 展示组件：`Image`、`Text`
- 交互组件：`Button`
- 状态组件：`remember`、`mutableStateOf`

### 3. 组件嵌套层级

```
MainActivity（应用入口）
  ↓
MaterialTheme + Surface（全局主题与背景）
  ↓
ArtSpaceApp（主界面容器）
  ↓
Column（根垂直布局）
  ├─ ArtworkWall（作品图片组件）→ Surface + Image
  ├─ ArtworkDescriptor（信息文本组件）→ Column + Text
  └─ DisplayController（切换按钮组件）→ Row + Button
```

### 4. 布局优化特性

- 根布局 `Column` 全屏展示，设置统一内边距，子组件水平居中、垂直均匀分布
- 按钮区域通过 `Arrangement.spacedBy` 设置间距，固定按钮宽度，保证布局美观统一
- 作品区使用 `Surface` 实现边框 + 阴影效果，模拟实体画框，提升视觉质感

## 三、Compose 状态管理实现

本应用采用**索引状态管理**模式，实现**数据驱动界面自动更新**。

### 1. 状态变量定义

```
var currentArtworkIndex by remember { mutableStateOf(0) }
```

- `remember`：保留状态数据，屏幕旋转等配置变更时状态不丢失
- `mutableStateOf()`：创建可观察状态，状态变化时自动触发界面重组
- `by` 关键字：简化状态读写，无需手动调用 `.value`

### 2. 状态使用逻辑

```
val currentArtwork = artworkList[currentArtworkIndex]
```

索引变化 → 自动重组 UI → 同步更新图片、标题、作者等所有内容。

## 四、作品切换逻辑（循环无越界）

### 1. 上一张（Previous）逻辑

```
currentArtworkIndex = when (currentArtworkIndex) {
    0 -> artworkList.size - 1
    else -> currentArtworkIndex - 1
}
```

### 2. 下一张（Next）逻辑

```
currentArtworkIndex = when (currentArtworkIndex) {
    artworkList.size - 1 -> 0
    else -> currentArtworkIndex + 1
}
```

### 3. 实现效果

无限循环切换作品，操作流畅，无卡顿、无崩溃，符合用户使用习惯。

## 五、问题排查与解决方案

|            问题现象            |                       根本原因                       |                       解决方案                       |
| :----------------------------: | :--------------------------------------------------: | :--------------------------------------------------: |
| 点击按钮界面无刷新、作品不切换 | 未使用 `remember` 包裹状态，Compose 无法监听状态变化 | 使用 `remember { mutableStateOf(0) }` 定义可观察状态 |
|  按钮点击应用崩溃（数组越界）  |             无边界判断，索引超出列表长度             |      用 `when` 表达式判断首尾索引，实现循环切换      |
|    作品图片无画框，界面单调    |           直接使用 Image 组件，无视觉装饰            |       嵌套 Surface，添加边框、阴影实现画框效果       |
|      按钮拥挤、样式不统一      |                  无间距、自适应宽度                  |         Row 设置间距，按钮固定宽度，统一样式         |

## 六、实验总结与收获

### 1. 完成目标

成功实现 Art Space 艺术画廊应用，满足**界面展示 + 交互功能**全部验收标准：

- 3 幅艺术作品循环轮播
- 画框样式图片展示
- 作品信息完整呈现
- 稳定无崩溃的切换操作

### 2. 核心技术掌握

1. 熟练使用 `Column`/`Row`/`Image`/`Button` 等 Compose 基础组件构建 UI
2. 理解 Compose **声明式 UI** 思想，掌握 `remember + mutableStateOf` 状态管理核心用法
3. 学会通过条件判断处理边界问题，提升应用健壮性
4. 掌握**组件模块化拆分**思想，优化代码结构，提高可维护性
5. 能够独立排查并解决 Compose 开发中的常见问题

### 3. 实验价值

本次实验夯实了 Jetpack Compose 基础开发能力，为后续复杂 Android 应用开发奠定了坚实的 UI 构建与状态管理基础。