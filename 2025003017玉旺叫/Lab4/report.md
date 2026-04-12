一、应用界面结构说明
本应用基于 Jetpack Compose 构建，整体采用垂直布局 Column 作为核心容器。
界面主要分为三部分：
1.Image 组件：用于显示当前骰子点数的图片。
2.Spacer：用于设置图片与按钮之间的间距，使界面更美观。
3.Button 组件：点击后随机生成骰子点数并刷新界面。
整个界面居中显示，布局简洁清晰，完全使用 Compose 声明式 UI 实现，无 XML 布局文件。
二、使用 Compose 状态保存骰子结果的方法
本次实验使用 remember + mutableStateOf 实现状态保存：
var result by remember { mutableStateOf(1) }
remember：让状态在界面重组时不会丢失，保持数据持续性。
mutableStateOf(1)：创建可观察的状态，当状态改变时，自动刷新界面。
按钮点击时执行 result = (1..6).random()，随机更新骰子点数。
三、根据点数切换图片资源的实现
通过 when 表达式将点数映射到对应的图片资源：
val imageResource = when(result) {
    1 -> R.drawable.dice_1
    2 -> R.drawable.dice_2
    3 -> R.drawable.dice_3
    4 -> R.drawable.dice_4
    5 -> R.drawable.dice_5
    else -> R.drawable.dice_6
}
每次 result 变化时，imageResource 会自动更新，Image 组件加载对应图片，实现点数与图片同步切换。
四、断点设置与观察结果
断点 1：在 DiceRollerApp() 调用处设置断点，用于观察应用启动流程。
断点 2：在 val imageResource = when(result) 处设置断点，用于观察状态变量变化。
调试时可观察到：
初始状态 result = 1。
点击 Roll 按钮后，result 变为 1~6 随机数。
imageResource 随点数同步更新。
五、Step Into、Step Over、Step Out、Resume Program 使用体会
Step Into (F7)：进入函数内部，查看代码执行细节。
Step Over (F8)：逐行执行代码，不进入子函数。
Step Out (Shift+F8)：跳出当前函数，回到上层调用位置。
Resume Program (F9)：继续执行程序，直到下一个断点。
通过这些调试操作，可以清晰观察变量变化、程序执行流程，有效排查问题。
六、遇到的问题及解决方法
问题：点击按钮后骰子图片不变化。
原因：Image 组件写死了 R.drawable.dice_1。
解决：改为使用动态变量 imageResource。
问题：调试时程序停在断点，模拟器不显示界面。
原因：断点暂停了程序执行。
解决：按 F9 继续运行即可显示完整界面。
七、实验结论
本次实验成功完成 Dice Roller 掷骰子应用，实现了：
Compose 声明式 UI 构建界面
状态管理保存骰子点数
点击按钮随机刷新点数与图片
Android Studio 断点调试与变量观察
按钮点击后图片自动刷新的原因是：Compose 状态驱动界面重组，当 result 变化时，依赖该状态的 Image 组件自动重新绘制。
调试过程中变量值与界面显示完全一致，验证了程序逻辑正确、状态管理有效。