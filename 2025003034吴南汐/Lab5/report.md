# 名画介绍应用 实验报告

## 一、应用内容说明
- **主题**：名画轮播展示，支持切换查看不同画作信息。
- **作品数量**：共展示 3 幅经典艺术作品，包括：
  1. 《Lady in the Garden》- Abbott Fuller Graves（20世纪初）
  2. 《Woman with a Parasol – Madame Monet and Her Son》- Claude Monet（1875）
  3. 《The Milkmaid》- Johannes Vermeer（1658–1660）

---

## 二、界面结构说明
### 1. 区块1：作品展示区
- 可组合项类型：`Surface` 嵌套 `Image`
- 作用：展示当前艺术作品图片，模拟画框效果。
- 关键配置：
  - 使用 `Surface` 作为画框容器，设置长方形尺寸（`width=360.dp, height=480.dp`），圆角 `28.dp`、浅灰色边框 `1.dp` 和柔和阴影。
  - 内部嵌套 `Image`，加载 drawable 资源中的图片，并设置内边距 `24.dp`，让图片与画框之间形成留白。

### 2. 区块2：作品说明区
- 可组合项类型：`Column` 嵌套两个 `Text`
- 作用：展示作品的标题、作者和创作年份信息。
- 关键配置：
  - 使用 `Column` 垂直排列两个 `Text`，标题设置为加粗大号字体，作者年份信息设置为灰色小号字体。
  - 设置 `horizontalAlignment = Alignment.CenterHorizontally`，让文字居中对齐。

### 3. 区块3：控制区
- 可组合项类型：`Row` 嵌套两个 `Button`
- 作用：提供“上一个/下一个”按钮，用于切换作品。
- 关键配置：
  - 使用 `Row` 水平排列两个 `Button`，按钮之间通过 `Spacer` 设置间距。
  - 按钮设置固定宽高（`width=120.dp, height=50.dp`），保证界面一致性。

---

## 三、Compose 状态管理说明
本应用通过 **`remember` + `mutableStateOf`** 实现作品索引的状态管理，核心逻辑如下：

1. **状态定义**：在 `ArtSpaceApp` 函数中，使用 `var currentArtworkIndex by remember { mutableStateOf(0) }` 定义当前作品索引的状态变量。
   - `mutableStateOf(0)`：创建一个可观察的状态对象，初始值为 `0`（表示显示列表中第一个作品）。
   - `by remember`：将状态对象委托给变量，并通过 `remember` 确保状态在重组时不会被重置。

2. **状态消费**：
   - 通过 `val currentArtwork = artworkList[currentArtworkIndex]`，根据当前索引获取列表中对应的作品数据。
   - 将 `currentArtwork` 传递给 `ArtworkWall` 和 `ArtworkDescriptor`，实现界面随状态自动更新。

3. **状态更新**：
   - 点击按钮时，通过回调函数修改 `currentArtworkIndex` 的值，触发 Compose 的重组流程，自动刷新界面上的作品展示和说明信息。

---

## 四、Next/Previous 按钮条件逻辑说明
两个按钮通过回调函数修改 `currentArtworkIndex`，并实现循环轮播效果，逻辑如下：

### 1. Previous（上一个）按钮逻辑
```kotlin
onPrevious = {
    currentArtworkIndex = if (currentArtworkIndex == 0) {
        artworkList.size - 1 // 索引为0时，跳转到最后一个作品
    } else {
        currentArtworkIndex - 1 // 索引减1，显示上一个作品
    }
}
```
- 当当前索引为 `0`（第一个作品）时，点击按钮会跳转到列表最后一个作品，实现循环效果。
- 其他情况下，索引减 1，显示前一个作品。

### 2. Next（下一个）按钮逻辑
```kotlin
onNext = {
    currentArtworkIndex = if (currentArtworkIndex == artworkList.size - 1) {
        0 // 索引为最后一个时，跳转到第一个作品
    } else {
        currentArtworkIndex + 1 // 索引加1，显示下一个作品
    }
}
```
- 当当前索引为 `artworkList.size - 1`（最后一个作品）时，点击按钮会跳转到列表第一个作品，实现循环效果。
- 其他情况下，索引加 1，显示下一个作品。

---

## 五、遇到的问题与解决过程
### 1. 问题1：图片展示区域为正方形，不够贴合画作比例
- **问题描述**：最初使用 `Modifier.size(300.dp)` 设置图片容器，导致所有画作都被强制显示为正方形，画面被压缩或裁剪，视觉效果差。
- **解决过程**：将 `size` 替换为 `width(360.dp).height(480.dp)`，改为竖版长方形容器，同时调整 `Image` 的内边距，让画作以更自然的比例展示。

### 2. 问题2：画框边框生硬，不够美观
- **问题描述**：最初使用 `BorderStroke(2.dp, Color.Gray)` 和 `RoundedCornerShape(16.dp)`，边框过粗、圆角过小，整体风格不够柔和。
- **解决过程**：将边框调整为 `BorderStroke(1.dp, Color.LightGray)`，圆角改为 `RoundedCornerShape(28.dp)`，同时调整 `shadowElevation` 为 `10.dp`，让画框更具质感和柔和度。

### 3. 问题3：状态变量修改后界面不更新
- **问题描述**：直接使用 `var currentArtworkIndex = 0` 定义索引变量，点击按钮修改值后，界面上的作品不会自动刷新。
- **解决过程**：改用 `var currentArtworkIndex by remember { mutableStateOf(0) }`，利用 Compose 的状态管理机制，让状态变化自动触发界面重组。

---

## 实验总结
本实验通过 Jetpack Compose 实现了一个功能完整的艺术作品浏览应用，掌握了以下核心知识点：
1. Compose 布局的组合与嵌套（`Column`/`Row`/`Surface` 等可组合项的使用）。
2. Compose 状态管理的基本用法（`remember` + `mutableStateOf`）。
3. 条件逻辑在交互中的应用（循环轮播的边界处理）。
4. 界面美化的常用技巧（圆角、边框、阴影、留白的设置）。

应用实现了预期的功能，界面交互流畅，视觉效果符合设计要求，完成了本次实验的目标。
