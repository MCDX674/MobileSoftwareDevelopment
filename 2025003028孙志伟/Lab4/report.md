# 掷骰子应用（DiceRoller）实验报告
## 实验概述
基于 Android Jetpack Compose 实现简易掷骰子应用，实现点击按钮随机生成骰子点数、切换对应骰子图片的功能，掌握 Compose 状态管理、界面布局与 Android Studio 调试方法。

## 一、应用界面结构说明
1. **整体架构**
应用入口为 `MainActivity`，通过 `setContent` 加载 Compose 界面，使用 `Scaffold` 搭建基础页面框架，适配沉浸式状态栏。

2. **核心界面组件**
- `DiceRollerApp`：应用根组件，仅用于包裹核心功能组件，简化结构；
- `DiceWithButtonAndImage`：主功能组件，承载全部交互与展示逻辑；
- 界面采用 **Column 垂直布局**，内部包含三个核心元素：
  1. `Image`：展示当前点数对应的骰子图片，居中显示；
  2. `Spacer`：设置 16dp 高度，分隔图片与按钮，优化界面间距；
  3. `Button`：点击触发掷骰子逻辑，按钮文本为 `roll`。

3. **布局约束**
通过 `Modifier.fillSize().wrapContentSize(Alignment.Center)` 让整个界面内容在屏幕中居中显示，`Column` 设置水平居中对齐，保证界面美观统一。

## 二、使用 Compose 状态保存骰子结果
本应用通过 **Compose 响应式状态** 保存和管理骰子点数，核心代码：
```kotlin
var result by remember { mutableStateOf(1) }
```

1. **`mutableStateOf(1)`**
创建一个**可观察的可变状态**，初始值为 1，用于存储当前骰子的点数，是界面刷新的数据源。
2. **`remember`**
将状态变量缓存起来，在屏幕旋转、组件重组时，**保持数值不丢失**，实现状态持久化。
3. **`by` 委托**
简化状态变量的读写操作，直接使用 `result` 即可获取和修改数值。

当按钮点击修改 `result` 时，Compose 会自动检测状态变化，触发界面重组。

## 三、根据点数切换图片资源
使用 `when` 条件表达式，根据状态变量 `result` 的数值，匹配对应的图片资源：
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

1. 当 `result` 为 1~5 时，分别对应 `dice_1` ~ `dice_5` 图片；
2. `else` 分支兜底处理点数 6 的情况，覆盖所有随机结果；
3. 将匹配到的资源 ID 传入 `Image` 组件的 `painterResource`，完成图片渲染更新。

每当 `result` 改变，`when` 表达式会重新执行，自动切换图片。

## 四、断点设置与观测内容
本次调试共设置 **3 个关键断点**，精准观测程序执行流程：

1. **断点 1**：`var result by remember { mutableStateOf(1) }`
   观察：应用启动时，骰子状态变量的**初始值是否为 1**，确认状态初始化正常。

2. **断点 2**：`Button` 的 `onClick` 点击事件内部 `result = (1..6).random()`
   观察：按钮点击时，**随机数是否正确生成**，`result` 变量是否被成功赋值。

3. **断点 3**：`val imageResource = when (result)` 代码行
   观察：`result` 更新后，`when` 表达式**是否匹配到正确的图片资源 ID**，验证图片切换逻辑。

## 五、`Step Into`、`Step Over`、`Step Out` 使用体会
1. **Step Over（跳过）**
逐行执行当前代码，不进入函数内部，是最常用的调试操作。
使用场景：逐行查看界面初始化、状态赋值、图片匹配的执行顺序，快速定位代码执行流程。

2. **Step Into（跳入）**
进入当前代码行调用的函数内部，查看底层实现。
使用场景：跳入 `random()` 函数，观察随机数生成逻辑；跳入 Compose 组件函数，了解组件重组过程。

3. **Step Out（跳出）**
跳出当前函数，返回到函数调用的上一级代码。
使用场景：查看完函数细节后，快速返回主逻辑，避免深入底层代码浪费时间。

**总结**：日常调试优先用 `Step Over`；需要深入函数细节用 `Step Into`；完成底层查看后用 `Step Out` 返回，三者配合高效排查问题。

## 六、遇到的问题与解决过程
### 问题 1：图片无法显示，界面空白
- 原因：`drawable` 文件夹中缺少 `dice_1~dice_6` 图片资源，或图片命名与代码不一致；
- 解决方案：将 6 张骰子图片命名为 `dice_1` 至 `dice_6`，放入 `res/drawable` 目录，同步项目后修复。

### 问题 2：按钮点击后图片不刷新
- 原因：最初未使用 `remember` + `mutableStateOf`，`result` 是普通变量，无法触发 Compose 重组；
- 解决方案：修改为 `var result by remember { mutableStateOf(1) }`，使用响应式状态驱动UI更新。

### 问题 3：界面内容不居中
- 原因：未给组件添加居中修饰符；
- 解决方案：添加 `Modifier.fillSize().wrapContentSize(Alignment.Center)`，并设置 `Column` 水平居中。

### 问题 4：字符串资源找不到报错
- 原因：`stringResource(R.string.roll)` 对应的 `roll` 字符串未在 `strings.xml` 中定义；
- 解决方案：在 `res/values/strings.xml` 中添加 `<string name="roll">Roll</string>`。

## 七、实验结论
1. **按钮点击后图片自动刷新的原因**
按钮点击 → 修改 `result` 状态变量 → Compose 检测到状态变化 → 触发组件重组 → `when` 重新匹配图片 → 界面自动刷新。
核心原理：**Compose 是状态驱动UI，状态改变自动刷新界面**。

2. **调试变量与界面结果一致性**
调试器中观察到的 `result` 数值、`imageResource` 资源ID，与界面显示的骰子图片完全一致，证明状态管理和图片切换逻辑正确。

3. **实验收获**
掌握了 Jetpack Compose 基础布局、状态管理、条件渲染的用法，熟练使用断点调试工具，理解了声明式UI的核心工作原理。