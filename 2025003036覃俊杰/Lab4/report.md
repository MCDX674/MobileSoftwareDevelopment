# Android 掷骰子应用（Dice Roller）实验报告

## 一、实验目的

1. 掌握 Android Studio 新建 Compose 项目的基本流程与基础配置方法。
2. 熟练使用 Jetpack Compose 实现 UI 布局、图片显示与按钮交互功能。
3. 理解 Compose 状态管理（remember + mutableStateOf）的核心原理，实现界面自动刷新。
4. 完成掷骰子应用开发，实现点击按钮随机切换骰子图片的完整功能。

## 二、实验环境

- 开发工具：Android Studio
- 开发语言：Kotlin
- UI 框架：Jetpack Compose
- 运行设备：Android 模拟器 / 实体设备

## 三、实验要求

1. 应用界面居中显示，包含骰子图片与 Roll 按钮。
2. 点击按钮随机生成 1-6 点数，并切换对应骰子图片。
3. 代码结构清晰，无语法错误，可正常编译运行。
4. 完成项目基础配置、资源导入与代码调试。

## 四、项目配置步骤

1. 新建 Empty Activity（Compose）项目，使用 Kotlin 语言。
2. 将 dice_1.png 至 dice_6.png 图片资源放入 res/drawable 目录。
3. 替换 MainActivity.kt 代码，修正包名、主题与语法错误。
4. 同步 Gradle，检查依赖与配置无误后运行项目。

## 五、功能实现

1. **界面布局**
   - 使用 Column 垂直布局，实现图片与按钮垂直居中排列。
   - 使用 fillMaxSize、Alignment.CenterHorizontally、Arrangement.Center 实现全屏居中。

2. **状态管理**
   - 使用 var result by remember { mutableStateOf(1) } 保存骰子点数。
   - 状态改变时自动重组 UI，更新图片显示。

3. **图片切换**
   - 通过 when 表达式将点数映射到对应图片资源。
   - 使用 Image + painterResource 加载并展示骰子图片。

4. **点击事件**
   - 按钮点击时调用 Random.nextInt(1,7) 生成随机点数。
   - 更新 result 状态，触发界面刷新。

## 六、运行结果

1. 应用启动正常，界面布局居中、美观。
2. 默认显示骰子点数 1 图片。
3. 点击 Roll 按钮，图片随机切换为 1-6 点。
4. 交互响应迅速，无报错、无闪退，满足所有实验要求。

## 七、常见错误与解决

1. 主题类报错：替换为项目自身主题 HwangTheme。
2. 布局方法报错：fillSize 改为正确的 fillMaxSize。
3. 图片不更新：使用 Compose 状态管理实现数据驱动 UI。
4. 资源报错：确保图片文件名正确并放入 drawable 目录。

## 八、实验总结

本次实验成功完成了基于 Jetpack Compose 的掷骰子应用开发，掌握了 Compose 声明式 UI 编写、状态管理、图片资源使用与按钮事件处理。实验符合所有要求，功能完整，运行稳定，有效理解了 Android Compose 开发的基础逻辑与核心思想。
