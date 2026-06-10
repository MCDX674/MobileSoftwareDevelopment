# Android Jetpack Compose 骰子滚动应用实验报告（同学版）
## 一、实验目的
1. 掌握 Jetpack Compose 基础 UI 搭建与布局使用。
2. 理解 Compose 状态管理与自动重组机制。
3. 实现根据数值动态切换图片资源。
4. 熟练使用 Android Studio 断点调试功能。
5. 学会排查 Compose 开发中常见问题。

## 二、应用界面结构说明
本应用整体为**单页面极简设计**，所有元素垂直居中排列：
1. **顶层结构**
    - 入口：`MainActivity` → `setContent` → `DiceRollerTheme` → `DiceRollerApp`。
    - 主题包裹：保证统一样式，支持全屏边缘到边缘显示。
2. **核心组件**
    - 垂直布局 `Column`：负责把图片和按钮上下摆放，并水平居中。
    - 骰子图片 `Image`：显示当前点数对应的骰子图案。
    - 间隔 `Spacer`：设置 16dp 间距，让图片和按钮不贴在一起。
    - 按钮 `Button`：点击执行掷骰子逻辑，显示文字“Roll”。
3. **修饰符作用**
    - `fillSize()`：让布局占满全屏。
    - `wrapContentSize(Alignment.Center)`：让内部元素整体居中。
    - `horizontalAlignment`：让子元素水平居中对齐。

界面层级简单清晰，交互直观，符合标准 Compose 应用结构。

## 三、Compose 状态保存骰子点数的实现
应用使用 **remember + mutableStateOf** 完成状态保存与监听：
1. **状态定义**
    ```kotlin
    var result by remember { mutableStateOf(1) }
    ```
2. **关键作用**
    - `mutableStateOf`：把变量变成**可观察状态**，值改变会触发 UI 刷新。
    - `remember`：在屏幕旋转、配置改变时**保留状态不丢失**。
    - `by` 委托：简化状态读写，直接赋值即可更新。
3. **状态更新**
    按钮点击时执行：`result = (1..6).random()`，随机生成 1–6 点数。
4. **机制总结**
    状态驱动 UI，只要 `result` 改变，用到它的组件就会自动重组。

## 四、根据点数切换图片资源的实现
通过 **when 分支判断** 实现点数与图片一一对应：
1. 提前准备 `dice_1` 到 `dice_6` 六张图片放在 `drawable` 文件夹。
2. 根据 `result` 当前值选择对应图片：
    ```kotlin
    val imageResource = when(result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }
    ```
3. 把得到的 `imageResource` 传给 `Image` 的 `painterResource`，实现图片实时切换。

## 五、断点设置与观察内容
本次调试设置 **3 个关键断点**，用于观察数据流向：
1. **断点 1：状态初始化行**
    - 观察：`result` 初始值是否为 1，`remember` 是否正常创建。
2. **断点 2：when 图片选择处**
    - 观察：`result` 数值与对应 `drawable` 资源 ID 是否匹配。
3. **断点 3：按钮点击 `result` 赋值处**
    - 观察：点击后是否生成 1–6 随机数，状态是否被正确修改。

所有断点观察结果均与预期一致，变量值正确。

## 六、Step Into、Step Over、Step Out 使用体会
1. **Step Over（单步跳过）**
    - 逐行执行当前函数，不进入子函数。
    - 适合快速走通主逻辑，最常用、最高效。
2. **Step Into（单步跳入）**
    - 进入当前行调用的函数内部。
    - 适合想看 `random()`、状态更新等底层细节时使用。
3. **Step Out（单步跳出）**
    - 立即跳出当前函数，回到上一级调用处。
    - 适合不小心跳进系统函数，快速返回自己代码。

**使用心得**：日常调试优先 Step Over；需要深入原理用 Step Into；想快速返回用 Step Out。

## 七、遇到的问题与解决方法
1. **问题：图片不显示 / 显示错误**
    - 原因：资源文件名写错、`when` 分支对应错误。
    - 解决：核对 `drawable` 文件名与代码里的 `R.drawable` 完全一致。
2. **问题：点击按钮图片不刷新**
    - 原因：没用 `mutableStateOf`，普通变量无法触发重组。
    - 解决：改用 `remember { mutableStateOf(1) }` 声明状态。
3. **问题：预览正常，运行后布局偏移**
    - 原因：`Modifier` 顺序或居中设置不对。
    - 解决：给 `Column` 添加完整居中修饰符。
4. **问题：断点打不上/不生效**
    - 原因：App 进程未附加调试器。
    - 解决：使用 Attach Debugger to Process 选中当前应用。

## 八、实验结论
1. **按钮点击图片自动刷新的原因**
    `result` 是 Compose 可观察状态，点击修改数值后，Compose 自动触发**重组（Recomposition）**，`Image` 重新读取最新状态并切换图片。
2. **调试变量与界面结果一致**
    调试器里看到的 `result` 随机数、图片资源 ID，和界面显示的骰子点数、图案完全一致，说明状态管理与逻辑正确。
3. **实验收获**
    掌握了 Compose 基础 UI、状态管理、图片切换和断点调试，理解了“**状态驱动 UI**”的核心思想，能独立完成简单交互应用开发。
