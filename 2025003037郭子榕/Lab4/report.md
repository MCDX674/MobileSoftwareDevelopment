# Lab4 实验报告 - Dice Roller 应用开发与调试

## 基本信息

- **姓名**：[郭子榕]
- **学号**：[2025003037]
- **实验日期**：2026年4月14日
- **项目名称**：Dice Roller（掷骰子应用）

------

## 一、应用界面结构说明

### 1.1 整体布局结构

本应用采用 Jetpack Compose 声明式 UI 框架构建，整体布局为垂直列（`Column`）结构，从上到下依次排列以下组件：

| 组件       | 类型     | 功能描述                               |
| :--------- | :------- | :------------------------------------- |
| 骰子图片   | `Image`  | 显示当前点数的骰子图案                 |
| 间距       | `Spacer` | 在图片和按钮之间添加垂直间距（32.dp）  |
| 掷骰按钮   | `Button` | 用户点击后触发随机数生成，更新骰子点数 |
| 按钮内文字 | `Text`   | 显示按钮文字 "Rolls"                   |

### 1.2 布局层级关系图

text

```
MainActivity
└── MyApplication4Theme (主题)
    └── Surface (主容器，浅蓝色背景)
        └── DiceRollerApp() (核心可组合函数)
            └── Column (垂直布局，居中显示)
                ├── Image (骰子图片，200x200dp)
                ├── Spacer (32dp 间距)
                └── Button (掷骰按钮)
                    └── Text ("Rolls")
```



### 1.3 界面特点

- **居中布局**：使用 `Arrangement.Center` 和 `Alignment.CenterHorizontally` 实现内容水平和垂直居中
- **自定义背景**：背景色设置为爱丽丝蓝（`Color(0xFFF0F8FF)`）
- **固定图片尺寸**：骰子图片大小为 200.dp，保证显示一致性

------

## 二、Compose 状态管理实现

### 2.1 状态定义方式

```kotlin
var diceResult by remember { mutableStateOf(6) }
```



这行代码实现了状态管理，各部分的作用如下：

| 关键字/函数         | 作用说明                                |
| :------------------ | :-------------------------------------- |
| `var`               | 声明可变变量，允许状态值被更新          |
| `by`                | Kotlin 委托语法，自动处理 getter/setter |
| `remember`          | 在重组时保留状态值，避免重置            |
| `mutableStateOf(6)` | 创建可观察状态，初始值为 6              |
| `diceResult`        | 状态变量，存储当前骰子点数（1-6）       |

### 2.2 状态更新流程

当用户点击按钮时，状态更新流程如下：

text

```
用户点击 Button
    ↓
onClick 回调执行
    ↓
调用 rollDice() 生成随机数 (1-6)
    ↓
将新值赋给 diceResult
    ↓
Compose 检测到 mutableStateOf 值变化
    ↓
触发重组 (Recomposition)
    ↓
重新执行 DiceRollerApp() 函数
    ↓
重新计算 imageResource
    ↓
Image 组件刷新，显示新骰子图片
```



### 2.3 为什么按钮点击后图片能够自动刷新？

这是 Jetpack Compose 的核心特性——**声明式 UI + 响应式状态**：

1. **观察者模式**：`mutableStateOf` 创建了一个可观察的状态对象
2. **自动追踪**：Compose 运行时自动追踪哪些组件读取了该状态
3. **精准重组**：当状态值改变时，只重组读取该状态的组件
4. **无需手动刷新**：开发者不需要调用 `setImageResource()` 或类似方法，框架自动处理 UI 更新

与传统 Android 开发的 `findViewById` + `setImageResource` 相比，Compose 方式大大简化了代码，减少了出错可能。

------

## 三、图片资源切换实现

### 3.1 资源文件准备

6 张骰子图片存放在以下目录：

```text
app/src/main/res/drawable/
├── dice_1.png
├── dice_2.png
├── dice_3.png
├── dice_4.png
├── dice_5.png
└── dice_6.png
```



