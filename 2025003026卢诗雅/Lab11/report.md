# Lab11：为 Sports 应用添加大屏自适应布局

## 1. WindowSizeClass 概念及 WindowWidthSizeClass 适用设备

### 1.1 WindowSizeClass 概念

`WindowSizeClass` 是 Jetpack Compose Material3 提供的窗口尺寸分类工具，用于抽象设备的窗口尺寸特征，而非直接绑定具体设备类型（如手机、平板）。它将窗口尺寸（宽度 / 高度）划分为预定义的类别，帮助开发者统一处理不同屏幕尺寸的布局适配，避免针对具体设备型号做硬编码适配，提升布局的通用性和可维护性。

### 1.2 WindowWidthSizeClass 三种宽度类别及适用设备

`WindowWidthSizeClass` 是基于窗口宽度的分类，包含三种核心类别：

|类别|宽度范围|适用设备|
|---|---|---|
|`Compact`（紧凑）|≤ 600dp|手机（竖屏）、小屏手机（横屏）|
|`Medium`（中等）|600dp \&lt; 宽度 \&lt; 840dp|大屏手机（横屏）、小型平板|
|`Expanded`（扩展）|≥ 840dp|平板（竖屏 / 横屏）、桌面设备、折叠屏展开状态|

在本项目中，核心适配了 `Compact` 和 `Expanded` 两类：`Compact` 对应手机等小屏设备，`Expanded` 对应平板等大屏设备；`Medium` 未单独处理，复用 `Compact` 的布局逻辑（`ListOnly`），符合移动端适配的 “最小成本覆盖多数场景” 原则。

## 2. SportsContentType 枚举设计思路

### 2.1 设计思路

`SportsContentType` 是基于窗口尺寸的**内容展示类型枚举**，核心作用是将 “窗口尺寸类别” 转换为 “业务可理解的布局类型”，解耦 “尺寸判断” 和 “布局渲染” 逻辑。其设计遵循 “单一职责” 原则：

- 尺寸判断层：`WindowWidthSizeClass` 负责识别设备窗口宽度类别；

- 业务布局层：`SportsContentType` 负责定义布局模式，让 `SportsApp` 只需根据枚举值渲染对应布局，无需关心具体尺寸数值。

### 2.2 仅设计 ListOnly 和 ListAndDetail 两种类型的原因

本 App 的核心内容是 “运动列表” 和 “运动详情”，适配场景可抽象为两种核心布局模式，无需更多类型：

1. `ListOnly`（仅列表）：单窗格布局，同一时间仅展示 “列表” 或 “详情”，适用于 `Compact`（手机）等小屏设备。小屏设备宽度有限，同时展示列表 \+ 详情会导致内容挤压、可读性下降，因此采用 “切换式” 单窗格；

2. `ListAndDetail`（列表 + 详情）：双窗格布局，同时展示列表和详情，适用于 `Expanded`（平板）等大屏设备。大屏设备宽度充足，双窗格布局可提升操作效率（用户无需在列表和详情间来回切换），符合大屏设备的交互习惯。

两种类型已覆盖本 App 的核心适配场景，若新增 `Medium` 适配，可基于现有枚举扩展（如复用 `ListOnly` 或新增 `ListAndDetailCondensed`），具备扩展性。

## 3. SportsListAndDetails 布局设计说明

### 3.1 整体布局结构

`SportsListAndDetails` 是大屏（`Expanded`）下的双窗格布局，核心容器为 `Row`，横向排列 “运动列表” 和 “运动详情” 两个子组件，整体逻辑如下：

```kotlin
Row(...) {
    // 左侧列表：权重1
    SportsList(modifier = Modifier.weight(1f), ...)
    // 右侧详情：权重2
    SportsDetail(modifier = Modifier.weight(2f), ...)
}
```

### 3.2 Row 中比例分配（1:2）的理由

1. **内容重要性**：详情页是用户查看的核心内容（包含图片、文字描述等），需要更多空间保证可读性；列表仅作为 “导航入口”，无需占用过多宽度；

2. **交互效率**：1:2 的比例符合大屏布局的 “黄金分割” 原则，列表宽度足够展示条目信息（不挤压），详情宽度足够承载完整内容（无需频繁滚动）；

3. **视觉平衡**：若列表占比过高（如 1:1），会导致详情区域狭窄，图片和文字展示不完整；若列表占比过低（如 1:3），则列表条目可能被截断，影响导航体验。

## 4. SportsAppBar 大屏 / 小屏行为差异的设计考虑

`SportsAppBar` 基于 `TopAppBar` 实现，核心差异在于 “标题文案” 和 “返回按钮显示”，设计考量如下：

### 4.1 标题文案差异

|场景|标题内容|设计考虑|
|---|---|---|
|大屏（ListAndDetail）/ 小屏列表页|列表标题（`list\_fragment\_label`）|大屏始终显示列表 + 详情，标题需体现 “列表主导”；小屏列表页是初始页面，标题需明确当前页面功能|
|小屏详情页|详情标题（`detail\_fragment\_label`）|小屏详情页是从列表页跳转而来，标题需明确当前页面为 “详情页”，符合用户的页面认知|

### 4.2 返回按钮显示差异

|场景|返回按钮状态|设计考虑|
|---|---|---|
|大屏（ListAndDetail）/ 小屏列表页|隐藏|大屏无 “返回” 逻辑（始终显示双窗格）；小屏列表页是初始页面，返回按钮无意义（返回会退出 App），隐藏可简化 UI；|
|小屏详情页|显示|小屏详情页是从列表页跳转，返回按钮需让用户回到列表页，符合移动端 “层级导航” 的交互习惯；|

### 4.3 设计核心原则

