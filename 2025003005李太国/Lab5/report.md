# Lab5 实验报告

## 应用内容

本应用展示了三幅世界著名的艺术作品：
1. 《星夜》（Starry Night）- 文森特·梵高，1889年
2. 《蒙娜丽莎》（Mona Lisa）- 列奥纳多·达·芬奇，1503年
3. 《记忆的永恒》（The Persistence of Memory）- 萨尔瓦多·达利，1931年

## 界面结构说明

界面划分为三个主要区块：

1. **艺术作品墙**：使用 `Surface` 组件创建带边框和阴影的画框效果，内部包含 `Image` 组件展示艺术作品。

2. **艺术作品说明**：使用 `Column` 组件垂直排列作品标题和艺术家信息，使用不同的字体大小和颜色区分标题和作者信息。

3. **显示控制器**：使用 `Row` 组件水平排列 "Previous" 和 "Next" 按钮，用于切换不同的艺术作品。

整体布局使用 `Column` 组件，通过 `verticalArrangement = Arrangement.SpaceBetween` 实现各区块的合理分布，并使用 `Spacer` 组件控制间距。

## 状态管理

使用 Compose 的状态管理功能 `remember` 和 `mutableStateOf` 来管理当前显示的艺术作品索引：

```kotlin
var currentArtwork by remember { mutableStateOf(1) }
```

根据当前索引，动态确定显示的图片资源、作品标题、艺术家姓名和年份：

```kotlin
val imageResource = when (currentArtwork) {
    1 -> R.drawable.artwork_1
    2 -> R.drawable.artwork_2
    else -> R.drawable.artwork_3
}
```

## 按钮逻辑

- **Previous 按钮**：当显示第一幅作品时，点击跳转到最后一幅；否则，显示前一幅作品。
- **Next 按钮**：当显示最后一幅作品时，点击跳转到第一幅；否则，显示后一幅作品。

使用 `when` 表达式实现切换逻辑，代码简洁清晰：

```kotlin
currentArtwork = when (currentArtwork) {
    1 -> 3
    else -> currentArtwork - 1
}
```

## 遇到的问题与解决过程

1. **图片资源问题**：需要准备至少3幅艺术作品图片。由于实验环境限制，代码中使用了占位符资源ID，实际运行时需要替换为真实的图片资源。

2. **布局调整**：通过多次调整 `Modifier` 的 `padding` 和 `Spacer` 的高度，确保界面元素布局合理，视觉效果良好。

3. **状态管理**：初次实现时，尝试使用多个状态变量分别管理图片、标题等信息，导致代码冗余。后来优化为使用单个索引状态，通过 `when` 表达式动态获取对应信息，代码更加简洁。

## 总结

本实验成功实现了一个具有交互功能的 Art Space 应用，展示了如何使用 Jetpack Compose 构建静态界面并添加交互功能。通过状态管理和条件逻辑，实现了艺术作品的切换功能，达到了实验目标。