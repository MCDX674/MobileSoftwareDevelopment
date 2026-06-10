# Lab10：为 Lunch Tray 添加导航实验报告

## 一、实验概述

本次实验基于 Jetpack Compose Navigation 组件，为 Lunch Tray 午餐点餐应用添加多屏导航功能。起始代码中页面 UI 已实现但缺少页面间的路由跳转。实验目标是通过 Navigation 组件构建完整的导航流，实现 Start → Entree → SideDish → Accompaniment → Checkout 的页面流转，并管理好返回堆栈。

## 二、NavController、NavHost 和 composable() 的关系

Navigation 组件的三个核心部分：

1. **NavController**：导航控制器，负责在目标页面之间导航，通过 `navigate()` 跳转、`popBackStack()` 返回、`navigateUp()` 向上导航，是操作导航的"执行者"。

2. **NavHost**：可组合项容器，根据当前路线显示对应的页面。内部通过 lambda 隐式定义 NavGraph，是"显示窗口"。

3. **composable()**：NavGraphBuilder 的扩展函数，在 NavHost 的 lambda 中调用，用于注册一条路线及其对应的可组合项。每条 `composable(route)` 调用定义了一个"目的地"。

**关系总结**：NavHost 充当容器，在其内容 lambda 中通过 `composable()` 注册路线→页面的映射关系；NavController 在外部控制跳转，改变当前路线后 NavHost 自动重组显示对应的可组合项。

## 三、LunchTrayScreen 枚举类的设计说明

枚举定义五个页面及其标题：

| 枚举值 | 标题资源 | 对应页面 |
|--------|----------|----------|
| Start | app_name | 开始点餐 |
| Entree | choose_entree | 选择主菜 |
| SideDish | choose_side_dish | 选择配菜 |
| Accompaniment | choose_accompaniment | 选择佐餐 |
| Checkout | order_checkout | 结账 |

**为什么使用枚举而非直接字符串：**

1. **编译期类型安全**：拼写错误在编译时就能发现，而非运行时崩溃
2. **IDE 自动补全**：输入 `LunchTrayScreen.` 即可看到所有可选页面
3. **可附加属性**：每个枚举值可以关联 `title` 资源 ID，`currentScreen.title` 直接得到对应标题
4. **`.name` 属性天然适合做路由**：枚举常量名与路由字符串一致，避免硬编码

## 四、LunchTrayAppBar 的设计思路

设计要点：

1. **动态标题**：通过 `currentScreen.title`（字符串资源 ID）传给 `stringResource()`，页面切换时标题自动更新
2. **返回按钮显示条件**：通过 `navController.previousBackStackEntry != null` 判断——当返回堆栈中当前页面不是栈底时显示箭头，Start 页面不显示
3. **返回行为**：点击箭头调用 `navController.navigateUp()` 弹出栈顶页面，回到上一页
4. **样式**：使用 `MaterialTheme.colorScheme.primaryContainer` 作为背景色

```kotlin
TopAppBar(
    title = { Text(stringResource(currentScreen.title)) },
    navigationIcon = {
        if (canNavigateBack) {
            IconButton(onClick = navigateUp) {
                Icon(imageVector = Icons.Filled.ArrowBack, ...)
            }
        }
    }
)
```

## 五、导航流程设计与返回堆栈管理

### 导航流程

```
Start → Entree → SideDish → Accompaniment → Checkout → Start(提交后)
  ↑         ↑          ↑            ↑            ↑
  |   Cancel |   Cancel |     Cancel |    Cancel |
  └──────────┴──────────┴────────────┴────────────┘
```

### 为什么 Start 页面需要被弹出

从 Start 页面进入 Entree 时，使用以下方式导航：

```kotlin
navController.navigate(LunchTrayScreen.Entree.name) {
    popUpTo(LunchTrayScreen.Start.name) { inclusive = true }
}
```

**原因**：当用户从 Start 进入点餐流程后，按系统返回键应退出应用，而不是回到 Start 页面。弹出 Start 页面后，返回堆栈中不再有 Start，系统返回键自然退出应用。这符合 Material Design 的导航原则——起始页面不应出现在点餐流程的返回堆栈中。

### Cancel 按钮的堆栈管理

```kotlin
navController.navigate(LunchTrayScreen.Start.name) {
    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
}
```

从任意页面 Cancel 时，清空整个返回堆栈回到 Start，同时调用 `viewModel.resetOrder()` 清空订单数据，确保用户重新点餐时状态干净。

## 六、实验中遇到的问题与解决过程

1. **viewModel() 获取的 OrderViewModel 与 NavHost 中不一致**：确保在 `LunchTrayApp` 函数中通过 `viewModel()` 获取 ViewModel 实例，并在所有 composable 回调中引用同一个实例。

2. **页面间导航后返回按钮不显示**：使用 `navController.currentBackStackEntryAsState()` 监听当前返回堆栈条目变化，正确设置 `LunchTrayAppBar` 的 `canNavigateBack` 参数。

3. **Preview 预览报错**：NavHost 和 NavController 无法在预览中自动创建，为 Preview 传入 mock 参数或暂时注释。

4. **返回堆栈管理混乱**：利用 `popUpTo` 的 `inclusive` 参数控制页面是否保留在栈中。Cancel 时使用 `findStartDestination().id` 配合 `inclusive = true` 确保完全清空堆栈。

## 七、实验总结

通过本次实验掌握了 Compose Navigation 的核心用法：

- 使用 `enum class` 定义类型安全的路由
- 通过 `NavHost` + `composable()` 建立路线到页面的映射
- 使用 `NavController` 控制导航跳转和返回堆栈管理
- 利用 `currentBackStackEntryAsState()` 实现响应式应用栏
- 通过函数类型参数隔离导航逻辑，使各个屏幕保持独立

导航逻辑集中在 NavHost 中管控，屏幕组件仅暴露 `onNextButtonClicked`、`onCancelButtonClicked` 等回调，体现了控制反转的设计思想，提升了代码的可维护性和可复用性。
