# Lab11 实验报告：为 Sports 应用添加大屏自适应布局

## 一、实验概述
本次实验基于 Jetpack Compose 的 `material3-window-size-class` 库，为 Sports 运动资讯应用实现大屏自适应布局。
应用原本仅支持手机单栏布局，在平板等大屏设备上体验较差。
实验目标：根据屏幕宽度自动切换「单栏模式」与「列表+详情双栏模式」，完成完整的自适应 UI 开发。

## 二、核心概念说明
### 1. WindowSizeClass
`WindowSizeClass` 是 Android 提供的屏幕尺寸分类工具，用于判断设备属于手机、折叠屏还是平板。
宽度分为三类：
- **Compact**：手机（小屏）
- **Medium**：折叠屏/小平板
- **Expanded**：平板（大屏）

本项目中，只有 `Expanded` 模式启用双栏布局。

### 2. SportsContentType
定义两种内容展示类型：
- `ListOnly`：仅列表，小屏使用
- `ListAndDetail`：列表+详情同时显示，大屏使用

通过枚举让布局逻辑更清晰。

## 三、实现思路
1. 在 `MainActivity` 中计算屏幕宽度类别 `WindowWidthSizeClass`。
2. 将尺寸类别传入 `SportsApp`，判断使用哪种布局类型。
3. 大屏使用 `Row` 实现左右分栏：列表占 1 份，详情占 2 份。
4. 小屏保持原有页面跳转逻辑。
5. 大屏隐藏返回按钮，按返回键直接退出应用。
6. 小屏详情页显示返回按钮，可回到列表。

## 四、关键实现步骤
### 1. MainActivity 计算窗口大小
使用 `calculateWindowSizeClass` 获取屏幕尺寸，并传递给 `SportsApp`。

### 2. SportsApp 根据尺寸切换布局
当宽度为 `Expanded` 时，使用 `ListAndDetail` 双栏布局；
否则使用 `ListOnly` 单栏布局。

### 3. 双栏布局 SportsListAndDetails
使用 `Row` + `weight` 实现左右分配：
- 左侧列表：`weight(1f)`
- 右侧详情：`weight(2f)`

### 4. 自适应 AppBar
- 大屏：始终显示 Sports，无返回键
- 小屏：列表页 Sports，详情页 Sport Info + 返回键

### 5. 返回键处理
- 小屏：返回上一页
- 大屏：直接退出应用

## 五、测试结果
- 手机（Compact）：正常单栏运行，列表可跳转详情
- 平板（Expanded）：自动双栏布局，左边列表右边详情
- 点击列表项：大屏右侧实时更新，无需跳转
- 返回键行为：符合预期，无崩溃
- UI 布局：正常显示，无错位

## 六、遇到的问题与解决方法
1. **运行闪退**
   原因：图片资源、状态获取方式不稳定
   解决：简化 UI，使用稳定状态获取方式。

2. **大屏无法切换双栏**
   原因：未正确获取 WindowSizeClass
   解决：在 MainActivity 正确计算并传递宽度类别。

3. **返回键在大屏错误返回列表**
   解决：为双栏布局添加独立 BackHandler，直接退出 Activity。

## 七、实验总结
本次实验成功完成了：
- 屏幕尺寸自适应布局
- 列表+详情大屏模式
- 不同屏幕的导航与返回键处理
- 完整的 Compose 自适应 UI 开发流程

通过实验掌握了 Android 大屏适配的核心思想与实现方法。