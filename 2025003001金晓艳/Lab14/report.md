# BusSchedule 公交查询应用 - 开发报告

## 一、项目概述
**项目名称**：BusSchedule 公交时刻表应用  
**开发环境**：Android Studio + Kotlin + Jetpack Compose（Material 3）  
**项目功能**：实现公交站点列表展示、站点详情查询、到站时间格式化显示，采用 Compose 导航实现页面切换，遵循 MVVM 架构模式。

## 二、技术栈
- 编程语言：Kotlin
- UI 框架：Jetpack Compose (Material3)
- 架构组件：ViewModel + Lifecycle + Flow
- 导航：Navigation Compose
- 数据：本地数据源 / Room 数据库（可扩展）
- 主题：深色/浅色模式 + 动态配色（Android 12+）


## 三、核心功能实现
1. **应用入口与主题配置**
- 在 MainActivity 中加载 BusscheduleTheme
- 统一使用 Surface 包裹页面，适配系统主题与背景色

2. **页面导航**
- 使用 Navigation Compose 实现两个页面：
  - ScheduleList：公交站点列表
  - StopDetail：站点详细时刻表
- 通过路由传参实现站点名称传递

3. **数据流与生命周期安全**
- ViewModel 通过 Flow 暴露数据
- 使用 collectAsStateWithLifecycle 保证生命周期安全，避免内存泄漏

4. **UI 组件**
- TopAppBar 带返回箭头
- LazyColumn 高效列表展示
- 时间格式化工具方法（毫秒 → 标准时间格式）

## 四、开发中遇到的问题及解决方案
1. **Unresolved reference: BusScheduleTheme**
- 原因：主题函数名为 BusscheduleTheme（小写 b），调用时名称不匹配
- 解决：统一导入与调用名称，修正为 BusscheduleTheme

2. **Unresolved reference: composable**
- 原因：缺少 navigation-compose 依赖
- 解决：添加导航依赖并同步 Gradle

3. **Unresolved reference: collectAsStateWithLifecycle**
- 原因：缺少 lifecycle-runtime-compose 依赖
- 解决：引入对应依赖并正确导入包

4. **@Composable 调用位置错误**
- 原因：Composable 函数写在非 Composable 环境中
- 解决：将所有 UI 代码放入 setContent 内

5. **未使用参数/函数警告**
- 原因：onBackClick、BusScheduleTopAppBar 未实际使用
- 解决：在导航结构中集成 TopAppBar，绑定返回逻辑

## 五、最终完成效果
- 正常启动无报错、无警告
- 列表页展示所有公交站点
- 点击站点进入详情页查看时刻表
- 支持返回导航
- 时间自动格式化显示
- 主题适配深色/浅色模式
- 项目结构规范，可维护性强

## 六、总结
本项目基于 Android Jetpack Compose 完成了一套完整的公交时刻表查询应用，实现了 MVVM 架构、Compose 导航、生命周期安全收集 Flow 数据、列表展示、时间格式化等核心能力。
开发过程中解决了依赖缺失、命名不统一、Composable 调用规范、导航配置等典型问题，最终项目可正常编译、运行并展示完整业务流程。
