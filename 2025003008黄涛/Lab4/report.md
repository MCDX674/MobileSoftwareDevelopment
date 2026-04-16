# Lab4：Dice Roller 交互应用与 Android Studio 调试 实验报告

 ## 1 应用界面结构说明

本应用基于 Jetpack Compose 开发，为单页面骰子滚动应用，界面结构如下：

 - 根容器：`MainActivity` 中使用 `Scaffold` 承载页面内容，适配系统边距

 - 核心布局：`DiceWithButtonAndImage` 组合函数内采用 `Column` 垂直布局

 - 界面元素：骰子图片 `Image` → 16dp 间距 `Spacer` → 滚动按钮 `Button`

 - 对齐方式：整体屏幕居中，子元素水平居中对齐

 ## 2 Compose 状态保存骰子结果

骰子点数通过 Compose 状态管理实现存储与更新，核心代码如下：

 ```kotlin

var result by remember { mutableStateOf(1) }
 ```

 - `mutableStateOf(1)`：创建可观察状态变量，初始骰子点数为 1

 - `remember`：在界面重组时保留状态，避免变量被重复初始化

 - 状态变更会自动触发重组，实现 UI 自动刷新

 ## 3 根据点数切换图片资源

通过 `when` 表达式实现点数与图片资源的动态绑定：

 ```kotlin

val ImageResource = when(result) {

    1 -> R.drawable.dice_1
     
    2 -> R.drawable.dice_2

    3 -> R.drawable.dice_3

    4 -> R.drawable.dice_4

    5 -> R.drawable.dice_5

    else -> R.drawable.dice_6

}

 ```

 - 状态变量 `result` 变化时，`when` 重新匹配对应骰子图片

 - `Image` 组件通过匹配后的资源 ID 刷新展示内容

 ## 4 断点设置与观察内容

本次调试共设置 3 处断点：

 1. 断点位置：`var result by remember { mutableStateOf(1) }`

   观察目标：`result` 初始值是否为 1，`remember` 缓存是否生效

 2. 断点位置：`val ImageResource = when(result)`

   观察目标：`result` 实时数值与图片资源匹配结果

 3. 断点位置：按钮点击事件 `result = (1..6).random()`

   观察目标：点击后随机数生成是否在 1-6 范围内

调试结果：变量数值与界面展示图片完全一致。

 ## 5 Step Into、Step Over、Step Out 使用体会

 - **Step Over**：单步执行当前代码行，不进入子函数，适合快速调试业务逻辑

 - **Step Into**：进入调用函数内部，可查看 Compose 底层实现，易进入系统源码

 - **Step Out**：跳出当前函数，返回上一级调用处，用于快速退出底层源码

 ## 6 遇到的问题与解决过程

 1. **问题**：按钮点击后图片不刷新

   原因：未使用 `remember` 缓存状态，重组时变量重置

   解决：使用 `remember` 包裹 `mutableStateOf` 保留状态

 2. **问题**：图片资源加载失败

   原因：图片命名与代码中资源 ID 不匹配

   解决：统一图片命名为 `dice_1` ~ `dice_6`

 3. **问题**：界面元素未居中

   原因：缺少居中对齐修饰符

   解决：添加 `Alignment.Center` 相关布局修饰

 ## 7 实验结论

 1. 按钮点击后图片自动刷新，是因为 `result` 状态变化触发 Compose 重组，重新执行图片匹配逻辑。

 2. 调试器中变量值与界面展示结果完全一致，状态管理机制运行正常。

 3. 应用可正常运行，点击按钮随机切换骰子图片，满足全部实验验收要求。