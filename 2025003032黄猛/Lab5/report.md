# Lab5 实验报告：Art Space 应用

## 1. 应用展示内容



本应用主题为数字艺术空间展示，一共展示 3 幅作品。

- 作品 1：laude Monet, Water Lilies, 1916: Soft blended hues veil the light on water, representing the ultimate expression of Impressionist philosophy of light and shadow.
- 作品 2：Pablo Picasso, Les Demoiselles d’Avignon, 1907: Fragmented and distorted forms marked the birth of Cubism, revolutionizing traditional painting entirely.
- 作品 3：Van Gogh, The Starry Night, 1889: Swirling nebulae surge and flow, embodying the blazing romance and wild passion within Van Gogh’s inner world.

用户可以通过 Previous 和 Next 按钮循环切换当前作品，图片和文字会同步变化。

## 2. 界面结构说明

界面按作业建议划分为 3 个区块，采用 Column 垂直组织：

1. 艺术作品墙
- 使用 Surface + Image 显示当前作品
- Surface 添加 border 与 shadowElevation，形成画框和悬浮感

2. 艺术作品说明
- 使用 Surface + Column 显示标题、作者和年份
- 作者使用加粗样式，年份使用普通样式

3. 显示控制器
- 使用 Row 放置 Previous 和 Next 两个 Button
- 两个按钮宽度统一，间距通过 Row 的 SpaceBetween 控制

整体布局通过 padding、Spacer、fillMaxWidth 等 Modifier 控制间距与对齐。

## 3. Compose 状态管理方式

应用通过 Compose 状态保存当前作品索引：

```kotlin
var currentArtworkIndex by remember { mutableStateOf(0) }
```

状态驱动流程如下：

- currentArtworkIndex 变化
- currentArtwork = artworks[currentArtworkIndex] 随之变化
- ArtworkWall 与 ArtworkDescription 重新组合
- 页面显示新的图片、标题、作者和年份

因此界面更新完全由状态驱动，不需要手动刷新。

## 4. Next / Previous 按钮逻辑说明

按钮逻辑使用 when 表达式实现“首尾循环”：

Previous：

```kotlin
currentArtworkIndex = when (currentArtworkIndex) {
    0 -> artworks.lastIndex
    else -> currentArtworkIndex - 1
}
```

Next：

```kotlin
currentArtworkIndex = when (currentArtworkIndex) {
    artworks.lastIndex -> 0
    else -> currentArtworkIndex + 1
}
```

这样可以保证：

- 在第一幅点击 Previous 会跳到最后一幅
- 在最后一幅点击 Next 会跳回第一幅

## 5. 遇到的问题与解决过程

问题 1：如何让图片、标题、作者、年份同时切换。

- 解决：把图片资源和文本信息放入 Artwork 数据结构中，只维护一个 currentArtworkIndex 状态。
- 效果：索引变化时，四项内容自动同步更新。

问题 2：界面层次不明显。

- 解决：使用 Surface 并加入边框、阴影和内边距，区分“作品墙”和“说明区”。
- 效果：结构更接近 Art Space 示例，视觉层次更清晰。

问题 3：按钮在不同屏幕宽度下容易不对齐。

- 解决：统一按钮宽度并使用 Row + SpaceBetween。
- 效果：按钮布局稳定，手机屏幕上显示整齐。

## 6. 提交说明

本次按要求提交：

- MainActivity.kt
- report.md
- screenshot.png  
