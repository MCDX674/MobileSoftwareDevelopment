# Art Space 应用实验报告

## 1. 应用内容说明

本应用展示了3幅经典艺术作品：
- 《星夜》(Starry Night) - 文森特·梵高，1889年
- 《花海中的少女》(A Maiden Among a Sea of Flowers) - 无署名，2025 年
- 《呐喊》(The Scream) - 爱德华・蒙克，1893 年

## 2. 界面结构说明

### 区块划分

应用界面划分为三个主要区块：

1. **艺术作品墙**：使用 `Surface` 和 `Image` 组件，展示当前艺术作品，带有边框和阴影效果，模拟真实画框。

2. **艺术作品说明**：使用 `Column` 和 `Text` 组件，显示作品名称、艺术家和创作年份，采用不同的字体大小和颜色以突出重点。

3. **显示控制器**：使用 `Row` 和 `Button` 组件，包含 "Previous" 和 "Next" 按钮，用于切换不同的艺术作品。

### 可组合项使用

- 根布局：`Column`，设置 `fillMaxSize()`、`horizontalAlignment = Alignment.CenterHorizontally` 和 `verticalArrangement = Arrangement.SpaceBetween`
- 作品展示：`Surface` + `Image`，添加边框和阴影效果
- 作品信息：`Column` + `Text`，设置不同的字体样式
- 控制按钮：`Row` + `Button`，水平排列并设置间距
- 间距控制：使用 `Spacer` 组件控制各个区块之间的间距

## 3. 状态管理实现

使用 Compose 的 `remember` 和 `mutableStateOf()` 管理当前作品索引：

```kotlin
var currentArtwork by remember { mutableStateOf(1) }
```

通过状态变量 `currentArtwork` 控制显示的艺术作品，当状态更新时，Compose 会自动重新渲染相关的 UI 组件。

## 4. Next / Previous 按钮逻辑

### Next 按钮逻辑

```kotlin
onNext = {
    currentArtwork = when (currentArtwork) {
        3 -> 1  // 最后一幅作品时，循环到第一幅
        else -> currentArtwork + 1
    }
}
```

### Previous 按钮逻辑

```kotlin
onPrevious = {
    currentArtwork = when (currentArtwork) {
        1 -> 3  // 第一幅作品时，循环到最后一幅
        else -> currentArtwork - 1
    }
}
```

使用 `when` 表达式替代 `if-else` 链，使代码更简洁易读。当用户点击按钮时，状态变量 `currentArtwork` 会更新，从而触发 UI 的重新渲染，显示相应的艺术作品和信息。

## 5. 遇到的问题与解决过程

1. **图片资源问题**：由于没有实际的图片资源，在代码中使用了占位符资源 ID。在实际运行时，需要将相应的图片文件添加到 `res/drawable` 目录中。

2. **布局间距问题**：初始布局中各元素之间的间距不够合理，通过使用 `Spacer` 组件和调整 `padding` 参数，解决了间距问题，使界面更加美观。

3. **状态管理逻辑**：在实现循环切换逻辑时，需要注意边界情况（第一幅和最后一幅作品的处理），通过 `when` 表达式清晰地处理了这些边界情况。

4. **可组合项组织**：将界面拆分为多个可组合函数（`ArtworkWall`、`ArtworkDescriptor`、`DisplayController`），使代码结构更清晰，便于维护和扩展。

## 6. 运行效果

应用运行时，用户可以通过点击 "Previous" 和 "Next" 按钮切换不同的艺术作品，界面会实时更新显示相应的图片和信息。整体布局简洁美观，交互流畅。

## 7. 总结

本次实验成功实现了一个功能完整的 Art Space 应用，综合运用了 Jetpack Compose 的核心概念和组件：

- 使用 `Column` 和 `Row` 构建多区块布局
- 使用 `Modifier` 自定义组件样式和间距
- 使用 `remember` 和 `mutableStateOf()` 管理界面状态
- 使用 `when` 表达式处理多分支条件逻辑
- 使用 `Surface` 组件创建带边框和阴影的画框效果

通过本次实验，我进一步掌握了 Jetpack Compose 的使用方法，能够独立构建具有基本交互功能的 Android 应用。