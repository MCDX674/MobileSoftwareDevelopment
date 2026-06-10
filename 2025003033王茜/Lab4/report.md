## 一、应用界面结构说明
整个应用采用 Jetpack Compose 声明式 UI 构建，界面结构非常清晰：
1. MainActivity：应用入口，负责加载界面。
2. DiceRollerApp：根布局函数，统一管理整个页面样式。
3. DiceWithButtonAndImage：核心界面，包含：
   -   一个 Image：显示骰子图片;
   -    一个 Button：点击掷骰子;
   -    一个 Column：垂直排列图片和按钮，让它们居中显示

---

## 二、如何使用 Compose 状态保存骰子结果
1. 使用 Compose 状态管理 来保存和更新骰子点数：
   ```
   var result by remember { mutableIntStateOf(1) }
   ```
2. 作用说明：
remember：让状态在屏幕旋转、重组时不会丢失；
mutableIntStateOf(1)：创建一个可观察的整数状态，默认值为 1；
var result by：使用属性委托，让界面能自动感知数据变化并刷新。

---
## 三、 如何根据点数切换图片资源
使用 when 表达式 根据 result 的值匹配对应图片：
```
val imageResource = when(result) {
    1 -> R.drawable.dice_1
    2 -> R.drawable.dice_2
    3 -> R.drawable.dice_3
    4 -> R.drawable.dice_4
    5 -> R.drawable.dice_5
    else -> R.drawable.dice_6}
```
- 当 result = 1 → 显示 dice_1.png
- 当 result = 2 → 显示 dice_2.png
- 当 result = 3 → 显示 dice_3.png
- 当 result = 4 → 显示 dice_4.png
- 当 result = 5 → 显示 dice_5.png
- 最后用 else 保证 6 一定会显示
  
---
## 四、设置了哪些断点，分别观察了什么
我在以下关键位置设置断点：

1. var result by remember { mutableIntStateOf(1) }
    - 观察：状态初始化是否成功，默认值是否为 1
2. Button 的 onClick 内部
    - 观察：点击按钮时，随机数是否正确生成
3. when 语句处
    - 观察：点数是否正确匹配到对应图片
4. Image 组件处
   - 观察：图片资源 ID 是否正确加载


---
## 五、Step Into、Step Over、Step Out 使用体会
   - 调试界面刷新时，Step Over 最方便；
   - 想看状态如何更新时用 Step Into；
   - 函数看多了想返回用 Step Out。
   - 三者配合能清晰看到代码运行流程。
   
   
---
## 六、遇到的问题与解决过程
1、 问题 1：界面全黑 / 不显示
- 原因：modifier 拼写错误 → moderfier
- 解决：全部修正为 modifier

2、问题 2：图片和按钮偏上，不居中
- 原因：缺少垂直居中配置
- 解决：添加
plaintext
verticalArrangement = Arrangement.Center

3、问题 3：点击按钮无反应
- 原因：状态缺少 by 委托或导入缺失
- 解决：添加 var / val 委托导入

4、问题 4：R.string.roll 报错
- 原因：字符串未定义
- 解决：在 strings.xml 添加
plaintext
<string name="roll">Roll</string>

5、问题 5：图片不显示
- 原因：图片文件名不匹配或未放入 drawable
- 解决：确保有 dice_1 ~ dice_6 图片
  
---
## 七、总结
本次掷骰子应用使用 Jetpack Compose 完成，通过状态管理实现点数保存与界面刷新，使用 when 表达式切换图片，通过调试工具理解代码运行流程。过程中学习了 Compose 布局居中、状态管理、调试技巧等知识，成功完成了可交互、界面美观的掷骰子应用。