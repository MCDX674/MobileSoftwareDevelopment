# Lab5：Art Space 应用开发实验报告
## 一、应用展示内容
本应用为数字艺术空间展示应用，核心主题为**多元艺术作品展示**，涵盖手绘静物、自然风光摄影、经典世界名画三类艺术内容，共计展示3幅独立艺术作品，分别为：
1. 手绘静物作品《Still Life of Blue Rose and Other Flowers》，作者Owen Scott，创作年份2021年
2. 自然风光摄影作品《山》，作者zyl，创作年份2025年
3. 后印象派经典名画《The Starry Night》（星月夜），作者Vincent Willem van Gogh，创作年份1889年

用户可通过按钮切换作品，完整查看每幅作品的画面与相关信息。

## 二、界面结构说明
本应用严格按照实验要求，将界面划分为**艺术作品墙、艺术作品说明、显示控制器**三大核心区块，整体采用Jetpack Compose声明式布局，所有可组合项的嵌套关系与使用说明如下：

### 整体布局框架
以`Column`作为根布局容器，设置`fillMaxSize()`填充全屏，通过`horizontalAlignment`和`verticalArrangement`配置全局居中对齐与垂直方向的空间分布，垂直排列三大核心区块，区块间通过`Spacer`控制固定间距。

### 各区块详细结构
1.  **艺术作品墙区块**
    - 核心可组合项：自定义`ArtworkWall`组件，内部嵌套`Surface`→`Box`→`Image`
    - 功能与实现：`Surface`组件配合`shadow`、`border`属性实现带阴影与边框的画框视觉效果；`Box`组件控制图片内边距；`Image`组件加载对应作品的图片资源，设置`contentScale = ContentScale.Fit`保证图片按原比例完整展示，同时配置`contentDescription`支持无障碍功能。

2.  **艺术作品说明区块**
    - 核心可组合项：自定义`ArtworkDescriptor`组件，内部嵌套`Surface`→`Column`→`Text`
    - 功能与实现：`Surface`组件设置浅灰色背景与圆角，作为信息卡片容器；内部`Column`垂直排列两个`Text`组件，分别展示作品标题、艺术家与创作年份，通过`fontSize`、`fontWeight`、`color`属性区分文本层级，优化信息可读性。

3.  **显示控制器区块**
    - 核心可组合项：自定义`DisplayController`组件，内部嵌套`Row`→`Button`+`Spacer`+`Button`
    - 功能与实现：`Row`组件水平排列两个按钮，通过`SpaceBetween`实现两端对齐；两个`Button`组件分别实现「上一个」「下一个」作品切换功能，配置了统一的圆角、背景色、尺寸样式，通过`weight`属性保证两个按钮宽度一致。

## 三、Compose 状态管理当前作品索引
本应用完全遵循Jetpack Compose的声明式UI设计范式，通过状态驱动界面更新，核心的作品索引状态管理实现如下：

1.  **状态定义与持久化**
    使用`var currentArtworkIndex by remember { mutableIntStateOf(0) }`定义核心状态变量：
    - `mutableIntStateOf(0)`创建可观察的Int类型状态，初始值为0，对应作品列表的第一个作品，状态变化时会自动触发关联UI的重组更新；
    - `remember`包裹状态，保证页面发生重组（如按钮点击、屏幕旋转）时，状态不会丢失，持续保留当前的作品索引；
    - 通过`by`委托语法简化状态的读写操作，无需手动调用`.value`即可直接修改与读取状态值。

2.  **状态与UI的联动**
    将所有作品的图片资源、标题、艺术家、年份信息封装到`Artwork`数据类中，存入`artworkList`列表。界面所有动态内容均通过`currentArtworkIndex`从列表中获取当前作品数据，实现「状态变化→UI自动更新」的单向数据流，无需手动修改组件内容，完全符合Compose的状态管理规范。

## 四、Next / Previous 按钮的条件逻辑说明
两个按钮均通过`when`表达式实现多分支条件逻辑，完成作品的循环切换，同时处理首尾边界场景，避免索引越界，具体逻辑如下：

### 核心前提
作品列表总长度为`totalCount = artworkList.size`（本次实现为3），作品索引的合法范围为`0 ~ totalCount - 1`（即0、1、2）。

1.  **Next 按钮逻辑**
    点击按钮时，执行以下索引更新逻辑：
    ```kotlin
    currentArtworkIndex = when (currentArtworkIndex) {
        totalCount - 1 -> 0
        else -> currentArtworkIndex + 1
    }
    ```
    - 边界处理：当当前索引为列表最后一位（即2，对应第三幅作品）时，点击后索引重置为0，跳转到第一幅作品，实现首尾循环；
    - 常规逻辑：其余场景下，索引自增1，切换到下一幅作品。

2.  **Previous 按钮逻辑**
    点击按钮时，执行以下索引更新逻辑：
    ```kotlin
    currentArtworkIndex = when (currentArtworkIndex) {
        0 -> totalCount - 1
        else -> currentArtworkIndex - 1
    }
    ```
    - 边界处理：当当前索引为列表第一位（即0，对应第一幅作品）时，点击后索引设置为`totalCount - 1`（即2），跳转到最后一幅作品，实现反向循环；
    - 常规逻辑：其余场景下，索引自减1，切换到上一幅作品。

该实现采用`when`表达式替代长串`if-else`，代码可读性更高，同时具备良好的扩展性，后续新增作品时无需修改切换逻辑，仅需在列表中添加作品数据即可正常适配。

## 五、遇到的问题与解决过程
### 问题1：资源引用类报错（Unresolved reference）
- 问题现象：代码中出现主题、R文件、图片资源的未定义引用报错，无法正常编译。
- 根本原因：① 初始代码的包名、主题名与新建项目的默认包名`com.example.myapplication`、默认主题`MyApplicationTheme`不匹配；② 图片资源命名包含非法字符，存放路径错误，不符合Android资源文件规范。
- 解决过程：① 修改代码包名为项目实际路径，将主题引用替换为项目默认的`MyApplicationTheme`，同步导入对应主题包；② 按照Android资源命名规范，将图片重命名为`artwork_1`、`artwork_2`、`artwork_3`，仅保留小写字母、数字和下划线，统一存放到`res/drawable`目录下；③ 点击Sync同步Gradle文件，执行Rebuild Project重建项目，彻底解决引用报错。

### 问题2：Android资源链接失败（Android resource linking failed）
- 问题现象：编译时出现启动图标资源链接失败报错，无法完成项目构建。
- 根本原因：项目默认的应用启动图标资源文件损坏，导致Android资源链接器无法正常解析与编译资源。
- 解决过程：① 先执行Clean Project + Rebuild Project清理项目缓存，未解决问题；② 通过Android Studio的Image Asset工具，自动生成一套完整的默认启动图标，覆盖原有损坏的资源文件；③ 再次重建项目，资源链接报错完全解决。

### 问题3：界面布局与图片展示异常
- 问题现象：图片出现拉伸变形，三大区块在屏幕中分布错位，与设计原型不符。
- 根本原因：Image组件未设置正确的缩放类型，布局权重与间距配置不当，导致界面适配异常。
- 解决过程：① 给Image组件设置`contentScale = ContentScale.Fit`，保证图片按原始比例完整展示，不出现拉伸变形；② 给艺术作品墙组件设置`weight(1f)`，让图片区域自适应填充屏幕剩余空间，保证三个区块在不同尺寸的屏幕中都能合理分布；③ 通过`padding`、`Spacer`组件精确控制组件间的间距，优化界面细节，最终实现与设计原型完全一致的视觉效果。