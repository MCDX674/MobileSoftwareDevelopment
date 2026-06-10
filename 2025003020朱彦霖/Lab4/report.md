# DiceRoller 掷骰子应用实验报告

## 一、实验目标
完成Jetpack Compose交互式界面开发，掌握Compose状态管理机制，学会使用Android Studio基础调试功能，理解状态更新与界面重组的关系。

---

## 二、应用界面结构说明
本次开发的掷骰子应用，采用垂直布局结构，核心界面元素如下：
1.  **Column布局容器**：作为根布局，将所有内容在屏幕中垂直居中排列
2.  **Image图片组件**：用于展示当前骰子点数对应的图片
3.  **Spacer间距组件**：在图片和按钮之间添加16dp垂直间距，优化界面观感
4.  **Button按钮组件**：“Roll”掷骰子按钮，点击后触发随机数生成与状态更新

---

## 三、Compose状态保存实现
使用`remember` + `mutableStateOf`实现骰子点数的状态管理，核心代码：
```kotlin
var result by remember { mutableStateOf(1) }
```
- 状态初始值设为1，对应应用启动时默认显示1点骰子图片
- `mutableStateOf`创建可被Compose观察的状态对象，`remember`保证状态在界面重组、屏幕旋转时不丢失
- 点击按钮时，通过`result = (1..6).random()`更新状态值，触发界面自动刷新

---

## 四、图片资源切换逻辑
通过`when`表达式，根据当前`result`的数值，匹配对应的骰子drawable资源：
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
当`result`状态更新时，`imageResource`会同步重新计算，Image组件自动加载对应图片，完全由状态驱动界面刷新，无需手动修改视图。

---

## 五、断点设置与调试观察
本次实验在 `DiceRollerApp()` 函数入口处设置了断点，用于完整观察 Composable 函数的重组过程与状态变化。

1.  **断点位置**：断点设置在 `DiceRollerApp()` 函数体的起始行（或函数定义行）
    - **观察内容**：
      1.  **初始进入时**：应用启动后，程序第一次停在断点处，观察到 `result` 状态的初始值为 `1`，对应界面显示 1 点骰子图片。
      2.  **点击按钮后**：点击 App 中的“Roll”按钮，程序再次停在断点处，此时可观察到：
         - 状态变量 `result$delegate` 的值已更新为新生成的随机数（如截图中的 `6`），与界面即将显示的骰子点数一致。
         - `changed` 和 `dirty` 变量值变为 `6`，表明 Compose 检测到状态变化，标记当前 Composable 为“脏”，准备触发重组。
         - `Recomposition State` 显示 `DiceRollerApp` 正在进行重组，直观验证了“状态变化驱动界面刷新”的核心机制。

---

## 六、调试功能使用体会
1.  **Step Over**：逐行执行代码，不进入方法内部，适合快速查看`result`变量的更新结果，高效跳过无需深入的代码
2.  **Step Into**：进入当前调用的方法内部，适合查看随机数生成的底层逻辑，深入理解代码执行流程
3.  **Step Out**：快速跳出当前方法，回到代码调用处，适合在进入系统底层方法后快速返回，节省调试时间
4.  **Resume Program**：继续执行程序直到下一个断点，适合快速验证多轮掷骰子的状态变化与界面刷新效果

---

## 七、遇到的问题与解决过程
1.  **问题1：找不到调试窗口**
    - 问题：初始打开的是Version Control版本控制面板，看不到调试变量信息
    - 解决：确认已通过Debug模式启动应用，使用快捷键`Alt+9`调出Debug面板，触发断点后即可正常查看变量
2.  **问题2：点击按钮图片不刷新**
    - 问题：随机数生成成功，但界面骰子图片没有变化
    - 解决：检查发现状态未使用`mutableStateOf`定义，修改为`remember + mutableStateOf`后，状态变化可被Compose检测，界面正常刷新

---

## 八、实验结论
1.  **界面自动刷新原理**：Compose是声明式UI框架，当`result`状态变量更新时，Compose会自动追踪状态的依赖项，触发对应组件的重组，Image组件根据最新状态加载对应图片，实现界面自动刷新。
2.  **调试一致性验证**：调试窗口中观察到的`result`变量值，与界面显示的骰子点数完全一致，验证了Compose状态管理的可靠性。

本次实验完全满足验收标准：应用可正常运行、点击按钮骰子图片正常切换、使用了Compose状态管理、提供了调试截图、完整说明断点设置与调试过程。
