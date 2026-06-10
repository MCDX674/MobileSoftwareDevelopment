# Lab7 报告 — Courses Grid App

此文档按课程实验要求整理，记录实现思路、数据模型、布局方案及遇到的问题与解决方案。

## 1. 应用整体结构
- Topic 数据类：用于描述课程主题，字段包含主题名称（字符串资源 ID）、课程数量、主题图片（ drawable 资源 ID）。
- DataSource 对象：提供静态数据源，包含 24 条主题信息，将 Topic 实例组成列表供网格使用。
- TopicCard 组件：单个课程主题卡片的可组合函数，负责图片、名称、数量的展示及样式布局。
- CoursesGrid 组件：基于 LazyVerticalGrid 的两列网格布局，将所有 TopicCard 以网格形式展示，支持垂直滚动。
- MainActivity：应用入口，加载 CoursesGrid，作为可滚动网格的宿主。 
- 资源：strings.xml 提供 24 个主题名称；drawable/ 下存放 24 张主题图片及装饰图标 ic_grain.xml。

代码引用（文件名简述）
- app/src/main/java/com/example/hwang/model/Topic.kt
- app/src/main/java/com/example/hwang/data/DataSource.kt
- app/src/main/java/com/example/hwang/TopicCard.kt
- app/src/main/java/com/example/hwang/CoursesGrid.kt
- app/src/main/java/com/example/hwang/MainActivity.kt
- app/src/main/res/values/strings.xml
- app/src/main/res/drawable/ic_grain.xml
- app/src/main/res/drawable/architecture.jpg 等 24 张图片

## 2. Topic 数据类字段设计与理由
- name: @StringRes Int — 字符串资源 ID，方便本地化与统一管理文本。
- count: Int — 该主题下的课程数量，直接显示为数字。
- image: @DrawableRes Int — 主题图片资源ID，用于卡片图片展示。
设计要点：使用资源 ID 而非原始文本，便于国际化和资源统一管理，减少硬编码，提高可维护性。

## 3. 卡片布局实现思路（Topic Card）
- 采用单个 Card 作为容器。
- 内部布局：Image（68dp 正方形，左侧） + Spacer + Column（文本区域，右侧）
- Column 内：主题名称文本（bodyMedium）、间距 8dp 的行显示图标与课程数量（logo 使用 ic_grain，数字使用 labelMedium）。
- 文字与图片的对齐通过 Row 和 Column 的组合实现，确保不同屏幕下自适应。

## 4. 网格布局实现思路（Courses Grid）
- 使用 LazyVerticalGrid，列数固定为 2：GridCells.Fixed(2)
- contentPadding = 8.dp，卡片之间水平和垂直间距均为 8.dp（Arrangement.spacedBy）
- Grid 的数据源来自 DataSource.topics，逐条渲染 TopicCard
- 图片通过 painterResource(id = topic.image) 加载，文本通过 stringResource(id = topic.name) 加载

## 5. 遇到的问题与解决过程
- 问题：构建时提示资源名错误，例如 drawable 目录中存在 README 文件导致构建失败。
  解决：删除不可用的 README 文件，确保 drawable 目录仅包含可识别的资源文件（.xml、.png、.jpg 等）。
- 问题：需要添加 24 张主题图片并确保命名与 DataSource 的引用一致。
 解决：将 Architecture、Automotive、Biology 等图片命名与 DataSource 的 R.drawable 及 strings 资源保持一致，确保 stringResource 与 drawable 一致性。
