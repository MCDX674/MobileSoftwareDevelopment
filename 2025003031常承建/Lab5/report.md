# Lab5：创建 Art Space 应用实验报告

## 一、应用展示内容
本应用为 Art Space 艺术作品展示应用，共包含3幅艺术作品：
1. 蒙娜丽莎 - 达·芬奇 - 1503-1519
2. 向日葵 - 梵高 - 1888
3. 星空 - 梵高 - 1889

用户可通过 Previous 和 Next 按钮切换作品，图片、标题、作者、年份同步更新。

## 二、界面结构说明
应用采用 Column 垂直布局，分为三个区域：

1. 作品展示区
   - 使用 Surface + Image 组件
   - Surface 提供阴影画框效果
   - Image 显示对应艺术作品

2. 作品信息区
   - 使用 Column 包含两个 Text
   - 上方为作品名称（加粗、大号字体）
   - 下方为作者与年份

3. 按钮控制区
   - 使用 Row 横向排列
   - Previous、Next 两个按钮
   - 实现作品切换功能

## 三、状态管理实现
使用 Compose 状态机制记录当前作品索引：
```kotlin
var currentArt by remember { mutableStateOf(1) }