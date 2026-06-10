# Lab5：创建 Art Space 应用 实验报告

## 一、应用展示内容
本次开发的 Art Space 数字艺术空间应用以自然风光摄影为主题，一共展示 3 幅艺术作品，分别为：樱花风景、日落西山、城市风光。应用通过 Previous 和 Next 按钮实现作品的循环切换，切换时会同步更新图片、作品标题、创作者以及创作年份，界面所有内容均居中展示，交互与布局均满足实验要求。

## 二、界面结构说明
应用按照实验要求划分为三大逻辑区块，整体使用 Column 垂直布局，所有元素均水平且垂直居中。

### 1. 艺术作品墙区块
使用 Surface 与 Image 组件实现。Surface 提供带阴影的白色画框效果，内部 Image 用于展示艺术图片，并通过 contentScale.Fit 保证图片不变形，配合内边距实现画框留白效果，整体居中展示。

### 2. 艺术作品说明区块
由 Surface、Column、Text 组合实现。外层为浅灰色背景卡片，内部使用 Column 垂直排列两行文本，分别显示作品标题与作者年份信息，文本全部水平居中，层次清晰、样式统一。

### 3. 显示控制器区块
使用 Row 水平排列 Previous、Next 两个 Button 按钮，按钮之间设置间距，整体居中布局，用于控制作品切换。

整体根布局 Column 设置 horizontalAlignment = Alignment.CenterHorizontally 与 verticalArrangement = Arrangement.Center，实现全局居中，完全符合实验布局结构要求。

## 三、Compose 状态管理实现
应用使用 Jetpack Compose 状态管理驱动界面更新：
1. 定义 Artwork 数据类，封装图片资源、标题、作者、年份信息，并使用 List 保存 3 幅作品数据。
2. 使用 remember { mutableIntStateOf(0) } 创建 current 状态变量，记录当前显示作品的索引，初始值为 0。
3. 界面中的图片、文字均通过 current 索引从列表中动态获取，不使用硬编码。
4. 按钮点击修改 current 值后，Compose 自动重组界面，完成内容刷新，符合实验要求的 remember 与 mutableStateOf 使用规范。

## 四、Next / Previous 按钮条件逻辑说明
按钮实现首尾循环切换，逻辑如下：

### Previous 按钮
- 当前索引大于 0 时，索引减 1，切换到上一幅作品；
- 当前为第一幅作品时，直接跳转到最后一幅作品。

### Next 按钮
- 当前索引未到达最后一幅时，索引加 1，切换到下一幅作品；
- 当前为最后一幅作品时，跳转到第一幅作品。

该逻辑满足实验要求的多分支条件判断与循环切换规则。

## 五、遇到的问题与解决过程
1. 界面元素无法全局居中
   问题：仅水平居中，垂直方向不居中。
   解决：为 Column 设置 verticalArrangement = Arrangement.Center，实现全局居中。

2. 图片没有画框与阴影效果
   问题：直接使用 Image 组件过于单调，无实验要求的画框效果。
   解决：使用 Surface 包裹 Image，添加 shadow 与 padding，实现画框样式。

3. 作品切换无法循环
   问题：到达首尾作品后按钮失效。
   解决：添加边界条件判断，实现首尾循环跳转。

4. 图片拉伸变形
   问题：图片尺寸不匹配导致拉伸。
   解决：设置 contentScale = ContentScale.Fit，保持图片比例。

5. 文本未对齐、样式不统一
   问题：文本左对齐、字号杂乱。
   解决：设置文本居中，统一调整 fontSize 与 fontWeight。

## 六、实验总结
通过本次实验，我掌握了 Compose 基础组件 Column、Row、Image、Text、Button、Surface 的使用，能够使用 Modifier 控制布局与样式，熟练使用 remember 和 mutableStateOf 实现界面状态管理，并通过条件表达式完成按钮切换与边界处理。
应用可以正常运行，展示 3 幅作品，切换功能正常，界面由状态驱动，布局结构清晰，完全满足实验所有验收标准。