# Lab5 Art Space 实验报告

## 1. 应用内容
本应用展示 3 幅世界名画：
1. 星空 - 梵高（1889）
2. 罗纳河上的星夜 - 梵高（1888）
3. 向日葵 - 梵高（1888）

## 2. 界面结构
界面分为 3 个核心区块：
1. **作品展示区**：使用 Surface + Image 实现画框效果
2. **作品信息区**：使用 Column + Text 显示标题、作者、年份
3. **按钮控制区**：使用 Row 包裹两个 Button

布局嵌套：
Column（整体）
  ├─ ArtworkDisplay（作品）
  ├─ ArtworkInfo（信息）
  └─ ControlButtons（按钮）

## 3. 状态管理
使用 Jetpack Compose 状态：
var currentArtwork by remember { mutableStateOf(1) }
通过状态记录当前作品编号，所有图片和文字都根据这个状态动态切换。

## 4. 按钮逻辑
- Next：1→2→3→1 循环
- Previous：1→3→2→1 循环
使用 when 表达式实现边界判断，比 if-else 更简洁。

## 5. 遇到的问题与解决
1. 图片不显示：检查 drawable 文件名与代码一致
2. 按钮点击无效：确认状态变量使用 by 委托
3. 布局混乱：使用 Arrangement.SpaceBetween 自动分配间距