# 数字艺术空间应用开发报告

## 一、应用界面结构说明
## 本应用基于 Android Jetpack Compose 实现，整体界面采用垂直居中结构：

### 1. 外层使用 Column 作为根布局，设置为全屏并居中显示；
### 2. 上方是 ArtworkWall 组件，包含 Surface 与 Image，用于显示当前艺术作品图片；
### 3. 中间是 ArtworkDescriptor 组件，包含 Surface 与 Column，展示作品标题、作者与年份；
### 4. 下方是 DisplayController 组件，包含 Row 与两个 Button，用于触发上一张/下一张切换逻辑。

## 整个界面简洁清晰，所有显示内容由状态数据驱动更新。

## 二、使用 Compose 状态管理当前作品索引
## 作品索引使用 Compose 的状态机制保存，核心代码：
```kotlin
var currentIndex by remember { mutableIntStateOf(0) }
```

## 三、Next / Previous 按钮条件逻辑说明
### 按钮点击逻辑实现循环切换作品，避免数组越界：

### 1. Previous 按钮逻辑
#### 当当前为第一幅作品（索引为 0）时，点击跳转到最后一幅作品；否则索引减 1。核心代码：
```kotlin
currentIndex = if (currentIndex == 0) artworks.size - 1 else currentIndex - 1
```
### 2. Next 按钮逻辑
#### 当当前为最后一幅作品（索引为 artworks.size - 1）时，点击跳转到第一幅作品；否则索引加 1。核心代码：
```kotlin
currentIndex = if (currentIndex == artworks.size - 1) 0 else currentIndex + 1
```
## 四、应用展示内容说明
### 本应用共展示 3 幅艺术作品，包含静物、风景、抽象三类主题。用户可通过按钮循环查看图片，并同步显示作品标题、作者及创作年份信息。

## 五、遇到的问题与解决过程
### 1. R 类报错与包名不匹配问题
#### 问题：无法识别 R.drawable.artwork_1，提示 Classifier 'class R' does not have a companion object。解决：通过 AndroidManifest.xml 确认项目包名，修改代码第一行 package 声明，并同步项目，问题解决。

### 2. 图片无法正常显示
#### 问题：界面显示系统默认图标，而非本地图片。解决：将图片正确放入 res/drawable 目录，并命名为 artwork_1.jpg、artwork_2.jpg、artwork_3.jpg，确保全小写命名，同步项目后图片正常加载。

### 3. 界面不随按钮更新
#### 问题：点击按钮无反应，界面不切换作品。解决：使用 remember { mutableIntStateOf(0) } 正确管理状态变量，并将界面与状态绑定，实现点击自动重组。

### 4. 布局混乱、元素不对齐
#### 问题：图片和文字位置错乱，布局不整齐。解决：为根 Column 设置 horizontalAlignment 与 verticalArrangement，并为组件添加 padding，实现居中与美观布局。