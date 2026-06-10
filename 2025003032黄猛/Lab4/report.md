# Lab4 实验报告：Dice Roller 交互应用与 Android Studio 调试

## 1. 应用界面结构说明

本实验使用 Jetpack Compose 构建界面，未使用 XML。

界面层级如下：

- MainActivity 在 onCreate() 中通过 setContent 加载 Compose 内容
- DiceRollerApp() 使用 Column 作为主布局容器
- DiceWithButtonAndImage() 包含一个 Image 和一个 Button
- Image 用于显示当前骰子结果图
- Button 点击后触发重新掷骰子

整体布局采用居中排版，便于观察每次点击后的图片变化。

## 2. 使用 Compose 状态保存骰子结果

在 DiceWithButtonAndImage() 中定义状态变量：

```kotlin
var result by remember { mutableStateOf(1) }
```

说明：

- remember 确保在重组期间保留状态
- mutableStateOf(1) 设置初始点数为 1
- result 变化时会触发 Compose 重组

因此点击按钮后只需要更新 result，界面会自动刷新，不需要手动刷新视图。

## 3. 根据点数切换图片资源

通过 when 表达式将点数映射到对应 drawable 资源：

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

再通过 painterResource(imageResource) 显示图片，实现点数与图片的同步变化。

## 4. 断点设置与观察内容

本次调试设置了两个断点：

1. MainActivity.onCreate() 中调用 DiceRollerApp() 的附近位置
2. DiceWithButtonAndImage() 中 imageResource 的 when 映射位置

观察到的现象：

- 程序首次进入时，result 初始值为 1
- 每次点击 Roll 按钮后，result 更新为 1~6 的随机整数
- imageResource 会随着 result 改变而切换到对应资源
- 调试变量中可看到类似 result$delegate 的委托字段，这是 Kotlin 状态委托的正常表现

## 5. Step Into / Step Over / Step Out 使用体会

- Step Into：用于进入 rollDice() 函数内部，确认随机数生成逻辑
- Step Over：用于逐行执行当前函数，快速观察状态赋值与分支映射
- Step Out：从当前函数返回上一层调用点，适合查看调用链关系

通过三种单步方式，可以更清楚地看到“按钮点击 -> 状态更新 -> 触发重组 -> 图片变化”的完整流程。

## 6. 遇到的问题与解决过程

问题：初始提交目录中没有 MainActivity.kt 与 report.md，且资源图片以单独素材目录形式提供。

解决：

- 新建 MainActivity.kt，按实验要求实现 Compose 交互逻辑
- 使用 remember + mutableStateOf 管理状态
- 使用 when 完成点数到图片资源映射
- 新建 report.md，补充实验过程、调试观察与结论

## 7. 实验结论

- 按钮点击后图片能够自动刷新，核心原因是 Compose 的状态驱动 UI 重组机制
- 调试器中观察到的 result 变量变化与界面显示结果一致
- 本实验验证了 Compose 声明式 UI 与 Android Studio 调试器联动分析的基本方法