### 3.2 映射逻辑

通过 `getDiceImageResource()` 函数将点数映射到对应的图片资源：

```kotlin
@Composable
fun getDiceImageResource(result: Int): Int {
    return when (result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1  // 默认值
    }
}
```



### 3.3 图片显示代码

```kotlin
Image(
    painter = painterResource(id = imageResource),
    contentDescription = "Dice showing number ${getDiceDescription(diceResult)}",
    modifier = Modifier.size(200.dp)
)
```



- `painterResource()`：根据资源 ID 加载图片
- `contentDescription`：为无障碍功能提供文字描述

------

## 四、断点设置与观察记录

### 4.1 设置的断点位置

| 断点  | 位置                     | 所在函数               | 观察目的                 |
| :---- | :----------------------- | :--------------------- | :----------------------- |
| 断点1 | `DiceRollerApp()` 调用处 | `onCreate`             | 观察应用启动时的界面构建 |
| 断点2 | `when (result)` 表达式   | `getDiceImageResource` | 观察点数如何映射到图片   |
| 断点3 | `return randomValue`     | `rollDice`             | 观察随机数的生成和传递   |

### 4.2 断点观察记录

**断点1（应用启动）**

- 触发时界面尚未渲染，屏幕为黑屏
- 继续执行后开始构建界面

**断点2（图片映射）**

| result值 | 执行分支 | 返回资源 |
| :------- | :------- | :------- |
| 1        | `1 ->`   | dice_1   |
| 2        | `2 ->`   | dice_2   |
| 3        | `3 ->`   | dice_3   |
| 4        | `4 ->`   | dice_4   |
| 5        | `5 ->`   | dice_5   |
| 6        | `6 ->`   | dice_6   |

**断点3（随机数生成）**

| 点击次数 | randomValue | 传递给 diceResult | 界面显示 |
| :------- | :---------- | :---------------- | :------- |
| 1        | 4           | 4                 | 4点骰子  |
| 2        | 1           | 1                 | 1点骰子  |
| 3        | 6           | 6                 | 6点骰子  |

------

## 五、调试功能使用体会

### 5.1 Step Into（进入函数内部）

**操作方式**：在断点处点击 Step Into 按钮（或按 F7）

**使用场景**：当程序执行到 `rollDice()` 函数调用时

**观察结果**：

- 调试器进入 `rollDice()` 函数内部
- 可以看到 `Random.nextInt(1, 7)` 的执行过程
- 能够观察 `randomValue` 变量的生成过程

**使用体会**：
Step Into 适合需要深入了解函数内部实现逻辑的场景。在本实验中，我使用它进入了 `rollDice()` 函数，清楚地看到了随机数是如何生成的。缺点是可能会进入系统或框架代码（如 Kotlin 标准库），需要谨慎使用。

### 5.2 Step Over（逐行执行）

**操作方式**：点击 Step Over 按钮（或按 F8）

**使用场景**：在 `getDiceImageResource()` 函数中逐行执行 when 表达式

**观察结果**：

- 程序按顺序检查每个 when 分支
- 当找到匹配的 result 值时，直接返回对应的资源 ID
- 跳过其他未执行的分支

**使用体会**：
Step Over 是最常用的调试操作，适合逐行分析当前函数的执行流程。当确定被调用函数没有问题（如 `rollDice()` 逻辑简单时），可以使用 Step Over 快速跳过，提高调试效率。

### 5.3 Step Out（跳出当前函数）

**操作方式**：点击 Step Out 按钮（或按 Shift + F8）

**使用场景**：在 `rollDice()` 函数内部分析完成后

**观察结果**：

- 程序快速执行完 `rollDice()` 剩余代码
- 自动返回到 Button 的 onClick lambda 中
- 继续执行 `diceResult = newValue` 赋值语句

**使用体会**：
Step Out 非常适合在已经分析完当前函数细节后，快速返回上一层调用位置。在本实验中，当我看完 `rollDice()` 的随机数生成逻辑后，使用 Step Out 直接返回按钮点击事件，避免了逐行执行到函数结束的繁琐操作。