- **一致性**：大屏始终展示 “列表 + 详情”，AppBar 无需返回按钮，保持 UI 简洁；

- **可用性**：小屏详情页必须提供返回入口，避免用户 “迷路”；

- **语义化**：标题文案与当前展示内容匹配，降低用户认知成本。

## 5. 返回键处理策略：小屏 vs 大屏

### 5.1 行为差异

|场景|返回键行为|
|---|---|
|小屏列表页|退出 App（系统默认行为）|
|小屏详情页|回到列表页（通过 `BackHandler` 拦截，调用 `viewModel\.navigateToListPage\(\)`）|
|大屏（ListAndDetail）|退出 App（通过 `BackHandler` 拦截，调用 `Activity\.finish\(\)`）|

### 5.2 行为不同的原因

1. **小屏详情页**：小屏采用 “层级导航”（列表→详情），返回键需符合用户的 “返回上一级” 预期，因此拦截返回事件，切换回列表页；

2. **大屏模式**：大屏采用 “并列导航”（列表和详情同时展示），无 “上一级页面” 概念，用户点击返回键的预期是 “退出 App”，因此直接调用 `Activity\.finish\(\)`；

3. **系统交互一致性**：小屏列表页未拦截返回键，复用系统默认行为（退出 App），避免过度拦截导致交互混乱；大屏主动拦截返回键并退出 App，是因为大屏布局下 `BackHandler` 无默认行为，需显式处理以符合用户预期。

## 6. 实验中遇到的问题与解决过程

### 6.1 问题 1：大屏模式下 Padding 适配异常

#### 现象

大屏双窗格布局中，`SportsList` 和 `SportsDetail` 的内边距（`contentPadding`）未正确适配，导致内容与屏幕边缘重叠。

#### 原因

`contentPadding` 是 `Scaffold` 传递的内边距（包含 TopAppBar 高度），大屏 `Row` 布局直接复用该 padding 时，未区分 “横向” 和 “纵向” padding，且未适配布局方向（LTR/RTL）。

#### 解决过程

1. 拆分 `contentPadding`：仅复用 `top` 方向的 padding，横向 padding 改用固定尺寸资源（`dimensionResource\(R\.dimen\.padding\_medium\)`）；

2. 使用 `LocalLayoutDirection` 处理布局方向：通过 `calculateStartPadding`/`calculateEndPadding` 适配左右 / 右左布局；

3. 调整 `SportsListAndDetails` 的 modifier：

    ```kotlin
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = dimensionResource(R.dimen.padding_medium),
                start = dimensionResource(R.dimen.padding_medium),
                end = dimensionResource(R.dimen.padding_medium),
            )
    )
    ```

### 6.2 问题 2：小屏详情页返回键无响应

#### 现象

小屏进入详情页后，点击物理返回键无反应，仅点击 AppBar 的返回按钮有效。

#### 原因

仅在 `SportsAppBar` 中提供了返回按钮，但未处理物理返回键（系统级返回事件）。

#### 解决过程

1. 在 `SportsDetail` 中添加 `BackHandler` 拦截系统返回事件：

    ```kotlin
    BackHandler {
        onBackPressed()
    }
    ```

2. 将返回逻辑从 AppBar 按钮点击透传至 `SportsDetail`，保证 “虚拟返回按钮” 和 “物理返回键” 行为一致；

3. 大屏模式下，在 `SportsListAndDetails` 中单独处理 `BackHandler`，确保返回键直接退出 App。

### 6.3 问题 3：WindowSizeClass 预览失效

#### 现象

在 Compose Preview 中无法正确模拟 `Expanded` 尺寸的 `WindowSizeClass`，预览始终显示 `Compact` 布局。

#### 原因

Preview 默认使用手机尺寸，且未显式设置 `WindowWidthSizeClass` 参数。

#### 解决过程

1. 为预览函数指定宽高：`@Preview\(widthDp = 840, heightDp = 600\)`（匹配 `Expanded` 阈值）；

2. 在预览中显式传递 `windowWidthSizeClass` 参数：

    ```kotlin
    @Preview(widthDp = 840, heightDp = 600)
    @Composable
    fun SportsAppExpandedPreview() {
        SportsTheme {
            SportsApp(windowWidthSizeClass = WindowWidthSizeClass.Expanded)
        }
    }
    ```

3. 新增 `SportsListAndDetailsPreview` 单独预览双窗格布局，验证大屏适配效果。

### 6.4 问题 4：列表项点击后大屏详情页未更新

#### 现象

大屏模式下点击列表项，详情页未刷新为选中的运动信息。

#### 原因

`SportsViewModel` 的 `currentSport` 状态未正确通知重组，且大屏布局未监听 `uiState` 的变化。

#### 解决过程

1. 确保 `uiState` 使用 `MutableStateFlow` 管理，且 `currentSport` 变更时触发状态更新；

2. 在 `SportsListAndDetails` 中通过 `collectAsState` 监听 `uiState`，保证状态变更时重组：

    ```kotlin
    val uiState by viewModel.uiState.collectAsState()
    SportsListAndDetails(
        currentSport = uiState.currentSport,
        // 其他参数
    )
    ```

3. 列表项点击时调用 `viewModel\.updateCurrentSport\(it\)`，确保 `currentSport` 状态正确更新。

## 总结

本项目基于 `WindowSizeClass` 实现了 “小屏单窗格、大屏双窗格” 的自适应布局，核心通过枚举解耦尺寸判断和布局渲染，通过权重分配、AppBar 差异化设计、返回键分层处理，兼顾了不同设备的交互体验。实验过程中解决了 padding 适配、返回键响应、预览失效、状态更新等问题，最终实现了一套符合 Material3 设计规范、适配多设备的自适应布局方案。

