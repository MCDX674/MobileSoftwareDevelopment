# ArtSpace 艺术作品展示应用 实验报告

## 一、应用展示内容
应用主题：基于 Jetpack Compose 开发的艺术作品展示应用，实现作品浏览与切换功能
作品数量：内置 3 幅 艺术作品
展示信息：作品图片、作品名称、艺术家姓名、创作年份
核心功能：支持 Previous / Next 按钮循环切换作品

## 二、界面结构说明
## 1. 界面区块划分
艺术作品展示区：显示作品图片
作品信息描述区：显示标题、艺术家、创作年份
交互控制区：包含切换作品的两个按钮

## 2. 使用的可组合项
布局类：Column、Row、Surface、Spacer
展示类：Image、Text
交互类：Button

## 3. 布局嵌套结构
根布局：使用 Column 垂直排列三大区块，设置居中对齐与间距
作品展示区：使用 Surface 实现卡片阴影效果，内部嵌套 Image 展示作品
作品信息区：使用 Column 垂直排列文本，使用 Spacer 控制间距
按钮控制区：使用 Row 水平排列两个按钮，左右分布，样式统一

## 三、Compose 状态管理
使用 Compose 状态管理当前作品索引
状态变量定义：
```kotlin
var currentIndex by remember { mutableIntStateOf(0) }
```

remember：保证界面重组时状态不丢失
mutableIntStateOf：监听索引变化，自动刷新界面
所有 UI 元素根据索引动态更新

## 四、Next / Previous 按钮逻辑
1. Next 按钮逻辑
```onNext = {
currentIndex = if (currentIndex == artworks.size - 1) 0 else currentIndex + 1
}
```
功能：切换到下一幅作品
边界处理：到达最后一幅时，循环回到第一幅

2. Previous 按钮逻辑
```onPrevious = {
currentIndex = if (currentIndex == 0) artworks.size - 1 else currentIndex - 1
}
```
功能：切换到上一幅作品
边界处理：到达第一幅时，循环回到最后一幅

## 五、遇到的问题与解决过程
1. 包名与文件路径不匹配
问题：Package directive does not match the file location
原因：包名声明与项目结构不一致
解决：修改 package 声明与项目包名一致

2. 图片资源找不到
问题：R.drawable 标红报错
原因：未导入 R 类 或 图片命名不规范
解决：导入正确的 R 类；图片命名为 artwork_1/2/3；放入 res/drawable 目录

3. 图片大小不一致
问题：原图尺寸不同，界面显示不统一
原因：未固定图片显示区域
解决：给 Surface 设置固定高度；给 Image 添加 ContentScale.Fit 保持比例不变形

4. 状态变量警告
问题：提示 Prefer mutableIntStateOf
原因：Compose 推荐使用专用数值状态优化
解决：将 mutableStateOf 替换为 mutableIntStateOf

## 六、实验总结
完成 Jetpack Compose 静态界面搭建，熟练使用 Column、Row、Surface 布局，实现卡片、文本、按钮等界面元素
完成交互功能开发，使用状态管理实现作品切换，实现循环切换逻辑
解决开发中常见问题，包括资源引用、界面适配、状态优化等
应用效果：界面美观、运行稳定、功能完整