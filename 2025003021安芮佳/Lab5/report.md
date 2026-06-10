# Lab5 实验报告

## 1. 应用展示内容
本次 Art Space 应用主题为**世界经典名画展示**，共展示 3 幅作品：
1. 《蒙娜丽莎》- 列奥纳多·达·芬奇
2. 《感恩祭》- 威廉・阿道夫・布格罗
3. 《桌球》- 路易斯 - 利奥波德・布瓦伊

应用支持点击“上一个/下一个”按钮循环切换作品，图片与文字信息同步更新。

## 2. 界面结构说明
界面分为三大区块，使用 Column 垂直布局：
- 艺术作品墙：使用 Surface + Image 实现画框效果。
- 作品信息区：使用 Column + Text 展示作品名、作者、年份
- 控制按钮区：使用 Row + Button 实现左右切换

布局嵌套清晰，使用 Spacer、padding、fillMaxWidth 等实现对齐与间距。

## 3. Compose 状态管理
使用 remember + mutableStateOf 管理当前作品索引：
var currentIndex by remember { mutableStateOf(0) }
索引变化时，Compose 自动重组界面，更新图片与文本，实现状态驱动 UI。状态与界面完全分离，代码结构清晰、易于维护和扩展。

## 4. 按钮切换逻辑
- 上一个：索引为 0 时跳转到最后一幅，否则索引 -1
- 下一个：索引为最后一幅时跳转到 0，否则索引 +1
实现循环切换，不会越界。
- 采用循环切换模式，避免数组越界导致崩溃，交互更友好。

## 5. 遇到的问题与解决
1. 图片拉伸：使用固定尺寸 Surface 容器 + padding 内边距，让图片按比例居中显示。
2. 状态不更新：改用 remember + mutableStateOf 管理索引状态，确保状态可观察。
3. 文本样式混乱：使用 buildAnnotatedString 实现单 Text 多样式，精简代码结构。
4. 按钮越界崩溃：增加边界判断实现循环切换

## 6. 实验总结
本次实验通过构建 Art Space 应用，综合运用了 Compose 的布局组件（Column/Row/Box）、状态管理（remember/mutableStateOf）、样式定制（Modifier/SpanStyle）等核心知识点。通过封装 Artwork 数据类、解耦状态与 UI、优化边界逻辑，实现了一个可扩展、易维护的交互式艺术展示应用。同时，在解决图片适配、状态联动、代码冗余等问题的过程中，加深了对 Compose「状态驱动 UI」核心思想的理解。