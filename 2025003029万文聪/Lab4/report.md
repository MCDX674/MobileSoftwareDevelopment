# Lab4：Dice Roller 交互应用与 Android Studio 调试实验报告
姓名：万文聪
学号：2025003029

一、应用界面结构说明
本次实验使用 Kotlin + Jetpack Compose 完成掷骰子应用，界面纯 Compose 实现。
界面结构：
- MainActivity：应用入口，加载 Compose 界面
- DiceRollerApp：根函数，Column 布局居中
- 包含 Image 显示骰子，Button 触发掷骰子
布局：Modifier.fillMaxSize()，水平居中，垂直居中。

二、Compose 状态保存骰子结果
使用状态管理：
var result = remember { mutableIntStateOf(1) }
- remember：防止重组时数据丢失
- mutableIntStateOf：可观察状态，初始值1
- result.intValue 变化时自动重组界面。

三、根据点数切换图片资源
when 表达式映射点数到图片：
val imageResource = when (result.intValue) {
    1 -> R.drawable.dice_1
    2 -> R.drawable.dice_2
    3 -> R.drawable.dice_3
    4 -> R.drawable.dice_4
    5 -> R.drawable.dice_5
    else -> R.drawable.dice_6
}
Image 通过 painterResource 加载，contentDescription 设为点数。

四、断点设置与观察内容
设置两个断点：
1. DiceRollerApp() 调用处，观察启动流程。
2. val imageResource 赋值处，观察点数与图片映射。
调试观察：点击 Roll 后 result.intValue 随机变化，触发重组，imageResource 重新计算，界面刷新。

五、调试功能使用体会
- Step Into：进入函数内部
- Step Over：逐行执行
- Step Out：跳出当前函数
通过调试理解了状态变化 → 重组 → UI 自动更新的过程。

六、遇到的问题与解决
1. 图片资源找不到：导入 dice_1 至 dice_6 到 drawable 后同步解决。
2. 布局不居中：添加 verticalArrangement = Arrangement.Center 解决。
3. 状态不更新：改用 remember + mutableIntStateOf 标准写法。
4. 按钮文字样式：使用 MaterialTheme 统一风格。
5. 调试时界面不显示：点击 Resume Program 继续运行。

七、实验结论
1. 应用实现随机掷骰子，图片正确切换。
2. 掌握 Compose 状态管理。
3. 通过 when 映射图片资源。
4. 调试验证了状态驱动 UI 机制。
5. 界面与功能符合实验要求。