# Lab5 实验报告

## 应用内容

本应用用于展示三幅经典艺术作品，用户可通过按钮切换不同作品，同步查看作品名称、作者与创作年份，实现轻量化、可交互的艺术作品浏览功能。

## 界面结构说明

界面划分为三个主要区块：

1. **艺术作品墙**：使用 `Surface` 组件创建带边框和阴影的画框效果，内部包含 `Image` 组件展示艺术作品。

2. **艺术作品说明**：使用 `Column` 组件垂直排列作品标题和艺术家信息，使用不同的字体大小和颜色区分标题和作者信息。

3. **显示控制器**：使用 `Row` 组件水平排列 "Previous" 和 "Next" 按钮，用于切换不同的艺术作品。

整体采用 Column 垂直布局，通过 verticalArrangement = Arrangement.SpaceBetween 实现区块均匀分布，并使用 Spacer 控制组件间距。
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

1. **画框效果实现**：Material3 中 elevation 已不推荐使用，改用 Modifier.shadow 实现阴影，搭配 BorderStroke 实现边框。

2. **界面布局优化**：通过多次调整 padding 与 Spacer 高度，使界面元素分布更合理、视觉更舒适。

3. **状态管理优化**：从多状态变量改为单一索引状态，通过 when 统一匹配信息，代码更简洁、易维护。

## 总结

本实验基于 Jetpack Compose 完成 Art Space 应用开发，实现了艺术作品展示与循环切换功能。通过本次实验，掌握了 Compose 基础布局、状态管理、界面样式与交互逻辑的实现方法，能够独立完成从界面设计到代码实现的完整流程，达到实验要求。