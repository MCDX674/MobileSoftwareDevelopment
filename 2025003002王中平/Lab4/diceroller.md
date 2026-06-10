# Lab4：Dice Roller 交互应用与 Android Studio 调试 实验报告
---

## 一、应用界面结构说明
本应用基于 **Kotlin + Jetpack Compose** 实现，**完全不使用 XML 布局**，界面结构严格按照代码实现，层次清晰：

1. **入口层**  
   `MainActivity` 作为应用唯一入口，在 `onCreate` 中调用 `enableEdgeToEdge()` 实现沉浸式布局，并通过 `setContent` 加载 `DicerollerTheme` 主题与主界面组件 `DiceRollerApp`。

2. **应用主组件**  
   `DiceRollerApp` 是顶层可组合函数，使用 `Surface` 填充全屏并应用主题背景色，仅负责承载核心交互组件。

3. **核心功能组件**  
   `DiceWithButtonAndImage` 实现全部交互逻辑，采用 **Column 垂直布局**，通过 `Modifier` 实现**全屏 + 居中对齐**，内部包含：
   - 骰子图片 `Image` 组件；
   - 16dp 高度的 `Spacer` 间隔组件；
   - 显示字符串资源 `R.string.roll` 的 `Button` 按钮。

4. **预览组件**  
   `DiceRollerPreview` 提供 Android Studio 实时预览功能，方便开发调试。

---

## 二、Compose 状态保存骰子结果的实现
应用使用 **Jetpack Compose 状态管理机制** 保存和更新骰子点数，核心代码：
```kotlin
var result by remember { mutableStateOf(1) }
```
- `mutableStateOf(1)`：创建可观察状态对象，初始点数为 1；
- `remember`：缓存状态变量，避免界面重组时数据丢失；
- `by`：Kotlin 委托属性，简化状态变量的读写操作；
- 按钮点击事件中通过 `result = (1..6).random()` 更新状态，**状态改变自动触发界面重组**。

---

## 三、根据点数切换图片资源的逻辑
通过 **`when` 表达式** 实现点数与图片资源的完整映射，代码如下：
```kotlin
val imageResource = when (result) {
    1 -> R.drawable.dice_1
    2 -> R.drawable.dice_2
    3 -> R.drawable.dice_3
    4 -> R.drawable.dice_4
    5 -> R.drawable.dice_5
    6 -> R.drawable.dice_6
    else -> R.drawable.dice_1
}
```
- 监听状态变量 `result` 的实时值；
- 为 1~6 所有点数匹配对应图片资源；
- 增加 `else` 兜底逻辑，提升程序健壮性；
- 图片通过 `painterResource(imageResource)` 渲染，`contentDescription` 设为点数文本，符合无障碍规范。

---

## 四、断点设置与观察内容
本次实验设置**两个关键断点**，完成调试观察：

1. **断点 1**  
   位置：`MainActivity` 的 `onCreate` 中 `DiceRollerApp()` 调用行  
   观察内容：应用启动流程、主题加载、函数调用栈。

2. **断点 2**  
   位置：`DiceWithButtonAndImage` 中 `val imageResource = when (result)` 行  
   观察内容：`result` 状态变量的随机值、`imageResource` 资源 ID、状态与图片的映射关系。

---

## 五、调试功能（Step Into/Step Over/Step Out）使用体会
1. **Step Into（步入）**  
   进入当前行调用的子函数内部，可深入查看 `DiceRollerApp`、`DiceWithButtonAndImage` 的执行细节，追踪界面加载流程。

2. **Step Over（步过）**  
   逐行执行当前函数代码，不进入子函数，效率最高，适合快速观察状态变量与资源赋值逻辑。

3. **Step Out（步出）**  
   跳出当前函数，返回上一层调用位置，快速回归主执行流程。

通过调试可直观理解 **Compose 状态变化驱动界面重组** 的完整机制。

---

## 六、遇到的问题与解决过程
1. **问题**：骰子图片无法正常显示  
   原因：`res/drawable` 目录未添加 `dice_1 ~ dice_6` 图片资源，或命名不匹配  
   解决：添加对应图片并严格按照代码规范命名。

2. **问题**：点击按钮后图片不刷新  
   原因：未使用 `remember + mutableStateOf` 实现状态管理  
   解决：使用 Compose 状态变量保存点数，通过状态驱动刷新。

3. **问题**：断点无法生效  
   原因：使用 Run 模式运行应用，未开启调试模式  
   解决：点击 `Debug 'app'` 以调试模式启动。

---

## 七、实验结论
1. **图片自动刷新原理**  
   点击按钮生成随机数 → 更新 `result` 状态变量 → Compose 检测到状态变化 → 触发组件重组 → `when` 重新匹配图片 → `Image` 组件刷新显示。

2. **调试与界面一致性**  
   调试器中观察到的 `result` 数值与界面骰子图片完全对应，验证了 **Compose 状态驱动界面** 的核心特性。

3. **实验收获**  
   熟练掌握 Jetpack Compose 状态管理、图片切换、按钮交互等技能，学会使用 Android Studio 断点、单步调试等基础调试方法。