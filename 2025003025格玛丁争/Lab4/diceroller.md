# Lab4 实验报告：Dice Roller 交互应用与 Android Studio 调试
## 一、实验信息
- **实验名称**：Dice Roller 交互应用与 Android Studio 调试
- **实验环境**：Android Studio Hedgehog | 2023.1.1、Kotlin、Jetpack Compose
- **项目名称**：DiceRoller

## 二、应用界面结构说明
本次实验的 Dice Roller 应用完全基于 Jetpack Compose 实现，无 XML 布局，整体界面结构清晰简洁，核心分为**顶层容器**、**骰子图片展示区**、**交互按钮区**三部分：

1. **整体布局容器**
   使用 `Column` 作为根布局容器，设置 `fillMaxSize()` 铺满屏幕，通过 `horizontalAlignment` 实现水平居中、`verticalArrangement` 实现垂直居中，保证界面元素在屏幕中对称展示，提升视觉效果。

2. **核心可组合函数**
   定义独立可组合函数 `DiceRollerApp()` 作为应用入口，内部调用 `DiceWithButtonAndImage()` 封装图片和按钮逻辑，实现代码解耦，符合 Compose 组件化设计思想。

3. **界面元素组成**
   - **Image 组件**：用于展示骰子图片，根据点数动态切换资源，设置 `contentDescription` 提升应用无障碍访问性；
   - **Button 组件**：用于触发掷骰子操作，点击后生成随机点数；
   - 所有元素垂直排列在 `Column` 中，布局简洁且交互直观。

## 三、Compose 状态保存骰子结果实现
应用的核心交互依赖 **Jetpack Compose 状态管理**，通过 `remember` 和 `mutableStateOf()` 实现骰子结果的保存与自动刷新：

1. **状态变量定义**
   在 `DiceWithButtonAndImage` 可组合函数中，定义状态变量：
   ```kotlin
   var result by remember { mutableStateOf(1) }
   ```
   - `mutableStateOf(1)`：创建一个可观察的状态对象，初始值为 1（默认展示骰子1点）；
   - `remember`：保证屏幕重组时，状态变量不会被重新初始化，持久保存当前骰子点数；
   - `by` 关键字：使用 Kotlin 委托属性，简化状态变量的读写操作。

2. **状态更新逻辑**
   按钮的 `onClick` 点击事件中，更新状态变量的值：
   ```kotlin
   onClick = { result = (1..6).random() }
   ```
   当 `result` 的值发生改变时，Compose 会自动检测到状态变化，触发**界面重组**，无需手动调用刷新方法。

## 四、根据点数切换图片资源实现
应用通过 `when` 表达式将随机生成的骰子点数，映射到对应的图片资源，实现图片动态切换：

1. **资源准备**
   将 6 张骰子图片（`dice_1.png` ~ `dice_6.png`）放入 `res/drawable` 目录，作为图片资源。

2. **资源映射逻辑**
   根据状态变量 `result` 的值，匹配对应的图片资源：
   ```kotlin
   val imageResource = when (result) {
       1 -> R.drawable.dice_1
       2 -> R.drawable.dice_2
       3 -> R.drawable.dice_3
       4 -> R.drawable.dice_4
       5 -> R.drawable.dice_5
       else -> R.drawable.dice_6
   }
   ```

3. **图片展示**
   使用 `Image` 组件加载映射后的资源，`contentDescription` 设置为当前点数，满足无障碍规范：
   ```kotlin
   Image(
       painter = painterResource(imageResource),
       contentDescription = "骰子点数：$result",
       modifier = Modifier.size(200.dp)
   )
   ```

## 五、断点设置与调试观察
本次实验按照要求在关键代码位置设置断点，使用 Android Studio 调试器观察程序运行流程和变量变化：

### 1. 断点位置
1. **断点1**：`MainActivity` 的 `onCreate` 方法中，调用 `DiceRollerApp()` 的位置，用于观察应用启动时的界面初始化流程；
2. **断点2**：`when` 表达式映射图片资源的代码行，用于观察骰子点数变化和图片资源的匹配过程。

### 2. 调试观察结果
- 程序启动后，停在断点1时，Variables 窗口显示 `result` 初始值为 **1**，对应图片资源为 `dice_1`；
- 点击 Roll 按钮后，程序停在断点2，可观察到 `result` 变为随机数（如3、5、6），`imageResource` 同步更新为对应骰子图片；
- 状态变量 `result` 委托属性显示为 `result$delegate`，这是 Kotlin 委托属性的正常表现，值与界面展示完全一致。

## 六、调试功能使用体会
本次实验熟练使用了 Android Studio 调试器的核心功能，对 `Step Into`、`Step Over`、`Step Out` 有了清晰的理解：

1. **Step Into（步入）**
   点击该按钮后，程序进入当前行调用的函数内部。例如在断点1处使用 `Step Into`，成功进入 `DiceRollerApp()` 可组合函数内部，查看函数执行细节。

2. **Step Over（步过）**
   逐行执行当前代码，不进入函数内部。在调试图片映射逻辑时，使用该功能逐行查看 `when` 表达式的执行过程，直观观察变量赋值流程。

3. **Step Out（步出）**
   跳出当前函数，返回上一层调用位置。在进入 `DiceWithButtonAndImage()` 函数后，使用 `Step Out` 快速返回 `DiceRollerApp()` 函数，提升调试效率。

4. **Resume Program（恢复程序）**
   继续运行程序，直到下一个断点。点击后应用正常运行，点击按钮触发断点，高效观察交互逻辑。

## 七、遇到的问题与解决过程
### 问题1：点击按钮后图片不刷新
- **原因**：未使用 `remember` 保存状态变量，每次重组时变量被重新赋值为初始值1；
- **解决方法**：将状态变量修改为 `var result by remember { mutableStateOf(1) }`，保证状态持久化。

### 问题2：调试时找不到状态变量
- **原因**：不理解 Kotlin 委托属性，误以为变量名错误；
- **解决方法**：在 Variables 窗口中查看 `result$delegate` 变量，其 `value` 属性即为当前骰子点数。

### 问题3：图片资源加载失败
- **原因**：图片文件名大小写不匹配，代码中引用的资源名与 drawable 目录下的文件名不一致；
- **解决方法**：统一图片命名为 `dice_1` ~ `dice_6`，修正资源引用代码。

## 八、实验结论
1. **状态驱动界面刷新原理**
   Jetpack Compose 的核心机制是**状态驱动UI**，当 `remember` 包裹的 `mutableStateOf` 状态变量发生变化时，Compose 会自动重组依赖该状态的组件，无需手动更新视图，这是按钮点击后图片自动刷新的根本原因。

2. **调试验证结果**
   调试器中观察到的 `result` 变量值、`imageResource` 资源值，与界面展示的骰子图片完全一致，证明状态管理和图片映射逻辑正确。

3. **实验收获**
   本次实验掌握了 Compose 状态管理、交互式界面开发、Android Studio 调试技巧，理解了声明式UI的核心思想，为后续 Android 开发打下了基础。