# Lab5 ArtSpace 实验报告

## 1. 应用功能说明
本应用实现了一个艺术作品展示应用，可通过Previous/Next按钮切换展示3幅作品，同时显示作品标题、艺术家及创作年份。

## 2. 界面结构说明
整体使用Column垂直布局，分为三个模块：
- 作品展示区：使用Surface+Image实现带阴影的画框效果，突出展示图片
- 作品信息区：使用Column+Text展示作品标题和作者年份
- 控制按钮区：使用Row布局放置Previous和Next按钮，实现循环切换

## 3. 状态管理实现
使用`remember { mutableStateOf(1) }`保存当前作品序号，点击按钮时更新状态，Compose自动触发界面重组，实现图片和文字的同步切换。

## 4. 核心逻辑说明
通过when表达式，根据当前序号匹配对应的图片、标题和作者年份；按钮点击时实现序号的循环切换（到边界自动回到另一端），实现无限轮播效果。

## 5. 遇到的问题与解决
1. 图片不显示：检查drawable文件夹中的图片文件名与代码一致
2. 界面排版混乱：使用Modifier.weight+padding控制布局间距，搭配Arrangement.SpaceBetween实现模块分布
3. 状态不更新：确保使用mutableStateOf管理状态，让Compose能感知状态变化并重组界面