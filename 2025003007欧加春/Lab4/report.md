# Dice Roller 实验报告

## 实验目标
完成一个交互式的 Dice Roller 应用，使用 Jetpack Compose 构建界面，并使用 Android Studio 调试器观察程序运行过程。

## 应用界面结构

### 整体结构
- 应用使用 `Column` 容器作为根布局，设置为全屏显示
- 水平方向居中对齐，垂直方向居中排列
- 包含一个骰子图片和一个 "Roll" 按钮

### 核心组件
1. **Image 组件**：用于显示骰子图片，根据当前点数动态更新
2. **Button 组件**：用于触发掷骰子操作
3. **状态管理**：使用 `remember { mutableStateOf(1) }` 保存当前骰子点数

## 实现细节

### 状态管理
使用 Compose 的状态管理机制保存骰子点数：
```kotlin
var result by remember { mutableStateOf(1) }
```
当 `result` 值发生变化时，Compose 会自动触发界面重组，更新显示的图片。

### 图片切换逻辑
根据当前点数使用 `when` 表达式选择对应的图片资源：
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

### 点击事件处理
按钮点击时生成 1-6 的随机数并更新状态：
```kotlin
Button(onClick = { result = (1..6).random() }) {
    Text(text = "Roll")
}
```

## 调试过程

### 断点设置
1. 在 `MainActivity.kt` 的 `onCreate` 方法中设置断点，观察应用启动过程
2. 在 `DiceWithButtonAndImage` 函数中 `imageResource` 赋值处设置断点，观察状态变化

### 调试操作
1. 使用 **Debug 'app'** 模式启动应用
2. 当程序停在 `onCreate` 断点时，点击 `Step Into` 进入 `DiceRollerApp` 函数
3. 使用 `Step Over` 逐行执行代码，观察 `result` 变量的初始值
4. 点击 "Roll" 按钮，程序会停在 `imageResource` 断点处
5. 在 Variables 窗格中观察 `result` 值的变化
6. 使用 `Step Out` 返回上一层调用位置

### 观察结果
- 初始状态：`result` 值为 1，显示 dice_1 图片
- 点击按钮后：`result` 值随机变为 1-6 之间的数字，界面自动更新为对应图片
- 调试器中看到的变量值与界面显示一致

## 问题与解决

### 问题 1：图片资源导入
**解决方案**：将 dice_images 目录中的 XML 文件复制到项目的 res/drawable 目录中。

### 问题 2：状态更新机制
**解决方案**：使用 `remember { mutableStateOf(...) }` 确保状态在重组时保持，并通过委托属性语法简化代码。

## 结论

1. **状态驱动界面**：按钮点击后，通过更新 `result` 状态变量，Compose 自动触发界面重组，实现图片的自动刷新。

2. **调试工具的重要性**：通过设置断点和单步执行，可以清晰观察到变量值的变化过程，理解 Compose 状态管理的工作原理。

3. **代码结构优化**：将界面逻辑拆分为多个可组合函数，提高代码的可读性和可维护性。

4. **用户体验**：应用界面简洁明了，交互响应迅速，符合现代 Android 应用的设计标准。

## 项目结构

```
app/
└── src/
    └── main/
        ├── java/com/example/diceroller/
        │   └── MainActivity.kt
        └── res/
            └── drawable/
                ├── dice_1.xml
                ├── dice_2.xml
                ├── dice_3.xml
                ├── dice_4.xml
                ├── dice_5.xml
                └── dice_6.xml
```



