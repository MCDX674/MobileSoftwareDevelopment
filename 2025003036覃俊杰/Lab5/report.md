# Lab5 Art Space 艺术空间应用实验报告

## 一、实验目的

1. 掌握 Jetpack Compose 基础布局（Column、Row、Spacer）的使用方法。
2. 学会使用 remember + mutableStateOf 实现 Compose 状态管理。
3. 实现图片展示、文本信息显示、按钮点击切换功能。
4. 完成模块化组件拆分，提升代码可读性与复用性。

## 二、应用功能

1. 展示 3 幅艺术作品：星月夜、蒙娜丽莎、向日葵。
2. 显示每幅作品的标题、艺术家、创作年份。
3. 提供 Previous / Next 按钮，实现作品循环切换。
4. 图片带边框与阴影效果，界面美观规范。

## 三、界面结构

整体使用 **Column 垂直布局**，分为三个部分：

1. **作品展示区**：使用 Image + Surface 实现带边框的图片展示。
2. **信息描述区**：使用 Text 显示作品名称、作者、年份。
3. **控制按钮区**：使用 Row 横向排列两个切换按钮。

## 四、核心技术实现

1. **状态管理**

   使用 `var currentArtwork by remember { mutableStateOf(1) }` 记录当前作品索引。状态改变时，界面自动重组刷新内容。

2. **资源动态加载**

   通过 when 表达式根据当前索引，动态加载对应图片与文本信息。

3. **按钮逻辑**

   - Next：1 → 2 → 3 → 1 循环
   - Previous：1 → 3 → 2 → 1 循环

4. **界面美化**

   使用 Surface 实现边框、阴影效果；使用 padding、spacer 优化间距。

## 五、代码结构

1. MainActivity：程序入口，加载主界面。
2. ArtSpaceApp：主界面组件，管理状态与整体布局。
3. ArtworkWall：图片展示组件。
4. ArtworkDescriptor：作品信息展示组件。
5. DisplayController：按钮控制组件。

## 六、遇到的问题与解决方法

1. **问题**：Surface 导入冲突，编译报错。

   解决方法：删除 foundation 包下的 Surface，保留 material3 版本。

2. **问题**：elevation 参数不兼容。

   解决方法：使用 shadowElevation 替代旧版 elevation。

3. **问题**：包名路径不匹配。

   解决方法：保持文件路径与 package 声明一致。

## 七、实验总结

本次实验完成了 Art Space 艺术作品展示应用，熟练掌握了 Compose 基础布局、状态管理、组件拆分、点击事件等核心技能，能够独立开发简单的图片展示类应用。