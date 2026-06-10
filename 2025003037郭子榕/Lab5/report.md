# Lab5 实验报告：Art Space 应用

## 1. 应用内容说明

本应用实现了一个数字艺术展示空间（Art Space），用于展示多幅艺术作品，并支持用户通过按钮切换不同作品。

应用共展示了 **3 幅艺术作品**，每幅作品包含以下信息：

- 图片（艺术作品本身）
- 作品名称（Title）
- 艺术家（Artist）
- 创作年份（Year）

用户可以通过界面下方的 **Previous** 和 **Next** 按钮，在不同作品之间循环切换，实现基本的浏览功能。

------

## 2. 界面结构说明

整个界面采用 Jetpack Compose 构建，使用 `Column` 作为顶层布局，将界面划分为三个主要区块：

### （1）艺术作品展示区（Artwork Wall）

- 使用 `Surface` + `Image` 组件实现
- `Surface` 提供背景、阴影（shadow）和圆角效果，用于模拟画框
- `Image` 用于展示当前艺术作品
- 使用 `Modifier.border` 增强视觉层次

结构：

- `Surface`
    - `Box`
        - `Image`

------

### （2）作品信息区（Artwork Descriptor）

- 使用 `Surface` + `Column` + `Text` 组件实现
- 展示作品名称、艺术家及年份
- 使用 `buildAnnotatedString` 实现部分文字加粗（艺术家名）

结构：

- `Surface`
    - `Column`
        - `Text`（标题）
        - `Text`（作者 + 年份）

------

### （3）控制区（Display Controller）

- 使用 `Row` + `Button` 组件
- 包含两个按钮：
    - Previous（上一幅）
    - Next（下一幅）
- 使用 `Arrangement.SpaceEvenly` 均匀分布按钮

结构：

- `Row`
    - `Button`
    - `Button`

------

## 3. 状态管理说明

本应用使用 Compose 提供的状态管理机制：

```kotlin
var currentArtwork by remember { mutableStateOf(1) }
```

说明：

- `currentArtwork` 表示当前显示的作品索引（1~3）
- 使用 `remember` 保证状态在重组（recomposition）时不会丢失
- 使用 `mutableStateOf` 使状态具备响应式能力

界面中的所有动态内容（图片、标题、作者、年份）均通过 `when` 表达式根据该状态动态计算，例如：

```kotlin
val imageRes = when (currentArtwork) {
    1 -> R.drawable.artwork_1
    2 -> R.drawable.artwork_2
    else -> R.drawable.artwork_3
}
```

当 `currentArtwork` 改变时，Compose 会自动触发 UI 重组，从而更新界面。

------

## 4. 按钮逻辑说明

### （1）Next 按钮逻辑

```kotlin
currentArtwork = when (currentArtwork) {
    3 -> 1
    else -> currentArtwork + 1
}
```

逻辑说明：

- 若当前是第 3 幅作品，则跳转到第 1 幅（形成循环）
- 否则索引 +1

------

### （2）Previous 按钮逻辑

```kotlin
currentArtwork = when (currentArtwork) {
    1 -> 3
    else -> currentArtwork - 1
}
```

逻辑说明：

- 若当前是第 1 幅作品，则跳转到第 3 幅
- 否则索引 -1

------

### （3）总结

通过 `when` 表达式实现多分支判断，使逻辑清晰且可读性较高。同时实现了**循环切换（环形列表）**的效果。

------

## 5. 遇到的问题与解决过程

### 问题 1：界面布局不居中 / 间距混乱

**原因：**

- 未正确使用 `Arrangement.SpaceBetween` 或 `Spacer`

**解决方法：**

- 使用 `Column` 的 `verticalArrangement = Arrangement.SpaceBetween`
- 使用 `Spacer` 控制区块之间的间距，使布局更清晰

------

### 问题 2：按钮点击后界面不更新

**原因：**

- 未使用 Compose 状态（写死变量）

**解决方法：**

- 使用 `remember { mutableStateOf() }` 定义状态变量
- 所有 UI 数据改为依赖状态计算

------

### 问题 3：图片不显示

**原因：**

- 图片未放入 `res/drawable` 目录
- 或资源名称不匹配

**解决方法：**

- 确认图片放在 `drawable` 目录
- 文件名使用小写字母和下划线（如 `artwork_1.png`）

------

### 问题 4：文本样式无法局部加粗

**原因：**

- 使用普通 `Text` 无法实现部分样式变化

**解决方法：**

- 使用 `buildAnnotatedString` + `SpanStyle` 实现富文本样式

------

## 6. 实验总结

通过本次实验，我掌握了以下关键内容：

- 使用 Jetpack Compose 构建多区块 UI 布局
- 使用 `Column` 和 `Row` 进行层级划分
- 使用 `Modifier` 控制样式、间距和尺寸
- 使用 `remember` 和 `mutableStateOf` 管理状态
- 使用 `when` 表达式实现多分支逻辑
- 实现 UI 与状态联动（响应式界面）

本实验将界面设计（低保真原型）成功转化为代码，并实现了基本的用户交互功能，为后续更复杂的 Compose 应用开发打下了基础。

------
