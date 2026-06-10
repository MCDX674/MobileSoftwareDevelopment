# ArtSpace 应用开发报告
## 1. 应用展示内容
### 主题
本应用为艺术作品展示应用（ArtSpace），以经典世界名画为核心，实现图片浏览、作品信息展示、前后切换的完整功能。

### 作品数量
共展示 3 幅经典艺术作品，支持循环切换，同时展示每幅作品的标题、作者、创作年份。
- 《呐喊》- 爱德华・蒙克
- 《星月夜》- 文森特・梵高
- 《戴珍珠耳环的少女》- 约翰内斯・维米尔

## 2. 界面结构说明
### 界面整体区块划分
整个界面垂直分为 3 个核心区块：
1. 作品展示区：带画框样式的图片展示区域
2. 作品信息区：展示作品标题、作者、年份
3. 按钮控制区：上一张 / 下一张 切换按钮

### 使用的可组合项（Composable）
- ArtSpaceApp()：主界面容器，统筹全局状态与整体布局
- ArtworkDisplay()：作品图片展示组件，包含画框、圆角、阴影效果
- ArtworkInfo()：作品文字信息展示组件
- ControlButtons()：切换按钮组件，封装“上一张”“下一张”逻辑

### 嵌套结构
Column（整体页面）
├─ ArtworkDisplay（图片展示）
│   └─ Surface + Image
├─ ArtworkInfo（作品信息）
│   └─ Column + Text + Spacer
└─ ControlButtons（按钮区）
    └─ Row + Button ×2

## 3. Compose 状态管理说明
使用 Jetpack Compose 状态管理实现当前作品索引的记录与刷新。

### 可观察状态定义
var currentArtwork by remember { mutableStateOf(1) }
- remember：保证配置变更（如屏幕旋转）状态不丢失
- mutableStateOf：提供可观察状态，值变化时自动刷新 UI

所有图片、标题、作者、年份均通过 currentArtwork 动态获取。

## 4. Next / Previous 按钮条件逻辑
### 上一张（Previous）
currentArtwork = if (currentArtwork == 1) 3 else currentArtwork - 1
- 当前为第 1 张 → 切换到最后 1 张（第 3 张）
- 否则 → 序号减 1

### 下一张（Next）
currentArtwork = if (currentArtwork == 3) 1 else currentArtwork + 1
- 当前为第 3 张 → 切换到第 1 张
- 否则 → 序号加 1

实现循环切换效果，不会出现越界崩溃。

## 5. 遇到的问题与解决过程
### 问题 1：应用运行闪退
- 原因：缺少对应名称的图片资源；包名不统一、资源引用错误
- 解决：放入 artwork1 / artwork2 / artwork3 图片；统一项目包名，删除错误导入

### 问题 2：图片无法占满画框，四周留白
- 原因：使用了 .padding(16.dp)；未设置图片缩放模式
- 解决：删除多余内边距；添加 contentScale = ContentScale.Crop，图片自动撑满整个展示区域

### 问题 3：运行后显示旧界面
- 原因：Android Studio 构建缓存；模拟器未卸载旧 APP
- 解决：Clean Project + Rebuild Project；模拟器卸载旧 APP 后重新运行

### 问题 4：代码报错、大括号不匹配
- 原因：语法书写错误；函数结构不完整
- 解决：规范代码缩进；统一函数与状态结构

## 6. 总结
本应用基于 Jetpack Compose 完成艺术作品展示，实现了状态管理、组件化拆分、循环切换逻辑、界面美化等功能，结构清晰、可扩展性强。