### 5.4 三种调试功能的对比总结

| 功能      | 快捷键   | 适用场景           | 优点                     |
| :-------- | :------- | :----------------- | :----------------------- |
| Step Into | F7       | 想了解函数内部实现 | 可以深入代码细节         |
| Step Over | F8       | 逐行分析当前函数   | 效率高，不会进入无关函数 |
| Step Out  | Shift+F8 | 已分析完当前函数   | 快速返回，节省时间       |

------

## 六、遇到的问题与解决过程

### 问题 1：Preview 窗口一直显示"正在加载"

**现象描述**：
在 Android Studio 的 Preview 窗口中，界面无法正常显示，一直处于加载状态。

**原因分析**：

1. Preview 代码中重复嵌套了两层 `MyApplication4Theme`
2. Android Studio 缓存问题
3. 图片资源可能不存在或命名不正确

**解决过程**：

1. 修改 Preview 代码，移除重复的 `MyApplication4Theme` 嵌套
2. 执行 `Build` → `Clean Project` → `Rebuild Project`
3. 点击 `File` → `Invalidate Caches and Restart` 清除缓存

**修正后的 Preview 代码**：

kotlin

```
@Preview(showBackground = true)
@Composable
fun DiceRollerAppPreview() {
    MyApplication4Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF0F8FF)
        ) {
            DiceRollerApp()
        }
    }
}
```



### 问题 2：图片资源找不到（ResourceNotFoundException）

**现象描述**：
运行应用时崩溃，提示找不到 `R.drawable.dice_x` 资源。

**原因分析**：
`res/drawable/` 目录下没有对应的骰子图片文件。

**解决过程**：

1. 确认图片文件命名规范：小写字母、数字、下划线
2. 将 6 张骰子图片放入 `app/src/main/res/drawable/` 目录
3. 执行 `Build` → `Rebuild Project` 重新编译

### 问题 3：断点不生效

**现象描述**：
设置断点后使用 Debug 模式运行，程序没有在断点处暂停。

**原因分析**：
使用了 Run 模式而非 Debug 模式启动应用。

**解决过程**：

1. 确认点击的是 Debug 按钮（小虫子图标），而不是 Run 按钮（绿色三角形）
2. 检查 `build.gradle.kts` 中 `isDebuggable = true` 设置
3. 重新启动 Debug 模式

------

## 七、实验结论

### 7.1 核心技术理解

**为什么按钮点击后图片能够自动刷新？**

这是 Jetpack Compose 状态驱动 UI 机制的结果：

1. `mutableStateOf(6)` 创建了一个可观察的状态
2. 当 `diceResult` 值改变时，Compose 运行时自动标记相关组件需要重组
3. `Image` 组件读取了 `diceResult` 的值，因此会被重新执行
4. 新的 `imageResource` 被计算并传递给 `painterResource`，图片随之更新

整个过程不需要开发者编写任何 `updateUI()` 或 `notifyDataSetChanged()` 代码，体现了声明式 UI 框架的简洁性。

### 7.2 调试结果验证

**调试器中看到的变量值与界面结果是否一致？**

**完全一致**。通过调试器观察：

- `diceResult` 的值变化后，界面上的骰子图片立即切换
- `randomValue` 生成的值与 `diceResult` 赋值的值相同
- `getDiceImageResource()` 返回的资源 ID 与预期骰子点数匹配

这证明了应用逻辑的正确性，也验证了 Compose 状态管理机制的可靠性。

### 7.3 实验收获

1. **掌握 Compose 状态管理**：学会了使用 `remember` + `mutableStateOf` 管理 UI 状态
2. **熟练使用调试器**：能够灵活运用 Step Into、Step Over、Step Out 进行代码调试
3. **理解声明式 UI**：认识到状态驱动 UI 刷新的优势和工作原理
4. **问题解决能力**：遇到 Preview 加载问题时能够独立排查解决
