# Lab5 Art Space 应用实验报告
## 一、应用展示内容
1. **应用主题**
本应用是基于 Android Jetpack Compose 开发的**数字艺术画廊展示应用**，核心功能为轮播展示艺术作品，包含作品图片、作品标题、艺术家名称、创作年份，为用户提供简洁美观的艺术作品浏览体验。
2. **作品数量**
应用内置 **3 幅艺术作品**，每幅作品均包含完整的图片资源、标题、作者和年份信息，支持循环切换展示。

## 二、界面结构说明
### 1. 界面区块划分
应用界面按照功能逻辑划分为**三大独立区块**：
- **艺术作品墙**：展示艺术作品图片，模拟带边框的画框效果
- **艺术作品说明区**：展示当前作品的标题、艺术家和创作年份
- **显示控制器**：包含 Previous 和 Next 两个按钮，用于切换作品

### 2. 核心可组合项
- 布局类：`Column`、`Row`、`Surface`
- 展示类：`Image`、`Text`
- 交互类：`Button`
- 状态管理：`remember`、`mutableStateOf`

### 3. 组件嵌套结构
```
MainActivity（应用入口）
  ↓
MaterialTheme + Surface（全局主题与背景）
  ↓
ArtSpaceApp（主界面容器）
  ↓
Column（根垂直布局）
  ├─ ArtworkWall（作品展示组件）
  │    ↳ Surface + Image
  ├─ ArtworkDescriptor（作品信息组件）
  │    ↳ Column + Text
  └─ DisplayController（按钮控制组件）
       ↳ Row + Button
```

### 4. 布局特性
- 根布局使用 `Column` 铺满屏幕，内边距 24dp，子组件水平居中、垂直均匀分布
- 按钮区域使用 `Row` 水平排列，设置间距保证界面美观
- 作品展示区使用 `Surface` 实现边框和阴影，提升视觉效果

## 三、Compose 状态管理实现
应用通过 **状态索引** 管理当前展示的作品，核心实现如下：
1. **状态变量定义**
```kotlin
var currentArtworkIndex by remember { mutableStateOf(0) }
```
- `remember`：保证屏幕旋转等配置变更时，状态不会丢失
- `mutableStateOf(0)`：创建可观察状态，初始值为 0（默认展示第一幅作品）
- `by` 关键字：简化状态的读写操作，无需手动调用 `.value`

2. **状态使用逻辑**
定义作品列表 `artworkList`，通过状态索引获取当前展示的作品：
```kotlin
artworkList[currentArtworkIndex]
```
当索引状态发生变化时，Compose 会自动重组界面，同步更新图片、标题、作者等所有信息。

## 四、Next / Previous 按钮条件逻辑说明
按钮通过**边界判断**实现作品**循环切换**，避免数组越界异常：
### 1. Previous（上一个）按钮逻辑
```kotlin
onPrevious = {
    currentArtworkIndex = when (currentArtworkIndex) {
        0 -> artworkList.size - 1  
        else -> currentArtworkIndex - 1  
    }
}
```

### 2. Next（下一个）按钮逻辑
```kotlin
onNext = {
    currentArtworkIndex = when (currentArtworkIndex) {
        artworkList.size - 1 -> 0  
        else -> currentArtworkIndex + 1  
    }
}
```

### 3. 核心效果
无论点击哪个按钮，都能实现**首尾循环**，用户可以无限切换作品，无卡顿、无崩溃。

## 五、遇到的问题与解决过程
1. **问题一：界面点击按钮无刷新，作品不切换**
   - 原因：初始未使用 `remember` 包裹状态，Compose 无法观察状态变化
   - 解决：使用 `remember { mutableStateOf(0) }` 定义状态变量，触发界面重组

2. **问题二：按钮点击导致应用崩溃（数组越界）**
   - 原因：未做边界判断，索引为 0 时减 1、为最后一位时加 1，超出列表范围
   - 解决：使用 `when` 表达式判断边界，实现首尾循环切换

3. **问题三：作品图片无画框效果，界面单调**
   - 原因：直接使用 `Image` 组件，无装饰样式
   - 解决：嵌套 `Surface` 组件，添加 `border` 边框、`shadowElevation` 阴影，模拟画框

4. **问题四：按钮布局拥挤、样式不统一**
   - 原因：按钮无间距、宽度自适应，界面不美观
   - 解决：使用 `Arrangement.spacedBy` 设置间距，为按钮设置固定宽度 `120.dp`

## 六、实验总结
本次实验完成了 Art Space 艺术画廊应用的开发，实现了**静态界面搭建**与**交互功能开发**两大目标：
1. 熟练使用 `Column`、`Row`、`Image`、`Button` 等 Compose 基础组件构建界面
2. 掌握 `remember` + `mutableStateOf` 状态管理方式，实现数据驱动界面更新
3. 使用 `when` 表达式完成按钮的循环切换逻辑，解决边界异常问题
4. 通过模块化拆分组件（`ArtworkWall`、`ArtworkDescriptor`、`DisplayController`），让代码结构更清晰、易于维护
5. 应用可正常运行，支持 3 幅作品循环切换，满足实验全部验收标准